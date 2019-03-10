package me.nathan3882.requestsResponses;

import android.app.Activity;
import android.os.AsyncTask;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.responseData.HasEnteredLessonsBeforeRequestResponseData;
import me.nathan3882.responseData.LessonNameRequestResponseData;
import me.nathan3882.responseData.UserdataRequestResponseData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class GetRequest extends AsyncTask<String, Integer, RequestResponse> implements AsyncContextRef<Activity> {
    private WeakReference<Activity> weakReference;
    private Client client;

    private Type action;

    private List<String> parameters;

    private RequestResponse requestResponse;
    private RequestResponseCompletionEvent requestResponseCompletionEvent;

    public GetRequest(WeakReference<Activity> weakReference, Client client, String parameter, RequestResponseCompletionEvent requestResponseCompletionEvent) {
        this.action = client.getAction();
        this.parameters = Collections.singletonList(parameter);
        this.weakReference = weakReference;
        this.client = client;
        this.requestResponseCompletionEvent = requestResponseCompletionEvent;
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        requestResponseCompletionEvent.onCompletion(requestResponse);
    }

    @Override
    public RequestResponse doInBackground(String... params) {
        publishProgress(20);
        String webService = client.getWebService() + parametersToRoute(getParameters(), false);
        RequestResponse requestResponse = null;
        try {
            System.out.println("trying with web service " + webService);
            URL url = new URL(webService); //my localhost

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            publishProgress(40);
            conn.connect();
            //Set data few lines down

            requestResponse = new RequestResponse(webService, getAction(), conn.getResponseCode(), null);

            if (requestResponse.getResponseCode() == 404) return requestResponse;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            publishProgress(60);
            StringBuilder builder = new StringBuilder();

            in.lines().forEach(builder::append);

            switch (getAction()) {
                case GET_USER_INFO:
                    requestResponse.setData(new UserdataRequestResponseData(builder.toString()));
                    break;
                case GET_LESSON_NAMES:
                    requestResponse.setData(new LessonNameRequestResponseData(builder.toString()));
                    break;
                case HAS_LESSON_NAMES:
                    requestResponse.setData(new HasEnteredLessonsBeforeRequestResponseData(builder.toString()));
                    break;
            }
            publishProgress(80);

        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(100);
        return requestResponse;
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

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }

    public enum Type {
        GET_USER_INFO("getUserInfo"),
        GET_LESSON_NAMES("getLessonNames"),
        HAS_LESSON_NAMES("hasLessonNames");

        private final String webServiceAction;

        Type(String webServiceAction) {
            this.webServiceAction = webServiceAction;
        }

        public String getWebServiceAction(boolean withTrailingFowardSlash) {
            return webServiceAction + (withTrailingFowardSlash ? "/" : "");
        }
    }
}
