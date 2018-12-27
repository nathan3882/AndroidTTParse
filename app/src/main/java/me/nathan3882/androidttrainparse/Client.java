package me.nathan3882.androidttrainparse;

import java.util.Arrays;

public class Client {

    private String webService;

    public Client(String webService) {
        this.webService = webService;
    }

    public Request generateRequest(String action, String... parameters) {
        return new Request(this, action, Arrays.asList(parameters));
    }

    public String getWebService() {
        return webService;
    }
}
