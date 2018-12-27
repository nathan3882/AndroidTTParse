package me.nathan3882.androidttrainparse;

import android.view.View;
import me.nathan3882.activities.MainActivity;
import me.nathan3882.data.Encryption;
import me.nathan3882.requests.UserdataRequest;

public class LoginAttempt {

    private String emailText;
    private boolean wasSuccessful;
    private String unsuccessfulReason;
    private MainActivity mainActivity;
    private String gottenDBSalt = "";
    private String gottenDBBytes = "";
    private boolean done = false;
    private UserdataRequest userdataRequest; //To allow network webservice access

    public LoginAttempt(MainActivity mainActivity, View view, long currentTimeMillis, String emailText, String localEnteredPassword) throws InterruptedException {
        this.mainActivity = mainActivity;
        this.emailText = emailText;
        Thread db = new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client("http://192.168.0.52:3000/ttrainparse/");
                Request request = client.generateRequest("getUserInfo", emailText);
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
                mainActivity.setUserdata(userdataRequest);
                if (!mainActivity.hasInternet()) {
                    setSuccessful(false, "Database connection not established");
                } else {
                    System.out.println("EMAIL TEXT = " + emailText);
                    if (gottenDBBytes.equals("") || gottenDBSalt.equals("")) { //= 'invalid email' when record doesnt exists
                        setSuccessful(false);
                        return;
                    }
                    boolean authenticated = Encryption.authenticate(localEnteredPassword, gottenDBBytes, gottenDBSalt);
                    if (!authenticated) {
                        setSuccessful(false);
                    } else {
                        setSuccessful(true);
                    }
                }
                done = true;
            }
        });
        db.start();

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mainActivity.hasInternet()) {
                    setSuccessful(false, "Database connection not established");
                } else {
                    System.out.println("EMAIL TEXT = " + emailText);
                    if (gottenDBBytes.equals("") || gottenDBSalt.equals("")) { //= 'invalid email' when record doesnt exists
                        setSuccessful(false);
                        return;
                    }
                    boolean authenticated = Encryption.authenticate(localEnteredPassword, gottenDBBytes, gottenDBSalt);
                    if (!authenticated) {
                        setSuccessful(false);
                    } else {
                        setSuccessful(true);
                    }
                }
            }
        });
        while(true) {
            if (!done) continue;
            thread.start();
            break;
        }*/
    }

    private void setSuccessful(boolean wasSuccessful, String unsuccessfulReason) {
        this.wasSuccessful = wasSuccessful;
        setUnsuccessfulReason(unsuccessfulReason);
    }


    private void setSuccessful(boolean wasSuccessful) {
        this.done = true;
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

    public String getEmailText() {
        return emailText;
    }

    public boolean isDone() {
        return done;
    }
}
