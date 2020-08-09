package com.example.easywateringsystem.models;

public class Schedule {

    private String start;
    private String finish;


    public Schedule(String start, String finish) {
        this.start = start;
        this.finish = finish;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

}
