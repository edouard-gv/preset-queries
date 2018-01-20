package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class H2DataSourceFactory extends DataSourceFactory {

    @Override
    protected DataSource createDataSource() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    public H2DataSourceFactory(Configuration configuration) {
        super(configuration);
    }

}
