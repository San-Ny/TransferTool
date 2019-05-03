package tests;

import utils.ConfigurationUtil;

import java.util.Properties;
import java.util.Set;

public class ConfigTest {
    public static void main(String[] args) {
        ConfigurationUtil.generateConf();
        Properties properties = ConfigurationUtil.getParams();
        if (properties != null){

            Set<String> keys = properties.stringPropertyNames();
            for(String s: keys){
                System.out.println(s);
            }
        }

    }
}
