package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/* TODO : pour être indépendant du type de base, par exemple pouvoir se brancher sur de l'ES, être indépendant de java.sql.DataSource
   et pouvoir par exemple renvoyer un méthode qui prend une query string et qui renvoie deux itérables, avec des classes abstraites de base
   de type jdbc, elastic-search-via-webservice ... Y aura besoin d'un type de paramètre replace every where */
public abstract class DataSourceFactory {

    //Singletons management part
    private static Map<String, DataSource> dataSourceSingletons = new HashMap<>();

    public static DataSource getDataSource(Configuration configuration) throws SQLException {
        if (dataSourceSingletons.containsKey(configuration.getName())) {
            DataSource dataSource = dataSourceSingletons.get(configuration.getName());
            if (dataSource instanceof WakableDataSource) {
                ((WakableDataSource) dataSource).wakeUp();
            }
            return dataSource;
        }

        return createDataSource(configuration);
    }

    private static synchronized DataSource createDataSource(Configuration configuration) throws SQLException {
        if (!dataSourceSingletons.containsKey(configuration.getName())) {
            DataSourceFactory factory = getFactory(configuration);
            DataSource dataSource = factory.createDataSource();
            if (dataSource instanceof WakableDataSource) {
                ((WakableDataSource) dataSource).wakeUp();
            }
            dataSourceSingletons.put(configuration.getName(), dataSource);
            return dataSource;
        }
        else return dataSourceSingletons.get(configuration.getName());
    }

    //Abstract factory part
    private static DataSourceFactory getFactory(Configuration configuration) {
        Class<?> clazz;
        Constructor<?> ctor;
        Object dataSourceFactory;
        try {
                clazz = Class.forName(configuration.getFactoryClass());
            } catch (ClassNotFoundException e) {
                throw new TechnicalRuntimeException(
                        "Factory class does not exist: "+configuration.getFactoryClass());
            }
        try {
                ctor = clazz.getConstructor(Configuration.class);
            } catch (NoSuchMethodException e) {
                throw new TechnicalRuntimeException(
                        "Factory class must implement constructor with Configuration: "+configuration.getFactoryClass());
        }
        try {
                dataSourceFactory = ctor.newInstance(new Object[]{configuration});
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e ) {
            throw new TechnicalRuntimeException(e);
        }
        if (!(dataSourceFactory instanceof DataSourceFactory)) {
            throw new TechnicalRuntimeException(
                    "Factory class must extends DataSourceFactory: "+ configuration.getFactoryClass()
            );
        }

        return (DataSourceFactory) dataSourceFactory;
    }

    protected abstract DataSource createDataSource()
            throws java.sql.SQLException;

    protected Configuration configuration;

    public DataSourceFactory(Configuration configuration) {
        this.configuration = configuration;
    }
}

