package net.koffeepot.presetqueries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.koffeepot.presetqueries.service.ParameterType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private String options;

    @JsonIgnore
    @ManyToMany(mappedBy = "parameters")
    private Set<Query> queries;

    protected Parameter() {}

    //Constructors are only for tests
    public Parameter(String name, ParameterType type, String options) {
        this.type = type;
        this.name = name;
        this.options = options;
        this.queries = new HashSet<>();
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

    public String getOptions() {
        return options;
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

    public void setOptions(String options) {
        this.options = options;
    }

    public void update(Parameter param) {
        setName(param.getName());
        setType(param.getType());
        setOptions(param.getOptions());
    }

    public Set<Query> getQueries() {
        return queries;
    }
}
