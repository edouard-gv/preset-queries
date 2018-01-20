package net.koffeepot.presetqueries.datasource.ssh;

import net.koffeepot.presetqueries.datasource.MysqlDataSourceFactory;
import net.koffeepot.presetqueries.datasource.WakableDataSource;
import net.koffeepot.presetqueries.entity.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class MysqlOverSshDataSourceFactory extends MysqlDataSourceFactory {

    public MysqlOverSshDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    protected DataSource createDataSource(String schema, String username, String password) throws SQLException {
        WakableDataSource dataSource = new SshDataSource(super.createDataSource("localhost", SshDataSource.LOCAL_PORT, schema, username, password));
        return dataSource;
    }
}
