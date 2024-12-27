package com.example.monolithmoija.controller;

import io.opencensus.common.Timestamp;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.LocalDateTime;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/logined")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String test() throws IOException {

        return "Hello!";
    }
    @GetMapping("/basic")
    public String basic() throws IOException {

        return "Hello! basic";
    }

    @GetMapping("/sleep/50")
    public void sleep1() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        long end = System.currentTimeMillis() + 50;

        for (int i = 0; i < cores; i++) {
            Thread thread = new Thread(() -> {
                while (System.currentTimeMillis() < end);
            });
            thread.start();
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/sleep/100")
    public void sleep2() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        long end = System.currentTimeMillis() + 200;

        for (int i = 0; i < cores; i++) {
            Thread thread = new Thread(() -> {
                while (System.currentTimeMillis() < end);
            });
            thread.start();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/sleep/200")
    public void sleep3() throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        long end = System.currentTimeMillis() + 200;

        for (int i = 0; i < cores; i++) {
            Thread thread = new Thread(() -> {
                while (System.currentTimeMillis() < end);
            });
            thread.start();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
