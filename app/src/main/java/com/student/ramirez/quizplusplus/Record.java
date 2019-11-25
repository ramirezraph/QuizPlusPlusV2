package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 2/22/2018.
 */

public class Record {
    private String room_id;
    private String room_title;
    private String room_owner;
    private String quiz_title;
    private String quiz_id;

    public Record() {
    }

    public Record(String room_id, String room_title, String room_owner, String quiz_title, String quiz_id) {
        this.room_id = room_id;
        this.room_title = room_title;
        this.room_owner = room_owner;
        this.quiz_title = quiz_title;
        this.quiz_id = quiz_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_owner() {
        return room_owner;
    }

    public void setRoom_owner(String room_owner) {
        this.room_owner = room_owner;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
}
