package com.qlthuvien.model;

public class Thesis extends Document {
    private String supervisor;
    private String university;

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

    @Override
    public void printInfo() {
        System.out.println("Thesis [ID=" + getId() + ", Title=" + getTitle() + ", Author=" + getAuthor() +
                ", Supervisor=" + supervisor + ", University=" + university + ", Status=" + getStatus() + "]");
    }
}

