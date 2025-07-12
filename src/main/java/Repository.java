import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    Path repoPath = Paths.get("geet");
    Path parentPath = repoPath.toAbsolutePath().getParent();

    //create .geet folder to store all the different commits
    public void initializeRepository() throws IOException {
        try {
            Files.createDirectory(repoPath);
        } catch (IOException e) {
            System.out.println("Error: geet repository already exists in " + parentPath);
            throw new RuntimeException(e);
        }
    }

    //returns the absolute path of the repository
    public String getRepoPath(){
        return repoPath.toAbsolutePath().toString();
    }
}
