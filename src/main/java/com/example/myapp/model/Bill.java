package com.example.myapp.model;

public class Bill {
    private int id;
    private int userId;
    private double amount;
    private int month;
    private int year;
    private String status;
    private String approvalStatus;
    private String utr;

    // Constructor for creating a new bill
    public Bill(int userId, double amount, int month, int year) {
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.status = "UNPAID";
        this.approvalStatus = "PENDING";
    }

    // Constructor for reading from DB
    public Bill(int id, int userId, double amount, int month, int year, String status, String approvalStatus, String utr) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.utr = utr;
    }
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUtr() {
        return utr;
    }

    public void setUtr(String utr) {
        this.utr = utr;
    }
}
