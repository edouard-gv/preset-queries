package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class H2DataSourceFactory extends DataSourceFactory {

    @Override
    protected DataSource createDataSource() throws SQLException {
        String closeDelay = configuration.getStringAttribute("db_close_delay");
        if (closeDelay==null || closeDelay.isEmpty()) {
            throw new TechnicalRuntimeException("Should never happen, it is a mandatory parameter, and someone changed getStringAttribute not to see it");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY="+closeDelay);
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    public H2DataSourceFactory(Configuration configuration) {
        super(configuration);
    }

}
