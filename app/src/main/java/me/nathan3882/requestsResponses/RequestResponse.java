package me.nathan3882.requestsResponses;

import android.support.annotation.Nullable;
import me.nathan3882.responseData.RequestResponseData;

public class RequestResponse {

    private final int responseCode;
    private String webService;
    private GetRequest.Type action;
    private RequestResponseData requestResponseData = null;

    public RequestResponse(String webService, GetRequest.Type action, int responseCode, RequestResponseData requestResponseData) {
        this.webService = webService;
        setData(requestResponseData);
        this.action = action;
        this.responseCode = responseCode;
        if (getResponseCode() == 404) {
            System.out.println("There was a 404, A parameter doesnt exist in database");
        }
    }

    public GetRequest.Type getAction() {
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
        if (requestResponseData == null) System.err.println("Request response data is null, invalid parse in " + GetRequest.class.getName());
        return requestResponseData;
    }

    public void setData(RequestResponseData requestResponseData) {
        this.requestResponseData = requestResponseData;
    }
}
