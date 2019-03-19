package me.nathan3882.androidttrainparse;

import java.util.Calendar;
import java.util.Date;

/**
 * @author natha
 */
public class TrainDate {

    private Date date;
    private Calendar cal;

    public TrainDate(Date date) {
        this.date = date;
        this.cal = Calendar.getInstance();
        this.cal.setTime(date);
    }

    public Date getDate() {
        return date;
    }

    public Calendar getCal() {
        return cal;
    }

    public int getHourOfDay() {
        return getCal().get(Calendar.HOUR_OF_DAY);
    }

    public int getHour() {
        return getCal().get(Calendar.HOUR);
    }

    public int getMinute() {
        return getDate().getMinutes();
    }

    public String withColon() {
        return getHourOfDay() + ":" + Util.getPrettyMinute(getMinute());
    }

    public String withoutColon() {
        return String.valueOf(getHourOfDay()) + Util.getPrettyMinute(getMinute());
    }
}