package me.nathan3882.androidttrainparse;

import android.support.v7.app.AppCompatActivity;
import me.nathan3882.activities.TimeDisplayActivity;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

public class DayEquivalent {

    private final int dayInt;

    private final DayOfWeek dayOfWeek;

    private final TimeDisplayActivity timeDisplayActivity;
    private DayClass dayClass;

    private static Map<DayOfWeek, DayClass.DayFragment> previous = new HashMap<>();

    public DayEquivalent(TimeDisplayActivity timeDisplayActivity, int dayInt) {
        this.dayInt = dayInt;
        this.dayOfWeek = DayOfWeek.of(dayInt);
        this.timeDisplayActivity = timeDisplayActivity;
        setDayClass(getEquivalent());
        System.out.println("new DayEquivalent of = " + dayInt);
    }

    public static Map<DayOfWeek, DayClass.DayFragment> getPrevious() {
        return previous;
    }

    public static void addToPrevious(DayOfWeek key, DayClass.DayFragment value) {
        DayEquivalent.previous.put(key, value);
    }

    public static DayClass.DayFragment getPrevious(DayOfWeek equiv) {
        return getPrevious().get(equiv);
    }


    private void setDayClass(DayOfWeek dayOfWeek) {
        this.dayClass = new DayClass(timeDisplayActivity, dayOfWeek);
    }

    public DayClass getDayClass() {
        return this.dayClass;
    }

    public DayOfWeek getEquivalent() {
        return dayOfWeek;
    }

    public static DayEquivalent of(TimeDisplayActivity timeDisplayActivity, DayOfWeek day) {
        return new DayEquivalent(timeDisplayActivity, day.getValue());
    }
    public static DayEquivalent of(TimeDisplayActivity timeDisplayActivity, String day) {
        return new DayEquivalent(timeDisplayActivity, DayOfWeek.valueOf(day.toUpperCase()).getValue());
    }

    public AppCompatActivity getTimeDisplayActivity() {
        return timeDisplayActivity;
    }
}
