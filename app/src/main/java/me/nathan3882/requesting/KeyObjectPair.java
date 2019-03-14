package me.nathan3882.requesting;

public class KeyObjectPair {

    private final String key;
    private final Object value;

    public KeyObjectPair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
