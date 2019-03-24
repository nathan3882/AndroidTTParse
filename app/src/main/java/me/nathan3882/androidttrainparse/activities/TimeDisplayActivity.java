package me.nathan3882.androidttrainparse.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;
import me.nathan3882.androidttrainparse.*;
import me.nathan3882.androidttrainparse.fragments.DayFragment;
import me.nathan3882.androidttrainparse.fragments.DayFragmentFactory;
import me.nathan3882.androidttrainparse.fragments.DayLessonsFragment;
import me.nathan3882.androidttrainparse.requesting.Action;
import me.nathan3882.androidttrainparse.requesting.GetRequest;
import me.nathan3882.androidttrainparse.requesting.Pair;
import me.nathan3882.androidttrainparse.responding.OcrRequestResponseData;
import me.nathan3882.androidttrainparse.responding.RequestResponse;
import me.nathan3882.androidttrainparse.responding.RequestResponseData;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.util.*;

public class TimeDisplayActivity extends AbstractPostLoginActivity
        implements DayFragment.OnFragmentInteractionListener {

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

    private DayOfWeek[] daysToShow = new DayOfWeek[5];

    private User user;

    private Bundle bundle;
    private WeakReference<Activity> weakReference;
    private ProgressBar progressBar;

    public DayOfWeek[] getDaysToShow() {
        return daysToShow;
    }

    /**
     * BundleName.EMAIL
     * BundleName.HOME_CRS
     * BundleName.LESSONS
     * BundleName.DAYS_TO_SHOW
     * BundleName.USER_LESSONS_POPULATED
     * BundleName.MONDAY tues wed etc
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_display);

        this.weakReference = new WeakReference<>(this);

        setInitialBundle(getIntent().getExtras(), savedInstanceState);

        int[] stringDays = getInitialBundle().getIntArray(BundleName.DAYS_TO_SHOW.asString()); //From lessonSelectActivity

        for (int i = 0; i < stringDays.length; i++) {
            DayOfWeek dayOfWeekVersion = DayOfWeek.of(stringDays[i]);
            daysToShow[i] = dayOfWeekVersion;
        }
        if (getInitialBundle().getBoolean(BundleName.USER_LESSONS_POPULATED.asString())) {
            System.out.println(getInitialBundle().getStringArrayList(BundleName.LESSONS.asString()));
            initUser(getInitialBundle(), true);
            doOcrForAllUsersDays();
        } else {
            Pair pair = new Pair();
            Pair.BooleanResponseEventPair eventPair = pair.new BooleanResponseEventPair(Boolean.TRUE, new ResponseEvent() {
                @Override
                public void doFinally() {
                    doOcrForAllUsersDays();
                }
            });

            initUser(getInitialBundle(), false, eventPair);
            //no lessons, must init the user for this instance, and then initiatePagers after the
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putAll(getInitialBundle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }

    @Override
    public User getUser() {
        return this.user;
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

    private void doOcrForAllUsersDays() {
        DayOfWeek[] daysToShow = getDaysToShow();
        int daysToShowLength = daysToShow.length;
        for (int i = 0; i < daysToShowLength; i++) {
            DayOfWeek dayToShow = daysToShow[i];

            boolean lastExecution = i == daysToShowLength - 1;

            doOcr(dayToShow, new ReturnEvent() {
                @Override
                public void registerReturn() {
                    if (lastExecution) {
                        initiatePagers();
                    }
                }
            });
        }
    }

    private boolean doOcr(DayOfWeek dayToShow, ReturnEvent event) {
        boolean hasLessonInfo = false;
        boolean hasOcrStored = false;
        BundleName daysBundleName = BundleName.getByValue(dayToShow.name());
        if (daysBundleName != null) {
            String key = daysBundleName.name();
            hasOcrStored = getInitialBundle().containsKey(key);
            if (hasOcrStored) {
                //has ocr stored locally
                String storedOcr = getInitialBundle().getString(key);
                if (storedOcr != null && !allDaysLessonInfo.containsKey(dayToShow)) {
                    //Has ocr in the bundle from the fetchAndStoreOcr function, but not in the all days lesson info
                    List<LessonInfo> lessonInfos = defineLessonInfo(dayToShow, storedOcr);
                    if (hasSqlValue(storedOcr, lessonInfos)) {
                        allDaysLessonInfo.put(dayToShow, lessonInfos);
                        getInitialBundle().putString(daysBundleName.asString(), storedOcr);
                        hasLessonInfo = true;
                    }
                }
            }
        }
        if (!hasOcrStored) {
            fetchAndStoreOcr(dayToShow, new ResponseEvent() {
                @Override
                public void onCompletion(@NonNull RequestResponse requestResponse) {
                    RequestResponseData data = requestResponse.getData();

                    if (data instanceof OcrRequestResponseData) {
                        OcrRequestResponseData ocrRequestResponseData = (OcrRequestResponseData) data;

                        String fetchedDepletedOcrString = ocrRequestResponseData.getDepletedOcrString();

                        List<LessonInfo> lessonInfos = defineLessonInfo(dayToShow, fetchedDepletedOcrString);
                        if (hasSqlValue(fetchedDepletedOcrString, lessonInfos)) {
                            if (daysBundleName != null) {
                                allDaysLessonInfo.put(dayToShow, lessonInfos);
                                getInitialBundle().putString(daysBundleName.asString(), fetchedDepletedOcrString);
                            }
                        }
                    }
                    event.registerReturn();
                }
            });
        } else {
            event.registerReturn();
        }

        return hasLessonInfo;
    }

    private boolean hasSqlValue(String storedOcr, List<LessonInfo> lessonInfos) {
        return lessonInfos.size() != 0 && !storedOcr.equalsIgnoreCase("null");
    }

    private void initiatePagers() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(mSectionsPagerAdapter);
    }

    private List<LessonInfo> defineLessonInfo(DayOfWeek day, String depletedOcrText) {
        List<LessonInfo> info = new LinkedList<>();

        List<String> words = new LinkedList<>(Arrays.asList(depletedOcrText.split(" ")));

        info.add(new LessonInfo(getUsersLocalLessons(), words, day));
        return info;
    }

    private void fetchAndStoreOcr(DayOfWeek dayToShow, ResponseEvent event) {
        Client client = new Client(Util.DEFAULT_TTRAINPARSE, Action.GET_OCR_STRING);
        new GetRequest(client,
                ("/" + getUsersEmail() + "/" + dayToShow.name() + Util.PARAMS), event).execute();
    }

    private Map<DayOfWeek, List<LessonInfo>> getAllDaysLessonInfo() {
        return allDaysLessonInfo;
    }

    /**
     * FragmentStatePagerAdapter was used as opposed to FragmentPagerAdapter to manually cache & load Fragments
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private Map<DayOfWeek, DayFragment> previouslyStoredFragments = new HashMap<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int dayInt = ++position; //0 is monday, 1 is tuesday etc

            ArrayList<String> usersCurrentLocalLessons = getUsersLocalLessons();

            DayEquivalent dayEquivalent = new DayEquivalent(getWeakReference(), dayInt);

            DayOfWeek dayOfWeek = dayEquivalent.getEquivalent();

            Map<DayOfWeek, DayFragment> previousFragments = getPreviouslyStoredFragments();

            if (previousFragments.containsKey(dayOfWeek)) {
                DayFragment gotten = previousFragments.get(dayOfWeek);

                if (gotten instanceof DayLessonsFragment) {
                    System.out.println(dayOfWeek.name() + " instanceof DayLessonsFragment");
                    // if has already made instance of it & is an instance that has lessons
                    // - get the lessons the user had configured at the time of instantiation
                    // - then see if contains different lessons, if so, redo with new lesson names

                    DayLessonsFragment fragment = (DayLessonsFragment)
                            getStoredFragmentByDay(dayOfWeek);

                    ArrayList<String> lessonsBeingTaught = fragment.getLessons();

                    // 'reinvented' containsAll algorithm below to check if the user has changed lessons in order to
                    // get times for different set of lessons on that day
                    boolean same = true;
                    for (String lesson : usersCurrentLocalLessons) {
                        if (!lessonsBeingTaught.contains(lesson)) {
                            // the current users lessons are different to when fragment instantiated
                            // must redo with updated lessons.
                            same = false;
                            break;
                        }
                        System.out.println(dayOfWeek.name() + " lesson " + lesson);
                    }
                    if (same) {
                        System.out.println(dayOfWeek.name() + "same lessons");
                        return fragment; //return stored
                    } else {
                        removeFromStoredFragments(dayOfWeek); //new set of lessons, must remove and redo
                    }
                }
            }

            // If got to this stage, "same" hasn't been
            // true and the DayFragment is instanceof DayNoLessonsFragment so must need rechecking

            // If got to this stage, either one of the following is true:
            // - the user hasn't got data for that specific day
            // - the current users lessons are different to when fragment instantiated / "same" was false
            // - DayFragment is instanceof DayNoLessonsFragment
            //
            // Which ever one, fragment must need attempting again + to reinstantiate once more

            boolean hasLessonInfoForDay = getAllDaysLessonInfo().containsKey(dayOfWeek); //

            DayFragmentFactory dayFragmentFactory = dayEquivalent.getDayFragmentFactory();

            DayFragment lessonsFragment;

            if (hasLessonInfoForDay) {
                List<LessonInfo> infoForDay = getAllDaysLessonInfo().get(dayOfWeek);
                lessonsFragment = dayFragmentFactory.createHasLessonsFragment(user, infoForDay);
            } else { //no lesson info for that day
                // either error occurred in the doOcr function or user doesn't have it in database
                lessonsFragment = dayFragmentFactory.createNoLessonsFragment();
            }
            System.out.println("redo " + dayOfWeek.name());
            addToStoredFragments(dayOfWeek, lessonsFragment);
            return lessonsFragment;
        }


        @Override
        public int getCount() {
            return getDaysToShow().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Util.upperFirst(DayEquivalent.getDay(++position));
        }

        private Map<DayOfWeek, DayFragment> getPreviouslyStoredFragments() {
            return previouslyStoredFragments;
        }

        private DayFragment getStoredFragmentByDay(DayOfWeek equiv) {
            return getPreviouslyStoredFragments().get(equiv);
        }

        private void removeFromStoredFragments(DayOfWeek key) {
            previouslyStoredFragments.remove(key);
        }

        private void addToStoredFragments(DayOfWeek key, DayFragment value) {
            previouslyStoredFragments.put(key, value);
        }
    }
}
