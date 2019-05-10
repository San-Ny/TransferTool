package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
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

    public static ArrayList<Path> getAllFilesInPath(Path path) throws FileNotFoundException, NullPointerException {
        ArrayList<Path> paths = new ArrayList<>();

        File folder = new File(path.toString());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) throw new FileNotFoundException("");
        for (File file : listOfFiles) if (file.isFile()) paths.add(Path.of(file.getAbsolutePath()));

        return paths;
    }

    public static ArrayList<Path> getAllFilesInPathVerbose(Path path) throws FileNotFoundException, NullPointerException {
        ArrayList<Path> paths = new ArrayList<>();

        File folder = new File(path.toString());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) throw new FileNotFoundException("");
        for (File file : listOfFiles) if (file.isFile()) if (ScannerUtil.getVerboseInput("Do you want to add the following file? [Y/n] " + file.getName() + "\n")) paths.add(Path.of(file.getAbsolutePath()));

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

    public static ArrayList<Path> getFile(Path path) throws IOException{
        return new ArrayList<>(Collections.singletonList(path));
    }

    /**
     * return the absolute path
     * @param path file path
     * @return absolute path as Array List
     */
    public static ArrayList<Path> isAbsolutePath(Path path){
        if (!path.isAbsolute()) return new ArrayList<>(Collections.singletonList(path.toAbsolutePath()));
        return new ArrayList<>(Collections.singletonList(path));
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
        if (path.substring(path.length() - 1).equals("*")) return path.substring(0, path.length() - 1);
        return path;
    }

    /**
     * check if the path ends with an asterisk
     * @param path path to check
     * @return boolean if asterisk found or not on final character
     */
    public static boolean hasAsterisk(String path){
        return  path.substring(path.length() - 1).equals("*");
    }

    /**
     * check if the path ends in a folder
     * @param path path to check
     * @return boolean if path is pointing to folder
     */
    public static boolean hasFinalBar(String path){
        return  path.substring(path.length() - 1).matches("[/]");
    }

    public static ArrayList<Path> getCorrectFormat(Path path, Properties properties) throws IOException{
        boolean recursive = false, verbose = false;
        if (properties.containsKey("recursive") && properties.getProperty("recursive").equals("1")) recursive = true;
        if (properties.containsKey("verbose") && properties.getProperty("verbose").equals("1")) verbose = true;
        if (hasAsterisk(path.toString())) {
            recursive = true;
            path = Path.of(removeAsterisk(path.toString()));
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) System.out.println("PathFinderUtil -> asterisk found -> removing: new path = '" + path.toString() + "'");
        }

        if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) System.out.println("PathFinderUtil ->\n\trecursive =" + recursive + ";\n\tverbose =" + verbose + ";\n\tfinal bar=" + hasFinalBar(path.toString()) + ";\n\tis valid=" + isValidPath(path.toString()));

        //choosing correct path returner

        //file
        if (isValidPath(path.toString()) && !hasFinalBar(path.toString()) && new File(path.toString()).isFile()) return getFile(path);

        //not recursive
        else if (isValidPath(path.toString()) && !recursive && !verbose) return getAllFilesInPath(path);
        else if (isValidPath(path.toString()) && !recursive && verbose) return getAllFilesInPathVerbose(path);

        //recursive
        else if (isValidPath(path.toString()) && recursive && !verbose) return getAllRecursivePaths(path);
        else if (isValidPath(path.toString()) && recursive && verbose) return getVerbosePaths(path);

        else throw new IOException("Fatal error processing local path\n error finding coincidences and arguments");
    }

}
