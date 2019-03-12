package me.nathan3882.requestsResponses;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.testingapp.R;

import java.lang.ref.WeakReference;

public class ProgressedGetRequest extends GetRequest {

    private final WeakReference<Activity> weakReference;

    public ProgressedGetRequest(WeakReference<Activity> weakReference, Client client, String parameter, ResponseEvent responseEvent) {
        super(client, parameter, responseEvent);
        System.out.println("new progressed get request " + client.getWebService() + parameter);
        this.weakReference = weakReference;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Activity reference = getWeakReference().get();
        if (reference != null) {
            ProgressBar bar = reference.findViewById(R.id.mainActivityProgressBar);
            if (bar.getVisibility() == View.INVISIBLE || bar.getVisibility() == View.GONE) {
                bar.setVisibility(View.VISIBLE);
            }
            bar.setProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        super.onPostExecute(requestResponse);
        Activity reference = getWeakReference().get();
        if (getWeakReference().get() != null) {
            ProgressBar bar = reference.findViewById(R.id.mainActivityProgressBar);
            bar.setVisibility(View.INVISIBLE);
        }
    }

    public WeakReference<Activity> getWeakReference() {
        return weakReference;
    }
}