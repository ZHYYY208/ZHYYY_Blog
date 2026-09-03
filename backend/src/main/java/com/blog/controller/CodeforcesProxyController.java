package com.blog.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/cf")
public class CodeforcesProxyController {

    // handle -> 缓存 JSON；每天 0 点刷新，避免触发 CF API 限流
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private String lastHandle = null;

    @GetMapping("/user")
    public String user(@RequestParam("handle") String handle) {
        lastHandle = handle;
        String hit = cache.get(handle);
        if (hit != null) {
            return hit;
        }
        String data = fetchUser(handle);
        if (data != null && data.contains("\"status\":\"OK\"")) {
            cache.put(handle, data);
        }
        return data == null ? "{\"status\":\"FAILED\",\"error\":\"upstream\"}" : data;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyRefresh() {
        if (lastHandle == null) {
            // 没有已知 handle，尝试从缓存键取一个刷新
            for (String h : cache.keySet()) {
                refresh(h);
                break;
            }
            return;
        }
        refresh(lastHandle);
    }

    private void refresh(String handle) {
        String data = fetchUser(handle);
        if (data != null && data.contains("\"status\":\"OK\"")) {
            cache.put(handle, data);
        }
    }

    private String fetchUser(String handle) {
        try {
            URL url = new URL("https://codeforces.com/api/user.info?handles="
                    + java.net.URLEncoder.encode(handle, StandardCharsets.UTF_8));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            int code = conn.getResponseCode();
            java.io.InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
            return code == 200 ? sb.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
