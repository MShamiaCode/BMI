package com.example.bmi.model;

public class OldStatues {
    String date;
    String time;
    String weight;
    String Length;

    public OldStatues() {
    }

    public OldStatues(String weight, String length, String date, String time) {
        this.date = date;
        this.weight = weight;
        this.Length = length;
        this.time = time;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return Length;
    }

    public void setLength(String length) {
        Length = length;
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
