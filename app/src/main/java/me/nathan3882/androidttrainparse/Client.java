package me.nathan3882.androidttrainparse;

import me.nathan3882.requestsResponses.GetRequest;

public class Client {

    private GetRequest.Type action;

    private String webService;

    public Client(String webService, GetRequest.Type action) {
        this.action = action;
        this.webService = webService + action.getWebServiceAction(false);
    }

    public String getWebService() {
        return webService;
    }

    public GetRequest.Type getAction() {
        return this.action;
    }
}
