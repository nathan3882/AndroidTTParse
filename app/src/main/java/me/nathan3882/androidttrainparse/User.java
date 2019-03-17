package me.nathan3882.androidttrainparse;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import me.nathan3882.activities.ProgressBarable;
import me.nathan3882.requesting.*;
import me.nathan3882.responding.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User implements ManipulableUser {

    private String userEmail;
    private String password;
    private String salt;
    private String homeCrs;
    private ArrayList<String> localLessons = new ArrayList<>();

    private User(String userEmail, String homeCrs) {
        this.userEmail = userEmail;
        this.homeCrs = homeCrs;
    }

    public static User fromPrimitive(String userEmail, String homeCrs) {
        User user = new User(userEmail, homeCrs);
        return user;
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

    public ArrayList<String> getLocalLessons() {
        return localLessons;
    }

    public void synchronise(@Nullable ArrayList<String> lessonNames) {
        if (lessonNames == null) return;
        this.localLessons = lessonNames;
    }

    @Override
    public void addLessonProgressed(String lesson, WeakReference<Activity> reference,
                                    ProgressBarable barable, ResponseEvent event) {
        Client client = getNewAddLessonClient();

        List<Pair.KeyObjectPair> body = getBodyFromLessonName(lesson);

        new ProgressedPostRequest(reference, barable.getProgressBarRid(), client, "/" + getUserEmail() + Util.PARAMS,
                body, event).execute();
    }

    @Override
    public void addLesson(String lesson, ResponseEvent event) {
        Client client = getNewAddLessonClient();

        List<Pair.KeyObjectPair> body = getBodyFromLessonName(lesson);

        new PostRequest(client, "/" + getUserEmail() + Util.PARAMS, body, event).execute();
    }

    @Override
    public void removeLesson(String lesson, ResponseEvent event) {
        Client client = getNewRemoveLessonClient();

        List<Pair.KeyObjectPair> body = getBodyFromLessonName(lesson);

        new PostRequest(client, "/" + getUserEmail() + Util.PARAMS, body, event).execute();
    }

    @Override
    public void removeLessonProgressed(String lesson, WeakReference<Activity> reference,
                                       ProgressBarable barable, ResponseEvent event) {
        Client client = getNewRemoveLessonClient();

        List<Pair.KeyObjectPair> body = getBodyFromLessonName(lesson);

        new ProgressedPostRequest(reference, barable.getProgressBarRid(), client, "/" + getUserEmail() + Util.PARAMS,
                body, event).execute();
    }

    @Override
    public void synchroniseWithDatabase(ResponseEvent event) {
        Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, Action.GET_USER_LESSON_NAMES);

        new GetRequest(lessonNamesClient, "/" + getUserEmail() + Util.PARAMS, new ResponseEvent() {
            @Override
            public void onCompletion(@NonNull RequestResponse requestResponse) {
                RequestResponseData data = requestResponse.getData();

                if (data instanceof LessonNameRequestResponseData) {

                    LessonNameRequestResponseData lessonNameData =
                            (LessonNameRequestResponseData) data;

                    ArrayList<String> lessonNames = lessonNameData.getLessonNames();
                    synchronise(lessonNames);
                }
                event.onCompletion(requestResponse);
            }
        }).execute();
    }

    private List<Pair.KeyObjectPair> getBodyFromLessonName(String lesson) {
        return Collections.singletonList(new Pair().new KeyObjectPair("lessonName", lesson));
    }

    private Client getNewRemoveLessonClient() {
        return new Client(Util.DEFAULT_TTRAINPARSE, Action.POST_REMOVE_USER_LESSON);
    }

    private Client getNewAddLessonClient() {
        return new Client(Util.DEFAULT_TTRAINPARSE, Action.POST_ADD_USER_LESSON);
    }
}
