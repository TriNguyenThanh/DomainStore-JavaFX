package com.utc2.domainstore.view;


import com.utc2.domainstore.entity.database.RoleEnum;

//Singleton lớp
public class UserSession {

    private static UserSession instance;
    private int userId = -1;
    private RoleEnum role;


    private UserSession() {
        // Private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) throws Exception {
        if (userId <= 0) throw new Exception("No user");
        this.userId = userId;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public void logout() {
        instance = null;
    }
}
