package me.nathan3882.androidttrainparse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;

public class DayFragment extends Fragment {

    protected DayOfWeek dayOfWeek;

    protected String header;
    private DayFragmentFactory.DayFragment.OnFragmentInteractionListener mListener;

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
        if (context instanceof DayFragmentFactory.DayFragment.OnFragmentInteractionListener) {
            mListener = (DayFragmentFactory.DayFragment.OnFragmentInteractionListener) context;
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
