package me.nathan3882.androidttrainparse.activities;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

@FunctionalInterface
public interface ProgressBarable {

    @Nullable
    default ProgressBar getProgressBar(Activity activity, int id) {
        return activity.findViewById(id);
    }

    int getProgressBarRid();

    default void doProgress(ProgressBar bar, int newProgress) {
        if (bar != null) {
            if (bar.getVisibility() == View.INVISIBLE || bar.getVisibility() == View.GONE) {
                bar.setVisibility(View.VISIBLE);
            }
            bar.setProgress(newProgress);
        }
    }

}
