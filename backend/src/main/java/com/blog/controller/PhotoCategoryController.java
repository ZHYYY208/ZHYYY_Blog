package com.blog.controller;

import com.blog.model.Photo;
import com.blog.model.PhotoCategory;
import com.blog.repository.PhotoCategoryRepository;
import com.blog.repository.PhotoRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo-categories")
public class PhotoCategoryController {

    private final PhotoCategoryRepository repo;
    private final PhotoRepository photoRepo;

    public PhotoCategoryController(PhotoCategoryRepository repo, PhotoRepository photoRepo) {
        this.repo = repo;
        this.photoRepo = photoRepo;
    }

    @GetMapping
    public List<PhotoCategory> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoCategory create(@RequestBody PhotoCategory c) {
        PhotoCategory exists = repo.findByName(c.getName());
        if (exists != null) {
            return exists;
        }
        c.setCreatedAt(TimeUtil.now());
        return repo.save(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        List<Photo> photos = photoRepo.findByCategoryId(id);
        for (Photo p : photos) {
            photoRepo.setCategory(p.getId(), null);
        }
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
