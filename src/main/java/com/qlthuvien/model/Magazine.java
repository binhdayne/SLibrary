package com.qlthuvien.model;

public class Magazine extends Document {
    private int issueNumber;
    private String publisher;
    private String coverPath;

    public Magazine(int id, String title, String author, String status, int issueNumber, String publisher, String coverPath) {
        super(id, title, author, status);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
        this.coverPath = coverPath;
    }

    public Magazine(int id, String title, String author, String status, int issueNumber, String publisher) {
        super(id, title, author, status);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    @Override
    public void printInfo() {
        System.out.println("Magazine [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Issue Number=" + issueNumber + ", Publisher=" + publisher + ", Status=" + getStatus() + "]");
    }
}

