package me.nathan3882.androidttrainparse;

import android.support.annotation.Nullable;

public enum BundleName {

    EMAIL("email"),
    HOME_CRS("homeCrs"),
    LESSONS("lessons"),
    DAYS_TO_SHOW("showTheseDays"),
    USER_LESSONS_POPULATED("userLessonsPopulated"),
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    HEADER_NO_HTML("headerNoHtml"),
    DAY_OF_WEEK_TO_SHOW("dayOfWeekToShow");

    private final String string;

    BundleName(String string) {
        this.string = string;
    }

    @Nullable
    public static BundleName getByValue(String value) {
        try {
            return BundleName.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String asString() {
        return string;
    }
}
