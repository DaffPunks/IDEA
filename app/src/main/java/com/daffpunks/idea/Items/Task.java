package com.daffpunks.idea.Items;

/**
 * Created by User on 18.04.2016.
 */
public class Task {

    long    id;
    String  title;
    String  description;
    String  date;
    boolean completed;

    public Task(String title, String description, String date, boolean completed) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = completed;
    }

    public Task(long id, String title, String description, String date, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
