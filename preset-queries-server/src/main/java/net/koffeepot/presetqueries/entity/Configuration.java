package net.koffeepot.presetqueries.entity;

import javax.persistence.*;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    String name;

    String factoryClass;

    public String getName() {
        return name;
    }

    public String getFactoryClass() {
        return factoryClass;
    }

    //Constructors are for testing purpose only
    public Configuration(String name, String factoryClass) {
        this.name = name;
        this.factoryClass = factoryClass;
    }
}
