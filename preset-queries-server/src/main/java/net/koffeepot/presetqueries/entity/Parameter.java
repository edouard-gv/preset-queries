package net.koffeepot.presetqueries.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;

@Entity
public class Parameter {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String type;
    @JsonInclude()
    @Transient
    private String userValue;
    private String optionalFragment;

    protected Parameter() {}

    //Constructors are only for tests
    public Parameter(String name, String type, String optionalFragment) {
        this.type = type;
        this.name = name;
        this.optionalFragment = optionalFragment;
    }

    public Parameter(String name, String type){
        this(name, type, null);
    }

    public Parameter(String name){
        this(name, null, null);
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOptionalFragment() {
        return optionalFragment;
    }



}
