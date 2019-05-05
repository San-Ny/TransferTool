package tests;

import utils.PathFinderUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathConstructor {
    public static void main(String[] args) throws IOException{
        ArrayList<Path> fileClasses = PathFinderUtil.getVerbosePaths(Paths.get("/home/san/Descargas"));


        for(int a = 0; a < fileClasses.size(); a++){
            System.out.println(fileClasses.get(a).toString());
        }
    }
}
