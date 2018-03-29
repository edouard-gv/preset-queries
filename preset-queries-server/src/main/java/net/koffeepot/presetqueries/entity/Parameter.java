package net.koffeepot.presetqueries.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.koffeepot.presetqueries.service.ParameterType;

import javax.persistence.*;

@Entity
public class Parameter {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ParameterType type;
    @JsonInclude()
    @Transient
    private String userValue;
    private String optionalFragment;

    protected Parameter() {}

    //Constructors are only for tests
    public Parameter(String name, ParameterType type, String optionalFragment) {
        this.type = type;
        this.name = name;
        this.optionalFragment = optionalFragment;
    }

    public Parameter(String name, ParameterType type){
        this(name, type, null);
    }

    public Parameter(String name){
        this(name, null, null);
    }

    public String getUserValue() {
        return userValue;
    }

    public String getName() {
        return name;
    }

    public ParameterType getType() {
        return type;
    }

    public String getOptionalFragment() {
        return optionalFragment;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public void setOptionalFragment(String optionalFragment) {
        this.optionalFragment = optionalFragment;
    }

    public void update(Parameter param) {
        setName(param.getName());
        setType(param.getType());
        setOptionalFragment(param.getOptionalFragment());
    }
}
