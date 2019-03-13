package me.nathan3882.requesting;

public class KeyObjectPair {

    private final Object value;
    private final String key;

    public KeyObjectPair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
