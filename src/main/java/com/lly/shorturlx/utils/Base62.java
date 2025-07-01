package com.lly.shorturlx.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Base62 {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();
    private static String stuffedAlphabet;
    private Base62(){
        //私有构造函数
        stuffedAlphabet = stuffleString();
    }

    public String generateShortUrl(Long id){
        return base62Encode(id);
    }
    private static String base62Encode(Long decimal){
        StringBuilder sb = new StringBuilder();
        while (decimal > 0){
            int remainder = (int)(decimal % 62);
            sb.append(stuffedAlphabet.charAt(remainder));
            decimal/=62;
        }
        return sb.reverse().toString();
    }

    /**
     * 打乱字符串，洗牌
     * @return
     */
    private static String stuffleString(){
        char[] chars = Base62.ALPHABET.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = RANDOM.nextInt(i+1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
