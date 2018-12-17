package me.nathan3882.testingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import me.nathan3882.data.SqlConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static long latestEvent = System.currentTimeMillis();
    private static Context context;

    private static boolean doneFadeOut = false;
    private static boolean doneSecondFadeIn = false;
    private static boolean first = true;

    private boolean hasInternet;
    private SqlConnection sqlConnection = null;

    private static final long TWO_SECS = 1500L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        updateSqlConnection();

        final TextView loginOrRegister = findViewById(R.id.loginOrRegisterText);
        loginOrRegister.setText("Register or login below!");

        final EditText emailEnter = findViewById(R.id.enterEmail);

        final TextView sixDigitPwHelper = findViewById(R.id.sixDigitPwHelper);

        final EditText numericPassword = findViewById(R.id.numericPassword);

        final TextView helloText = findViewById(R.id.helloText);
        helloText.setText(html("<html><center>Hello student..."));

        initViews(loginOrRegister, emailEnter, sixDigitPwHelper, numericPassword, helloText);

        showNetworkToasts();

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setActivated(false);

        loginButton.setOnClickListener(getLoginBtnClickListener(loginButton, emailEnter, numericPassword));

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                final long dif = current - latestEvent;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dif >= 11000L) {
                            fadeInAlpha(emailEnter, TWO_SECS, true);
                            fadeInAlpha(sixDigitPwHelper, TWO_SECS, true);
                            fadeInAlpha(numericPassword, TWO_SECS, true);
                            cancel();
                        }
                        if (dif >= 8000L && !doneSecondFadeIn) { //After fade out of welcome has finished
                            fadeInAlpha(loginOrRegister, TWO_SECS, true);
                            doneSecondFadeIn = true;
                        } else if (dif >= 5000L && !doneFadeOut) {
                            fadeOutAlpha(helloText, helloText.getAlpha(), TWO_SECS, true);
                            doneFadeOut = true;
                        } else if (first) {
                            fadeInAlpha(helloText, TWO_SECS, true);
                            first = false;
                        }
                    }
                });
            }
        };
        new Timer().scheduleAtFixedRate(task, 1000L, 1000L);
    }

    private void initViews(View... views) {
        for (View view : views) {
            view.setAlpha(0F);
        }
    }

    private View.OnClickListener getLoginBtnClickListener(Button loginButton, final EditText emailBox, final EditText password) {
        return new View.OnClickListener() {

            public void onClick(final View currentView) {
                String emailText = emailBox.getText().toString();
                String passwordText = password.getText().toString();
                if (!sqlConnection.connectionEstablished()) {
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
                            int passwordInt = Integer.parseInt(passwordText);
                            LoginAttempt attempt = new LoginAttempt(getSqlConnection(), System.currentTimeMillis(), emailText, passwordInt);
                            if (attempt.wasSuccessful()) {
                                attemptLogin(currentView, emailText, Integer.parseInt(passwordText));
                            }else{
                                showToast("Login unsuccessful! " + attempt.getUnsuccessfulReason(), "short");
                            }
                        }else{
                            showToast("That's not a valid password!", "short");
                        }
                    }else{
                        showToast("That's not a valid email!", "short");
                    }
                }
            }
        };
    }

    private void attemptLogin(View currentView, String email, int sixDigitPass) {
        if (sqlConnection.connectionEstablished()) { //To be safe

        }
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
        } else if (!sqlConnection.connectionEstablished()) {
            showToast(getString(R.string.internetNoDatabaseCon), "long");
        } else {
            showToast(getString(R.string.internetAndDatabaseConnected), "long");
        }
    }

    private void updateSqlConnection() {
        this.sqlConnection = new SqlConnection(this);
    }

    private void setAlpha(View view, float f) {
        view.setAlpha(f);
    }

    private Toast showToast(Context c, String text, String length, boolean show) {
        Toast toast = Toast.makeText(c, text, length == "short" ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
        return toast;
    }

    private void showToast(String text, String length) {
        showToast(getApplicationContext(), text, length, true);
    }

    private Animation fadeInAlpha(View view, long duration, boolean start) {
        view.setAlpha(1F);
        Animation inAnimation = new AlphaAnimation(0F, 1F);
        inAnimation.setDuration(duration);
        if (start) view.startAnimation(inAnimation);

        return inAnimation;
    }

    private Animation fadeOutAlpha(TextView view, float startAlpha, long outDuration, String newText, boolean start) {
        Animation outAnimation = fadeOutAlpha(view, startAlpha, outDuration, false);
        view.setText(newText);
        if (start) outAnimation.start();
        return outAnimation;
    }

    private Animation fadeOutAlpha(final TextView view, float startAlpha, long outDuration, boolean start) {
        final Animation outAnimation = new AlphaAnimation(startAlpha, 0F);
        outAnimation.setDuration(outDuration);
        if (start) view.startAnimation(outAnimation);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(0F); //keeps it hidden
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return outAnimation;
    }

    private Spanned html(String string) {
        return Html.fromHtml(string);
    }

    public static Context getRuntimeContext() {
        return context;
    }

    public boolean hasInternet() {
        return fetchIp() != null;
    }

    private String fetchIp() {
        BufferedReader reader = null;
        try {
            URL amazonWS = new URL("http://checkip.amazonaws.com");
            reader = new BufferedReader(new InputStreamReader(
                    amazonWS.openStream()));
            hasInternet = true;
            String ip = reader.readLine();
            return ip;
        } catch (Exception e) {
            System.out.println("No Internet???");
            hasInternet = false;
        }
        return null;
    }

    public SqlConnection getSqlConnection() {
        if (this.sqlConnection == null || !this.sqlConnection.connectionEstablished()) {
            updateSqlConnection();
            if (this.sqlConnection.connectionEstablished()) {
                return this.sqlConnection;
            } else {
                return null;
            }
        }
        return this.sqlConnection;
    }
}
