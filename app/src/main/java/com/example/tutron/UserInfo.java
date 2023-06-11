package com.example.tutron;

import android.hardware.usb.UsbRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInfo implements Serializable {
    private String account;
    private String password;
    private String nickname;
    private String role;

    static List<UserInfo> userInfos;
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static List<UserInfo> getUserInfos() {
        if (userInfos == null){
            userInfos = new ArrayList<>();
            UserInfo adminUser = new UserInfo();
            adminUser.setAccount("admin");
            adminUser.setPassword("12345");
            adminUser.setNickname("admin");
            adminUser.setRole("0");
            userInfos.add(adminUser);
        }
        return userInfos;
    }

}
