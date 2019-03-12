package me.nathan3882.activities;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.requestsResponses.IActivityReferencer;

import java.util.ArrayList;

public abstract class AbstractPostLoginActivity extends AppCompatActivity implements IActivityReferencer<Activity> {

    abstract User getUser();

    abstract void setUser(User user);

    public String getUsersCrs() {
        return getUser().getHomeCrs();
    }

    @Nullable
    public String getUsersEmail() {
        return getUser().getUserEmail();
    }

    public void addLesson(String lesson) {
        getUser().addLesson(lesson);
    }

    public void removeLesson(String lesson) {
        getUser().removeLesson(lesson);
    }

    public void synchroniseWithDatabase() {
        getUser().uploadLessons();
    }

    @Nullable
    public ArrayList<String> getUsersLessons(boolean freshRequest) {
        return getUser().getLessons();
    }
}
