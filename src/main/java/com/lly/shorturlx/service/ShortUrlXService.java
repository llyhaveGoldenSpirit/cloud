package com.lly.shorturlx.service;

public interface ShortUrlXService {
    String getShortUrl(String longUrl);
    String getLongUrl(String shortUrl);
}
