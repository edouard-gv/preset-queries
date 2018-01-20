package net.koffeepot.presetqueries.datasource.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.datasource.WakableDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class SshDataSource extends WakableDataSource {

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
