package me.nathan3882.androidttrainparse;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spanned;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.time.DayOfWeek;

public class Util {

    public static final String LESSON_STORAGE_SPLIT_CHAR = ", ";
    public static final String DEFAULT_TTRAINPARSE = "http://nathan3882.me/api/apps/ttrainparse/";
    public static final long ONE_HALF_SECS = 1500L;
    private static final String TOKEN = "aToken";
    public static final String PARAMS = "?format=json&token=" + TOKEN;

    public static Spanned html(String string) {
        return Html.fromHtml(string);
    }

    public static void addToArray(String[] array, String val, boolean fill) {
        int length = getArrayLength(array);
        int refIndex = length + 1;
        if (fill) {
            int index = refIndex;
            for (int i = 0; i < length; i++) {
                if (array[i] == null) {
                    index = i;
                    break;
                }
            }
            array[index] = val;
            return;
        }
        array[refIndex] = val;
    }

    public static String upperFirst(DayOfWeek dayOfWeek) {
        String string = dayOfWeek.name();
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static int getArrayLength(String[] array) {
        int length = 0;
        for (String s : array) {
            if (s != null) {
                length++;
            }
        }
        return length;
    }

    public static void updateProgress(Activity reference, ProgressBar bar, Integer progress) {
        bar.setProgress(progress);
        dimBackground(reference.getWindow().getAttributes(), 0.75f);
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void dimBackground(WindowManager.LayoutParams wp, float dimAmount) {
        wp.dimAmount = dimAmount;
    }
}
