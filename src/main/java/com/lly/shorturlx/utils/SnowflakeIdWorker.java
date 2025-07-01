package com.lly.shorturlx.utils;

import com.lly.shorturlx.common.conf.AppConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SnowflakeIdWorker {
    @Autowired
    private AppConf appConf;

    //工作机器ID（默认0~1023）
    private long workerId;

    //序列号（1ms内默认0~4095）
    private long sequence = 0L;
    //开始时间戳
    private long startTimeStamp = System.currentTimeMillis();

    //workerId位数
    private long workerIdBits = 10L;
    //最大值
    private long maxWorkerId;
    //序列号位数
    private long sequenceBits = 12L;
    //workerId左移12位
    private long workerIdShift = sequenceBits;
    //时间戳左移22位(10+22)
    private long timestampLeftShift;
    //生成序列的掩码
    private long sequenceMask = ~(-1L<<sequenceBits);
    private long lastTimestamp = -1L;

    @PostConstruct//在Bean初始化阶段执行
    public void init(){
        //获取work_id
        workerId = appConf.getWorkId();
        //获取workerIdBits
        workerIdBits = appConf.getWorkerIdBits();
        maxWorkerId = ~(-1L<<workerIdBits);
        timestampLeftShift = sequenceBits+workerIdBits;
        sequence = 0L;
        //做校验
        if(workerId>maxWorkerId||workerId<0){
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }else {
            System.out.println("workerId: "+workerId);
        }

        if(workerIdBits>12||workerIdBits<0){
            throw new IllegalArgumentException("worker Id bits can't be greater than 12 or less than 0");
        }else{
            System.out.println("workerIdBits: "+workerIdBits);
        }
        System.out.println("InitByPostConstructAnnotation do something");;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //毫秒内序列数已经达到最大值，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //不同毫秒内，序列号置0
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - startTimeStamp) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
