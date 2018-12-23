package me.nathan3882.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import me.nathan3882.androidttrainparse.DayClass;
import me.nathan3882.androidttrainparse.DayEquivalent;
import me.nathan3882.androidttrainparse.LessonInfo;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.data.SqlConnection;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;
import java.util.*;

public class TimeDisplayActivity extends AppCompatActivity implements DayClass.DayFragment.OnFragmentInteractionListener {

    private final TimeDisplayActivity timeDisplayActivity = this;

    private Map<DayOfWeek, List<LessonInfo>> allDaysLessonInfo = new HashMap<>();
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
    private ArrayList<String> lessonNames;
    private String email;
    private DayOfWeek[] daysToShow = new DayOfWeek[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display);
        Bundle extras = getIntent().getExtras();
        setLessonNames(extras.getStringArrayList("lessons"));
        setEmail(extras.getString("email"));
        String[] stringDays = extras.getStringArray("days"); //From lessonSelectActivity or alternatively, a file

        for (int i = 0; i < stringDays.length; i++) {
            String eye = stringDays[i];
            DayOfWeek dayOfWeekVersion = DayOfWeek.valueOf(eye);
            daysToShow[i] = dayOfWeekVersion;
        }

        for (DayOfWeek dayToShow : getDaysToShow()) {
            allDaysLessonInfo.put(dayToShow, defineLessonInformation(dayToShow)); //CHANGE THIS TO NOT BE HARD CODED
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(mSectionsPagerAdapter);
    }

    public Map<DayOfWeek, List<LessonInfo>> getAllDaysLessonInfo() {
        return allDaysLessonInfo;
    }

    private List<LessonInfo> defineLessonInformation(DayOfWeek day) {
        List<LessonInfo> info = new LinkedList<>();
        SqlConnection con = new SqlConnection(true);

        String dayName = day.name();

//        SqlQuery query = new SqlQuery(con);
//        query.executeQuery("SELECT " + dayName + " FROM {table} WHERE userEmail = '" + getEmail() + "'",
//                SqlConnection.SqlTableName.TIMETABLE_LESSONS);
//
//        String depletedOcrText = "";
//        if (query.next(false)) {
//            depletedOcrText = query.getString(dayName);
//        }
        String depletedOcrText = "Wednesday Business studies 14:10 - 15:15 Business studies 15:15 - 16:20";
        List<String> words = new LinkedList<>(Arrays.asList(depletedOcrText.split(" ")));
        info.add(new LessonInfo(getLessonNames(), words, day));
        return info;
    }

    public ArrayList<String> getLessonNames() {
        return this.lessonNames;
    }

    public void setLessonNames(ArrayList<String> lessonNames) {
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

    public TimeDisplayActivity getTimeDisplayActivity() {
        return timeDisplayActivity;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Util.upperFirst(DayEquivalent.getDay(++position));
        }

        @Override
        public Fragment getItem(int position) {
            int dayInt = ++position; //0 is monday, 1 is tuesday etc
            DayEquivalent dEquiv = new DayEquivalent(getTimeDisplayActivity(), dayInt);
            DayOfWeek dayOfWeek = dEquiv.getEquivalent();
            if (dEquiv.getPreviouslyStoredEquivalents().containsKey(dayOfWeek)) {
                return DayEquivalent.getPreviouslyStoredEquivalent(dayOfWeek);
            }
            DayClass dayClass = dEquiv.getDayClass();
            List<LessonInfo> infoForDay = getAllDaysLessonInfo().get(dayOfWeek);
            DayClass.DayFragment fragment = dayClass.newInstance(dayClass.getPageTitle(), infoForDay/*, true*/);

            DayEquivalent.addToPrevious(dayOfWeek, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return getDaysToShow().length;
        }
    }
}
