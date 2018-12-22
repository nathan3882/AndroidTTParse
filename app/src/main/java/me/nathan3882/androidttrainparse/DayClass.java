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
import java.util.List;

public class DayClass {

    private final DayOfWeek dayOfWeek;

    private final TimeDisplayActivity activity;
    private String title;

    public DayClass(TimeDisplayActivity activity, DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.activity = activity;
    }

    public DayFragment newInstance(String headerNoHtml) {
        DayFragment dayFragment = new DayFragment();
        dayFragment.synchroniseLessonInfo(getActivity());
        Bundle bundle = new Bundle();
        bundle.putInt("dayOfWeekToShow", dayOfWeek.getValue());
        dayFragment.setArguments(bundle);
        TextView dayFragHeader = getActivity().findViewById(R.id.dayFragHeader);
        dayFragHeader.setText(Util.html("<html>" + headerNoHtml + "</html>"));
        return dayFragment;
    }

    public DayFragment newInstance(String pageTitle, boolean b) {
        DayFragment newInst = newInstance(pageTitle);
        if (b) DayEquivalent.addToPrevious(getDayOfWeek(), newInst);
        return newInst;
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
        private List<LessonInfo> thisDayLessonInfo;

        public DayFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                dayOfWeek = DayOfWeek.of(getArguments().getInt("dayOfWeekToShow"));

            }
        }

        public DayOfWeek getDayOfWeekToShow() {
            return this.dayOfWeek;
        }

        public void synchroniseLessonInfo(TimeDisplayActivity inst) {
            this.thisDayLessonInfo = inst.getLessonInfo().get(dayOfWeek);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_day, container, false);
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

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFragmentInteraction(Uri uri);
        }
    }
}
