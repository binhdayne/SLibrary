package com.qlthuvien.model;

public class User {
    private String membershipId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String user_name;
    private String avatar;



    public User(String membershipId, String name, String email, String phone, String password, String user_name) {
        this.membershipId = membershipId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.user_name = user_name;
    }

    public User(String membershipId, String name, String email, String phone, String avatar) {
        this.membershipId = membershipId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }

    public User(String membershipId, String name, String email, String phone) {
        this.membershipId = membershipId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User( String name, String email, String phone) {

        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setmembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
