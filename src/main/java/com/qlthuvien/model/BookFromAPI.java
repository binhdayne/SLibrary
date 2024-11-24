package com.qlthuvien.model;

import java.util.Objects;

public class BookFromAPI extends Document {
    private String isbn;
    private String publisher;
    private String publishedDate;
    private String description;
    private String BookCover;

    public BookFromAPI(int id, String title, String author, String status, String isbn, String publisher, String publishedDate, String description, String BookCover) {
        super(id, title, author, status);
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.BookCover = BookCover;
    }

    public BookFromAPI(int id, String title, String author, String status, String isbn, String publisher, String publishedDate, String description) {
        super(id, title, author, status);
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
    }

    public String getBookCover() {
        return BookCover;
    }

    public void setBookCover(String bookCover) {
        BookCover = bookCover;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void printInfo() {
        System.out.println("Book [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", ISBN=" + isbn + ", Publisher=" + publisher + ", Published Date=" + publishedDate +
                ", Description=" + description + ", Status=" + getStatus() + "]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookFromAPI that = (BookFromAPI) o;
        return getId() == that.getId() &&
               Objects.equals(getIsbn(), that.getIsbn()) &&
               Objects.equals(getTitle(), that.getTitle()) &&
               Objects.equals(getAuthor(), that.getAuthor()) &&
               Objects.equals(getPublisher(), that.getPublisher()) &&
               Objects.equals(getPublishedDate(), that.getPublishedDate()) &&
               Objects.equals(getDescription(), that.getDescription()) &&
               Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIsbn(), getTitle(), getAuthor(), getPublisher(), getPublishedDate(), getDescription(), getStatus());
    }
}
