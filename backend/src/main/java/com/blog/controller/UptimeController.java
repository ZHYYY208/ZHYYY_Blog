package com.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.lang.management.ManagementFactory;

@RestController
@RequestMapping("/api/uptime")
public class UptimeController {

    @GetMapping
    public Map<String, Object> uptime() {
        long ms = ManagementFactory.getRuntimeMXBean().getUptime();
        long s = ms / 1000;
        long d = s / 86400;
        long h = (s % 86400) / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;
        return Map.of(
                "days", d, "hours", h, "minutes", m, "seconds", sec, "totalSeconds", s);
    }
}
