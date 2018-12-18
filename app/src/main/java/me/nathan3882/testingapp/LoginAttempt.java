package me.nathan3882.testingapp;

import me.nathan3882.data.Encryption;
import me.nathan3882.data.SqlConnection;

public class LoginAttempt {

    private final SqlConnection connection;
    private String emailText;
    private boolean wasSuccessful;
    private String unsuccessfulReason;
    private MainActivity mainActivity;

    public LoginAttempt(MainActivity mainActivity, SqlConnection connection, long currentTimeMillis, String emailText, String localEnteredPassword) {
        this.mainActivity = mainActivity;
        this.connection = connection;
        this.emailText = emailText;

        if (!connection.connectionEstablished()) {
            setSuccessful(false, true, "Database connection not established");
        } else {
            String gottenDBSalt = mainActivity.getDatabaseSalt(emailText);
            String gottenDBBytes = mainActivity.getDatabaseStoredPwBytes(emailText);

            if (gottenDBBytes.equals("invalid email")) { //= 'invalid email' when record doesnt exists
                setSuccessful(false, true, "This email is incorrect!");
                return;
            }

            boolean authenticated = Encryption.authenticate(localEnteredPassword, gottenDBBytes, gottenDBSalt);
            if (!authenticated) {
                String reason = "This password is incorrect!";
                setSuccessful(false, true, reason);
                return;
            }else{
                mainActivity.showToast("Welcome to the app - swipe to view other days!", "long");
            }
            setSuccessful(true);
        }
    }

    private void setSuccessful(boolean wasSuccessful, boolean toast, String unsuccessfulReason) {
        this.wasSuccessful = wasSuccessful;
        if (!wasSuccessful) {
            if (toast) {
                setUnsuccessfulReason(unsuccessfulReason);
                mainActivity.showToast(getUnsuccessfulReason(), "short");
            }
        }
    }

    private void setSuccessful(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
    }

    public boolean wasSuccessful() {
        return this.wasSuccessful;
    }

    public String getUnsuccessfulReason() {
        return this.unsuccessfulReason;
    }

    public void setUnsuccessfulReason(String unsuccessfulReason) {
        this.unsuccessfulReason = unsuccessfulReason;
    }

    public SqlConnection getConnection() {
        return connection;
    }

    public String getEmailText() {
        return emailText;
    }
}
