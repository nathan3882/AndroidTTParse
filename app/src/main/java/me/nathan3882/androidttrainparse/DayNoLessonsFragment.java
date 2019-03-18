package me.nathan3882.androidttrainparse;

import android.os.Bundle;

import java.time.DayOfWeek;

/**
 * @instantiated by DayFragmentFactory#createNoLessonsFragment
 */
public class DayNoLessonsFragment extends DayFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            this.dayOfWeek = DayOfWeek.of(args.getInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString()));
            this.header = args.getString(BundleName.HEADER_NO_HTML.asString());
        }
    }

    @Override
    public StringBuilder getStringToDisplay() {
        return new StringBuilder("You have not got any lessons today - enjoy the day off");
    }
}