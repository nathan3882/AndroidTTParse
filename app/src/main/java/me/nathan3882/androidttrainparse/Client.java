package me.nathan3882.androidttrainparse;

import me.nathan3882.requesting.Action;

public class Client {

    private Action action;

    private String webService;

    public Client(String webService, Action action) {
        this.action = action;
        this.webService = webService + action.getWebServiceAction(false);
    }

    public String getWebService() {
        return webService;
    }

    public Action getAction() {
        return this.action;
    }
}
