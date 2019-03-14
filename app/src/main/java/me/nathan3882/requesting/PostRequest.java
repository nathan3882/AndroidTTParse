package me.nathan3882.requesting;

import android.os.AsyncTask;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.responding.RequestResponse;
import me.nathan3882.responding.ResponseEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PostRequest extends AsyncTask<String, Integer, RequestResponse> {

    private final List<KeyObjectPair> body;
    private final String parameter;
    private Client client;
    private Action action;
    private RequestResponse requestResponse;
    private ResponseEvent responseEvent;
    private int progress = 0;

    public PostRequest(Client client, String parameter, List<KeyObjectPair> body, ResponseEvent responseEvent) {
        this.action = client.getAction();
        this.parameter = parameter;
        this.body = body;
        this.client = client;
        this.responseEvent = responseEvent;
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        System.out.println("response code = " + requestResponse.getResponseCode());
        if (requestResponse != null && requestResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
            responseEvent.onCompletion(requestResponse);
        } else {
            responseEvent.onFailure();
        }
        responseEvent.doFinally();
    }

    //So many publish progresses to make it seem more of a gradual thing, not just instantly done
    @Override
    public RequestResponse doInBackground(String... params) {

        //Set data few lines down\
        publishProgress(20);

        HttpURLConnection connection = null;
        int responseCode;
        try {
            connection = (HttpURLConnection) new URL(client.getWebService() + parameter).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            publishProgress(40);

            JSONObject data = new JSONObject();

            getBody().forEach(pair -> {
                try {
                    data.put(pair.getKey(), pair.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(data.toString());
            writer.close();

            connection.connect();

            responseCode = connection.getResponseCode();

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
            return requestResponse;
        }
            publishProgress(60);

            RequestResponse requestResponse = new RequestResponse(
                    client.getWebService(),
                    client.getAction(),
                    responseCode, null);
        publishProgress(100);
        return requestResponse;
    }

    private Action getAction() {
        return action;
    }

    public List<KeyObjectPair> getBody() {
        return body;
    }
}
