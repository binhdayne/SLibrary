package com.qlthuvien.model;

public class Thesis extends Document {
    private String supervisor;
    private String university;
    private String coverPath;

    public Thesis(int id, String title, String author, String supervisor, String university,String status, String coverPath) {
        super(id, title, author, status);
        this.supervisor = supervisor;
        this.university = university;
        this.coverPath = coverPath;
    }

    public Thesis(int id, String title, String author, String supervisor, String university, String status) {
        super(id, title, author, status);
        this.supervisor = supervisor;
        this.university = university;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
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

