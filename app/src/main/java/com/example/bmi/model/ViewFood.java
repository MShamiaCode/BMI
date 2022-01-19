package com.example.bmi.model;

public class ViewFood {
    String name;
    String category;
    String calory;
    String image;
    String date;
    String time;

    public ViewFood() {
    }

    public ViewFood(String name, String category, String calory, String image, String date, String time) {
        this.name = name;
        this.category = category;
        this.calory = calory;
        this.image = image;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalory() {
        return calory;
    }

    public void setCalory(String calory) {
        this.calory = calory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
