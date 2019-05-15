package utils;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Stream;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class PathFinderUtil {

    /**
     * get all paths from folder recursively
     *
     * @param path the starting path
     * @return ArrayList<Path> return all path as ArrayList or IOException
     * @throws IOException if walk method found en empty or unreachable folder
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
     * read a folder and only return files, ignoring folders
     * @param path path to check files
     * @return return array of file paths
     * @throws FileNotFoundException if folder is not reachable
     */
    public static ArrayList<Path> getAllFilesInPath(Path path) throws FileNotFoundException {
        ArrayList<Path> paths = new ArrayList<>();

        File folder = new File(path.toString());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) throw new FileNotFoundException("There are no files in this path");
        for (File file : listOfFiles) if (file.isFile()) paths.add(Path.of(file.getAbsolutePath()));

        return paths;
    }

    /**
     * get all paths from folder recursively but returned array will be filtered for only allow accepted paths from user
     * @param path folder to check
     * @return array with accepted paths from user
     * @throws FileNotFoundException if folder is not reachable
     */
    public static ArrayList<Path> getAllFilesInPathVerbose(Path path) throws FileNotFoundException {
        ArrayList<Path> paths = new ArrayList<>();

        File folder = new File(path.toString());
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) throw new FileNotFoundException("No selected file");
        for (File file : listOfFiles) if (file.isFile()) if (ScannerUtil.getVerboseInput("Do you want to add the following file? [Y/n] " + file.getName() + "\n")) paths.add(Path.of(file.getAbsolutePath()));

        return paths;
    }

    /**
     * returns file name
     * @param path gets the file path
     * @return return the name as string
     */
    public static String getPathFileName(Path path) {
        File file = new File(path.toString());
        return file.getName();
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
     * convert path to array with only one instance
     * @param path file path
     * @return return an ArrayList
     */
    public static ArrayList<Path> getFile(Path path){
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
     * check if path is reachable
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

    /**
     * gets a path and user properties to return requested ArrayList
     * @param path gets user path input
     * @param properties gets user properties
     * @return ArrayList with the requested paths; verbose onli folder files, file, or recursive folder
     * @throws IOException method will throw up exceptions from submethods or end up with exception if path is unhandled
     */
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

    /**
     * prints all paths from session
     * @throws JSchException session unavailable
     * @throws SftpException problems with SFPT on session
     */
    private void listFiles(Session session, Path path) throws JSchException, SftpException {

        session.connect();
        System.out.println("Connected to SFTP server");

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        Vector<ChannelSftp.LsEntry> directoryEntries = sftpChannel.ls(path.toString());
        for (ChannelSftp.LsEntry file : directoryEntries) {
            System.out.println(String.format("File - %s", file.getFilename()));
        }
        sftpChannel.exit();
        session.disconnect();
    }

}
