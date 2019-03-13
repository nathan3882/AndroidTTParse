package me.nathan3882.requesting;

public enum Action {
    GET_USER_INFO("getUserInfo"),
    GET_USER_LESSON_NAMES("getLessonNames"),
    GET_USER_HAS_LESSON_NAMES("hasLessonNames"),

    POST_ADD_USER_LESSON("addLesson"),
    POST_REMOVE_USER_LESSON("removeLesson");

    private final String webServiceAction;

    Action(String webServiceAction) {
        this.webServiceAction = webServiceAction;
    }

    public String getWebServiceAction(boolean withTrailingFowardSlash) {
        return webServiceAction + (withTrailingFowardSlash ? "/" : "");
    }
}
