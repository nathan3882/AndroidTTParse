package me.nathan3882.androidttrainparse;

import android.text.Html;
import android.text.Spanned;

import java.time.DayOfWeek;

public class Util {

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
}
