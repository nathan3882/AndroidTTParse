package me.nathan3882.androidttrainparse.responding;

import android.support.annotation.NonNull;

public interface ResponseEvent {

    default void onCompletion(@NonNull RequestResponse requestResponse) {

    }
    default void onFailure() {

    }
    default void doFinally() {

    }

    default void onPreExecute() {

    }
}
