package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 2/22/2018.
 */

public class StudentRecord {
    private String studentname;
    private String score;
    private String key;

    public StudentRecord() {
    }

    public StudentRecord(String studentname, String score, String key) {
        this.studentname = studentname;
        this.score = score;
        this.key = key;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
