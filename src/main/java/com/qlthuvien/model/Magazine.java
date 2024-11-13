package com.qlthuvien.model;

public class Magazine extends Document {
    private int issueNumber;
    private String publisher;

    /**
     * Constructor Magazine with 0 parameters.
     */
    public Magazine() {
        super();
        this.issueNumber = 0;
        this.publisher = "";
    }

    /**
     * Constructor Magazine with 2 parameters.
     * @param issueNumber so phat hanh
     * @param publisher nha xuat ban
     */
    public Magazine(int issueNumber, String publisher) {
        super();
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }

    /**
     * Constructor Magazine with 6 parameters.
     * @param id id
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     * @param issueNumber so phat hanh
     * @param publisher nha xuat ban
     */
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

    /**
     * print Information of Magazine.
     */
    @Override
    public void printInfo() {
        System.out.println("Magazine [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Issue Number=" + issueNumber + ", Publisher=" + publisher + ", Status=" + getStatus() + "]");
    }
}

