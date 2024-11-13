package com.qlthuvien.model;

public class Book extends Document {
    private String genre;

    /**
     * Constructor Book with 0 parameters.
     */
    public Book() {
        super();
        this.genre = "";
    }

    /**
     * Constructor Book with 1 parameters.
     * @param genre the loai
     */
    public Book(String genre) {
        super();
        this.genre = genre;
    }

    /**
     * Constructor Book with 5 parameters.
     * @param id id
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     * @param genre the loai
     */
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

    /**
     * print Information of Book.
     */
    @Override
    public void printInfo() {
        System.out.println("Book [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Genre=" + genre + ", Status=" + getStatus() + "]");
    }
}

