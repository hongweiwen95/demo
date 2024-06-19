package com.hww.controller;

import com.hww.pojo.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
class PongController {

    private static final Logger logger = LoggerFactory.getLogger(PongController.class);

    private RateLimiter rateLimiter = new RateLimiter(1);

    @GetMapping("/ping")
    public Mono<String> getPing() {
        if (rateLimiter.tryAcquire()) {
            logger.info("send message: World");
            return Mono.just("World");
        } else {
            logger.info("send message: 402");
            return Mono.just("402");
        }
    }
}