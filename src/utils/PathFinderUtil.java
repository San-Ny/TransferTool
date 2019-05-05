package utils;

import pojos.FileClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class PathFinderUtil {

    /**
     * get all paths from folder recursively
     *
     * @param path the starting path
     * @return ArrayList<Path> return all path as ArrayList or IOException
     */
    public static ArrayList<Path> getAllRecursivePaths(Path path) throws IOException {
        ArrayList<Path> paths = new ArrayList<>();
        try (Stream<Path> filePathStream = Files.walk(path)) {
            filePathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) paths.add(filePath);
            });
            return paths;
        }
    }

    /**
     * ask for every file in the folder recursively
     * @param path starting path
     * @return ArrayList<Path> return added paths from user as ArrayList or IOException
     * @throws IOException error with some path
     */
    public static ArrayList<Path> getVerbosePaths(Path path) throws IOException{
        ArrayList<Path> paths = new ArrayList<>();
        try (Stream<Path> filePathStream = Files.walk(path)) {
            filePathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    if (ScannerUtil.getVerboseInput("Do you want to add the following file? [Y/n] " + filePath + "\n")) paths.add(filePath);
                }
            });
            return paths;
        }
    }

    /**
     * return the absolute path
     * @param path file path
     * @return absolute path
     * @throws IOException exception
     */
    public static Path getFilePaths(Path path) throws IOException{
        if (!path.isAbsolute()){
            return path.toAbsolutePath();
        }
        return path;
    }
}
