package me.nathan3882.androidttrainparse;

import android.view.View;
import me.nathan3882.activities.MainActivity;
import me.nathan3882.data.Encryption;
import me.nathan3882.requests.UserdataRequest;

public class LoginAttempt extends Thread {

    private final String localEnteredPassword;
    private final View view;
    private String emailText;
    private boolean wasSuccessful;
    private String unsuccessfulReason;
    private MainActivity mainActivity;
    private String gottenDBSalt = "";
    private String gottenDBBytes = "";
    private UserdataRequest userdataRequest; //To allow network webservice access

    public LoginAttempt(MainActivity mainActivity, View view, long currentTimeMillis, String emailText, String localEnteredPassword) {
        this.mainActivity = mainActivity;
        this.emailText = emailText;
        this.localEnteredPassword = localEnteredPassword;
        this.view = view;
    }

    public boolean wasSuccessful() {
        return this.wasSuccessful;
    }

    public UserdataRequest getUserdataRequest() {
        return userdataRequest;
    }

    @Override
    public void run() {
        Client client = new Client("http://192.168.0.52:3000/ttrainparse/");
        Request request = client.generateRequest("getUserInfo", getEmailText());
        Response response = request.getResponse();
        Object jsonResult = response.getJsonResult();
        UserdataRequest userdataRequest = null;
        if (jsonResult == null) {
            setSuccessful(false);
            return;
        }
        if (jsonResult instanceof UserdataRequest) {
            userdataRequest = (UserdataRequest) jsonResult;
            gottenDBBytes = userdataRequest.getPassword();
            gottenDBSalt = userdataRequest.getSalt();
        } else {
            setSuccessful(false);
            return;
        }
        this.userdataRequest = userdataRequest;
        if (!mainActivity.hasInternet()) {
            setSuccessful(false, "Database connection not established");
        } else {
            if (getGottenDBBytes().equals("") || getGottenDBSalt().equals("")) { //= 'invalid email' when record doesnt exists
                setSuccessful(false);
                return;
            }
            boolean authenticated = Encryption.authenticate(getLocalEnteredPassword(), getGottenDBBytes(), getGottenDBSalt());
            if (!authenticated) {
                setSuccessful(false);
            } else {
                setSuccessful(true);
            }
        }
    }

    private void setSuccessful(boolean wasSuccessful, String unsuccessfulReason) {
        this.wasSuccessful = wasSuccessful;
        setUnsuccessfulReason(unsuccessfulReason);
    }

    private void setSuccessful(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
        Util.notifyThread(mainActivity);
    }

    public String getUnsuccessfulReason() {
        return this.unsuccessfulReason;
    }

    public void setUnsuccessfulReason(String unsuccessfulReason) {
        this.unsuccessfulReason = unsuccessfulReason;
    }

    public String getEmailText() {
        return emailText;
    }

    public String getLocalEnteredPassword() {
        return localEnteredPassword;
    }

    public View getView() {
        return view;
    }
    public String getGottenDBSalt() {
        return gottenDBSalt;
    }

    public String getGottenDBBytes() {
        return gottenDBBytes;
    }
}
