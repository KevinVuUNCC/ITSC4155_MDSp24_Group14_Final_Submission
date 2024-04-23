package edu.uncc.myapplication.models;

import java.io.Serializable;

public class Recipe implements Serializable {
    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String name, time, description;

    public Recipe() {
    }

    public Recipe(String name, String time, String description) {
        this.name = name;
        this.time = time;
        this.description = description;
    }
}
