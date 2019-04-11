package pojos;

import java.util.Arrays;
import java.util.Objects;

public class FileClass {
    private String fileName;
    private byte[] fileInfo;

    public FileClass(String fileName, byte[] fileInfo) {
        this.fileName = fileName;
        this.fileInfo = fileInfo;
    }

    public FileClass() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
        return Objects.equals(getFileName(), fileClass.getFileName()) &&
                Arrays.equals(getFileInfo(), fileClass.getFileInfo());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getFileName());
        result = 31 * result + Arrays.hashCode(getFileInfo());
        return result;
    }
}
