package me.nathan3882.androidttrainparse;

import android.app.Activity;
import me.nathan3882.androidttrainparse.fragments.DayFragmentFactory;
import me.nathan3882.androidttrainparse.requesting.IActivityReferencer;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;

public class DayEquivalent implements IActivityReferencer<Activity> {

    private final int dayInt;
    private final DayOfWeek dayOfWeek;
    private final WeakReference<Activity> timeDisplayActivity;
    private DayFragmentFactory dayFragmentFactory;

    public DayEquivalent(WeakReference<Activity> timeDisplayActivity, int dayInt) {
        this.dayInt = dayInt;
        this.dayOfWeek = DayOfWeek.of(dayInt);
        this.timeDisplayActivity = timeDisplayActivity;
        this.dayFragmentFactory = new DayFragmentFactory(timeDisplayActivity, getEquivalent());
    }

    public static DayOfWeek getDay(int dayInt) {
        return DayOfWeek.of(dayInt);
    }

    public DayFragmentFactory getDayFragmentFactory() {
        return this.dayFragmentFactory;
    }

    public DayOfWeek getEquivalent() {
        return dayOfWeek;
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.timeDisplayActivity;
    }
}
