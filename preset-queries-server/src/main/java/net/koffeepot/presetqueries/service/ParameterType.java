package net.koffeepot.presetqueries.service;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ParameterType {
    FROM("From clause"),
    WHERE("Where clause"),
    WHERE_OPTIONAL("Where clause with optional fragment", "Optional fragment", "will be added when parameter is not null");

    private String label;
    private String parametersLabel;
    private String hint;

    ParameterType(String label, String parametersLabel, String hint) {
        this(label);
        this.parametersLabel = parametersLabel;
        this.hint = hint;
    }

    ParameterType(String label) {
        this.label = label;
    }

    //getters for json representation used by jackson Object shape formatting
    public String getLabel() {
        return label;
    }

    public String getParametersLabel() {
        return parametersLabel;
    }

    public String getHint() {
        return hint;
    }

    public String getName() {
        return this.name();
    }
}
