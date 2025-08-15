import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    private static final Path repoPath = Paths.get(".geet");

    //creating paths for json files within .geet
    private static final String stagingAreaPathString = repoPath + "\\stagingArea.json";
    private static final String commitStoragePathString = repoPath + "\\commitLog.json";
    private static final Path stagingAreaPath = Paths.get(stagingAreaPathString);
    private static final Path commitStoragePath = Paths.get(commitStoragePathString);

    //create .geet folder to store all the different commits
    public boolean initializeRepository() {
        try {
            Files.createDirectory(repoPath);
            Files.createFile(stagingAreaPath);
            Files.createFile(commitStoragePath);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    //returns the absolute path of the repository
    public String getRepoPath() {
        return repoPath.toAbsolutePath().toString();
    }

    public String getParentPath() {
        return repoPath.toAbsolutePath().getParent().toString();
    }
}
