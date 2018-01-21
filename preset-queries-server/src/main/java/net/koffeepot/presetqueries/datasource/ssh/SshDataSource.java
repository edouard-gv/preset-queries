package net.koffeepot.presetqueries.datasource.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.datasource.WakableDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class SshDataSource extends WakableDataSource {

    private com.jcraft.jsch.Session session;
    private int localPort;
    private String username;
    private String host;
    private int sshPort;
    private int remotePort;
    private String keyPath;

    public SshDataSource(DataSource dataSource, String username, String keyPath, String host, int sshPort, int localPort, int remotePort) {
        super(dataSource);
        this.username = username;
        this.keyPath = keyPath;
        this.host = host;
        this.sshPort = sshPort;
        this.localPort = localPort;
        this.remotePort = remotePort;
        initSshSession();
    }

    private void initSshSession() {
        final JSch jsch = new JSch();

        String localhost = "127.0.0.1";

        try {
            jsch.addIdentity(keyPath);

            session = jsch.getSession(
                    username,
                    host,
                    sshPort);
            session.setServerAliveInterval(1000*60*2); //Every two minutes
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            session.setPortForwardingL(localPort, localhost, remotePort);
        } catch (JSchException e) {
            throw new TechnicalRuntimeException(e);
        }
    }

    /***
     * Test if connection seems ok, if ko or never initialized (==null) restart it
     */
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
        }
    }
}
