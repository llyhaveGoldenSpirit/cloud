package com.lly.shorturlx.model;

import com.lly.shorturlx.common.BaseModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class UrlMap extends BaseModel implements Serializable {
    private Long id;
    private String longUrl;
    private String shortUrl;
    private String createAt;

    @Override
    public String toString() {
        return "UrlMap{" +
                "id=" + id +
                ", longUrl='" + longUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", createAt='" + createAt + '\'' +
                '}';
    }

    public UrlMap(String longUrl) {
        this.longUrl = longUrl;
    }

    public UrlMap(String longUrl, String shortUrl) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
    }
}
