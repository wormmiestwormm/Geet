import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    Path repoPath = Paths.get("geet");
    Path parentPath = repoPath.toAbsolutePath().getParent();
    public void initializeRepository() throws IOException {
        try {
            Files.createDirectory(repoPath);
            //create .geet folder to store all the different commits
            //geet folder has to be able to be accessed and changed
        } catch (IOException e) {
            System.out.println("Error: Geet repository already exists in " + parentPath);
            throw new RuntimeException(e);
        }
    }


    public String getRepoPath(){
        return repoPath.toAbsolutePath().toString();
    }
}
