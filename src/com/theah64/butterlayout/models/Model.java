package com.theah64.butterlayout.models;

import java.util.List;

/**
 * Created by theapache64 on 6/1/18.
 */
public class Model {
    private final String name;
    private final List<Property> properties;

    Model(String name, List<Property> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public static class Property {
        private final String dataType, variableName;

        public Property(String dataType, String variableName) {
            this.dataType = dataType;
            this.variableName = variableName;
        }

        public String getDataType() {
            return dataType;
        }

        public String getVariableName() {
            return variableName;
        }
    }
}


