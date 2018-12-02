package com.schindlerdavid.statify.singleton;

import com.schindlerdavid.statify.entity.User;

public class StatifyRepo {
    private static final String CLIENT_ID = "36b9546c22b44e31aea665bf90fa0f5d";
    private static final String REDIRECT_URI = "com.schindlerdavid.statify://callback";
    private static User userInstance = null;
    protected StatifyRepo() {
        // Exists only to defeat instantiation.
    }

    public static void setUserInstance(User userInstance) {
        StatifyRepo.userInstance = userInstance;
    }

    public static User getUserInstance() {
        if(userInstance == null) {
            userInstance = new User();
        }
        return userInstance;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getRedirectUri() {
        return REDIRECT_URI;
    }
}