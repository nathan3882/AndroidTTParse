package me.nathan3882.androidttrainparse;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.nathan3882.activities.TimeDisplayActivity;
import me.nathan3882.requesting.IActivityReferencer;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DayFragmentFactory implements IActivityReferencer<Activity> {

    private final DayOfWeek dayOfWeek;

    private final WeakReference<Activity> activity;
    private String title;

    public DayFragmentFactory(WeakReference<Activity> activity, DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.activity = activity;
    }


    @Nullable
    public NoLessonsFragment createNoLessonsFragment() {
        //Called when either:
        // - LessonInfo parsing failed / no LessonInfo objects for specified day
        // - the user doesn't have OCR string for specified day

        NoLessonsFragment noLessonsFragment = new NoLessonsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString(), getDayOfWeek().getValue());
        bundle.putString(BundleName.HEADER_NO_HTML.asString(), getPageTitle(false));
        noLessonsFragment.setArguments(bundle);

        return noLessonsFragment;
    }

    @Nullable
    public LessonsFragment createHasLessonsFragment(List<LessonInfo> lessonInfo) {

        LessonsFragment dayFragment = new LessonsFragment();

        dayFragment.synchroniseLessonInfo(lessonInfo);

        Bundle bundle = new Bundle();
        bundle.putInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString(), getDayOfWeek().getValue());
        bundle.putString(BundleName.HEADER_NO_HTML.asString(), getPageTitle(true));

        if (getReferenceValue() != null) {

            ArrayList<String> lessons = ((TimeDisplayActivity) getReferenceValue()).getUsersLocalLessons();

            bundle.putStringArrayList(BundleName.LESSONS.asString(), lessons);

            dayFragment.setArguments(bundle);
            return dayFragment;
        } else {
            return null;
        }
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getPageTitle(boolean hasLessons) {
        String string;
        if (hasLessons) {
            string = "Lessons for {day}:";
        } else {
            string = "You have no lessons configured for {day}:";
        }

        return string.replace("{day}", Util.upperFirst(getDayOfWeek()));
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.activity;
    }


    /**
     * @instantiated by DayFragmentFactory#createHasLessonsFragment
     */
    public static class LessonsFragment extends DayFragment {

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

        private String getPrettyMinute(int minute) {
            String prettyMinute = String.valueOf(minute);
            if (minute < 10) prettyMinute = "0" + prettyMinute;
            return prettyMinute;
        }
    }

    /**
     * @instantiated by DayFragmentFactory#createNoLessonsFragment
     */
    public static class NoLessonsFragment extends DayFragment {

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

    public static class DayFragment extends Fragment {

        protected DayOfWeek dayOfWeek;

        protected String header;
        private OnFragmentInteractionListener mListener;

        public DayFragment() {

        }

        public StringBuilder getStringToDisplay() {
            return new StringBuilder("");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_day, container, false);

            TextView dayFragHeader = view.findViewById(R.id.dayFragHeader);
            dayFragHeader.setText(Util.html("<html>" + getHeader() + "</html>"));

            TextView actualDisplay = view.findViewById(R.id.lessonDisplay);
            actualDisplay.setText(Util.html(getStringToDisplay().toString()));
            return view;
        }

        public String getHeader() {
            return header;
        }

        public DayOfWeek getDayOfWeek() {
            return this.dayOfWeek;
        }

        // TODO: Rename method, update argument and hook method into UI event
        public void onButtonPressed(Uri uri) {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        public interface OnFragmentInteractionListener {
            void onFragmentInteraction(Uri uri);
        }

    }
}
