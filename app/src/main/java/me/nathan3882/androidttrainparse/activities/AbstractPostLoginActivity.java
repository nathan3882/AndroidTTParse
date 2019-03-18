package me.nathan3882.androidttrainparse.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import me.nathan3882.androidttrainparse.BundleName;
import me.nathan3882.androidttrainparse.ManipulableUser;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.requesting.IActivityReferencer;
import me.nathan3882.androidttrainparse.requesting.Pair;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public abstract class AbstractPostLoginActivity extends AppCompatActivity
        implements IActivityReferencer<Activity>, ManipulableUser {

    abstract User getUser();

    abstract void setUser(User user);

    abstract Bundle getInitialBundle();

    abstract void setInitialBundle(Bundle bundle, Bundle savedInstanceState);

    public ArrayList<String> getUsersLocalLessons() {
        return getUser().getLocalLessons();
    }

    protected void initUser(Bundle bundle, boolean userLessonsPopulated, Pair.BooleanResponseEventPair... keyWhetherSyncWithDBObjectResponseEvent) {
        if (bundle != null) {
            setUser(User.fromPrimitive(
                    bundle.getString(BundleName.EMAIL.asString()),
                    bundle.getString(BundleName.HOME_CRS.asString())));
            if (userLessonsPopulated) {
                getUser().synchronise(bundle.getStringArrayList(BundleName.LESSONS.asString()));
            } else if (keyWhetherSyncWithDBObjectResponseEvent.length > 0) {
                Pair.BooleanResponseEventPair pair = keyWhetherSyncWithDBObjectResponseEvent[0];
                if (pair.getKey()) {
                    getUser().synchroniseWithDatabase(pair.getValue()); //this way, can still make sure UI is updated with the callback
                }
            }
        }
    }

    protected String getUsersCrs() {
        return getUser().getHomeCrs();
    }

    protected void showToast(String text, String length, boolean show) {
        Toast toast = Toast.makeText(this, text, length.equals("short") ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
    }

    @Nullable
    protected String getUsersEmail() {
        return getUser().getUserEmail();
    }

    @Override
    public void addLessonProgressed(String lesson, WeakReference<Activity> reference, ProgressBarable barable, ResponseEvent event) {
        getUser().addLessonProgressed(lesson, reference, barable, event);
    }

    @Override
    public void addLesson(String lesson, ResponseEvent event) {
        getUser().addLesson(lesson, event);
    }

    @Override
    public void removeLesson(String lesson, ResponseEvent event) {
        getUser().removeLesson(lesson, event);
    }

    @Override
    public void removeLessonProgressed(String lesson, WeakReference<Activity> reference, ProgressBarable barable, ResponseEvent event) {
        getUser().removeLessonProgressed(lesson, reference, barable, event);
    }

    @Override
    public void synchroniseWithDatabase(ResponseEvent event) {
        getUser().synchroniseWithDatabase(event);
    }
}
