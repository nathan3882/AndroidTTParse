package me.nathan3882.requestsResponses;

import android.support.annotation.NonNull;

public interface ResponseEvent {

    void onCompletion(@NonNull RequestResponse requestResponse);
    void onFailure();

}
