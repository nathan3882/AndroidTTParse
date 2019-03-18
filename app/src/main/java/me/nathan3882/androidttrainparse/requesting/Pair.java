package me.nathan3882.androidttrainparse.requesting;

import me.nathan3882.androidttrainparse.responding.ResponseEvent;

public class Pair {

    protected Object key;
    protected Object value;

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public class BooleanResponseEventPair extends Pair {

        public BooleanResponseEventPair(Boolean key, ResponseEvent value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public ResponseEvent getValue() {
            return (ResponseEvent) value;
        }

        @Override
        public Boolean getKey() {
            return (Boolean) key;
        }
    }

    public class KeyObjectPair extends Pair {

        public KeyObjectPair(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return (String) super.key;
        }

        @Override
        public Object getValue() {
            return super.value;
        }
    }


}
