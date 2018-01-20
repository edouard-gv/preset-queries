package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class MysqlDataSourceFactory extends DataSourceFactory {

    public MysqlDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    protected DataSource createDataSource(String host, int port, String schema, String username, String password) throws SQLException {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(String.format("jdbc:mysql://%s:%d/%s", host, (port==0?3306:port), schema));
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource)dataSourceProperties.initializeDataSourceBuilder().build();

        dataSource.getPoolProperties().setTestOnBorrow(true);
        dataSource.getPoolProperties().setValidationQuery("SELECT 1");

        return dataSource;
    }
}
