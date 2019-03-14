package me.nathan3882.responding;

import android.support.annotation.NonNull;

public interface ResponseEvent {

    default void onCompletion(@NonNull RequestResponse requestResponse) {

    }
    default void onFailure() {

    }
    default void doFinally() {

    }

}
