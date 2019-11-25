package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 1/26/2018.
 */

public class Quiz {

    private String quiz_id;
    private String quiz_title;
    private String quiz_owner;
    private String quiz_desc;
    private String quiz_questionsize;
    private String quiz_timer;

    public Quiz() {
    }

    public Quiz(String quiz_id, String quiz_title, String quiz_owner, String quiz_desc, String quiz_questionsize, String quiz_timer) {
        this.quiz_id = quiz_id;
        this.quiz_title = quiz_title;
        this.quiz_owner = quiz_owner;
        this.quiz_desc = quiz_desc;
        this.quiz_questionsize = quiz_questionsize;
        this.quiz_timer = quiz_timer;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_owner() {
        return quiz_owner;
    }

    public void setQuiz_owner(String quiz_owner) {
        this.quiz_owner = quiz_owner;
    }

    public String getQuiz_desc() {
        return quiz_desc;
    }

    public void setQuiz_desc(String quiz_desc) {
        this.quiz_desc = quiz_desc;
    }

    public String getQuiz_questionsize() {
        return quiz_questionsize;
    }

    public void setQuiz_questionsize(String quiz_questionsize) {
        this.quiz_questionsize = quiz_questionsize;
    }

    public String getQuiz_timer() {
        return quiz_timer;
    }

    public void setQuiz_timer(String quiz_timer) {
        this.quiz_timer = quiz_timer;
    }
}
