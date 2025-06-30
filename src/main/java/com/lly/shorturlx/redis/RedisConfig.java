package com.lly.shorturlx.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        //设置连接工厂
        template.setConnectionFactory(factory);
        @SuppressWarnings({"rawtypes","unchecked"})
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        
        ObjectMapper om = new ObjectMapper();
        // 设置Jackson可见性，允许序列化所有字段
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型信息写入，支持反序列化时的多态类型处理
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 将配置好的ObjectMapper绑定到Jackson2JsonRedisSerializer
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置Redis的value序列化方式为Jackson2JsonRedisSerializer，用于支持Java对象的序列化与反序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // 配置Redis的hashValue序列化方式也为Jackson2JsonRedisSerializer
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 创建String类型的key和hashKey的序列化器，使用StringRedisSerializer以避免乱码问题
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置Redis key的序列化方式为StringRedisSerializer
        template.setKeySerializer(stringRedisSerializer);
        // 设置Redis hashKey的序列化方式也为StringRedisSerializer
        template.setHashKeySerializer(stringRedisSerializer);

        // 初始化模板，确保所有配置生效
        template.afterPropertiesSet();
        return template;
    }
}
