import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class StagingArea {
    //constructor
    private static ArrayList<File> stage = new ArrayList<>();
    private FileOutputStream stageFile;

    private Path repoPath = Paths.get("geet");

    public StagingArea() throws FileNotFoundException {
        System.out.println(repoPath);
    }

    //adds file to staging area
    public void addFileToStagingArea(File newFile) throws IOException {
        stage.add(newFile);

        Files.copy(newFile.toPath(),
                new File(repoPath + "\\" + newFile.getName()).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    //adds files in staging area to repository in storage module
    public void commitChanges(){

    }
}
