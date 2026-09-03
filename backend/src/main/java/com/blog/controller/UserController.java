package com.blog.controller;

import com.blog.model.User;
import com.blog.repository.UserRepository;
import com.blog.util.PasswordUtil;
import com.blog.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository repo;
    private final String adminToken;
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public UserController(UserRepository repo,
                          @org.springframework.beans.factory.annotation.Value("${blog.admin-token}") String adminToken) {
        this.repo = repo;
        this.adminToken = adminToken;
    }

    private boolean isAdmin(String tok) {
        return tok != null && tok.equals(adminToken);
    }

    // -------- 管理员接口 --------
    @GetMapping("/admin/list")
    public ResponseEntity<?> adminList(@RequestHeader(value = "X-Admin-Token", required = false) String tok) {
        if (!isAdmin(tok)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(repo.findAll().stream()
                .map(u -> Map.of("id", u.getId(), "username", u.getUsername(), "createdAt", u.getCreatedAt()))
                .toList());
    }

    @PutMapping("/admin/{id}/password")
    public ResponseEntity<?> adminReset(@PathVariable Long id,
                                        @RequestHeader(value = "X-Admin-Token", required = false) String tok,
                                        @RequestBody Map<String, String> body) {
        if (!isAdmin(tok)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String pwd = body.getOrDefault("password", "");
        if (pwd.length() < 6) return ResponseEntity.badRequest().body(Map.of("error", "密码至少 6 位"));
        if (repo.findById(id) == null) return ResponseEntity.notFound().build();
        repo.updatePassword(id, PasswordUtil.hash(pwd));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> adminDelete(@PathVariable Long id,
                                         @RequestHeader(value = "X-Admin-Token", required = false) String tok) {
        if (!isAdmin(tok)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        if (username.length() < 2 || username.length() > 20) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名长度需 2-20 位"));
        }
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码至少 6 位"));
        }
        if (repo.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "用户名已存在"));
        }
        User u = new User();
        u.setUsername(username);
        u.setPassHash(PasswordUtil.hash(password));
        u.setCreatedAt(TimeUtil.now());
        repo.save(u);
        String token = newSession(u.getId());
        return ResponseEntity.ok(publicUser(u, token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String password = body.getOrDefault("password", "");
        User u = repo.findByUsername(username);
        if (u == null || !u.getPassHash().equals(PasswordUtil.hash(password))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户名或密码错误"));
        }
        String token = newSession(u.getId());
        return ResponseEntity.ok(publicUser(u, token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "X-User-Token", required = false) String token) {
        if (token != null) sessions.remove(token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "X-User-Token", required = false) String token) {
        Long uid = token == null ? null : sessions.get(token);
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not-logged-in"));
        }
        User u = repo.findById(uid);
        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "user-gone"));
        return ResponseEntity.ok(publicUser(u, token));
    }

    private String newSession(Long uid) {
        String t = PasswordUtil.randomToken();
        sessions.put(t, uid);
        return t;
    }

    private Map<String, Object> publicUser(User u, String token) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("createdAt", u.getCreatedAt());
        if (token != null) m.put("token", token);
        return m;
    }

    // 供留言/管理使用
    public Long resolve(String token) {
        return token == null ? null : sessions.get(token);
    }
}
