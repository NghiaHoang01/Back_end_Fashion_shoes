package com.example.request;

import java.util.List;

public class OrderRequest {
    private String fullNameOfUser;
    private String addressOfUser;
    private String district;
    private String province;
    private String ward;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String transactionId;
    private List<OrderProductQuantityRequest> productQuantities;

    public OrderRequest() {
    }

    public OrderRequest(String fullNameOfUser, String addressOfUser, String district, String province, String ward, String phoneNumber, String alternatePhoneNumber, String transactionId, List<OrderProductQuantityRequest> productQuantities) {
        this.fullNameOfUser = fullNameOfUser;
        this.addressOfUser = addressOfUser;
        this.district = district;
        this.province = province;
        this.ward = ward;
        this.phoneNumber = phoneNumber;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.transactionId = transactionId;
        this.productQuantities = productQuantities;
    }

    public String getFullNameOfUser() {
        return fullNameOfUser;
    }

    public void setFullNameOfUser(String fullNameOfUser) {
        this.fullNameOfUser = fullNameOfUser;
    }

    public String getAddressOfUser() {
        return addressOfUser;
    }

    public void setAddressOfUser(String addressOfUser) {
        this.addressOfUser = addressOfUser;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public List<OrderProductQuantityRequest> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(List<OrderProductQuantityRequest> productQuantities) {
        this.productQuantities = productQuantities;
    }
}
