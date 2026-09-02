package com.blog.controller;

import com.blog.model.MusicTrack;
import com.blog.repository.MusicRepository;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final MusicRepository repo;

    public MusicController(MusicRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<MusicTrack> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MusicTrack create(@RequestBody MusicTrack m) {
        m.setCreatedAt(TimeUtil.now());
        return repo.save(m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
