package com.theah64.butterlayout.models;

import java.util.List;

/**
 * Created by theapache64 on 6/1/18.
 */
public class View {

    final String id, type;
    final List<Property> properties;

    public View(String id, String type, List<Property> properties) {
        this.id = id;
        this.type = type;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "View{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static class Property {

        private final String key, value;

        public Property(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Property{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

}
