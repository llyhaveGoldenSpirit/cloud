package com.lly.shorturlx.common.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConf {
    @Value("1")
    private int workId;
    @Value("10")
    private int workerIdBits;
}
