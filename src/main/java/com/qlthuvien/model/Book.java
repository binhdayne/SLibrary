package com.qlthuvien.model;

public class Book extends Document {
    private String genre;

    public Book(int id, String title, String author, String status, String genre) {
        super(id, title, author, status);
        this.genre = genre;
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

