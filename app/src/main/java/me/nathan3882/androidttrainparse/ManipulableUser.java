package me.nathan3882.androidttrainparse;

import android.app.Activity;
import me.nathan3882.activities.ProgressBarable;
import me.nathan3882.responding.ResponseEvent;

import java.lang.ref.WeakReference;

public interface ManipulableUser {


    void addLessonProgressed(String lesson, WeakReference<Activity> reference,
                             ProgressBarable barable, ResponseEvent event);

    void addLesson(String lesson, ResponseEvent event);

    void removeLesson(String lesson, ResponseEvent event);

    void removeLessonProgressed(String lesson, WeakReference<Activity> reference,
                                ProgressBarable barable, ResponseEvent event);

}
