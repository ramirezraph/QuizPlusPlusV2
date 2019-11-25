package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 2/17/2018.
 */

public class UserAtRoom {
    private String name;
    private String key;

    public UserAtRoom() {
    }

    public UserAtRoom(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
