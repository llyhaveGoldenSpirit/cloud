package com.lly.shorturlx.service.impl;

import com.lly.shorturlx.mapper.UrlMapMapper;
import com.lly.shorturlx.service.ShortUrlXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlXServiceImpl implements ShortUrlXService {
    @Autowired
    private UrlMapMapper urlMapMapper;

    @Override
    public String getShortUrl(String longUrl) {
        return null;
    }

    @Override
    public String getLongUrl(String shortUrl) {
        return null;
    }
}
