package com.student.ramirez.quizplusplus;

import java.util.List;

/**
 * Created by Ramirez on 1/27/2018.
 */

public class Question {
    private int quiz_id;
    private String question;
    private String answer;
    private int type;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    public static final int TRUEFALSE_TYPE = 1;
    public static final int MULTIPLECHOICE_TYPE = 2;
    public static final int IDENTIFICATION_TYPE = 3;
    public Question() {
    }
    public Question(int quiz_id, String question, String answer, int type, String choiceA, String choiceB, String choiceC, String choiceD) {
        this.quiz_id = quiz_id;
        this.question = question;
        this.answer = answer;
        this.type = type;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
    }
    public int getQuiz_id() {
        return quiz_id;
    }
    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getChoiceA() {
        return choiceA;
    }
    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }
    public String getChoiceB() {
        return choiceB;
    }
    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }
    public String getChoiceC() {
        return choiceC;
    }
    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }
    public String getChoiceD() {
        return choiceD;
    }
    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
    }
}
