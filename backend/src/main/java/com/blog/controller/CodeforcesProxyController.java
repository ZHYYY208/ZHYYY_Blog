package com.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/cf")
public class CodeforcesProxyController {

    @GetMapping("/user")
    public String user(@RequestParam("handle") String handle) {
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
            if (code != 200) {
                return "{\"status\":\"FAILED\",\"http\":" + code + ",\"body\":" + quote(sb.toString()) + "}";
            }
            return sb.toString();
        } catch (Exception e) {
            return "{\"status\":\"FAILED\",\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
