package me.nathan3882.androidttrainparse.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;

public abstract class DayFragment extends Fragment {

    protected DayOfWeek dayOfWeek;

    protected String header;
    protected String stringToDisplay = null;
    private DayFragment.OnFragmentInteractionListener mListener;
    private ViewGroup container;
    private User user;


    public DayFragment() {

    }

    abstract void makeStringToDisplay(StringBuilder builder, ResponseEvent event);

    abstract String getHeader();

    abstract DayOfWeek getDayOfWeek();

    public void synchroniseUser(User user) {
        this.user = user;
    }

    @Nullable
    public User getUser() {
        return this.user;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public ViewGroup getContainer() {
        return container;
    }

    @Nullable
    protected String getStringToDisplay() {
        return stringToDisplay;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DayFragment.OnFragmentInteractionListener) {
            mListener = (DayFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        TextView dayFragHeader = view.findViewById(R.id.dayFragHeader);
        dayFragHeader.setText(Util.html("<html>" + getHeader() + "</html>"));

        TextView actualDisplay = view.findViewById(R.id.lessonDisplay);
        StringBuilder builder = new StringBuilder();

        if (getStringToDisplay() == null) {
            makeStringToDisplay(builder, new ResponseEvent() {

                @Override
                public void doFinally() {
                    String stringToDisplay = builder.toString();

                    actualDisplay.setText(Util.html(stringToDisplay));

                    DayFragment.this.stringToDisplay = stringToDisplay;

                }
            });
        }else{
            actualDisplay.setText(Util.html(getStringToDisplay()));
        }
        return view;
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
