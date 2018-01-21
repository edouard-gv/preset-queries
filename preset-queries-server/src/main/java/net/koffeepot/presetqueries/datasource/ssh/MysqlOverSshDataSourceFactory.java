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

    protected DataSource createDataSource() throws SQLException {

        WakableDataSource dataSource = new SshDataSource(
                super.createDataSource(),
                configuration.getStringAttribute("sshUsername"),
                configuration.getStringAttribute("sshKeyPath"),
                configuration.getStringAttribute("sshHost"),
                configuration.getIntegerAttribute("sshPort"),
                configuration.getIntegerAttribute("port"),
                configuration.getIntegerAttribute("remotePort")
                );
        return dataSource;
    }
}
