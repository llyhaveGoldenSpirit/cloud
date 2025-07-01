package com.lly.shorturlx.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public boolean set(String key, Object value){
        try{
            redisTemplate.opsForValue().set(key, value);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, Object value, long time){
        try{
            if(time>0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else {
                set(key, value);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取过期时间
     * @param key
     * @return
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    //执行lua脚本
    public List<Object> executeLua(String redisScript, List<String> keys, List<Object> args) {
        try {
            List<Object> result = redisTemplate.execute(new DefaultRedisScript<>(redisScript, List.class), keys, args);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
