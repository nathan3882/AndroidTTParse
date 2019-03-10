package me.nathan3882.requestsResponses;

import java.lang.ref.WeakReference;

public interface AsyncContextRef<T> {


    WeakReference<T> getWeakReference();

}
