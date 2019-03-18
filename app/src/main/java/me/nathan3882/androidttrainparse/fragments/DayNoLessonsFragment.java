package me.nathan3882.androidttrainparse.fragments;

import android.os.Bundle;
import me.nathan3882.androidttrainparse.BundleName;

import java.time.DayOfWeek;

/**
 * @instantiated by DayFragmentFactory#createNoLessonsFragment
 */
public class DayNoLessonsFragment extends DayFragment {

    private String header;
    private StringBuilder stringToDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            this.header = args.getString(BundleName.HEADER_NO_HTML.asString());
            this.dayOfWeek = DayOfWeek.of(args.getInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString()));
        }
        this.stringToDisplay = new StringBuilder("You have not got any lessons today - enjoy the day off");
    }

    @Override
    public String getHeader() {
        return this.header;
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    @Override
    public StringBuilder getStringToDisplay() {
        return this.stringToDisplay;
    }
}