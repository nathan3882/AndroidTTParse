package me.nathan3882.responding;

import android.support.annotation.Nullable;
import me.nathan3882.requesting.Action;

public class RequestResponse {

    private final int responseCode;
    private String webService;
    private Action action;
    private RequestResponseData requestResponseData = null;

    public RequestResponse(String webService, Action action, int responseCode, RequestResponseData requestResponseData) {
        this.webService = webService;
        setData(requestResponseData);
        this.action = action;
        this.responseCode = responseCode;
        if (getResponseCode() == 404) {
            System.out.println("There was a 404, A parameter doesnt exist in database");
        }
    }

    public Action getAction() {
        return action;
    }

    public String getWebService() {
        return webService;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Nullable
    public RequestResponseData getData() {
        return requestResponseData;
    }

    public void setData(RequestResponseData requestResponseData) {
        this.requestResponseData = requestResponseData;
    }
}
