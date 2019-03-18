package me.nathan3882.androidttrainparse.responding;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class LessonNameRequestResponseData implements RequestResponseData {

    private JSONObject jsonObject;
    private ArrayList<String> lessonNames = new ArrayList<>();
    private String responseJsonString;

    public LessonNameRequestResponseData(String responseJsonString) {
        this.responseJsonString = responseJsonString;
        try {
            setJsonObject(new JSONObject(responseJsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateSubclassValues();
    }

    @Nullable
    public ArrayList<String> getLessonNames() {
        return this.lessonNames;
    }

    @Override
    public void updateSubclassValues() {
        if (getJsonObject() != null) {
            String[] lessonNames = new String[0];
            try {
                lessonNames = getJsonObject().getString("lessonNames").split(", ");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.lessonNames.addAll(Arrays.asList(lessonNames));
        }
    }

    @Override
    public String getResponseString() {
        return this.responseJsonString;
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
