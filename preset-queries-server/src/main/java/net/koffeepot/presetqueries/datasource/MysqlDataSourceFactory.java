package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MysqlDataSourceFactory extends DataSourceFactory {

    public MysqlDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected DataSource createDataSource() throws SQLException {
        return this.createDataSource(
                configuration.getStringAttribute("host"),
                configuration.getStringAttribute("port", "3306"),
                configuration.getStringAttribute("schema", ""),
                configuration.getStringAttribute("username"),
                configuration.getStringAttribute("password")
        );
    }


    /***
     * In order to allow a subclass to override parameters name
     * @param host
     * @param port
     * @param schema
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    protected DataSource createDataSource(String host, String port, String schema, String username, String password) throws SQLException {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setUrl(String.format("jdbc:mysql://%s:%s/%s", host, port, schema));
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource)dataSourceProperties.initializeDataSourceBuilder().build();

        dataSource.getPoolProperties().setTestOnBorrow(true);
        dataSource.getPoolProperties().setValidationQuery("SELECT 1");

        return dataSource;
    }
}
