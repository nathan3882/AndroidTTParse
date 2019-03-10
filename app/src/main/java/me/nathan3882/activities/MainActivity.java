package me.nathan3882.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import me.nathan3882.requestsResponses.AsyncContextRef;
import me.nathan3882.requestsResponses.GetRequest;
import me.nathan3882.requestsResponses.RequestResponse;
import me.nathan3882.requestsResponses.RequestResponseCompletionEvent;
import me.nathan3882.responseData.HasEnteredLessonsBeforeRequestResponseData;
import me.nathan3882.responseData.LessonNameRequestResponseData;
import me.nathan3882.responseData.RequestResponseData;
import me.nathan3882.responseData.UserdataRequestResponseData;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements AsyncContextRef<Activity> {

    private MainActivity mainActivity;
    private WeakReference<Activity> weakReference;
    private Switch addLessonInfoButton;
    private ProgressBar mainActivityProgressBar;
    private EditText emailEnter;
    private TextView sixDigitPwHelper;
    private EditText numericPassword;
    private ImageView loginButtonView;
    private TextView loginOrRegister;

    public static void startTimeDisplayActivity(Context currentContext, WeakReference<Activity> reference, User user) {
        Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.GET_LESSON_NAMES);

        new GetRequest(reference, lessonNamesClient, "/" + user.getUserEmail() + Util.PARAMS, new RequestResponseCompletionEvent() {
            @Override
            public void onCompletion(RequestResponse requestResponse) {
                RequestResponseData data = requestResponse.getData();
                if (data instanceof LessonNameRequestResponseData) {
                    LessonNameRequestResponseData lessonNameData =
                            (LessonNameRequestResponseData) data;

                    ArrayList<String> lessonNames = lessonNameData.getLessonNames();

                    Intent toTimeDisplay = new Intent(currentContext, TimeDisplayActivity.class);
                    toTimeDisplay.putExtra("email", user.getUserEmail());
                    toTimeDisplay.putExtra("homeCrs", user.getHomeCrs());
                    toTimeDisplay.putStringArrayListExtra("lessons", lessonNames);
                    currentContext.startActivity(toTimeDisplay);
                }
            }
        }).execute();
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainActivityProgressBar = findViewById(R.id.mainActivityProgressBar);

        this.mainActivity = this;
        this.weakReference = new WeakReference<>(mainActivity);
        this.loginOrRegister = findViewById(R.id.loginOrRegisterText);

        loginOrRegister.setText("Login below using your previously registered account!");

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

    private void sendHasEnteredLessonsBeforeRequest(String userEmail, RequestResponseCompletionEvent callback) {
        Client client = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.HAS_LESSON_NAMES);
        try {
            new ProgressedGetRequest(getWeakReference(),
                    client,
                    "/" + userEmail + Util.PARAMS,
                    callback)
                    .execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void initViews(View[] views) {
        for (View view : views) {
            view.setAlpha(0F);
        }
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
                                    loginClient, "/" + emailText + Util.PARAMS, new RequestResponseCompletionEvent() {
                                @Override
                                public void onCompletion(RequestResponse requestResponse) {
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

                                        }
                                    }
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
            sendHasEnteredLessonsBeforeRequest(responseData.getUserEmail(),
                    new RequestResponseCompletionEvent() {
                        @Override
                        public void onCompletion(RequestResponse requestResponse) {
                            HasEnteredLessonsBeforeRequestResponseData data = (HasEnteredLessonsBeforeRequestResponseData) requestResponse.getData();
                            Context context = getMainActivity().getBaseContext();
                            User newUser = User.fromData(responseData);
                            if (data != null && data.hasEnteredLessonsBefore()) {
                                boolean updating = addLessonInfoButton.isChecked();
                                if (updating) {
                                    startLessonSelect(context, true, responseData);
                                } else { //show time display
                                    startTimeDisplayActivity(context, getWeakReference(),
                                            newUser);
                                }
                            } else { //open lesson select
                                startLessonSelect(context, false, responseData);
                            }
                        }
                    });
        }
    }

    private void startLessonSelect(Context context, boolean updating, UserdataRequestResponseData request) {
        Intent msgToLessonSelectActivity = new Intent(context, LessonSelectActivity.class);
        Bundle bundleForLessonSelect = new Bundle();
        bundleForLessonSelect.putBoolean("isUpdating", updating);
        bundleForLessonSelect.putString("email", request.getUserEmail());
        bundleForLessonSelect.putString("homeCrs", request.getHomeCrs());

        Client lessonNamesClient = new Client(Util.DEFAULT_TTRAINPARSE, GetRequest.Type.GET_LESSON_NAMES);
        if (updating) {
            new ProgressedGetRequest(getWeakReference(), lessonNamesClient, "/" + request.getUserEmail() + Util.PARAMS, new RequestResponseCompletionEvent() {
                @Override
                public void onCompletion(RequestResponse requestResponse) {
                    RequestResponseData data = requestResponse.getData();
                    if (data instanceof LessonNameRequestResponseData) {
                        LessonNameRequestResponseData lessonNameData = (LessonNameRequestResponseData) data;

                        addLessonsToBundle(msgToLessonSelectActivity,
                                lessonNameData.getLessonNames(),
                                bundleForLessonSelect);


                        startActivity(msgToLessonSelectActivity);
                    }
                }
            });
        } else {
            addLessonsToBundle(msgToLessonSelectActivity,
                    new ArrayList<String>(),
                    bundleForLessonSelect);
            startActivity(msgToLessonSelectActivity);
        }
    }

    private void addLessonsToBundle(Intent msgToLessonSelectActivity, ArrayList<String> array, Bundle bundleForLessonSelect) {
        bundleForLessonSelect.putStringArrayList("lessons", array);
        msgToLessonSelectActivity.putExtras(bundleForLessonSelect);
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
        Toast toast = Toast.makeText(c, text, length == "short" ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
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

    /**
     * Inner class to allow manipulation of ProgressBar
     */
    private class ProgressedGetRequest extends GetRequest {

        private ProgressedGetRequest(WeakReference<Activity> weakReference, Client client, String parameter, RequestResponseCompletionEvent requestResponseCompletionEvent) {
            super(weakReference, client, parameter, requestResponseCompletionEvent);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Activity reference = getWeakReference().get();
            if (reference != null) {
                ProgressBar bar = reference.findViewById(R.id.mainActivityProgressBar);
                if (bar.getVisibility() == View.INVISIBLE || bar.getVisibility() == View.GONE) {
                    bar.setVisibility(View.VISIBLE);
                }
                bar.setProgress(progress[0]);
            }
        }

        @Override
        protected void onPostExecute(RequestResponse requestResponse) {
            super.onPostExecute(requestResponse);
            Activity reference = getWeakReference().get();
            if (getWeakReference().get() != null) {
                ProgressBar bar = reference.findViewById(R.id.mainActivityProgressBar);
                bar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
