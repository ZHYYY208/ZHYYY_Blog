package com.blog.controller;

import com.blog.model.ShuoShuo;
import com.blog.repository.ShuoShuoRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shuoshuo")
public class ShuoShuoController {

    private final ShuoShuoRepository repo;

    public ShuoShuoController(ShuoShuoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ShuoShuo> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShuoShuo create(@RequestBody ShuoShuo s) {
        s.setCreatedAt(TimeUtil.now());
        return repo.save(s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
