package net.koffeepot.presetqueries.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ParameterType {
    FROM(false),
    FROM_LIST(true),
    WHERE(false),
    WHERE_LIST(true),
    WHERE_OPTIONAL(true);

    private boolean isParameterized;

    ParameterType(boolean isParameterized) {
        this.isParameterized = isParameterized;
    }

    public boolean isParameterized() {
        return isParameterized;
    }

    public static class ParameterTypePOJO {

        public String getName() {
            return name;
        }

        public boolean getIsParameterized() {
            return isParameterized;
        }

        private String name;
        private boolean isParameterized;

        private ParameterTypePOJO(ParameterType type) {
            this.name = type.name();
            this.isParameterized = type.isParameterized();
        }

        @JsonIgnore
        public static List<ParameterTypePOJO> buildList() {
            return Arrays.stream(ParameterType.values()).map(parameterType -> new ParameterTypePOJO(parameterType)).collect(Collectors.toList());
        }

        //For Jackson serialization
        public ParameterTypePOJO() {
            super();
        }
    }
}
