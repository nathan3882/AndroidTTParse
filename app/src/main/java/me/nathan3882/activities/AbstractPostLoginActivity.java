package me.nathan3882.activities;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.requesting.IActivityReferencer;
import me.nathan3882.responding.ResponseEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class AbstractPostLoginActivity extends AppCompatActivity
        implements IActivityReferencer<Activity> {

    abstract User getUser();

    abstract void setUser(User user);

    public String getUsersCrs() {
        return getUser().getHomeCrs();
    }

    public void showToast(String text, String length, boolean show) {
        Toast toast = Toast.makeText(this, text, length.equals("short") ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
    }

    @Nullable
    public String getUsersEmail() {
        return getUser().getUserEmail();
    }

    public void addLesson(String lesson, ResponseEvent event) {
        getUser().addLesson(lesson, event);
    }

    public void addLessonProgressed(String lesson, WeakReference<Activity> reference, ProgressBarable barable, ResponseEvent event) {
        getUser().addLessonProgressed(lesson, reference, barable, event);
    }

    public void removeLesson(String lesson, ResponseEvent event) {
        getUser().removeLesson(lesson, event);
    }

    public void removeLessonProgressed(String lesson, WeakReference<Activity> reference, ProgressBarable barable, ResponseEvent event) {
        getUser().removeLessonProgressed(lesson, reference, barable, event);
    }

    public void synchroniseWithDatabase() {
        getUser().syncWithDatabase();
    }

    public ArrayList<String> getUsersLessons(boolean freshRequest) {
        return getUser().getLessons();
    }
}
