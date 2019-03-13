package me.nathan3882.requesting;

import android.os.AsyncTask;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.responding.RequestResponse;
import me.nathan3882.responding.ResponseEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class PostRequest extends AsyncTask<String, Integer, RequestResponse> {

    private final List<KeyObjectPair> body;
    private Client client;
    private Action action;
    private RequestResponse requestResponse;
    private ResponseEvent responseEvent;
    private int progress = 0;

    public PostRequest(Client client, List<KeyObjectPair> body, ResponseEvent responseEvent) {
        this.action = client.getAction();
        this.body = body;
        this.client = client;
        this.responseEvent = responseEvent;
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        this.requestResponse = requestResponse;
        if (requestResponse != null) {
            responseEvent.onCompletion(requestResponse);
        } else {
            responseEvent.onFailure();
        }
    }

    //So many publish progresses to make it seem more of a gradual thing, not just instantly done
    @Override
    public RequestResponse doInBackground(String... params) {

        //Set data few lines down\
        publishProgress(20);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(client.getWebService());
        publishProgress(40);
        JSONObject data = new JSONObject();

        getBody().forEach(pair -> {
            try {
                data.put(pair.getKey(), pair.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        publishProgress(60);
        post.setEntity(new StringEntity(data.toString(), ContentType.APPLICATION_JSON));
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(80);

        RequestResponse requestResponse = new RequestResponse(
                client.getWebService(),
                client.getAction(),
                response.getStatusLine().getStatusCode(), null);

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
