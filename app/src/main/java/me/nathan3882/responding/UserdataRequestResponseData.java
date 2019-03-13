package me.nathan3882.responding;

import org.json.JSONException;
import org.json.JSONObject;

public class UserdataRequestResponseData implements RequestResponseData {

    private final String responseJson;
    private JSONObject jsonObject;
    private String userEmail;
    private String homeCrs;
    private String salt;
    private String password;

    public UserdataRequestResponseData(String responseJson) {
        this.responseJson = responseJson;
        try {
            setJsonObject(new JSONObject(responseJson));
            updateSubclassValues();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubclassValues() {
        try {
            setHomeCrs(getJsonObject().getString("homeCrs"));
            setPassword(getJsonObject().getString("password"));
            setSalt(getJsonObject().getString("salt"));
            setUserEmail(getJsonObject().getString("userEmail"));
        } catch (NullPointerException | JSONException e) {
            System.out.println("Couldn't parse JSON response");
            e.printStackTrace();
        }
    }

    @Override
    public String getResponseString() {
        return this.responseJson;
    }

    public String getHomeCrs() {
        return homeCrs;
    }

    public void setHomeCrs(String homeCrs) {
        this.homeCrs = homeCrs;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSalt() {
        return salt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "{ userEmail: \"" + userEmail + "\", password: \"" + password + "\", salt: \"" + salt + "\", homeCrs: \"" + homeCrs + "\" };";
    }
}
