package me.nathan3882.androidttrainparse;

import me.nathan3882.responseData.UserdataRequestResponseData;

import java.util.ArrayList;

public class User {

    private String userEmail;
    private String password;
    private String salt;
    private String homeCrs;
    private ArrayList<String> lessons;

    private User(String userEmail, String homeCrs) {
        this.userEmail = userEmail;
        this.homeCrs = homeCrs;
    }

    public static User fromPrimitive(String userEmail, String homeCrs) {
        return new User(userEmail, homeCrs);
    }

    public static User fromData(UserdataRequestResponseData data) {
        return new User(data.getUserEmail(), data.getHomeCrs());
    }


    public String getUserEmail() {
        return userEmail;
    }

    public String getHomeCrs() {
        return homeCrs;
    }

    public ArrayList<String> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<String> lessons) {
        this.lessons = lessons;
    }
}
