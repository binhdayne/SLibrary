package com.qlthuvien.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Magazine magazine = (Magazine) o;
        return issueNumber == magazine.issueNumber &&
               Objects.equals(getId(), magazine.getId()) &&
               Objects.equals(getTitle(), magazine.getTitle()) &&
               Objects.equals(getAuthor(), magazine.getAuthor()) &&
               Objects.equals(getStatus(), magazine.getStatus()) &&
               Objects.equals(publisher, magazine.publisher) &&
               Objects.equals(coverPath, magazine.coverPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAuthor(), getStatus(), issueNumber, publisher, coverPath);
    }
}

