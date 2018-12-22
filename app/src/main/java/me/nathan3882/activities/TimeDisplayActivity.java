package me.nathan3882.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import me.nathan3882.androidttrainparse.DayClass;
import me.nathan3882.androidttrainparse.DayEquivalent;
import me.nathan3882.androidttrainparse.LessonInfo;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.data.SqlConnection;
import me.nathan3882.data.SqlQuery;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;
import java.util.*;

public class TimeDisplayActivity extends AppCompatActivity {

    private final TimeDisplayActivity timeDisplayActivity = this;

    private Map<DayOfWeek, List<LessonInfo>> lessonInfo = new HashMap<>();
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager pager;
    private String[] lessonNames;
    private String email;
    private DayOfWeek[] daysToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display);
        Bundle extras = getIntent().getExtras();
        setLessonNames(extras.getStringArray("lessonNames"));
        setEmail(extras.getString("email"));

        setDaysToShow(extras.getStringArray("days"));

        //DO LESSON INFORMATION
        for (DayOfWeek dayToShow : getDaysToShow()) {
            lessonInfo.put(dayToShow, defineLessonInformation(dayToShow));
        }

        TextView dayHeaderOne = findViewById(R.id.dayHeaderOne);
        dayHeaderOne.setText(Util.html("My Text"));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        pager = findViewById(R.id.container);
        pager.setAdapter(mSectionsPagerAdapter);
//        TabLayout layout = findViewById(R.id.tabs);
    }

    public Map<DayOfWeek, List<LessonInfo>> getLessonInfo() {
        return lessonInfo;
    }

    private List<LessonInfo> defineLessonInformation(DayOfWeek day) {
        List<LessonInfo> info = new LinkedList<>();
        SqlConnection con = new SqlConnection(true);

        SqlQuery query = new SqlQuery(con);

        String dayName = day.name();

        query.executeQuery("SELECT " + dayName + " FROM {table} WHERE userEmail = '" + getEmail() + "'",
                SqlConnection.SqlTableName.TIMETABLE_LESSONS);

        String depletedOcrText = "";
        if (query.next(false)) {
            depletedOcrText = query.getString(dayName);
        }
        List<String> words = new LinkedList<>(Arrays.asList(depletedOcrText.split(" ")));
        info.add(new LessonInfo(getLessonNames(), words, day));
        return info;
    }

    private String[] getLessonNames() {
        return this.lessonNames;
    }

    public void setLessonNames(String[] lessonNames) {
        this.lessonNames = lessonNames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DayOfWeek[] getDaysToShow() {
        return daysToShow;
    }

    public void setDaysToShow(String[] daysToShow) {
        for (int i = 0; i < daysToShow.length; i++) {
            this.daysToShow[i] = DayOfWeek.valueOf(daysToShow[i]);
        }
    }

    public TimeDisplayActivity getTimeDisplayActivity() {
        return timeDisplayActivity;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            int dayInt = ++position; //0 is monday, 1 is tuesday etc
            DayEquivalent dOf = new DayEquivalent(getTimeDisplayActivity(), dayInt);
            DayOfWeek equiv = dOf.getEquivalent();
            if (dOf.getPrevious().containsKey(equiv)) {
                return DayEquivalent.getPrevious(equiv);
            }

            DayClass dayClass = dOf.getDayClass();

            DayClass.DayFragment fragment = dayClass.newInstance(dayClass.getPageTitle()/*, true*/);
            DayEquivalent.addToPrevious(equiv, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
