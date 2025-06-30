package com.lly.shorturlx.mapper;

import com.lly.shorturlx.model.UrlMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UrlMapMapper {
    void dbCreate(@Param("urlMap")UrlMap urlMap);
    String dbGetShortUrl(@Param("longUrl")String longUrl);
    String dbGetLongUrl(@Param("shortUrl")String shortUrl);
    void dpUpdate(@Param("shortUrl")String shortUrl, @Param("longUrl")String longUrl);
}
