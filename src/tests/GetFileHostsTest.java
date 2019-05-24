package tests;

import utils.ArgumentReaderUtil;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;

public class GetFileHostsTest {

    public static void main(String[] args) {
        try {
            HashMap<String, String> hosts = ArgumentReaderUtil.getFileHosts(Path.of("/home/grdar/Downloads/hosts.txt"));

//            System.out.println(Arrays.asList(map));
            System.out.println(Collections.singletonList(hosts));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
