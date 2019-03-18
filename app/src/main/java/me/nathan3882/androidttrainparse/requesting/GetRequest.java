package me.nathan3882.androidttrainparse.requesting;

import android.os.AsyncTask;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.androidttrainparse.responding.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class GetRequest extends AsyncTask<String, Integer, RequestResponse> {
    private Client client;

    private Action action;

    private List<String> parameters;

    private RequestResponse requestResponse;
    private ResponseEvent responseEvent;
    private int progress = 0;

    public GetRequest(Client client, String parameter, ResponseEvent responseEvent) {
        this.action = client.getAction();
        this.parameters = Collections.singletonList(parameter);
        this.client = client;
        this.responseEvent = responseEvent;
    }

    //So many publish progresses to make it seem more of a gradual thing, not just instantly done
    @Override
    public RequestResponse doInBackground(String... params) {
        String webService = client.getWebService() + parametersToRoute(getParameters(), false);
        RequestResponse requestResponse = null;
        try {
            System.out.println("trying with web service " + webService);
            URL url = new URL(webService); //my localhost

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            publishProgress(15);
            conn.connect();
            publishProgress(45);
            //Set data few lines down

            requestResponse = new RequestResponse(webService, getAction(), conn.getResponseCode(), null);

            if (requestResponse.getResponseCode() == 404) return requestResponse;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();

            in.lines().forEach(builder::append);

            publishProgress(70);

            String builderToString = builder.toString();
            switch (getAction()) {
                case GET_USER_INFO:
                    requestResponse.setData(new UserdataRequestResponseData(builderToString));
                    break;
                case GET_USER_LESSON_NAMES:
                    requestResponse.setData(new LessonNameRequestResponseData(builderToString));
                    break;
                case GET_USER_HAS_LESSON_NAMES:
                    requestResponse.setData(new HasLessonNamesRequestResponseData(builderToString));
                    break;
                case GET_OCR_STRING:
                    requestResponse.setData(new OcrRequestResponseData(builderToString));
                    break;
            }
            publishProgress(80);

        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(100);
        return requestResponse;
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        if (requestResponse != null) {
            responseEvent.onCompletion(requestResponse);
        } else {
            responseEvent.onFailure();
        }
        responseEvent.doFinally();
    }

    private String parametersToRoute(List<String> params, boolean trailingSlash) {
        String route = "";
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            route += param + (trailingSlash ? "/" : (i == params.size() - 1 ? "" : "/")); //trailing slash or not
        }
        return route;
    }

    private Action getAction() {
        return action;
    }

    private List<String> getParameters() {
        return parameters;
    }

}
