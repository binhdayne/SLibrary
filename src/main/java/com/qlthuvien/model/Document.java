package com.qlthuvien.model;

public abstract class Document {
    private int id;
    private String title;
    private String author;
    private String status;

    /**
     * Constructor Document with 0 parameters.
     */
    public Document() {
        this.id = 0;
        this.title = "";
        this.author = "";
        this.status = "";
    }

    /**
     * Constructor Document with 3 parameters.
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     */
    public Document(String title, String author, String status) {
        super();
        this.title = title;
        this.author = author;
        this.status = status;
    }

    /**
     * Constructor Document with 4 parameters.
     * @param id id
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     */
    public Document(int id, String title, String author, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract void printInfo();
}


