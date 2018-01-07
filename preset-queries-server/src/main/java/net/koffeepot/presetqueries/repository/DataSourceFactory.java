package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class DataSourceFactory {
    //Singletons management part
    private static Map<String, DataSource> dataSourceSingletons = new HashMap<>();

    public static DataSource getDataSource(String type) throws SQLException {
        if (dataSourceSingletons.containsKey(type)) {
            return dataSourceSingletons.get(type);
        }

        DataSourceFactory factory = getFactory(type);

        synchronized (factory.getClass()) {
            //Yes we can synchronize on this class but we just have to check that indeed it was not blocked
            if (!dataSourceSingletons.containsKey(type)) {
                DataSource dataSource = factory.createDataSource();
                dataSourceSingletons.put(type, dataSource);
                return dataSource;
            }
            else return dataSourceSingletons.get(type);
        }
    }

    //Abstract factory part
    private static DataSourceFactory getFactory(String type) {
        switch (type) {
            case "mysql-local":
                return new MysqlLocahostDataSourceFactory();
            default:
                throw new TechnicalRuntimeException("Type of connection unknowm: "+type);
        }
    }

    protected abstract DataSource createDataSource()
            throws java.sql.SQLException;
}

abstract class MysqlDataSourceFactory extends DataSourceFactory {

    protected DataSource createDataSource(String host, int port, String schema, String username, String password) throws SQLException {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(String.format("jdbc:mysql://%s:%d/%s", host, (port==0?3306:port), schema));
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        return dataSourceProperties.initializeDataSourceBuilder().build();

    }
}

class MysqlLocahostDataSourceFactory extends MysqlDataSourceFactory {

    public DataSource createDataSource() throws SQLException {
        return super.createDataSource("localhost", 0, "test","root", "root");
    }
}