package me.nathan3882.androidttrainparse.fragments;

import android.os.Bundle;
import me.nathan3882.androidttrainparse.BundleName;
import me.nathan3882.androidttrainparse.LessonInfo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @instantiated by DayFragmentFactory#createHasLessonsFragment
 */
public class DayLessonsFragment extends DayFragment {

    private List<LessonInfo> lessonInfo;
    private ArrayList<String> lessons;

    public void synchroniseLessonInfo(List<LessonInfo> lessonInfo) {
        this.lessonInfo = lessonInfo;
    }

    public List<LessonInfo> getLessonInfo() {
        return lessonInfo;
    }

    public ArrayList<String> getLessons() {
        return lessons;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            this.dayOfWeek = DayOfWeek.of(args.getInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString()));
            this.header = args.getString(BundleName.HEADER_NO_HTML.asString());
            this.lessons = args.getStringArrayList(BundleName.LESSONS.asString());

        }
    }

    @Override
    public StringBuilder getStringToDisplay() {
        StringBuilder mainString = new StringBuilder();
        mainString.append("<html><center>");
        List<LessonInfo> infoForOneDay = getLessonInfo();
        for (int i = 0; i < infoForOneDay.size(); i++) {
            LessonInfo lessonDifTimes = infoForOneDay.get(i);
            if (i != 0) mainString.append("<br>");
            LinkedList<String> les = lessonDifTimes.getLessons();
            for (String lessonName : les) {
                List<LocalTime> startTimes = lessonDifTimes.getStartTimes(lessonName);
                List<LocalTime> finishTimes = lessonDifTimes.getFinishTimes(lessonName);
                for (int k = 0; k < startTimes.size(); k++) {
                    LocalTime startTime = startTimes.get(k);
                    LocalTime finishTime = finishTimes.get(k);

                    String startString = getPrettyMinute(startTime.getMinute());
                    String endString = getPrettyMinute(finishTime.getMinute());

                    mainString.append(lessonName).append(" lesson number ").append(k + 1).append(":<br>Starts at ").append(startTime.getHour()).append(":").append(startString).append(" and Ends at:<br>").append(finishTime.getHour()).append(" ").append(endString).append("<br>");
                    if (k != 0) mainString.append("<br>");

                }
            }
        }
        mainString.append("</center></html>");
        return mainString;
    }

    @Override
    public String getHeader() {
        return this.header;
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    private String getPrettyMinute(int minute) {
        String prettyMinute = String.valueOf(minute);
        if (minute < 10) prettyMinute = "0" + prettyMinute;
        return prettyMinute;
    }
}