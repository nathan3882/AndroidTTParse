package me.nathan3882.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.androidttrainparse.LoginAttempt;
import me.nathan3882.androidttrainparse.User;
import me.nathan3882.androidttrainparse.Util;
import me.nathan3882.requestsResponses.*;
import me.nathan3882.responseData.HasEnteredLessonsBeforeRequestResponseData;
import me.nathan3882.responseData.LessonNameRequestResponseData;
import me.nathan3882.responseData.RequestResponseData;
import me.nathan3882.responseData.UserdataRequestResponseData;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements IActivityReferencer<Activity> {

    private MainActivity mainActivity;
    private WeakReference<Activity> weakReference;
    private Switch addLessonInfoButton;
    private ProgressBar mainActivityProgressBar;
    private EditText emailEnter;
    private TextView sixDigitPwHelper;
    private EditText numericPassword;
    private ImageView loginButtonView;
    private TextView loginOrRegister;

    public static void startTimeDisplayActivity(WeakReference<Activity> reference, User user) {
        Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.GET_LESSON_NAMES);

        new ProgressedGetRequest(reference, lessonNamesClient, "/" + user.getUserEmail() + Util.PARAMS, new ResponseEvent() {
            @Override
            public void onCompletion(@NonNull RequestResponse requestResponse) {
                RequestResponseData data = requestResponse.getData();
                if (data instanceof LessonNameRequestResponseData) {
                    LessonNameRequestResponseData lessonNameData =
                            (LessonNameRequestResponseData) data;

                    ArrayList<String> lessonNames = lessonNameData.getLessonNames();
                    Activity activity = reference.get();
                    if (activity != null) {
                        Intent toTimeDisplay = new Intent(activity, TimeDisplayActivity.class);
                        toTimeDisplay.putExtra("email", user.getUserEmail());
                        toTimeDisplay.putExtra("homeCrs", user.getHomeCrs());
                        toTimeDisplay.putStringArrayListExtra("lessons", lessonNames);
                        activity.startActivity(toTimeDisplay);
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        }).execute();
    }

    public static void startLessonSelect(WeakReference<Activity> reference, boolean updating, UserdataRequestResponseData request) {
        Intent msgToLessonSelectActivity = new Intent(reference.get(), LessonSelectActivity.class);
        Bundle bundleForLessonSelect = new Bundle();
        bundleForLessonSelect.putBoolean("isUpdating", updating);
        bundleForLessonSelect.putString("email", request.getUserEmail());
        bundleForLessonSelect.putString("homeCrs", request.getHomeCrs());

        Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.GET_LESSON_NAMES);
        if (updating) {
            System.out.println("updating");
            new ProgressedGetRequest(reference, lessonNamesClient, "/" + request.getUserEmail() + Util.PARAMS,
                    new ResponseEvent() {
                        @Override
                        public void onCompletion(@NonNull RequestResponse requestResponse) {
                            RequestResponseData data = requestResponse.getData();
                            if (data instanceof LessonNameRequestResponseData) {

                                LessonNameRequestResponseData lessonNameData = (LessonNameRequestResponseData) data;
                                System.out.println("Lesson names = " + lessonNameData);
                                //Business studies, Biology, Computer Science
                                String lessonNamesResponse = lessonNameData.getResponseString();
                                if (lessonNamesResponse.contains(Util.LESSON_STORAGE_SPLIT_CHAR)) { //if they have a ", " in their db string
                                    bundleForLessonSelect.putStringArrayList("lessons", new ArrayList<>(
                                            Arrays.asList(lessonNamesResponse.split(Util.LESSON_STORAGE_SPLIT_CHAR))));
                                } else { //no comma in their db string, no lessons
                                    bundleForLessonSelect.putStringArrayList("lessons", new ArrayList<>());
                                }

                                msgToLessonSelectActivity.putExtras(bundleForLessonSelect);

                                reference.get().startActivity(msgToLessonSelectActivity);
                            }
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
        } else {
            bundleForLessonSelect.putStringArrayList("lessons", new ArrayList<>());
            msgToLessonSelectActivity.putExtras(bundleForLessonSelect);
            reference.get().startActivity(msgToLessonSelectActivity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainActivityProgressBar = findViewById(R.id.mainActivityProgressBar);

        this.mainActivity = this;
        this.weakReference = new WeakReference<>(mainActivity);

        this.loginOrRegister = findViewById(R.id.loginOrRegisterText);

        loginOrRegister.setText(R.string.loginBelow);

        this.addLessonInfoButton = findViewById(R.id.addLessonInfoButton);

        this.emailEnter = findViewById(R.id.enterEmail);

        this.sixDigitPwHelper = findViewById(R.id.sixDigitPwHelper);

        this.numericPassword = findViewById(R.id.numericPassword);

        this.loginButtonView = findViewById(R.id.loginButton);

        View[] initViews = {loginOrRegister, emailEnter, sixDigitPwHelper, numericPassword, addLessonInfoButton};

        initViews(initViews);

        showNetworkToasts();

        fadeInAlpha(Util.ONE_HALF_SECS, true, emailEnter, addLessonInfoButton, sixDigitPwHelper, numericPassword, loginButtonView);
        fadeInAlpha(Util.ONE_HALF_SECS, true, loginOrRegister);

        loginButtonView.setOnClickListener(getLoginBtnClickListener(emailEnter, numericPassword));

    }

    private View.OnClickListener getLoginBtnClickListener(final EditText emailBox,
                                                          final EditText password) {
        return new View.OnClickListener() {

            public void onClick(final View currentView) {
                String emailText = emailBox.getText().toString();
                String passwordText = password.getText().toString();

                if (!hasInternet()) {
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
                                    GetRequest.Type.GET_USER_INFO);

                            new ProgressedGetRequest(getWeakReference(),
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
                                            getMainActivity().doPostSuccessfulLogin(fetched); //user logs in, if they stored their stuff already
                                            getMainActivity().showToast("Success - redirecting...", "long");
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
        if (hasInternet()) { //To be safe
            Client client = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.HAS_LESSON_NAMES);
            new ProgressedGetRequest(getWeakReference(),
                    client,
                    "/" + responseData.getUserEmail() + Util.PARAMS,
                    new ResponseEvent() {
                        @Override
                        public void onCompletion(@NonNull RequestResponse requestResponse) {
                            HasEnteredLessonsBeforeRequestResponseData data = (HasEnteredLessonsBeforeRequestResponseData) requestResponse.getData();

                            if (data != null && data.hasEnteredLessonsBefore()) {
                                boolean updating = addLessonInfoButton.isChecked();
                                System.out.println("is checked = " + updating);
                                if (updating) {
                                    startLessonSelect(getWeakReference(), true, responseData);
                                } else { //show time display
                                    User newUser = User.fromData(responseData);
                                    startTimeDisplayActivity(getWeakReference(), newUser);
                                }
                            } else { //open lesson select
                                System.out.println("data = " + data);
                                System.out.println("has entered = " + data.hasEnteredLessonsBefore());

                                startLessonSelect(getWeakReference(), false, responseData);
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

    public ProgressBar getMainActivityProgressBar() {
        return mainActivityProgressBar;
    }

    private boolean isValidPasscode(char[] password) {
        if (password.length != 6) return false;
        for (int i = 0; i < password.length; i++) {
            char charAt = password[i];
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
        if (!hasInternet()) {
            showToast(getString(R.string.internetRequired), "long");
        } else {
            if (!hasInternet()) {
                showToast(getString(R.string.internetNoDatabaseCon), "long");
            } else {
                showToast(getString(R.string.internetAndDatabaseConnected), "long");
            }
        }
    }

    public void showToast(Context c, String text, String length, boolean show) {
        Toast toast = Toast.makeText(c, text, length.equals("short") ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
    }

    public void showToast(String text, String length) {
        showToast(getApplicationContext(), text, length, true);
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

    public boolean hasInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }
}
