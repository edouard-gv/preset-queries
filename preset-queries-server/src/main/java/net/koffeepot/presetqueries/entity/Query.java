package net.koffeepot.presetqueries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;
    private String template;
    private String source;

    @ManyToMany
    @JoinTable(name="query_parameter",
            joinColumns=
                @JoinColumn(name="query_id", referencedColumnName="id"),
            inverseJoinColumns=
                @JoinColumn(name="parameter_id", referencedColumnName="id")
    )
    private Set<Parameter> parameters;

    //Constructors are only used by test classes.
    public Query(String name, String description, String source, String template) {
        this(name);
        this.description = description;
        this.source = source;
        this.template = template;
    }

    public Query(String name) {
        this.name = name;
        parameters = new HashSet<>();
    }

    public Query() {
        //just there, need by Jackson library, used for testing
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    @JsonIgnore
    public String getTemplate() {
        return template;
    }

    public String getSource() {
        return source;
    }


}
