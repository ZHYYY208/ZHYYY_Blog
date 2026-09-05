package com.blog.controller;

import com.blog.repository.InviteCodeRepository;
import com.blog.util.PasswordUtil;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invite")
public class InviteCodeController {

    private final InviteCodeRepository repo;
    private final String adminToken;

    public InviteCodeController(InviteCodeRepository repo,
                                @org.springframework.beans.factory.annotation.Value("${blog.admin-token}") String adminToken) {
        this.repo = repo;
        this.adminToken = adminToken;
    }

    private boolean isAdmin(String tok) {
        return tok != null && tok.equals(adminToken);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestHeader(value = "X-Admin-Token", required = false) String tok) {
        if (!isAdmin(tok)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(Map.of("list", repo.list(), "available", repo.countAvailable()));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestHeader(value = "X-Admin-Token", required = false) String tok,
                                      @RequestBody(required = false) Map<String, Object> body) {
        if (!isAdmin(tok)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        int n = 1;
        if (body != null && body.get("count") != null) {
            n = Math.max(1, Math.min(50, ((Number) body.get("count")).intValue()));
        }
        String now = TimeUtil.now();
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String code = PasswordUtil.randomToken().substring(0, 12);
            codes.add(code);
            repo.insert(code, now);
        }
        return ResponseEntity.ok(Map.of("codes", codes));
    }
}
