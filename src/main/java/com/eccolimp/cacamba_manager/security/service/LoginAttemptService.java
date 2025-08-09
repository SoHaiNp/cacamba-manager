package com.eccolimp.cacamba_manager.security.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço em memória para contagem de falhas, backoff incremental e lockout temporário.
 * Chaves: por IP e por username (normalizado).
 */
@Service
public class LoginAttemptService {

    private static final int MAX_FAILS_FOR_LOCK = 10;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private static final Duration[] BACKOFF_SEQUENCE = new Duration[] {
            Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(4),
            Duration.ofSeconds(6), Duration.ofSeconds(8), Duration.ofSeconds(10)
    };

    private static class State {
        int fails;
        Instant lockedUntil;
        Instant lastFail;
    }

    private final Map<String, State> byIp = new ConcurrentHashMap<>();
    private final Map<String, State> byUser = new ConcurrentHashMap<>();

    private State getState(Map<String, State> map, String key) {
        return map.computeIfAbsent(key, k -> new State());
    }

    public boolean isLocked(String ip, String username) {
        Instant now = Instant.now();
        State sIp = getState(byIp, ip);
        if (sIp.lockedUntil != null && now.isBefore(sIp.lockedUntil)) return true;
        if (username != null && !username.isBlank()) {
            State sUser = getState(byUser, username.toLowerCase());
            return sUser.lockedUntil != null && now.isBefore(sUser.lockedUntil);
        }
        return false;
    }

    public Duration currentBackoff(String ip, String username) {
        int idx = 0;
        State sIp = getState(byIp, ip);
        idx = Math.max(idx, Math.min(sIp.fails, BACKOFF_SEQUENCE.length) - 1);
        if (username != null && !username.isBlank()) {
            State sUser = getState(byUser, username.toLowerCase());
            idx = Math.max(idx, Math.min(sUser.fails, BACKOFF_SEQUENCE.length) - 1);
        }
        if (idx < 0) return Duration.ZERO;
        return BACKOFF_SEQUENCE[idx];
    }

    public void onSuccess(String ip, String username) {
        byIp.remove(ip);
        if (username != null && !username.isBlank()) {
            byUser.remove(username.toLowerCase());
        }
    }

    public void onFailure(String ip, String username) {
        Instant now = Instant.now();
        State sIp = getState(byIp, ip);
        sIp.fails++;
        sIp.lastFail = now;
        if (sIp.fails >= MAX_FAILS_FOR_LOCK) {
            sIp.lockedUntil = now.plus(LOCK_DURATION);
        }
        if (username != null && !username.isBlank()) {
            State sUser = getState(byUser, username.toLowerCase());
            sUser.fails++;
            sUser.lastFail = now;
            if (sUser.fails >= MAX_FAILS_FOR_LOCK) {
                sUser.lockedUntil = now.plus(LOCK_DURATION);
            }
        }
    }
}


