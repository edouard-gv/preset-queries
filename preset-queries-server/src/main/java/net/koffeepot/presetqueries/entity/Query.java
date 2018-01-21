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

    @ManyToOne
    private Configuration configuration;

    @Column(name="configuration_id", updatable=false, insertable=false)
    private Long configurationId;

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
    public Query(Long id, String name, String description, Configuration configuration, String template) {
        this(id);
        this.name = name;
        this.description = description;
        this.configuration = configuration;
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


    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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

    @JsonIgnore
    public Configuration getConfiguration() { return configuration; }

    public Long getConfigurationId() { return configurationId; }

    //For JSON
    public String getSource() {
        return (configuration == null ? null : configuration.getName());
    }

}
