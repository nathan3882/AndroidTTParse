package me.nathan3882.requestResponses;

import android.support.annotation.Nullable;
import org.json.JSONObject;

public interface RequestResponseData {

    void updateSubclassValues();

    public String getResponseString();

    @Nullable
    JSONObject getJsonObject();

    void setJsonObject(JSONObject jsonObject);

    String toString();
}
