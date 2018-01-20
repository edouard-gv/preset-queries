package net.koffeepot.presetqueries.datasource;

import net.koffeepot.presetqueries.entity.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

public class MysqlLocahostDataSourceFactory extends MysqlDataSourceFactory {

    public MysqlLocahostDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    public DataSource createDataSource() throws SQLException {
        return super.createDataSource("localhost", 0, "test","test", "test");
    }
}
