package com.qlthuvien.model;

public class Book extends Document {
    private String genre;
    private String bookcover;
    public Book(int id, String title, String author, String status, String genre, String bookcover) {
        super(id, title, author, status);
        this.genre = genre;
        this.bookcover = bookcover;
    }

    public Book(int id, String title, String author, String status, String genre) {
        super(id, title, author, status);
        this.genre = genre;
    }

    public String getBookcover() {
        return bookcover;
    }

    public void setBookcover(String bookcover) {
        this.bookcover = bookcover;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void printInfo() {
        System.out.println("Book [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Genre=" + genre + ", Status=" + getStatus() + "]");
    }
}

