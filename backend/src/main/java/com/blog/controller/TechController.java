package com.blog.controller;

import com.blog.model.TechNode;
import com.blog.repository.TechRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tech")
public class TechController {

    private final TechRepository repo;

    public TechController(TechRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TechNode> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TechNode create(@RequestBody TechNode n) {
        if (n.getParentId() == null) n.setParentId(0L);
        n.setCreatedAt(TimeUtil.now());
        return repo.save(n);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
