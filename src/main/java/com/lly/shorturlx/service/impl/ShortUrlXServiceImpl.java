package com.lly.shorturlx.service.impl;

import com.lly.shorturlx.mapper.UrlMapMapper;
import com.lly.shorturlx.model.UrlMap;
import com.lly.shorturlx.redis.RedisUtil;
import com.lly.shorturlx.service.ShortUrlXService;
import com.lly.shorturlx.utils.Base62;
import com.lly.shorturlx.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShortUrlXServiceImpl implements ShortUrlXService {
    @Autowired
    private UrlMapMapper urlMapMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Base62 base62;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    private final String projectPrefix = "shortUrlX-";
    private final String shortUrlBloomFilterKey = projectPrefix + "BloomFilter-ShortUrl";
    private final String shortUrlPrefix = projectPrefix + "ShortUrl:";
    //当布隆过滤器命中且缓存命中时，返回{0，value}，布隆过滤器命中缓存未命中返回{1，''}布隆过滤器未命中时，返回{0，‘’}
    private final String findShortUrlFormBloomFilterAndCacheLua = "local bloomKey = KEYS[1]\nlocal cacheKey = KEYS[2]\nlocal bloomVal = ARGV[1]\n\n-- 检查val是否存在于布隆过滤器对应的bloomKey中\nlocal exists = redis.call('BF.EXISTS', bloomKey, bloomVal)\n\n-- 如果bloomVal不存在于布隆过滤器中，直接返回空字符串, 返回0代表不需要查db了\nif exists == 0 then\n    return {0, ''}\nend\n\n-- 如果bloomVal存在于布隆过滤器中，查询cacheKey\nlocal value = redis.call('GET', cacheKey)\n\n-- 如果cacheKey存在，返回对应的值，否则返回空字符串\nif value then\n    return {0, value}\nelse\n    return {1, ''}\nend\n";
    private final String longUrlPrefix = projectPrefix + "LongUrl:";

    private final String addShortUrlToBloomFilterLua = "redis.call('bf.add', KEYS[1], ARGV[1])";
    @Override
    public String getShortUrl(String longUrl) {
        //先查缓存里面是否有对应的短链
        String shortUrl = (String) redisUtil.get(longUrlPrefix+longUrl);
        //缓存中有这个短链，直接返回
        if(shortUrl!=null&&!shortUrl.isEmpty()){
            return shortUrl;
        }
        //再查询数据库里面是否有长链对应的短链
        shortUrl = urlMapMapper.dbGetShortUrl(longUrl);
        //数据库中有这个短链，顺便保存到缓存中
        if(shortUrl!=null&&!shortUrl.isEmpty()){
            redisUtil.set(longUrlPrefix+longUrl,shortUrl);
            return shortUrl;
        }

        //还是没找到，那就利用雪花算法生成ID
        Long id = snowflakeIdWorker.nextId();
        //再用base62进行62进制转换
        shortUrl = base62.generateShortUrl(id);
        //将短链保存到布隆过滤器中
        addShortUrlToBloomFilterLua(shortUrl);
        //保存到缓存中，以便下次查询
        redisUtil.set(longUrlPrefix+longUrl,shortUrl);
        //保存到数据库
        urlMapMapper.dbCreate(new UrlMap(longUrl, shortUrl));
        return shortUrl;
    }

    @Override
    public String getLongUrl(String shortUrl) {
        //先查布隆过滤器，布隆过滤器命中再查redis缓存
        List<String> keys = new ArrayList<>();
        keys.add(shortUrlBloomFilterKey);
        keys.add(shortUrlPrefix + shortUrl);
        List<Object> values = new ArrayList<>();
        values.add(shortUrl);
        List<Object> result = redisUtil.executeLua(findShortUrlFormBloomFilterAndCacheLua,keys,values);
        if (result==null){
            return null;
        }
        long need = (long) result.get(0);
        if(need==1){
            return urlMapMapper.dbGetLongUrl(shortUrl);
        }
        return (String) result.get(1);
    }

    /**
     * 将短链添加到布隆过滤器中
     * @param shortUrl
     */
    private void addShortUrlToBloomFilterLua(String shortUrl) {
        List<String> keys = new ArrayList<>();
        keys.add(shortUrlBloomFilterKey);
        List<Object> values = new ArrayList<>();
        values.add(shortUrl);
        redisUtil.executeLua(addShortUrlToBloomFilterLua, keys, values);
    }
}
