package com.qlthuvien.model;

public class Thesis extends Document {
    private String supervisor;
    private String university;

    /**
     * Constructor Thesis with 0 parameters.
     */
    public Thesis() {
        super();
        this.supervisor = "";
        this.university = "";
    }

    /**
     * Constructor Thesis with 2 parameters.
     * @param supervisor giám khảo
     * @param university truong dai hoc
     */
    public Thesis(String supervisor, String university) {
        super();
        this.supervisor = supervisor;
        this.university = university;
    }

    /**
     * Constructor Thesis with 6 parameters.
     * @param id id
     * @param title tieu de
     * @param author tac gia
     * @param status trang thai
     * @param supervisor giám khảo
     * @param university truong dai hoc
     */
    public Thesis(int id, String title, String author, String status, String supervisor, String university) {
        super(id, title, author, status);
        this.supervisor = supervisor;
        this.university = university;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    /**
     * print Information of Thesis.
     */
    @Override
    public void printInfo() {
        System.out.println("Thesis [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Supervisor=" + supervisor + ", University=" + university + ", Status=" + getStatus() + "]");
    }
}

