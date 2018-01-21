package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

}
