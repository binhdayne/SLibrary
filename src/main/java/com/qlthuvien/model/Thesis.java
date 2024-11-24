package com.qlthuvien.model;

import java.util.Objects;

public class Thesis extends Document {
    private String supervisor;
    private String university;
    private String coverPath;

    public Thesis(int id, String title, String author, String status, String supervisor, String university, String coverPath) {
        super(id, title, author, status);
        this.supervisor = supervisor;
        this.university = university;
        this.coverPath = coverPath;
    }

    public Thesis(int id, String title, String author, String status, String supervisor, String university) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thesis)) return false;
        
        Thesis thesis = (Thesis) o;
        return getId() == thesis.getId() &&
               Objects.equals(getTitle(), thesis.getTitle()) &&
               Objects.equals(getAuthor(), thesis.getAuthor()) &&
               Objects.equals(getStatus(), thesis.getStatus()) &&
               Objects.equals(getSupervisor(), thesis.getSupervisor()) &&
               Objects.equals(getUniversity(), thesis.getUniversity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAuthor(), getStatus(), 
                           getSupervisor(), getUniversity());
    }
}

