package me.nathan3882.androidttrainparse.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.androidttrainparse.activities.LessonSelectActivity;
import me.nathan3882.androidttrainparse.activities.ProgressBarable;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;
import me.nathan3882.testingapp.R;

import java.time.DayOfWeek;

public abstract class DayFragment extends Fragment implements ProgressBarable {

    protected DayOfWeek dayOfWeek;

    protected String header;
    protected String stringToDisplay = null;
    private DayFragment.OnFragmentInteractionListener mListener;
    private ViewGroup container;
    private User user;
    private ProgressBar progressBar;
    private ImageView toLessonSelect;


    public DayFragment() {

    }

    abstract void makeStringToDisplay(SpannableStringBuilder builder, ResponseEvent event);

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

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Nullable
    protected String getStringToDisplay() {
        return stringToDisplay;
    }

    @Override
    public int getProgressBarRid() {
        return this.progressBar.getId();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        this.progressBar = view.findViewById(R.id.timeDisplayFragmentProgressBar);
        this.toLessonSelect = view.findViewById(R.id.configureLessonsButton);
        toLessonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LessonSelectActivity.class);
                intent.putExtras(getUser().newBundle(true));
                getContext().startActivity(intent);
            }
        });


        TextView dayFragHeader = view.findViewById(R.id.dayFragHeader);
        dayFragHeader.setText(Util.html("<html>" + getHeader() + "</html>"));

        TextView actualDisplay = view.findViewById(R.id.lessonDisplay);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (getStringToDisplay() == null) {
            makeStringToDisplay(builder, new ResponseEvent() {

                @Override
                public void doFinally() {
                    actualDisplay.setText(builder);
                    DayFragment.this.stringToDisplay = stringToDisplay;
                }
            });
        } else {
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
