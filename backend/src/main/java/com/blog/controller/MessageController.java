package com.blog.controller;

import com.blog.model.Message;
import com.blog.repository.MessageRepository;
import com.blog.repository.UserRepository;
import com.blog.util.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository repo;
    private final UserController userController;
    private final com.blog.repository.UserRepository userRepo;
    private final String adminToken;

    public MessageController(MessageRepository repo, UserController userController,
                             com.blog.repository.UserRepository userRepo,
                             @Value("${blog.admin-token}") String adminToken) {
        this.repo = repo;
        this.userController = userController;
        this.userRepo = userRepo;
        this.adminToken = adminToken;
    }

    @GetMapping
    public List<Message> list() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(value = "X-User-Token", required = false) String token,
                                    @RequestBody Map<String, String> body) {
        Long uid = userController.resolve(token);
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "请先登录"));
        }
        String content = body.getOrDefault("content", "").trim();
        if (content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "内容不能为空"));
        }
        if (content.length() > 1000) {
            return ResponseEntity.badRequest().body(Map.of("error", "内容过长（最多 1000 字）"));
        }
        com.blog.model.User u = userRepo.findById(uid);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "账号不存在"));
        }
        Message m = new Message();
        m.setUserId(uid);
        m.setUsername(u.getUsername());
        m.setRole(u.getRole());
        m.setContent(content);
        m.setCreatedAt(TimeUtil.now());
        repo.save(m);
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestHeader(value = "X-User-Token", required = false) String userToken,
                                    @RequestHeader(value = "X-Admin-Token", required = false) String adminTok) {
        // 管理员可删任意；普通用户只能删自己的（本实现简化为仅管理员可删）
        if (adminTok == null || !adminTok.equals(adminToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "仅站长可删除留言"));
        }
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
