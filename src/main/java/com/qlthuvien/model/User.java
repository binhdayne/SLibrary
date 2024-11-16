package com.qlthuvien.model;

public class User {
    private String membershipId;
    private String name;
    private String email;
    private String phone;

    public User(String membershipId, String name, String email, String phone) {
        this.membershipId = membershipId;
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
}
