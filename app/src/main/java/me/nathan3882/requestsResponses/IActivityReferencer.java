package me.nathan3882.requestsResponses;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

@FunctionalInterface
public interface IActivityReferencer<T> {

    WeakReference<T> getWeakReference();

    @Nullable
    default WeakReference<T> getFrom(Object object, Class clazz) {
        try {
            return new WeakReference<T>((T) object);
        }catch(ClassCastException e) {
            return null;
        }
    }

}
