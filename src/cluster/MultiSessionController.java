package cluster;

import java.util.Properties;

public class MultiSessionController extends Thread{

    private Properties properties;

    public MultiSessionController(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run() {
        super.run();

    }
}
