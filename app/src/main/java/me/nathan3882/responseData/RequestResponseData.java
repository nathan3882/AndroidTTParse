package me.nathan3882.responseData;

import android.support.annotation.Nullable;
import org.json.JSONObject;

public interface RequestResponseData {

    void updateSubclassValues();

    String getResponseString();

    @Nullable
    JSONObject getJsonObject();

    void setJsonObject(JSONObject jsonObject);

    String toString();
}
