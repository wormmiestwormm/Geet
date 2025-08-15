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
        System.out.println("Copy directory: " + copyDirectory);

        for (String filePath: uniqueFileArray) {
            StringBuilder fileNameBuilder = new StringBuilder(filePath);
            fileNameBuilder.delete(0, 43);
            String fileName = String.valueOf(fileNameBuilder);
            Path sourcePath = Paths.get(filePath);

            System.out.println(filePath);
            System.out.println(filePath.length());
            System.out.println(fileName);

            if (fileName.contains("\\")) {
                String[] pathLayers = fileName.split("\\\\");
                String currentPath = pathLayers[0];
                String commitPath = copyDirectory + "\\" + pathLayers[0];

                for (int x = 0; x < pathLayers.length; x++) {
                    System.out.println("current layer:" + currentPath);
                    if (Files.isDirectory(Paths.get(currentPath))) {
                        if (Files.exists(Paths.get(commitPath))) {
                            System.out.println("Directory " + commitPath + "already exists");
                        } else {
                            System.out.println("Creating directory");
                            Files.createDirectory(Paths.get(commitPath));
                        }
                        currentPath += "\\" + pathLayers[x + 1];
                        commitPath += "\\" + pathLayers[x + 1];
                    }
                    else {
                        Path copyPath = Paths.get(commitPath);

                        System.out.println("Source path: " + sourcePath);
                        System.out.println("Source path: " + copyPath);
                        Files.copy(sourcePath,
                                copyPath,
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            else {
                Path copyPath = Paths.get(copyDirectory + "\\" + fileName);

                System.out.println("Source path: " + sourcePath);
                System.out.println("Copy path: " + copyPath);

                Files.copy(sourcePath,
                        copyPath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
