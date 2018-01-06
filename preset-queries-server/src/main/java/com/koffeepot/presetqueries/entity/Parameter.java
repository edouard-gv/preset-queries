package com.koffeepot.presetqueries.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Parameter {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private String label;
    private String type;

    protected Parameter() {}

    public Parameter(String label, String type) {
        this.type = type;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

}
