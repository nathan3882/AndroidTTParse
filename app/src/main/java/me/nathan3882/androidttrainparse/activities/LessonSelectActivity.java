package me.nathan3882.androidttrainparse.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.*;
import me.nathan3882.androidttrainparse.BundleName;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.androidttrainparse.requesting.IActivityReferencer;
import me.nathan3882.androidttrainparse.responding.RequestResponse;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Pattern;

public class LessonSelectActivity extends AbstractPostLoginActivity implements IActivityReferencer<Activity>, ProgressBarable {

    private static Pattern validLesson = Pattern.compile("(^((?![^a-zA-Z ]).)*)$");

    private WeakReference<Activity> weakReference;
    private User user;
    private ProgressBar progressBar;
    private TextView headerText;
    private TextView yourLessons;
    private FloatingActionButton lessonSelectFloatingActionBtn;
    private boolean allowLessonModification = true;
    private Bundle bundle;
    private EditText addLessonInputBox;
    private EditText removeLessonInputBox;
    private ImageView toTrainsButton;
    private Button addLessonButton;
    private Button removeLessonButton;

    public EditText getRemoveLessonInputBox() {
        return removeLessonInputBox;
    }

    public ImageView getToTrainsButton() {
        return toTrainsButton;
    }

    public boolean isAllowLessonModification() {
        return allowLessonModification;
    }

    @Override
    public int getProgressBarRid() {
        return this.progressBar.getId();
    }

    private void setAllowLessonModification(boolean value) {
        this.allowLessonModification = value;
        if (value) showToast("You can now add/remove lessons.", "long", true);
    }

    public TextView getYourLessons() {
        return yourLessons;
    }

    public TextView getHeaderText() {
        return headerText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_select);

        this.addLessonInputBox = findViewById(R.id.addLessonInputBox);
        this.removeLessonInputBox = findViewById(R.id.removeLessonInputBox);
        this.headerText = findViewById(R.id.headerText);
        this.toTrainsButton = findViewById(R.id.toTrainsButton);
        this.yourLessons = findViewById(R.id.lessonSelectHeader);
        this.weakReference = new WeakReference<>(this);
        this.progressBar = findViewById(R.id.lessonSelectActivityProgressBar);

        this.addLessonButton = findViewById(R.id.addLessonButton);
        this.removeLessonButton = findViewById(R.id.removeLessonButton);

        setInitialBundle(getIntent().getExtras(), savedInstanceState);

        if (bundle != null) { //make this a define user func in super class
            if (bundle.getBoolean(BundleName.USER_LESSONS_POPULATED.asString())) {
                System.out.println("calling pop");
                initUser(bundle, true);
            }else{
                System.out.println("calling non pop");
                initUser(bundle, false);
            }
        }


        refreshHeaderText();

    }

    @Override
    protected void onResume() {
        super.onResume();
        addLessonButton.setOnClickListener(getAddRemoveListener(true));

        removeLessonButton.setOnClickListener(getAddRemoveListener(false));

        toTrainsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.startTimeDisplayActivity(getWeakReference(), LessonSelectActivity.this, getUser(), true);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putAll(getInitialBundle());
        super.onSaveInstanceState(outState);
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
    public Bundle getInitialBundle() {
        return this.bundle;
    }

    @Override
    public void setInitialBundle(Bundle bundle, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            this.bundle = bundle;
        }else{
            this.bundle = savedInstanceState;
        }
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return weakReference;
    }


    private View.OnClickListener getAddRemoveListener(boolean addingLesson) {
        return view -> {
            EditText inputBox = addingLesson ? getAddLessonInputBox() : getRemoveLessonInputBox();
            String oneOrMoreLessons = inputBox.getText().toString().trim();

            if (!validLesson.matcher(oneOrMoreLessons).find()) {
                showToast("Please enter your lesson correctly", "long", true);
                return;
            }

            if (addingLesson && getUsersLocalLessons().contains(oneOrMoreLessons)) {
                showToast("You already have this lesson configured!", "long", true);
                return;
            } else if (!addingLesson && !getUsersLocalLessons().contains(oneOrMoreLessons)) {
                showToast("You can't remove a lesson that isn't configured!", "long", true);
                return;
            }

            if (isAllowLessonModification()) {
                setAllowLessonModification(false);
                if (addingLesson) {
                    //add lesson
                    addLesson(oneOrMoreLessons, new ResponseEvent() {
                        @Override
                        public void onCompletion(@NonNull RequestResponse requestResponse) {

                            showToast("Lesson, " + oneOrMoreLessons + ", added!", "long", true);
                            getUser().getLocalLessons().add(oneOrMoreLessons);
                            getAddLessonInputBox().setText("");

                        }

                        @Override
                        public void onFailure() {
                            showToast("Lesson, " + oneOrMoreLessons + ", couldn't be added!", "long", true);
                        }

                        @Override
                        public void doFinally() {
                            refreshHeaderText();
                            setAllowLessonModification(true);
                        }
                    });
                } else {
                    //remove lesson
                    removeLesson(oneOrMoreLessons, new ResponseEvent() {
                        @Override
                        public void onCompletion(@NonNull RequestResponse requestResponse) {
                            showToast("Lesson, " + oneOrMoreLessons + ", removed!", "long", true);
                            getUser().getLocalLessons().remove(oneOrMoreLessons);
                            getRemoveLessonInputBox().setText("");
                        }

                        @Override
                        public void onFailure() {
                            showToast("Lesson, " + oneOrMoreLessons + ", couldn't be removed!", "long", true);
                        }

                        @Override
                        public void doFinally() {
                            refreshHeaderText();
                            setAllowLessonModification(true);
                        }
                    });
                }
            } else {
                showToast("Please wait...", "long", true);
            }
        };
    }

    private EditText getAddLessonInputBox() {
        return addLessonInputBox;
    }

    private void refreshHeaderText() {
        String newText;
        if (!getUsersLocalLessons().isEmpty()) { //Has previously set, load and show to prevent duplicate

            List<String> userLessons = getUsersLocalLessons();

            int size = userLessons.size();

            getYourLessons().setText("Your " + size + " lesson" + (size == 1 ? " is" : "s are") + "...");
            newText = "<html>";

            for (int i = 0; i < size; i++) {
                String aLesson = userLessons.get(i);
                int lastIndex = size - 1;
                if (i == lastIndex) { //the last
                    newText += getPrefix() + aLesson + ".";
                } else if (i == lastIndex - 1) { //one before the final, "'aLesson' and FINAL"
                    newText += getPrefix() + aLesson + " and finally " + userLessons.get(++i) + "."; //break the loop
                } else {
                    newText += getPrefix() + aLesson + ", ";
                }
            }
            newText += "</html>";
        } else {
            newText = "<html>You have no lessons configured.</html>";
        }
        getHeaderText().setText(Util.html(newText));
    }

    private String getPrefix() {
        return Util.SPANNABLE_BREAK + "- ";
    }

}
