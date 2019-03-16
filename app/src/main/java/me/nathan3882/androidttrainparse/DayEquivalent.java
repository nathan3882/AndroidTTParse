package me.nathan3882.androidttrainparse;

import android.app.Activity;
import me.nathan3882.requesting.IActivityReferencer;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class DayEquivalent implements IActivityReferencer<Activity> {

    private static Map<DayOfWeek, DayClass.DayFragment> previouslyStoredEquivalents = new HashMap<>();
    private final int dayInt;
    private final DayOfWeek dayOfWeek;
    private final WeakReference<Activity> timeDisplayActivity;
    private DayClass dayClass;

    public DayEquivalent(WeakReference<Activity> timeDisplayActivity, int dayInt) {
        this.dayInt = dayInt;
        this.dayOfWeek = DayOfWeek.of(dayInt);
        this.timeDisplayActivity = timeDisplayActivity;
        this.dayClass = new DayClass(timeDisplayActivity, getEquivalent());
    }

    public static Map<DayOfWeek, DayClass.DayFragment> getPreviouslyStoredEquivalents() {
        return previouslyStoredEquivalents;
    }


    public static void addToPrevious(DayOfWeek key, DayClass.DayFragment value) {
        DayEquivalent.previouslyStoredEquivalents.put(key, value);
    }


    public static DayClass.DayFragment getPreviouslyStoredEquivalent(DayOfWeek equiv) {
        return getPreviouslyStoredEquivalents().get(equiv);
    }

    public static DayOfWeek getDay(int dayInt) {
        return DayOfWeek.of(dayInt);
    }

    public DayClass getDayClass() {
        return this.dayClass;
    }

    public DayOfWeek getEquivalent() {
        return dayOfWeek;
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.timeDisplayActivity;
    }
}
