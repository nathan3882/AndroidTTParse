package me.nathan3882.androidttrainparse;

import android.app.Activity;
import me.nathan3882.activities.ProgressBarable;
import me.nathan3882.requesting.Action;
import me.nathan3882.requesting.GetRequest;
import me.nathan3882.requesting.KeyObjectPair;
import me.nathan3882.requesting.ProgressedPostRequest;
import me.nathan3882.responding.ResponseEvent;
import me.nathan3882.responding.UserdataRequestResponseData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User implements ManipulableUser {

    private String userEmail;
    private String password;
    private String salt;
    private String homeCrs;
    private ArrayList<String> lessons = new ArrayList<>();

    private User(String userEmail, String homeCrs) {
        this.userEmail = userEmail;
        this.homeCrs = homeCrs;
    }

    public static User fromPrimitive(String userEmail, String homeCrs) {
        User user = new User(userEmail, homeCrs).syncWithDatabase();
        return user;
    }

    public static User fromData(UserdataRequestResponseData data) {
        return new User(data.getUserEmail(), data.getHomeCrs());
    }

    public User syncWithDatabase() {

        return this;
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

    @Override
    public void addLesson(String lesson, ResponseEvent event) {
        Client client = getNewAddLessonClient();

        List<KeyObjectPair> body = getBodyFromLessonName(lesson);

        new GetRequest(client, "/" + getUserEmail(), event).execute();
    }

    @Override
    public void removeLesson(String lesson, ResponseEvent event) {

    }

    @Override
    public void removeLessonProgressed(String lesson, WeakReference<Activity> reference, ProgressBarable barable, ResponseEvent event) {

    }

    @Override
    public void addLessonProgressed(String lesson, WeakReference<Activity> reference,
                                    ProgressBarable barable, ResponseEvent event) {

        Client client = getNewAddLessonClient();

        List<KeyObjectPair> body = getBodyFromLessonName(lesson);

        new ProgressedPostRequest(reference, barable, client, "/" + getUserEmail(), body, event).execute();
    }

    private List<KeyObjectPair> getBodyFromLessonName(String lesson) {
        return Collections.singletonList(new KeyObjectPair("lessonName", lesson));
    }

    private Client getNewAddLessonClient() {
        return new Client(Util.DEFAULT_TTRAINPARSE, Action.POST_ADD_USER_LESSON);
    }

    public void updateLocalTo(ArrayList<String> lessonNames) {
        this.lessons = lessonNames;
    }
}
