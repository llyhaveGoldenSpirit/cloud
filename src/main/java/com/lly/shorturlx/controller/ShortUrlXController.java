package com.lly.shorturlx.controller;

import com.lly.shorturlx.service.ShortUrlXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/shorturlx")
public class ShortUrlXController {
    @Autowired
    private ShortUrlXService shortUrlXService;

    //问题：SpringMVC要求请求体必须绑定到一个对象，
    // 所以需要一个包装对象，除非请求携带的是纯文本
    //包装一个对象扩展性也更强

    /**包装对象*/
    public static class CreateShortUrlRequest {
        public String longUrl;
    }
    @PostMapping("/shorten")
    public ResponseEntity<String> createShortUtl(@RequestBody CreateShortUrlRequest request){
        String shortUrl = shortUrlXService.getShortUrl(request.longUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public void redirectToLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        String longUrl = shortUrlXService.getLongUrl(shortUrl);
        sendRedirect(longUrl, response);
    }
    // 进行重定向的函数
    public void sendRedirect(String longUrl, HttpServletResponse response) throws IOException {
        response.sendRedirect(longUrl);
        response.setHeader("Location", longUrl);
        response.setHeader("Connection", "close");
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}
