package me.nathan3882.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import me.nathan3882.androidttrainparse.*;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.util.*;

public class TimeDisplayActivity extends AbstractPostLoginActivity implements DayClass.DayFragment.OnFragmentInteractionListener {

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
    private User user;

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

        String dayName = day.name();


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

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    User getUser() {
        return null;
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

    @Override
    public WeakReference<Activity> getWeakReference() {
        return null;
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
            DayEquivalent dEquiv = new DayEquivalent(getWeakReference(), dayInt);
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
