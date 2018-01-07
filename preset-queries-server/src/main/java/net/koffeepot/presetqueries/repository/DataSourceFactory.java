package net.koffeepot.presetqueries.repository;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class DataSourceFactory {
    public static DataSourceFactory getFactory(String type) {
        switch (type) {
            case "mysql-local":
                return new MysqlLocahostDataSourceFactory();
            default:
                throw new TechnicalRuntimeException("Type of connection unknowm: "+type);
        }
    }

    public abstract DataSource createDataSource()
            throws java.sql.SQLException;
}

abstract class MysqlDataSourceFactory extends DataSourceFactory {

    protected DataSource createConnection(String host, int port, String schema, String username, String password) throws SQLException {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(String.format("jdbc:mysql://%s:%d/%s", host, (port==0?3306:port), schema));
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        return dataSourceProperties.initializeDataSourceBuilder().build();

    }
}

class MysqlLocahostDataSourceFactory extends MysqlDataSourceFactory {
    public DataSource createDataSource() throws SQLException {
        return super.createConnection("localhost", 0, "test","root", "root");
    }
}