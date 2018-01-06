package net.koffeepot.presetqueries.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Query {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Column(unique=true)
    private String name;

    private String description;

    public Query(String name, String description) {
        this.name = name;
        this.description = description;
        parameters = new HashSet<>();
    }

    public Query() {
        //just there, need by Jackson library
    }

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Parameter> parameters;

    public Set<Parameter> getParameters() {
        return parameters;
    }
}
