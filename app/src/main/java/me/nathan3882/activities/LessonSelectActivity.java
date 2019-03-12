package me.nathan3882.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.requestsResponses.IActivityReferencer;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class LessonSelectActivity extends AbstractPostLoginActivity implements IActivityReferencer<Activity> {

    private boolean isUpdating = false;
    private WeakReference<Activity> weakReference;
    private User user;
    private Button advanceButton;
    private TextView alreadyConfiguredText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_select);

        this.alreadyConfiguredText = findViewById(R.id.headerText);
        this.weakReference = new WeakReference<>(this);
        this.advanceButton = findViewById(R.id.advanceToTimeDisplayBtn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setUser(User.fromPrimitive(extras.getString("email"), extras.getString("homeCrs")));
            setUpdating(extras.getBoolean("isUpdating"));
        }
        ArrayList<String> lessonNames = new ArrayList<>();

        String newText;
        if (isUpdating() && extras != null && extras.containsKey("lessons")) { //Has previously set, load and show to prevent duplicate

            lessonNames = extras.getStringArrayList("lessons");

            int length = lessonNames.size();

            newText = "<html>Bare in mind... you already have " + length + " lessons configured:\n";

            for (int i = 0; i < length; i++) {
                String aLesson = lessonNames.get(i);
                if (i != length - 1) { //not the last
                    newText += aLesson + ", ";
                } else if (i == length - 2) { //one before the final, "'aLesson' and FINAL"
                    newText += aLesson + " and ";
                }
            }
            newText += ".</html>";
        } else {
            newText = "<html>Enter your lessons below! One per line</html>";
        }

        alreadyConfiguredText.setText(Util.html(newText));

        ArrayList<String> finalLessonNames = lessonNames;
        advanceButton.setOnClickListener(view -> {
            EditText lessonInput = findViewById(R.id.inputBox);
            String text = lessonInput.getText().toString();

            String splitChar = ", ";

            if (text.contains(splitChar)) {
                finalLessonNames.addAll(Arrays.asList(text.split(splitChar))); //add multiple
            } else {
                finalLessonNames.add(text); //just add the one lesson
            }
            getUser().setLessons(finalLessonNames);

            MainActivity.startTimeDisplayActivity(getWeakReference(),
                    getUser());
        });
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onBackPressed() {

    }

    public boolean isUpdating() {
        return isUpdating;
    }

    private void setUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return weakReference;
    }

}
