package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

    public List<Configuration> findByName(String name);

    public default Configuration findConfigurationByName(String name) {
        List<Configuration> configurations = this.findByName(name);
        if (configurations.isEmpty())
            return null;

        if (configurations.size()>1)
            throw new TechnicalRuntimeException("Found "+ configurations.size()+" configurations for name "+name);

        return configurations.get(0);
    }
}
