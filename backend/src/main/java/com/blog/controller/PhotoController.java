package com.blog.controller;

import com.blog.model.Photo;
import com.blog.repository.PhotoRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoRepository repo;

    public PhotoController(PhotoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Photo> list() {
        return repo.findAll();
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return repo.findCategories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Photo create(@RequestBody Photo photo) {
        photo.setCreatedAt(TimeUtil.now());
        return repo.save(photo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
