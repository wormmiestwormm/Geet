import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    private static Path repoPath = Paths.get(".geet");

    //creating paths for json files within .geet
    private static String stagingAreaPathString = repoPath.toString() + "\\stagingArea.json";
    private static String commitStoragePathString = repoPath.toString() + "\\commitLog.json";
    private static Path stagingAreaPath = Paths.get(stagingAreaPathString);
    private static Path commitStoragePath = Paths.get(commitStoragePathString);

    //create .geet folder to store all the different commits
    public boolean initializeRepository() throws IOException {
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
    public String getRepoPath(){
        return repoPath.toAbsolutePath().toString();
    }

    public String getParentPath(){
        return repoPath.toAbsolutePath().getParent().toString();
    }
}
