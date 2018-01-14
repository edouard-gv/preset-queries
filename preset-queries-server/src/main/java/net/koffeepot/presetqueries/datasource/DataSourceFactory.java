package net.koffeepot.presetqueries.datasource;

import com.jcraft.jsch.*;
import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/* TODO : pour être indépendant du type de base, par exemple pouvoir se brancher sur de l'ES, être indépendant de java.sql.DataSource
   et pouvoir par exemple renvoyer un méthode qui prend une query string et qui renvoie deux itérables, avec des classes abstraites de base
   de type jdbc, elastic-search-via-webservice ... Y aura besoin d'un type de paramètre replace every where */
public abstract class DataSourceFactory {

    //Singletons management part
    private static Map<String, DataSource> dataSourceSingletons = new HashMap<>();

    public static DataSource getDataSource(Configuration configuration) throws SQLException {
        if (dataSourceSingletons.containsKey(configuration.getName())) {
            DataSource dataSource = dataSourceSingletons.get(configuration.getName());
            if (dataSource instanceof WakableDataSource) {
                ((WakableDataSource) dataSource).wakeUp();
            }
            return dataSource;
        }

        return createDataSource(configuration);
    }

    private static synchronized DataSource createDataSource(Configuration configuration) throws SQLException {
        if (!dataSourceSingletons.containsKey(configuration.getName())) {
            DataSourceFactory factory = getFactory(configuration);
            DataSource dataSource = factory.createDataSource();
            if (dataSource instanceof WakableDataSource) {
                ((WakableDataSource) dataSource).wakeUp();
            }
            dataSourceSingletons.put(configuration.getName(), dataSource);
            return dataSource;
        }
        else return dataSourceSingletons.get(configuration.getName());
    }

    //Abstract factory part
    private static DataSourceFactory getFactory(Configuration configuration) {
        Class<?> clazz;
        Constructor<?> ctor;
        Object dataSourceFactory;
        try {
                clazz = Class.forName(configuration.getFactoryClass());
            } catch (ClassNotFoundException e) {
                throw new TechnicalRuntimeException(
                        "Factory class does not exist: "+configuration.getFactoryClass());
            }
        try {
                ctor = clazz.getConstructor(Configuration.class);
            } catch (NoSuchMethodException e) {
                throw new TechnicalRuntimeException(
                        "Factory class must implement constructor with Configuration: "+configuration.getFactoryClass());
        }
        try {
                dataSourceFactory = ctor.newInstance(new Object[]{configuration});
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e ) {
            throw new TechnicalRuntimeException(e);
        }
        if (!(dataSourceFactory instanceof DataSourceFactory)) {
            throw new TechnicalRuntimeException(
                    "Factory class must extends DataSourceFactory: "+ configuration.getFactoryClass()
            );
        }

        return (DataSourceFactory) dataSourceFactory;

            /*
        switch (configuration.getName()) {
            case "mysql-local":
                return new MysqlLocahostDataSourceFactory();
            case "drupal":
                return new DrupalDataSourceFactory();
            case "h2":
                return new H2DataSourceFactory();
            default:
                throw new TechnicalRuntimeException("Type of connection unknowm: "+configuration.getName());
            }
                */
    }

    protected abstract DataSource createDataSource()
            throws java.sql.SQLException;

    Configuration configuration;

    public DataSourceFactory(Configuration configuration) {
        this.configuration = configuration;
    }
}

class H2DataSourceFactory extends DataSourceFactory {

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

abstract class MysqlDataSourceFactory extends DataSourceFactory {

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

class MysqlLocahostDataSourceFactory extends MysqlDataSourceFactory {

    public MysqlLocahostDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    public DataSource createDataSource() throws SQLException {
        return super.createDataSource("localhost", 0, "test","test", "test");
    }
}

class SshDataSource extends WakableDataSource {

    public static final int LOCAL_PORT = 13307;

    private com.jcraft.jsch.Session session;

    public SshDataSource(DataSource dataSource) {
        super(dataSource);
        initSshSession();
    }

    private void initSshSession() {
        final JSch jsch = new JSch();
        String host = "127.0.0.1";
        int remotePort = 3307;

        try {
            jsch.addIdentity("src/main/resources/id_rsa_psh");
            session = jsch.getSession(
                    "parrot",
                    "parrot.ent.platform.sh",
                    22);
            session.setServerAliveInterval(1000*60*2); //Every two minutes
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            session.setPortForwardingL(LOCAL_PORT, host, remotePort);
        } catch (JSchException e) {
            throw new TechnicalRuntimeException(e);
        }
    }

    @Override
    public void wakeUp() {

        //cf code https://stackoverflow.com/questions/16127200/jsch-how-to-keep-the-session-alive-and-up
        if (session != null)
        {
            try {
                ChannelExec testChannel = (ChannelExec) session.openChannel("exec");
                testChannel.setCommand("true");
                testChannel.connect();
                testChannel.disconnect();
            } catch (JSchException e) {
                session = null; //will thus be initialized below
            }
        }

        if (session == null) {
            initSshSession();
            return;
        }
    }
}

abstract class MysqlOverSshDataSourceFactory extends MysqlDataSourceFactory {

    public MysqlOverSshDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    protected DataSource createDataSource(String schema, String username, String password) throws SQLException {
        WakableDataSource dataSource = new SshDataSource(super.createDataSource("localhost", SshDataSource.LOCAL_PORT, schema, username, password));
        return dataSource;
    }
}

class DrupalDataSourceFactory extends MysqlOverSshDataSourceFactory {

    public DrupalDataSourceFactory(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected DataSource createDataSource() throws SQLException {
        return createDataSource("parrot_stg", "stg_reader", "**************");
    }
}

abstract class WakableDataSource implements DataSource {
    private DataSource dataSource;

    public WakableDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract void wakeUp();

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }
}

