package net.koffeepot.presetqueries.datasource.parrot;

import net.koffeepot.presetqueries.datasource.ssh.MysqlOverSshDataSourceFactory;
import net.koffeepot.presetqueries.entity.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DrupalDataSourceFactory extends MysqlOverSshDataSourceFactory {

    public DrupalDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected DataSource createDataSource() throws SQLException {
        return createDataSource("parrot_stg", "stg_reader", "**************");
    }
}
