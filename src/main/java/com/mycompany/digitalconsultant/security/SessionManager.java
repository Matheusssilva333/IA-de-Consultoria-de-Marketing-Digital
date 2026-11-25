package com.mycompany.digitalconsultant.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestão de Sessão do Usuário com proteção contra Brute Force (Rate Limiting).
 */
public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Duration SESSION_TTL = Duration.ofHours(4);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    private record UserSession(String token, String username, Instant createdAt, Instant lastAccessedAt) {}
    private record FailedAttemptTracker(int attempts, Instant lockedUntil) {}

    private final Map<String, UserSession> activeSessionsByToken = new ConcurrentHashMap<>();
    private final Map<String, String> activeTokenByUsername = new ConcurrentHashMap<>();
    private final Map<String, FailedAttemptTracker> failedAttempts = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public synchronized boolean isUserLockedOut(String username) {
        if (username == null) return false;
        FailedAttemptTracker tracker = failedAttempts.get(username.toLowerCase());
        if (tracker == null) return false;
        if (tracker.lockedUntil() != null) {
            if (Instant.now().isBefore(tracker.lockedUntil())) {
                return true;
            } else {
                failedAttempts.remove(username.toLowerCase());
                return false;
            }
        }
        return false;
    }

    public synchronized void recordFailedAttempt(String username) {
        if (username == null) return;
        String key = username.toLowerCase();
        FailedAttemptTracker current = failedAttempts.getOrDefault(key, new FailedAttemptTracker(0, null));
        int nextAttempts = current.attempts() + 1;
        Instant lockedUntil = null;
        if (nextAttempts >= MAX_FAILED_ATTEMPTS) {
            lockedUntil = Instant.now().plus(LOCKOUT_DURATION);
        }
        failedAttempts.put(key, new FailedAttemptTracker(nextAttempts, lockedUntil));
    }

    public synchronized void recordSuccessfulLogin(String username) {
        if (username != null) {
            failedAttempts.remove(username.toLowerCase());
        }
    }

    public synchronized String createSession(String username) {
        if (username == null) throw new IllegalArgumentException("Username não pode ser nulo");
        // Invalida sessão anterior do usuário se existir
        String previousToken = activeTokenByUsername.get(username.toLowerCase());
        if (previousToken != null) {
            activeSessionsByToken.remove(previousToken);
        }

        String token = "sao-sess-" + UUID.randomUUID().toString();
        Instant now = Instant.now();
        UserSession session = new UserSession(token, username, now, now);

        activeSessionsByToken.put(token, session);
        activeTokenByUsername.put(username.toLowerCase(), token);
        return token;
    }

    public synchronized boolean isValidSession(String token) {
        if (token == null) return false;
        UserSession session = activeSessionsByToken.get(token);
        if (session == null) return false;

        if (Instant.now().isAfter(session.lastAccessedAt().plus(SESSION_TTL))) {
            destroySession(token);
            return false;
        }

        // Renova timestamp de acesso
        activeSessionsByToken.put(token, new UserSession(
                session.token(), session.username(), session.createdAt(), Instant.now()));
        return true;
    }

    public synchronized String getUsernameByToken(String token) {
        UserSession session = activeSessionsByToken.get(token);
        return session != null ? session.username() : null;
    }

    public synchronized void destroySession(String token) {
        if (token != null) {
            UserSession session = activeSessionsByToken.remove(token);
            if (session != null) {
                activeTokenByUsername.remove(session.username().toLowerCase());
            }
        }
    }
}
