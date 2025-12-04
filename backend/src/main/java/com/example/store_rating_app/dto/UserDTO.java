package com.example.store_rating_app.dto;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String address;
    private String role;
    private Object extra1; // placeholder if you used these earlier
    private Object extra2;

    public UserDTO() {
    }

    public UserDTO(Long id, String name, String email, String address, String role, Object extra1, Object extra2) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.role = role;
        this.extra1 = extra1;
        this.extra2 = extra2;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public Object getExtra1() {
        return extra1;
    }

    public Object getExtra2() {
        return extra2;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setExtra1(Object extra1) {
        this.extra1 = extra1;
    }

    public void setExtra2(Object extra2) {
        this.extra2 = extra2;
    }
}
