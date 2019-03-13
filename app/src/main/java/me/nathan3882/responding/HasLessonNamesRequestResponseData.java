package me.nathan3882.responding;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class HasLessonNamesRequestResponseData implements RequestResponseData {


    private JSONObject jsonObject;
    private boolean hasEnteredLessonsBefore;
    private String response;

    public HasLessonNamesRequestResponseData(String response) {
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
            this.hasEnteredLessonsBefore = getJsonObject().getBoolean("hasLessonNames");
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
