package tests;

import utils.PathFinderUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PathConstructor {
    public static void main(String[] args) throws IOException{
//        ArrayList<Path> fileClasses = PathFinderUtil.getVerbosePaths(Paths.get(PathFinderUtil.removeAsterisk("/home/grdar/Downloads/*")));
//        ArrayList<Path> fileClasses = PathFinderUtil.getAllFilesInPath(Path.of("/home/grdar/Downloads/fonts/Wicons"));
//
//
//        for (Path fileClass : fileClasses) System.out.println(fileClass.toString());

        System.out.println(PathFinderUtil.removeExtension("/home/grdar/Downloads/force_https.htaccess"));
    }
}
