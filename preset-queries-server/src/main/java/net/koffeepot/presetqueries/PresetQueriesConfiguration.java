package net.koffeepot.presetqueries;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SpringBootApplication
public class PresetQueriesConfiguration {

	//name of the http header to grab the token from
    public static final String KOFFE_POT_TOKEN = "KoffePot-Token";

    //use RoleRepository configured in properties, or the one which authorized everyone by default
    @Value("${role-repository-class:net.koffeepot.presetqueries.repository.EmptyRoleRepositoryImpl}")
	String roleRepositoryClassName;

	@Bean
	public RoleRepository roleRepository() {
		Class<RoleRepository> clazz;
		Constructor<RoleRepository> ctor;
		try {
			clazz = (Class<RoleRepository>) Class.forName(roleRepositoryClassName);
		} catch (ClassNotFoundException e) {
			throw new TechnicalRuntimeException(
					"Repository class does not exist: "+roleRepositoryClassName);
		}
		try {
			ctor = clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new TechnicalRuntimeException(
					"Repository class must implement constructor with Configuration: "+roleRepositoryClassName);
		}
		try {
			return ctor.newInstance(new Object[]{});
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e ) {
			throw new TechnicalRuntimeException(e);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(PresetQueriesConfiguration.class, args);
	}
}

