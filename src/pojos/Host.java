package pojos;

import java.util.Objects;
import java.util.Properties;

public class Host {

    private String user, host, port;
    int position;
    private Properties properties;

    public Host(){

    }

    public Host(String user, String host, String port, int position, Properties properties) {
        this.user = user;
        this.host = host;
        this.port = port;
        this.position = position;
        this.properties = properties;
    }

    public Host(String user, String host, String port, int position) {
        this.user = user;
        this.host = host;
        this.port = port;
        this.position = position;
    }

    public Host(String user, String host) {
        this.user = user;
        this.host = host;
        port = "22";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Host{" +
                "user='" + user +
                ", host='" + host +
                ", port='" + port +
                ", properties=" + properties + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Host)) return false;
        Host host1 = (Host) o;
        return Objects.equals(getUser(), host1.getUser()) &&
                Objects.equals(getHost(), host1.getHost()) &&
                Objects.equals(getPort(), host1.getPort()) &&
                Objects.equals(getProperties(), host1.getProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getHost(), getPort(), getProperties());
    }
}
