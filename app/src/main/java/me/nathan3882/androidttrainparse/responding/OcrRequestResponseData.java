package me.nathan3882.androidttrainparse.responding;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class OcrRequestResponseData implements RequestResponseData {

    private JSONObject jsonObject;
    private String depletedOcrString = "";
    private String responseString;

    public OcrRequestResponseData(String responseString) {
        this.responseString = responseString;
        try {
            this.jsonObject = new JSONObject(responseString);
            updateSubclassValues();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSubclassValues() {
        try {
            this.depletedOcrString = jsonObject.getString("ocrString");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDepletedOcrString() {
        return depletedOcrString;
    }

    @Override
    public String getResponseString() {
        return this.responseString;
    }

    @Nullable
    @Override
    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {

    }
}
