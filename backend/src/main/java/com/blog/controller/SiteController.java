package com.blog.controller;

import com.blog.repository.SiteConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    private final SiteConfigRepository repo;

    public SiteController(SiteConfigRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Map<String, String> get() {
        return repo.findAll();
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> update(@RequestBody Map<String, String> body) {
        body.forEach(repo::set);
        return ResponseEntity.ok(repo.findAll());
    }
}
