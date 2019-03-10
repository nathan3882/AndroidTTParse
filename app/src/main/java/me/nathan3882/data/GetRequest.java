package me.nathan3882.data;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.requestResponses.HasEnteredLessonsBeforeRequestResponseData;
import me.nathan3882.requestResponses.LessonNameRequestResponseData;
import me.nathan3882.requestResponses.UserdataRequestResponseData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class GetRequest extends AsyncTask<String, Integer, RequestResponse> {
    private final Client client;

    private Type action;

    private List<String> parameters;

    private int progress = 0;
    private RequestResponse requestResponse;
    private ProgressBar bar = null;
    private RequestResponseCompletionEvent requestResponseCompletionEvent;

    public GetRequest(Client client, String parameter, RequestResponseCompletionEvent requestResponseCompletionEvent) {
        this.action = client.getAction();
        this.parameters = Collections.singletonList(parameter);
        this.client = client;
        this.requestResponseCompletionEvent = requestResponseCompletionEvent;
    }

    public GetRequest supplementWithProgressBar(ProgressBar bar) {
        this.bar = bar;
        return this;
    }

    @Nullable
    public ProgressBar getBar() {
        return bar;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        this.progress = progress[0];
        if (getBar() != null) getBar().setProgress(this.progress);
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        System.out.println("RESSY = " + requestResponse);
        requestResponseCompletionEvent.onCompletion(requestResponse);

        if (getBar() != null) getBar().setVisibility(View.INVISIBLE);
    }

    @Override
    public RequestResponse doInBackground(String... params) {
        publishProgress(35);
        String webService = client.getWebService() + parametersToRoute(getParameters(), false);
        RequestResponse requestResponse = null;
        try {
            System.out.println("trying with web service " + webService);
            URL url = new URL(webService); //my localhost

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            //Set data few lines down

            requestResponse = new RequestResponse(webService, getAction(), conn.getResponseCode(), null);

            if (requestResponse.getResponseCode() == 404) return requestResponse;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();

            in.lines().forEach(builder::append);

            publishProgress(70);
            switch (getAction()) {
                case GET_USER_INFO:
                    requestResponse.setData(new UserdataRequestResponseData(builder.toString()));
                    break;
                case GET_LESSON_NAMES:
                    requestResponse.setData(new LessonNameRequestResponseData(builder.toString()));
                    break;
                case HAS_ENTERED_LESSONS_BEFORE:
                    requestResponse.setData(new HasEnteredLessonsBeforeRequestResponseData(builder.toString()));
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(100);
        return requestResponse;
    }

    public int getProgress() {
        return progress;
    }

    public RequestResponse getRequestResponse() {
        return requestResponse;
    }

    private String parametersToRoute(List<String> params, boolean trailingSlash) {
        String route = "";
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            route += param + (trailingSlash ? "/" : (i == params.size() - 1 ? "" : "/")); //trailing slash or not
        }
        return route;
    }

    private Type getAction() {
        return action;
    }

    private List<String> getParameters() {
        return parameters;
    }

    public enum Type {
        GET_USER_INFO("getUserInfo"),
        GET_LESSON_NAMES("getLessonNames"),
        HAS_ENTERED_LESSONS_BEFORE("hasEnteredLessonsBefore");

        private final String webServiceAction;

        Type(String webServiceAction) {
            this.webServiceAction = webServiceAction;
        }

        public String getWebServiceAction(boolean withTrailingFowardSlash) {
            return webServiceAction + (withTrailingFowardSlash ? "/" : "");
        }
    }
}
