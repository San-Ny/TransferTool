package pojos;

import java.util.Arrays;
import java.util.Objects;

public class FolderClass {

    private String path;
    private FileClass[] fileClasses;
    private FolderClass[] folderClasses;

    public FolderClass[] getFolderClasses() {
        return folderClasses;
    }

    public void setFolderClasses(FolderClass[] folderClasses) {
        this.folderClasses = folderClasses;
    }

    public FolderClass(String path) {
        this.path = path;
    }

    public FolderClass(String path, FileClass[] fileClasses) {
        this.path = path;
        this.fileClasses = fileClasses;
    }

    public FolderClass(String path, FileClass[] fileClasses, FolderClass[] folderClasses) {
        this.path = path;
        this.fileClasses = fileClasses;
        this.folderClasses = folderClasses;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileClass[] getFileClasses() {
        return fileClasses;
    }

    public void setFileClasses(FileClass[] fileClasses) {
        this.fileClasses = fileClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FolderClass)) return false;
        FolderClass folderClass = (FolderClass) o;
        return Objects.equals(getPath(), folderClass.getPath()) &&
                Arrays.equals(getFileClasses(), folderClass.getFileClasses());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getPath());
        result = 31 * result + Arrays.hashCode(getFileClasses());
        return result;
    }
}
