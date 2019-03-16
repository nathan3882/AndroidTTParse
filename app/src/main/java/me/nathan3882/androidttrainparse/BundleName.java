package me.nathan3882.androidttrainparse;

public enum BundleName {

    EMAIL("email"),
    IS_UPDATING("isUpdating"),
    HOME_CRS("homeCrs"),
    LESSONS("lessons"),
    DAYS_TO_SHOW("showTheseDays"),
    USER_LESSONS_POPULATED("userLessonsPopulated");

    private final String string;

    BundleName(String string) {
        this.string = string;
    }

    public String asString() {
        return string;
    }

}
