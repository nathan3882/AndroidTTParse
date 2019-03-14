package me.nathan3882.requesting;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import me.nathan3882.activities.ProgressBarable;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.responding.RequestResponse;
import me.nathan3882.responding.ResponseEvent;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProgressedPostRequest extends PostRequest implements ProgressBarable, IActivityReferencer<Activity> {

    private final WeakReference<Activity> weakReference;
    private final String parameter;
    private final int barId;
    private final Client client;

    public ProgressedPostRequest(WeakReference<Activity> weakReference, int barId,
                                 Client client, String parameter, List<KeyObjectPair> body, ResponseEvent responseEvent) {
        super(client, parameter, body, responseEvent);
        this.barId = barId;
        this.client = client;
        this.parameter = parameter;
        this.weakReference = weakReference;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        ProgressBar bar = getProgressBar(getReferenceValue(), getProgressBarRid());
        if (bar != null) {
            if (bar.getVisibility() == View.INVISIBLE || bar.getVisibility() == View.GONE) {
                bar.setVisibility(View.VISIBLE);
                bar.setProgress(values[0]);
            }
        }
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        super.onPostExecute(requestResponse);
        ProgressBar bar = getProgressBar(getReferenceValue(), getProgressBarRid());
        if (bar != null && bar.getVisibility() == View.VISIBLE) {
            bar.setVisibility(View.INVISIBLE);
            bar.setProgress(0);
        }
    }

    @Override
    public int getProgressBarRid() {
        return barId;
    }

    public String getParameter() {
        return parameter;
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return this.weakReference;
    }
}
