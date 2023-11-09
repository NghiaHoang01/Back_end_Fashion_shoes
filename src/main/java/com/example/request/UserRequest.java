package com.example.request;

import com.example.Entity.Role;

import java.util.Set;

public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private String mobile;
    private String address;
    private String province;
    private String district;
    private String ward;
    private Set<String> lstRole;

    public UserRequest() {
    }

    public UserRequest(String firstName, String lastName, String email, String password,
                       String gender, String mobile, String address, String province, String district, String ward, Set<String> lstRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.mobile = mobile;
        this.address = address;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.lstRole = lstRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public Set<String> getLstRole() {
        return lstRole;
    }

    public void setLstRole(Set<String> lstRole) {
        this.lstRole = lstRole;
    }
}
