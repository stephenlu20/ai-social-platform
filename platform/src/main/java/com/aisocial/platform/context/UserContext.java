package com.aisocial.platform.context;

import com.aisocial.platform.entity.User;

/**
 * Thread-local storage for the current user.
 * Set by UserContextFilter at the start of each request.
 */
public class UserContext {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }

    /**
     * Check if a user is currently set in the context.
     */
    public static boolean hasUser() {
        return currentUser.get() != null;
    }
}