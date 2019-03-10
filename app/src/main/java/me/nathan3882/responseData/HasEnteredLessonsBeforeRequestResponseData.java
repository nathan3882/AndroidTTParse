package me.nathan3882.responseData;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class HasEnteredLessonsBeforeRequestResponseData implements RequestResponseData {


    private JSONObject jsonObject;
    private boolean hasEnteredLessonsBefore;
    private String response;

    public HasEnteredLessonsBeforeRequestResponseData(String response) {
        this.response = response;
        try {
            setJsonObject(new JSONObject(response));
            updateSubclassValues();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean hasEnteredLessonsBefore() {
        return hasEnteredLessonsBefore;
    }

    @Override
    public void updateSubclassValues() {
        try {
            this.hasEnteredLessonsBefore = getJsonObject().getBoolean("hasEnteredLessonsBefore");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getResponseString() {
        return this.response;
    }

    @Nullable
    @Override
    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
this.jsonObject = jsonObject;
    }
}
