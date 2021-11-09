package com.task.ana.component;

import com.task.ana.model.Access;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;

public class AnaManager {

    private static HashSet<String> tokens = new HashSet<>(); // md5 + username

    public static boolean register(String username, String hash, String access) {
        Access right;
        try {
            right = Access.valueOf(access.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return UserRepo.saveUser(username, hash, right);
    }

    public static String login(String username, String hash) {
        if (UserRepo.isAuthenticated(username, hash)) {
            String token = DigestUtils.md5DigestAsHex((username + LocalDateTime.now().toString()).getBytes());
            tokens.add(token + username);
            return token + username;
        } else {
            return null;
        }
    }

    public static void logout(String token) {
        tokens.remove(token);
    }

    public static boolean authenticate(String token) {
        return tokens.contains(token);
    }

    public static Access getAccess(String token) {
        if (!authenticate(token)) {
            return null;
        }
        return UserRepo.getAccess(token.substring(32));
    }
}
