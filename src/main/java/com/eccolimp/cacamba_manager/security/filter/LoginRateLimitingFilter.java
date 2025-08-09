package com.eccolimp.cacamba_manager.security.filter;

import com.eccolimp.cacamba_manager.security.service.LoginAttemptService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro simples de rate limiting para endpoints de login.
 * Estratégia: 5 req/min com burst 10 por IP e por username.
 */
@Component
public class LoginRateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitingFilter.class);

    private static final String[] PROTECTED_PATHS = { "/auth/login", "/admin/auth/login" };

    // Janela fixa de 60s, máximo 10 req por chave (burst control simples)
    private static final long WINDOW_MS = 60_000L;
    private static final int MAX_REQUESTS_PER_WINDOW = 10;

    private static class WindowCounter {
        long windowStartMs;
        int count;
    }

    private final Map<String, WindowCounter> ipCounters = new ConcurrentHashMap<>();
    private final Map<String, WindowCounter> userCounters = new ConcurrentHashMap<>();
    private final LoginAttemptService loginAttemptService;

    public LoginRateLimitingFilter(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    private boolean allowRequest(Map<String, WindowCounter> map, String key) {
        long now = Instant.now().toEpochMilli();
        WindowCounter counter = map.computeIfAbsent(key, k -> {
            WindowCounter wc = new WindowCounter();
            wc.windowStartMs = now;
            wc.count = 0;
            return wc;
        });
        synchronized (counter) {
            if (now - counter.windowStartMs >= WINDOW_MS) {
                counter.windowStartMs = now;
                counter.count = 0;
            }
            if (counter.count >= MAX_REQUESTS_PER_WINDOW) {
                return false;
            }
            counter.count++;
            return true;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getRequestURI();
        for (String p : PROTECTED_PATHS) {
            if (path.equals(p)) return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = resolveClientIp(request);
        String username = request.getParameter("login");

        if (loginAttemptService.isLocked(ip, username)) {
            log.warn("[LOCKOUT] ip='{}' user='{}' path='{}'", ip, username, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Muitas tentativas. Tente novamente mais tarde.");
            return;
        }

        Duration backoff = loginAttemptService.currentBackoff(ip, username);
        if (!backoff.isZero() && backoff.toMillis() > 0) {
            try {
                Thread.sleep(backoff.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        boolean ipAllowed = allowRequest(ipCounters, ip);
        boolean userAllowed = username == null || username.isBlank() || allowRequest(userCounters, username.toLowerCase());

        if (!ipAllowed || !userAllowed) {
            log.warn("[RATE_LIMIT] blocked ip='{}' user='{}' path='{}'", ip, username, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Muitas tentativas. Tente novamente em instantes.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = headerFirstIp(request.getHeader("X-Forwarded-For"));
        if (ip == null || ip.isBlank()) {
            ip = headerFirstIp(request.getHeader("X-Real-IP"));
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String headerFirstIp(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) return null;
        // X-Forwarded-For: client, proxy1, proxy2
        String[] parts = headerValue.split(",");
        if (parts.length == 0) return headerValue.trim();
        return parts[0].trim();
    }
}


