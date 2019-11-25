package com.student.ramirez.quizplusplus;

/**
 * Created by Ramirez on 1/23/2018.
 */

public class User {
    private String FullName;
    private String Birthdate;
    private String Email;
    private String Type;

    public User(){

    }

    public User(String fullName, String birthdate, String email, String type) {
        FullName = fullName;
        Birthdate = birthdate;
        Email = email;
        Type = type;
    }

    public String getFullName() {
        return FullName;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public String getEmail() {
        return Email;
    }

    public String getType() {
        return Type;
    }
}
