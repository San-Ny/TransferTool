package pojos;

/**
 * @deprecated
 */
public class FilePackage {
    private FileClass[] fileClasses;

    public FilePackage(FileClass[] fileClasses) {
        this.fileClasses = fileClasses;
    }

    public FileClass[] getFileClasses() {
        return fileClasses;
    }

    public void setFileClasses(FileClass[] fileClasses) {
        this.fileClasses = fileClasses;
    }
}
