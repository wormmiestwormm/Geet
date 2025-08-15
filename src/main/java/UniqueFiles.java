import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class UniqueFiles {
    ArrayList<String> uniqueFileArray;

    public UniqueFiles () {
        uniqueFileArray = new ArrayList<>();
    }

    public ArrayList<String> getUniqueFileArray() {
        return uniqueFileArray;
    }

    public void addFilePath(String filePath) {
        uniqueFileArray.add(filePath);
    }

    public void addRemainingFiles (Set<String> fileListOtherBranch) {
        for (String filePath : fileListOtherBranch) {
            addFilePath(filePath);
        }
    }

    public void copyUniqueFiles(Path copyDirectory) throws IOException {
        for (String filePath: uniqueFileArray) {
            StringBuilder fileNameBuilder = new StringBuilder(filePath);
            fileNameBuilder.delete(0, 43);
            String fileName = String.valueOf(fileNameBuilder);
            Path sourcePath = Paths.get(filePath);

            if (fileName.contains("\\")) {
                String[] pathLayers = fileName.split("\\\\");
                String currentPath = pathLayers[0];
                String commitPath = copyDirectory + "\\" + pathLayers[0];

                for (int x = 0; x < pathLayers.length; x++) {
                    if (Files.isDirectory(Paths.get(currentPath))) {
                        if (!Files.exists(Paths.get(commitPath))) {
                            Files.createDirectory(Paths.get(commitPath));
                        }

                        currentPath += "\\" + pathLayers[x + 1];
                        commitPath += "\\" + pathLayers[x + 1];
                    }
                    else {
                        Path copyPath = Paths.get(commitPath);
                        Files.copy(sourcePath, copyPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            else {
                Path copyPath = Paths.get(copyDirectory + "\\" + fileName);
                Files.copy(sourcePath, copyPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
