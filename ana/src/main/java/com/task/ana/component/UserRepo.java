package com.task.ana.component;

import com.task.ana.model.Access;

import java.util.concurrent.ConcurrentHashMap;

public class UserRepo {

    private static UserRepo singleton = new UserRepo();

    private ConcurrentHashMap<String, String> authentationInfo;

    private ConcurrentHashMap<String, Access> authorisationInfo;

    private UserRepo() {
        authentationInfo = new ConcurrentHashMap<>();
        authorisationInfo = new ConcurrentHashMap<>();
    }

    public static UserRepo getSingleton() {
        return singleton;
    }

    public static boolean saveUser(String username, String hash, Access access) {
        if (getSingleton().authentationInfo.containsKey(username)) {
            return false;
        } else {
            getSingleton().authentationInfo.put(username, hash);
            getSingleton().authorisationInfo.put(username, access);
            return true;
        }
    }

    public static boolean isAuthenticated(String username, String hash) {
        return getSingleton().authentationInfo.containsKey(username) && getSingleton().authentationInfo.get(username).equals(hash);
    }

    public static Access getAccess(String username) {
        return getSingleton().authorisationInfo.get(username);
    }
}
