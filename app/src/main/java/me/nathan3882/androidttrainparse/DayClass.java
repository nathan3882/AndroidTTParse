package me.nathan3882.androidttrainparse;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.nathan3882.activities.TimeDisplayActivity;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DayClass {

    private final DayOfWeek dayOfWeek;

    private final TimeDisplayActivity activity;
    private String title;

    public DayClass(TimeDisplayActivity activity, DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.activity = activity;
    }

    public DayFragment newInstance(String headerNoHtml, List<LessonInfo> lessonInfo) {
        DayFragment dayFragment = new DayFragment();
        dayFragment.synchroniseLessonInfo(lessonInfo);

        Bundle bundle = new Bundle();
        bundle.putInt("dayOfWeekToShow", dayOfWeek.getValue());
        bundle.putString("headerNoHtml", headerNoHtml);
        bundle.putStringArrayList("lessons", activity.getLessonNames());
        dayFragment.setArguments(bundle);
        return dayFragment;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    private TimeDisplayActivity getActivity() {
        return activity;
    }

    public String getPageTitle() {
        Resources res = getActivity().getResources();
        return res.getString(R.string.dayTitle).replace("{day}", Util.upperFirst(getDayOfWeek()));
    }

    /**
     * A simple {@link android.support.v4.app.Fragment} subclass.
     * Activities that contain this fragment must implement the
     * {@link OnFragmentInteractionListener} interface
     * to handle interaction events.
     * Use the {@link DayFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public static class DayFragment extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        private OnFragmentInteractionListener mListener;
        private DayOfWeek dayOfWeek;
        private List<LessonInfo> lessonInfo;
        private ArrayList<String> lessonsBeingTaught;
        private String header;

        public DayFragment() {
        }

        public ArrayList<String> getLessonsBeingTaught() {
            return lessonsBeingTaught;
        }

        public String getHeader() {
            return header;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            if (args != null) {
                this.dayOfWeek = DayOfWeek.of(getArguments().getInt("dayOfWeekToShow"));
                this.header = args.getString("headerNoHtml");
                this.lessonsBeingTaught = args.getStringArrayList("lessons");
            }
        }

        public DayOfWeek getDayOfWeekToShow() {
            return this.dayOfWeek;
        }

        public void synchroniseLessonInfo(List<LessonInfo> lessonInfo) {
            this.lessonInfo = lessonInfo;
        }

        public List<LessonInfo> getLessonInfo() {
            return lessonInfo;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_day, container, false);

            TextView dayFragHeader = view.findViewById(R.id.dayFragHeader);
            dayFragHeader.setText(Util.html("<html>" + getHeader() + "</html>"));

            StringBuilder toDisplay = getStringToDisplay(getLessonInfo());

            TextView actualDisplay = view.findViewById(R.id.lessonDisplay);
            actualDisplay.setText(Util.html(toDisplay.toString()));

            return view;
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

        private StringBuilder getStringToDisplay(List<LessonInfo> infoForOneDay) {
            StringBuilder mainString = new StringBuilder();
            mainString.append("<html><center>");
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

        public interface OnFragmentInteractionListener {
            void onFragmentInteraction(Uri uri);
        }

    }
}
