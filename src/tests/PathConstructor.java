package tests;

import utils.PathFinderUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathConstructor {
    public static void main(String[] args) throws IOException{
        ArrayList<Path> fileClasses = PathFinderUtil.getVerbosePaths(Paths.get(PathFinderUtil.removeAsterisk("/home/grdar/Downloads/*")));


        for (Path fileClass : fileClasses) System.out.println(fileClass.toString());
    }
}
