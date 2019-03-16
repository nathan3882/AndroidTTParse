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

    private final List<Pair.KeyObjectPair> body;
    private final String parameter;
    private Client client;
    private Action action;
    private RequestResponse requestResponse;
    private ResponseEvent responseEvent;
    private int progress = 0;

    public PostRequest(Client client, String parameter, List<Pair.KeyObjectPair> body, ResponseEvent responseEvent) {
        this.action = client.getAction();
        this.parameter = parameter;
        this.body = body;
        this.client = client;
        this.responseEvent = responseEvent;
    }

    public List<Pair.KeyObjectPair> getBody() {
        return body;
    }

    //So many publish progresses to make it seem more of a gradual thing, not just instantly done
    @Override
    public RequestResponse doInBackground(String... params) {

        //Set data few lines down\
        publishProgress(20);

        int responseCode = 500;

        String webService = client.getWebService() + parameter;

        System.out.println("trying with web service " + webService);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(webService).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            publishProgress(40);

            JSONObject data = new JSONObject();

            getBody().forEach(pair -> {
                try {
                    data.put(pair.getKey(), pair.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            publishProgress(50);

            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
            writer.writeBytes(data.toString());
            writer.close();
            publishProgress(60);

            connection.connect();

            publishProgress(70);
            responseCode = connection.getResponseCode();

            connection.disconnect();
            publishProgress(80);

            requestResponse = new RequestResponse(
                    client.getWebService(),
                    client.getAction(),
                    responseCode, null);
            publishProgress(90);
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

    private Action getAction() {
        return action;
    }
}
