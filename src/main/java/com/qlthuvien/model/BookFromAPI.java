package com.qlthuvien.model;

public class BookFromAPI extends Document {
    private String isbn;
    private String publisher;
    private String publishedDate;
    private String description;

    /**
     * Constructor BookFromAPI with 0 parameters.
     */
    public BookFromAPI() {
        super();
        this.isbn = "";
        this.publisher = "";
        this.publishedDate = "";
        this.description = "";
    }

    /**
     * Constructor BookFromAPI with 4 parameters.
     * @param isbn ISBN
     * @param publisher nha xuat ban
     * @param publishedDate ngay xuat ban
     * @param description mo ta
     */
    public BookFromAPI(String isbn, String publisher, String publishedDate, String description) {
        super();
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
    }

    /**
     * Constructor BookFromAPI with 8 parameters.
     * @param id id
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     * @param isbn ISBN
     * @param publisher nha xuat ban
     * @param publishedDate ngay xuat ban
     * @param description mo ta
     */
    public BookFromAPI(int id, String title, String author, String status, String isbn, String publisher, String publishedDate, String description) {
        super(id, title, author, status);
        this.isbn = isbn;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
    }

    // Getters and Setters
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

    /**
     * Print information of BookFromAPI.
     */
    @Override
    public void printInfo() {
        System.out.println("Book [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", ISBN=" + isbn + ", Publisher=" + publisher + ", Published Date=" + publishedDate +
                ", Description=" + description + ", Status=" + getStatus() + "]");
    }
}
