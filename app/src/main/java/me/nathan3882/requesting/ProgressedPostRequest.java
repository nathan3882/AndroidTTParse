package me.nathan3882.requesting;

import android.app.Activity;
import android.widget.ProgressBar;
import me.nathan3882.activities.ProgressBarable;
import me.nathan3882.androidttrainparse.Client;
import me.nathan3882.responding.ResponseEvent;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProgressedPostRequest extends PostRequest implements ProgressBarable {

    private final WeakReference<Activity> weakReference;
    private final String parameter;
    private final ProgressBarable barable;

    public ProgressedPostRequest(WeakReference<Activity> weakReference, ProgressBarable barable,
                                 Client client, String parameter, List<KeyObjectPair> body, ResponseEvent responseEvent) {
        super(client, body, responseEvent);
        this.barable = barable;
        this.parameter = parameter;
        this.weakReference = weakReference;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    public ProgressBar getProgressBar() {
        return barable.getProgressBar();
    }

    @Override
    public int getProgressBarRid() {
        return barable.getProgressBarRid();
    }

    public String getParameter() {
        return parameter;
    }
}
