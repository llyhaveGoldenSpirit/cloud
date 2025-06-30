package com.lly.shorturlx.common;

import lombok.Data;

import java.util.Date;
@Data
public class BaseModel {
    protected Date createTime;
    protected Date updateTime;

    @Override
    public String toString() {
        return "BaseModel{" +
                "createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
