package tests;

import com.jcraft.jsch.*;
import pojos.SSH2User;

import java.util.Properties;

public class JSchSftpTest {

    public static void main(String[] args) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession("grdar", "192.168.1.203", 22); //default port is 22
            UserInfo ui = new SSH2User(true);
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "yes");
            session.setConfig(properties);
            session.setUserInfo(ui);
            session.setPassword("grdar".getBytes());
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("Connected");
        } catch (JSchException e) {
            e.printStackTrace(System.out);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            session.disconnect();
            System.out.println("Disconnected");
        }
    }
}