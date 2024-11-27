package com.qlthuvien.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return getId() == book.getId() &&
               Objects.equals(getTitle(), book.getTitle()) &&
               Objects.equals(getAuthor(), book.getAuthor()) &&
               Objects.equals(getStatus(), book.getStatus()) &&
               Objects.equals(getGenre(), book.getGenre()) &&
               Objects.equals(getBookcover(), book.getBookcover());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAuthor(), getStatus(), getGenre(), getBookcover());
    }
}

