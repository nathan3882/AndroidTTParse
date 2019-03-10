package me.nathan3882.responseData;

import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LessonNameRequestResponseData implements RequestResponseData {

    private JSONObject jsonObject;
    private ArrayList<String> lessonNames = null;
    private String responseJsonString;

    public LessonNameRequestResponseData(String responseJsonString) {
        this.responseJsonString = responseJsonString;
        try {
            setJsonObject(new JSONObject(responseJsonString));
            updateSubclassValues();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public ArrayList<String> getLessonNames() {
        return this.lessonNames;
    }

    @Override
    public void updateSubclassValues() {
        try {
            JSONArray array = getJsonObject().getJSONArray("lessonNames");
            lessonNames = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                lessonNames.add(array.getString(i));
            }
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
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
