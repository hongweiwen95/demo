package com.hww.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Component
public class PingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PingScheduler.class);

    private final WebClient webClient;
    private final Lock lock;
    private final String pongServiceUrl;

    public PingScheduler(@Value("${pong.service.url}") String pongServiceUrl) {
        this.webClient = WebClient.create();
        this.lock = new ReentrantLock();
        this.pongServiceUrl = pongServiceUrl;
    }

    @Scheduled(fixedRate = 1000)
    public void pingPongService() {
        lock.lock();
        try {

            Mono<String> response = webClient.get()
                    .uri(pongServiceUrl + "/ping")
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(this::logSuccess)
                    .onErrorResume(this::handleError);

            response.block();
        } finally {
            lock.unlock();
        }
    }

    private void logSuccess(String message) {
        if (!Objects.equals("World", message)) {
            logToFile("402: too many requests");
        } else {
            logToFile("success: " + message);
        }
    }

    private Mono<String> handleError(Throwable throwable) {
        if (throwable instanceof IOException) {
            logToFile("request not sent: " + throwable.getMessage());
        } else {
            logToFile("request throttled: " + throwable.getMessage());
        }
        return Mono.empty();
    }

    private void logToFile(String message) {
        String logFilePath = "ping-service.log";
        try {
            String logMessage = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message + "\n";
            Files.write(Paths.get(logFilePath), logMessage.getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Failed to write to log file: " + e.getMessage());
        }
    }
}