package com.blog.controller;

import com.blog.model.Post;
import com.blog.repository.PostRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository repo;

    public PostController(PostRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Post> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> detail(@PathVariable Long id) {
        Post post = repo.findById(id);
        return post == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(post);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post p) {
        p.setCreatedAt(TimeUtil.now());
        return repo.save(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repo.findById(id) == null) return ResponseEntity.notFound().build();
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
