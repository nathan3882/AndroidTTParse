package me.nathan3882.androidttrainparse;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import me.nathan3882.activities.TimeDisplayActivity;
import me.nathan3882.requesting.IActivityReferencer;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class DayFragmentFactory implements IActivityReferencer<Activity> {

    private final DayOfWeek dayOfWeek;

    private final WeakReference<Activity> activity;
    private String title;

    public DayFragmentFactory(WeakReference<Activity> activity, DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.activity = activity;
    }


    @Nullable
    public DayNoLessonsFragment createNoLessonsFragment() {
        //Called when either:
        // - LessonInfo parsing failed / no LessonInfo objects for specified day
        // - the user doesn't have OCR string for specified day

        DayNoLessonsFragment dayNoLessonsFragment = new DayNoLessonsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString(), getDayOfWeek().getValue());
        bundle.putString(BundleName.HEADER_NO_HTML.asString(), getPageTitle(false));
        dayNoLessonsFragment.setArguments(bundle);

        return dayNoLessonsFragment;
    }

    @Nullable
    public DayLessonsFragment createHasLessonsFragment(List<LessonInfo> lessonInfo) {

        DayLessonsFragment dayFragment = new DayLessonsFragment();

        dayFragment.synchroniseLessonInfo(lessonInfo);

        Bundle bundle = new Bundle();
        bundle.putInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString(), getDayOfWeek().getValue());
        bundle.putString(BundleName.HEADER_NO_HTML.asString(), getPageTitle(true));

        if (getReferenceValue() != null) {

            ArrayList<String> lessons = ((TimeDisplayActivity) getReferenceValue()).getUsersLocalLessons();

            bundle.putStringArrayList(BundleName.LESSONS.asString(), lessons);

            dayFragment.setArguments(bundle);
            return dayFragment;
        } else {
            return null;
        }
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getPageTitle(boolean hasLessons) {
        String string;
        if (hasLessons) {
            string = "Lessons for {day}:";
        } else {
            string = "You have no lessons configured for {day}:";
        }

        return string.replace("{day}", Util.upperFirst(getDayOfWeek()));
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.activity;
    }


}
