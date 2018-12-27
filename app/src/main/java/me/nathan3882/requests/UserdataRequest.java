package me.nathan3882.requests;

public class UserdataRequest {

    private String userEmail;
    private String homeCrs;
    private String salt;
    private String password;

    public UserdataRequest() {
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

    public String toString() {
        return "GetHomeCrsRequest { userEmail: " + userEmail + ", password: " + password + ", salt: " + salt + ", homeCrs: " + homeCrs + " }";
    }
}
