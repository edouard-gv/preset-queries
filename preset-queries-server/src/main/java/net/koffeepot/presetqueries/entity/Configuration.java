package net.koffeepot.presetqueries.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    String name;

    String factoryClass;

    @Transient
    Map parametersMap;

    String parameters;

    public String getName() {
        return name;
    }

    public String getFactoryClass() {
        return factoryClass;
    }

    //Constructors are for testing purpose only
    public Configuration(String name, String factoryClass, String parameters) {
        this.name = name;
        this.factoryClass = factoryClass;
        this.parameters = parameters;
    }

    /***
     * Get a parameter from json in database. If parameter not found, throws a TRE!
     * If parameter *not* mandatory, check before its existence with isParameterSet()
     * @param key
     * @return
     */
    public String getStringParameter(String key) {
        return null;
    }

    public boolean isParameterSet() {
        return false;
    }
}
