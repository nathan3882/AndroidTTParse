package me.nathan3882.androidttrainparse;

import me.nathan3882.androidttrainparse.activities.MainActivity;
import me.nathan3882.androidttrainparse.responding.UserdataRequestResponseData;

public class LoginAttempt {

    private String localEnteredPassword;
    private UserdataRequestResponseData fetched;
    private String enteredEmail;
    private boolean wasSuccessful;
    private String unsuccessfulReason;
    private MainActivity mainActivity;
    private String gottenDBSalt = "";
    private String gottenDBBytes = "";

    public LoginAttempt(MainActivity mainActivity, String enteredEmail, String localEnteredPassword, UserdataRequestResponseData fetched) {
        this.mainActivity = mainActivity;
        this.enteredEmail = enteredEmail;
        this.localEnteredPassword = localEnteredPassword;
        this.fetched = fetched;
        this.gottenDBBytes = fetched.getPassword();
        this.gottenDBSalt = fetched.getSalt();
        boolean authenticated = Encryption.authenticate(getLocalEnteredPassword(), getGottenDBBytes(), getGottenDBSalt());
        if (!authenticated) {
            setSuccessful(false, "Invalid credentials");
        } else {
            setSuccessful(true);
        }
    }

    public UserdataRequestResponseData getFetched() {
        return fetched;
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

    public String getLocalEnteredPassword() {
        return localEnteredPassword;
    }

    public String getGottenDBSalt() {
        return gottenDBSalt;
    }

    public String getGottenDBBytes() {
        return gottenDBBytes;
    }

    private void setSuccessful(boolean wasSuccessful, String unsuccessfulReason) {
        setSuccessful(wasSuccessful);
        setUnsuccessfulReason(unsuccessfulReason);
    }

    private void setSuccessful(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
    }
}