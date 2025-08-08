package com.eccolimp.cacamba_manager.security.controller;

import com.eccolimp.cacamba_manager.security.model.User;
import com.eccolimp.cacamba_manager.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;

@RestController
@RequestMapping("/dev/security")
@RequiredArgsConstructor
@Profile("dev")
public class DevSecurityController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @GetMapping("/hash")
    public ResponseEntity<Map<String, Object>> hash(@RequestParam(name = "raw", defaultValue = "admin123") String raw) {
        String hash = passwordEncoder.encode(raw);
        Map<String, Object> body = new HashMap<>();
        body.put("raw", raw);
        body.put("hash", hash);
        body.put("head", hash.substring(0, Math.min(hash.length(), 4)));
        body.put("len", hash.length());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/reset-admin")
    public ResponseEntity<Map<String, Object>> resetAdmin(@RequestParam(name = "raw", defaultValue = "admin123") String raw) {
        String hash = passwordEncoder.encode(raw);
        User admin = userRepository.findByUsername("admin").orElseThrow();
        admin.setPassword(hash);
        userRepository.save(admin);
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ok");
        body.put("username", admin.getUsername());
        body.put("newHead", hash.substring(0, 4));
        body.put("newLen", hash.length());
        return ResponseEntity.ok(body);
    }
}


