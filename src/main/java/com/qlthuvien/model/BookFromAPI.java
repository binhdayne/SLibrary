package com.qlthuvien.model;

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
}
