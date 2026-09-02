package com.blog.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DbDirInitializer {

    @PostConstruct
    public void init() {
        new File("data").mkdirs();
    }
}
