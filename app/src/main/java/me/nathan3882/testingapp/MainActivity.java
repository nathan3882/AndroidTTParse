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
import me.nathan3882.data.SqlQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final long TWO_SECS = 1500L;
    private static long latestEvent = System.currentTimeMillis();
    private static Context context;
    private static boolean doneFadeOut = false;
    private static boolean doneSecondFadeIn = false;
    private static boolean first = true;
    private boolean hasInternet;
    private SqlConnection sqlConnection = null;
    private MainActivity mainActivity;

    public static Context getRuntimeContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        this.mainActivity = this;
        this.sqlConnection = new SqlConnection(this);
        final TextView loginOrRegister = findViewById(R.id.loginOrRegisterText);
        loginOrRegister.setText("Login below using previously made details!");

        final EditText emailEnter = findViewById(R.id.enterEmail);

        final TextView sixDigitPwHelper = findViewById(R.id.sixDigitPwHelper);

        final EditText numericPassword = findViewById(R.id.numericPassword);

        final Button loginButton = findViewById(R.id.loginButton);

        final TextView helloText = findViewById(R.id.helloText);
        helloText.setText(html("<html><center>Hello student..."));

        initViews(loginOrRegister, emailEnter, sixDigitPwHelper, numericPassword, helloText, loginButton);

        showNetworkToasts();

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
                            fadeInAlpha(TWO_SECS, true, emailEnter, sixDigitPwHelper, numericPassword, loginButton);
                            cancel();
                        }
                        if (dif >= 8000L && !doneSecondFadeIn) { //After fade out of welcome has finished
                            fadeInAlpha(TWO_SECS, true, loginOrRegister);
                            doneSecondFadeIn = true;
                        } else if (dif >= 5000L && !doneFadeOut) {
                            fadeOutAlpha(helloText, helloText.getAlpha(), TWO_SECS, true);
                            doneFadeOut = true;
                        } else if (first) {
                            fadeInAlpha(TWO_SECS, true, helloText);
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
                            LoginAttempt attempt = new LoginAttempt(mainActivity, getSqlConnection(), System.currentTimeMillis(), emailText, passwordText);
                            if (attempt.wasSuccessful()) {
                                doPostLogin(currentView, emailText, passwordText);
                            } else {
                                showToast("Login unsuccessful! " + attempt.getUnsuccessfulReason(), "short");
                            }
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

    private void doPostLogin(View currentView, String email, String sixDigitPass) {
        if (sqlConnection.connectionEstablished()) { //To be safe

        }
    }

    public String getDatabaseStoredPwBytes(String email) {
        SqlQuery query = new SqlQuery(sqlConnection);
        query.executeQuery("SELECT password FROM {table} WHERE userEmail = '" + email + "'",
                SqlConnection.SqlTableName.TIMETABLE_USERDATA);
        ResultSet set = query.getResultSet();
        try {
            if (!set.next()) return "invalid email";
            return set.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDatabaseSalt(String email) {
        SqlQuery query = new SqlQuery(sqlConnection);
        query.executeQuery("SELECT salt FROM {table} WHERE userEmail = '" + email + "'",
                SqlConnection.SqlTableName.TIMETABLE_USERDATA);
        ResultSet set = query.getResultSet();
        try {
            if (!set.next()) return "invalid email";
            return set.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
            if (!sqlConnection.connectionEstablished()) {
                showToast(getString(R.string.internetNoDatabaseCon), "long");
            } else{
                showToast(getString(R.string.internetAndDatabaseConnected), "long");
            }
        }
    }

    private void setAlpha(View view, float f) {
        view.setAlpha(f);
    }

    public Toast showToast(Context c, String text, String length, boolean show) {
        Toast toast = Toast.makeText(c, text, length == "short" ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        if (show) toast.show();
        return toast;
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

    public boolean hasInternet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL amazonWS = new URL("http://checkip.amazonaws.com");
                    new BufferedReader(new InputStreamReader(
                            amazonWS.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        hasInternet = true;
        return hasInternet;
    }


    public SqlConnection getSqlConnection() {
        if (this.sqlConnection == null || !this.sqlConnection.connectionEstablished()) {
            if (this.sqlConnection.connectionEstablished()) {
                return this.sqlConnection;
            } else {
                return null;
            }
        }
        return this.sqlConnection;
    }
}
