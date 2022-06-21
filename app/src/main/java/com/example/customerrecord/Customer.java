package com.example.customerrecord;

public class Customer {
    private String customerName;
    private String customerPhone;
    private String imageUrl;
    private String userID;

    public Customer(String customerName, String customerPhone, String imageUrl, String userID) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.imageUrl = imageUrl;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
