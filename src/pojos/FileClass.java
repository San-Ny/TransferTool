package pojos;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * @deprecated
 */
public class FileClass {
    private Path path;
    private byte[] fileInfo;

    public FileClass(Path path, byte[] fileInfo) {
        this.path = path;
        this.fileInfo = fileInfo;
    }

    public FileClass() {

    }

    public FileClass(Path path) {
        this.path = path;
    }


    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public byte[] getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(byte[] fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileClass)) return false;
        FileClass fileClass = (FileClass) o;
        return Objects.equals(getPath(), fileClass.getPath()) &&
                Arrays.equals(getFileInfo(), fileClass.getFileInfo());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getPath());
        result = 31 * result + Arrays.hashCode(getFileInfo());
        return result;
    }

    @Override
    public String toString() {
        return "FileClass{" +
                "path=" + path +
                ", fileInfo=" + "0" +
                '}';
    }
}
