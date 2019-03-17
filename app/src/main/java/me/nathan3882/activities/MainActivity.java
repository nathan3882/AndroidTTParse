package me.nathan3882.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import me.nathan3882.androidttrainparse.*;
import me.nathan3882.requesting.Action;
import me.nathan3882.requesting.IActivityReferencer;
import me.nathan3882.requesting.ProgressedGetRequest;
import me.nathan3882.responding.*;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements IActivityReferencer<Activity>, ProgressBarable {

    private static Calendar calendar;
    private MainActivity mainActivity;
    private WeakReference<Activity> weakReference;
    private Switch addLessonInfoButton;
    private ProgressBar mainActivityProgressBar;

    /**
     * @param userLessonsPopulated only set to true if User#getLocalLessons() is populated
     */
    public static void startTimeDisplayActivity(WeakReference<Activity> reference,
                                                ProgressBarable barable, User user, boolean userLessonsPopulated) {
        if (userLessonsPopulated) {
            System.out.println("no 1");
            startTimeDisplayIntent(reference.get(), user);
        } else {
            System.out.println("no 2");

            Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, Action.GET_USER_LESSON_NAMES);

            new ProgressedGetRequest(reference, barable.getProgressBarRid(), lessonNamesClient, "/" + user.getUserEmail() + Util.PARAMS, new ResponseEvent() {
                @Override
                public void onCompletion(@NonNull RequestResponse requestResponse) {
                    RequestResponseData data = requestResponse.getData();

                    if (data instanceof LessonNameRequestResponseData) {

                        LessonNameRequestResponseData lessonNameData =
                                (LessonNameRequestResponseData) data;

                        ArrayList<String> lessonNames = lessonNameData.getLessonNames();
                        user.synchronise(lessonNames);
                        Activity activity = reference.get();
                        if (activity != null) {
                            startTimeDisplayIntent(activity, user);
                        } else {
                            onFailure();
                        }
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(reference.get(), "Sorry, lesson times could not be shown", Toast.LENGTH_LONG).show();
                }
            }).execute();
        }
    }

    private static void startTimeDisplayIntent(Activity activity, User user) {

        Intent toTimeDisplay = new Intent(activity, TimeDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BundleName.EMAIL.asString(), user.getUserEmail());
        bundle.putStringArrayList(BundleName.LESSONS.asString(), user.getLocalLessons());
        bundle.putString(BundleName.HOME_CRS.asString(), user.getHomeCrs());
        bundle.putBoolean(BundleName.USER_LESSONS_POPULATED.asString(), true);
        bundle.putIntArray(BundleName.DAYS_TO_SHOW.asString(), getDaysToShow(getCalendar().get(Calendar.DAY_OF_WEEK)));
        toTimeDisplay.putExtras(bundle);
        activity.startActivity(toTimeDisplay);
    }

    public static void startLessonSelect(WeakReference<Activity> reference, ProgressBarable barable, User user) {
        Intent msgToLessonSelectActivity = new Intent(reference.get(), LessonSelectActivity.class);
        Bundle bundleForLessonSelect = new Bundle();
        bundleForLessonSelect.putString(BundleName.EMAIL.asString(), user.getUserEmail());
        bundleForLessonSelect.putString(BundleName.HOME_CRS.asString(), user.getHomeCrs());

        user.synchroniseWithDatabase(new ResponseEvent() {
            @Override
            public void onCompletion(@NonNull RequestResponse requestResponse) {

                bundleForLessonSelect.putStringArrayList(BundleName.LESSONS.asString(), user.getLocalLessons());
                bundleForLessonSelect.putBoolean(BundleName.USER_LESSONS_POPULATED.asString(), true);

                msgToLessonSelectActivity.putExtras(bundleForLessonSelect);
                reference.get().startActivity(msgToLessonSelectActivity);
            }
        });
    }

    public static Calendar getCalendar() {
        return calendar;
    }

    private static int[] getDaysToShow(int currentDay) {
        return new int[]{1, 2, 3, 4, 5}; //all days
    }

    public void showToast(Context c, String text, String length, boolean show) {
        Toast toast = Toast.makeText(c, text, length.equals("short") ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
    }

    public void showToast(String text, String length) {
        showToast(getApplicationContext(), text, length, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        this.mainActivityProgressBar = findViewById(R.id.mainActivityProgressBar);

        this.mainActivity = this;
        this.weakReference = new WeakReference<>(mainActivity);

        TextView welcome = findViewById(R.id.welcomeStudent);

        this.addLessonInfoButton = findViewById(R.id.addLessonInfoButton);

        EditText emailEnter = findViewById(R.id.enterEmail);

        TextView sixDigitPwHelper = findViewById(R.id.sixDigitPwHelper);
        TextView emailHelper = findViewById(R.id.emailHelper);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat-Regular.otf");

        EditText numericPassword = findViewById(R.id.numericPassword);

        ImageView loginButtonView = findViewById(R.id.loginButton);

        View[] initViews = {welcome, emailEnter, sixDigitPwHelper, numericPassword, addLessonInfoButton};

        initViews(initViews);

        showNetworkToasts();

        fadeInAlpha(Util.ONE_HALF_SECS, true, emailEnter, addLessonInfoButton, sixDigitPwHelper, numericPassword, loginButtonView);
        fadeInAlpha(Util.ONE_HALF_SECS, true, welcome);

        loginButtonView.setOnClickListener(getLoginBtnClickListener(emailEnter, numericPassword));

    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }

    @Override
    public int getProgressBarRid() {
        return mainActivityProgressBar.getId();
    }

    private View.OnClickListener getLoginBtnClickListener(final EditText emailBox,
                                                          final EditText password) {
        return new View.OnClickListener() {

            public void onClick(final View currentView) {
                String emailText = emailBox.getText().toString();
                String passwordText = password.getText().toString();

                if (!Util.hasInternet(getWeakReference().get())) {
                    Snackbar snackbar = Snackbar.make(currentView, "WiFi / data on?", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Let me know!", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showNetworkToasts();
                                }
                            });
                    snackbar.show();
                } else {
                    if (isValidEmailAddress(emailText)) {
                        if (isValidPasscode(passwordText.toCharArray())) {

                            Client loginClient = new Client(
                                    Util.DEFAULT_TTRAINPARSE,
                                    Action.GET_USER_INFO);

                            new ProgressedGetRequest(getWeakReference(), getProgressBarRid(),
                                    loginClient, "/" + emailText + Util.PARAMS, new ResponseEvent() {
                                @Override
                                public void onCompletion(@NonNull RequestResponse requestResponse) {
                                    if (requestResponse.getResponseCode() == 404) {
                                        getMainActivity().showToast("Invalid username / password", "long");
                                        return;
                                    }
                                    RequestResponseData data = requestResponse.getData();
                                    if (data instanceof UserdataRequestResponseData) {
                                        UserdataRequestResponseData fetched = (UserdataRequestResponseData) data;
                                        LoginAttempt loginAttempt =
                                                new LoginAttempt(getMainActivity(),
                                                        emailText,
                                                        passwordText,
                                                        fetched);
                                        if (loginAttempt.wasSuccessful()) {
                                            getMainActivity().showToast("Success - redirecting...", "long");
                                            getMainActivity().doPostSuccessfulLogin(fetched); //user logs in, if they stored their stuff already
                                        } else {
                                            getMainActivity().showToast("Invalid username / password", "long");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure() {
                                    showToast("An unknown error occurred", "long");
                                }
                            }).execute();

                        } else {
                            showToast("That's not a valid password!", "short");
                        }
                    } else {
                        showToast("That's not a valid email!", "short");
                    }
                }
            }
        };
    }

    private void doPostSuccessfulLogin(UserdataRequestResponseData responseData) {
        if (Util.hasInternet(getWeakReference().get())) { //To be safe
            Client client = new Client(Util.DEFAULT_TTRAINPARSE, Action.GET_USER_HAS_LESSON_NAMES);
            new ProgressedGetRequest(getWeakReference(), getProgressBarRid(),
                    client,
                    "/" + responseData.getUserEmail() + Util.PARAMS,
                    new ResponseEvent() {
                        @Override
                        public void onCompletion(@NonNull RequestResponse requestResponse) {
                            HasLessonNamesRequestResponseData data = (HasLessonNamesRequestResponseData) requestResponse.getData();

                            boolean updating = addLessonInfoButton.isChecked();
                            User user = User.fromData(responseData);
                            if (updating) {
                                startLessonSelect(getWeakReference(), MainActivity.this, user);
                            } else { //show time display
                                startTimeDisplayActivity(getWeakReference(), MainActivity.this, user, false);
                            }
                        }

                        @Override
                        public void onFailure() {
                            showToast("An unknown error occurred", "long");
                        }
                    }).execute();
        } else {
            showNetworkToasts();
        }
    }

    private void initViews(View[] views) {
        for (View view : views) {
            view.setAlpha(0F);
        }
    }

    private MainActivity getMainActivity() {
        return mainActivity;
    }

    private boolean isValidPasscode(char[] password) {
        if (password.length != 6) return false;
        for (char charAt : password) {
            if (!Character.isDigit(charAt)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private void showNetworkToasts() {
        if (!Util.hasInternet(getWeakReference().get())) {
            showToast(getString(R.string.internetRequired), "long");
        } else {
            showToast(getString(R.string.internetAndDatabaseConnected), "long");
        }
    }

    private void fadeInAlpha(long duration, boolean start, View... views) {
        for (View view : views) {
            view.setAlpha(1F);
            Animation inAnimation = new AlphaAnimation(0F, 1F);
            inAnimation.setDuration(duration);
            if (start) view.startAnimation(inAnimation);
        }
    }

    private Animation fadeInAlpha(long duration, boolean start, View view) {
        view.setAlpha(1F);
        Animation inAnimation = new AlphaAnimation(0F, 1F);
        inAnimation.setDuration(duration);
        if (start) view.startAnimation(inAnimation);

        return inAnimation;
    }
        /*int[] showThese = new int[2];
        // TODO Allow user configuration
        if (currentDay == 6 || currentDay == 7) { //Weekend, show monday
            showThese[0] = java.time.DayOfWeek.MONDAY.getValue();
            showThese[1] = java.time.DayOfWeek.TUESDAY.getValue();
        } else if (currentDay == 5) { //Friday, show friday and monday
            showThese[0] = DayOfWeek.FRIDAY.getValue();
            showThese[1] = DayOfWeek.MONDAY.getValue();
        } else { //Show today and tomorrow
            showThese[0] = DayOfWeek.of(currentDay).getValue();
            showThese[1] = DayOfWeek.of(currentDay + 1).getValue();
        }
        return showThese;*/
}
