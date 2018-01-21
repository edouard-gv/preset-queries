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

    public Query() {
        //just there, need by Hibernate (and Jackson library, used for testing)
    }

    //Constructors are only used by test classes.
    public Query(Long id, String name, String description, String source, String template) {
        this(id);
        this.name = name;
        this.description = description;
        this.source = source;
        this.template = template;
    }

    public Query(Long id) {
        this.id = id;
        parameters = new HashSet<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getId() { return id; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public String getTemplate() {
        return template;
    }

    public String getSource() {
        return source;
    }


}
