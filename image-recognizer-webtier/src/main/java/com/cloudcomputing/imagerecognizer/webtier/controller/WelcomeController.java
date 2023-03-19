package com.cloudcomputing.imagerecognizer.webtier.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/")
public class WelcomeController {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("cc")
    public String welcome() {
        log.info("received a request..." + atomicInteger.incrementAndGet());
        return "Welcome to Cloud Computing Project 1 - Image Recognition Service!   "  + atomicInteger.get();
    }
}
