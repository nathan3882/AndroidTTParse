package me.nathan3882.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
     * fragments for each of the sections.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The ViewPager that will host the section contents.
     */
    private ViewPager pager;
    private String email;
    private DayOfWeek[] daysToShow = new DayOfWeek[2];
    private User user;
    private Bundle bundle;

    public DayOfWeek[] getDaysToShow() {
        return daysToShow;
    }


    /**
     * BundleName.EMAIL
     * BundleName.HOME_CRS
     * BundleName.LESSONS
     * BundleName.DAYS_TO_SHOW
     * BundleName.USER_LESSONS_POPULATED
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display);

        setInitialBundle(getIntent().getExtras(), savedInstanceState);


        if (getInitialBundle().getBoolean(BundleName.USER_LESSONS_POPULATED.asString())) {
            initUser(getInitialBundle(), true);
        }

        int[] stringDays = bundle.getIntArray(BundleName.DAYS_TO_SHOW.asString()); //From lessonSelectActivity

        for (int i = 0; i < stringDays.length; i++) {
            DayOfWeek dayOfWeekVersion = DayOfWeek.of(stringDays[i]);
            daysToShow[i] = dayOfWeekVersion;
        }

        for (DayOfWeek dayToShow : getDaysToShow()) {
            allDaysLessonInfo.put(dayToShow, defineLessonInformation(dayToShow));
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putAll(getInitialBundle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Bundle getInitialBundle() {
        return this.bundle;
    }

    @Override
    public void setInitialBundle(Bundle bundle, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.bundle = bundle;
        } else {
            this.bundle = savedInstanceState;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return null;
    }

    private Map<DayOfWeek, List<LessonInfo>> getAllDaysLessonInfo() {
        return allDaysLessonInfo;
    }

    private List<LessonInfo> defineLessonInformation(DayOfWeek day) {
        List<LessonInfo> info = new LinkedList<>();

        String dayName = day.name();

        String depletedOcrText = "Wednesday Business studies 14:10 - 15:15 Business studies 15:15 - 16:20";
        List<String> words = new LinkedList<>(Arrays.asList(depletedOcrText.split(" ")));

        info.add(new LessonInfo(getUsersLocalLessons(), words, day));
        return info;
    }

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        int dayInt = ++position; //0 is monday, 1 is tuesday etc
        DayEquivalent dEquiv = new DayEquivalent(getWeakReference(), dayInt);
        DayOfWeek dayOfWeek = dEquiv.getEquivalent();

        if (DayEquivalent.getPreviouslyStoredEquivalents().containsKey(dayOfWeek) && DayEquivalent.getPreviouslyStoredEquivalents().get(dayOfWeek) != null) { //If has already made it
            return DayEquivalent.getPreviouslyStoredEquivalent(dayOfWeek); //return stored
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

    @Override
    public CharSequence getPageTitle(int position) {
        return Util.upperFirst(DayEquivalent.getDay(++position));
    }
}
}
