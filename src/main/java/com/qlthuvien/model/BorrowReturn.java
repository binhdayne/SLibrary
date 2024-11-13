package com.qlthuvien.model;

import java.time.LocalDate;

public class BorrowReturn {
    private String membershipId;
    private int documentId;
    private String documentType; // Loại tài liệu: BOOK, MAGAZINE, THESIS, ...
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;

    /**
     * Constructor BorrowReturn with 0 parameters.
     */
    public BorrowReturn() {
        this.membershipId = "";
        this.documentId = 0;
        this.documentType = "";
        this.borrowDate = LocalDate.now();
        this.returnDate = LocalDate.now();
        this.status = "";
    }

    /**
     * Constructor BorrowReturn with 6 parameters.
     * @param membershipId ID thẻ thành viên
     * @param documentId ID tài liệu
     * @param documentType Loại tài liệu
     * @param borrowDate ngày mượn
     * @param returnDate ngày trả
     * @param status trạng thái
     */
    public BorrowReturn(String membershipId, int documentId, String documentType, LocalDate borrowDate, LocalDate returnDate, String status) {
        this.membershipId = membershipId;
        this.documentId = documentId;
        this.documentType = documentType;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
