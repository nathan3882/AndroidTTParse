package me.nathan3882.androidttrainparse.requesting;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import me.nathan3882.androidttrainparse.activities.ProgressBarable;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.androidttrainparse.responding.RequestResponse;
import me.nathan3882.androidttrainparse.responding.ResponseEvent;

import java.lang.ref.WeakReference;

public class ProgressedGetRequest extends GetRequest implements ProgressBarable, IActivityReferencer<Activity> {

    private final WeakReference<Activity> weakReference;
    private final int progressBarId;

    public ProgressedGetRequest(WeakReference<Activity> weakReference, int progressBarId, Client client, String parameter, ResponseEvent responseEvent) {
        super(client, parameter, responseEvent);
        this.weakReference = weakReference;
        this.progressBarId = progressBarId;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Activity reference = getReferenceValue();
        if (reference != null) {
            ProgressBar bar = getProgressBar(reference, getProgressBarRid());
            doProgress(bar, progress[0]);
        }
    }

    @Override
    protected void onPostExecute(RequestResponse requestResponse) {
        super.onPostExecute(requestResponse);
        Activity reference = getReferenceValue();
        if (reference != null) {
            ProgressBar bar = getProgressBar(getReferenceValue(), getProgressBarRid());
            if (bar != null) {
                bar.setVisibility(View.INVISIBLE);
                bar.setProgress(0);
            }
        }
    }

    @Override
    public WeakReference<Activity> getWeakReference() {
        return weakReference;
    }

    @Override
    public int getProgressBarRid() {
        return progressBarId;
    }
}