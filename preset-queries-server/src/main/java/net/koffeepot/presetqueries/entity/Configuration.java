package net.koffeepot.presetqueries.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.koffeepot.presetqueries.common.TechnicalRuntimeException;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String name;

    private String factoryClass;

    @Transient
    private Map<String, Object> attributeMap;

    private String attributes;

    public String getName() {
        return name;
    }

    public String getFactoryClass() {
        return factoryClass;
    }

    protected Configuration() {}

    //Constructors are for testing purpose only
    public Configuration(String name, String factoryClass, String attributes) {
        this.name = name;
        this.factoryClass = factoryClass;
        this.attributes = attributes;
    }

    /***
     * Get an attribute String value from the json in database. The key is not case sensitive.
     * If the attribute is not found, throws a TRE! Thus, If attribute is *not* mandatory, check before its existence with isAttributeSet()
     * @param key
     * @return the attribute value, converted into String
     */
    public String getStringAttribute(String key) {
        //no need to check if attributes map is initialized, it's done in isAttributeSet.
        if (!this.isAttributeSet(key)) {
            throw new TechnicalRuntimeException("Attribute not found for "+name+" configuration: "+key);
        }

        return attributeMap.get(key).toString();
    }

    public int getIntegerAttribute(String key) {
        //no need to check if attributes map is initialized, it's done in isAttributeSet.
        if (!this.isAttributeSet(key)) {
            throw new TechnicalRuntimeException("Attribute not found for "+name+" configuration: "+key);
        }

        if (attributeMap.get(key) instanceof Integer) {
            return ((Integer)attributeMap.get(key)).intValue();
        }
        if (attributeMap.get(key) instanceof String) {
            return Integer.parseInt((String) attributeMap.get(key));
        }
        throw new TechnicalRuntimeException("Attribute "+key+" value is not an integer in "+name+"configuration: "+attributeMap.get(key));
    }

    public String getStringAttribute(String key, String defaultValue) {
        if (!this.isAttributeSet(key)) {
            return defaultValue;
        }

        return attributeMap.get(key).toString();
    }

    public boolean isAttributeSet(String key) {
        this.initializeAttributeMap();
        return attributeMap.containsKey(key);
    }

    /***
     * Initialize attributes Map if needed, or do nothing if already initialized.
     * We copy it into a TreeMap. Note that only the first level of attributes key will thus be case insensitive
     */
    private void initializeAttributeMap() {
        if (attributeMap == null) {
            if (attributes == null) {
                attributeMap = new HashMap<>();
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                //noinspection unchecked
                HashMap<String, String> caseSensitiveMap = mapper.readValue(attributes, HashMap.class);
                attributeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
                attributeMap.putAll(caseSensitiveMap);
            } catch (IOException ioe) {
                throw new TechnicalRuntimeException(ioe);
            }
        }
    }
}
