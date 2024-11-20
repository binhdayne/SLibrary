package com.qlthuvien.model;

import java.time.LocalDate;

public class WaitingBorrow {
    private String membershipId;
    private int documentId;
    private String documentType; // Loại tài liệu: BOOK, MAGAZINE, THESIS, ...
    private LocalDate borrowDate;
    private String status;

    public WaitingBorrow(String membershipId, int documentId, String documentType, String status, LocalDate borrowDate) {
        this.membershipId = membershipId;
        this.documentId = documentId;
        this.documentType = documentType;
        this.status = status;
        this.borrowDate = borrowDate;
    }

    public WaitingBorrow(String membershipId, int documentId, String documentType, LocalDate borrowDate, String status) {
        this.membershipId = membershipId;
        this.documentId = documentId;
        this.documentType = documentType;
        this.borrowDate = borrowDate;
        this.status = status;
    }

    public WaitingBorrow(String membershipId, int documentId, String documentType, String status) {
        this.membershipId = membershipId;
        this.documentId = documentId;
        this.documentType = documentType;
        this.status = status;
    }

    // Getters and Setters
    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
