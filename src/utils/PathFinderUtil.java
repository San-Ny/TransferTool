package utils;

import pojos.FileClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static ArrayList<Path> getAllFilesInPath(Path path) throws IOException, NullPointerException {
        ArrayList<Path> paths = new ArrayList<>();

        File folder = new File("/Users/you/folder/");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                paths.add(Path.of(file.getAbsolutePath()));
                System.out.println(file.getName());
            }
        }

        return paths;
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

    /**
     * check if path is valid
     * @param path get the possible path string
     * @return return if string is a valid path or not
     */
    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    /**
     * check if a path ends with a asterisk
     * @param path path to validate or clean
     * @return return the path with the asterisk if is the case
     */
    public static String removeAsterisk(String path){
        String lastChar = path.substring(path.length() - 1);
        if (lastChar.equals("*")) return path.substring(0, path.length() - 1);
        return path;
    }

    public static boolean hasAsterisk(String path){
        return  path.substring(path.length() - 1).equals("*");
    }

}
