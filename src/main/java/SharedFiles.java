import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SharedFiles {
    String[][] sharedFileArray;
    String[] localFilePaths;

    int numFileTracker;
    int filePathTracker;

    public SharedFiles(int numFiles) {
        sharedFileArray = new String[numFiles][3];
        localFilePaths = new String[numFiles];
        numFileTracker = 0;
        filePathTracker = 0;
    }

    public void addSharedFiles(String baseFilePath, String File1Path, String File2Path) {
        sharedFileArray[numFileTracker][0] = baseFilePath;
        sharedFileArray[numFileTracker][1] = File1Path;
        sharedFileArray[numFileTracker][2] = File2Path;

        setFilePath(baseFilePath);
        numFileTracker++;
    }

    public void setFilePath (String absFilePath) {
        StringBuilder fileNameBuilder = new StringBuilder(absFilePath);
        fileNameBuilder.delete(0, 43);
        localFilePaths[numFileTracker] = String.valueOf(fileNameBuilder);
    }

    public String[] getLocalFilePaths() {
        return localFilePaths;
    }

    public String getLocalFilePath(int filePathTracker) {
        System.out.println(localFilePaths[filePathTracker]);
        return localFilePaths[filePathTracker];
    }

    private void createDirectories(String filePath, Path copyDirectory) throws IOException {
        String[] pathLayers = filePath.split("\\\\");
        String currentPath = pathLayers[0] + "\\" + pathLayers[1] + "\\" + pathLayers[2];
        String commitPath = copyDirectory + "\\" + pathLayers[2];
        int x = 0;

        while (Files.isDirectory(Paths.get(currentPath))) {
            System.out.println(currentPath);
            System.out.println(commitPath);
            if (Files.exists(Paths.get(commitPath))) {
                System.out.println("Directory " + commitPath + "already exists");
            } else {
                Files.createDirectory(Paths.get(commitPath));
                System.out.println("Creating directory");
            }
            currentPath += "\\" + pathLayers[x + 1];
            commitPath += "\\" + pathLayers[x + 1];
        }
        System.out.println("completed directory copying");
    }

    private void deleteDirectories (File file) throws IOException {
        System.out.println("Looking at " + file.getPath());
        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            System.out.println("Directory found at " + file.getPath());
            for(File x : fileArray) {
                deleteDirectories(x);
            }
            Files.delete(file.toPath());
        }
        else {
            System.out.println("Deleting " + file.getPath());
            Files.delete(file.toPath());
        }
    }

    public boolean compareSharedFiles(Path copyDirectory) throws IOException {
        BufferedReader reader;
        BufferedWriter writer;

        for (String[] sharedFile : sharedFileArray) {
            if (sharedFile[0] == null) {
                return true;
            }
            System.out.println(getLocalFilePath(filePathTracker).contains("\\"));
            if (getLocalFilePath(filePathTracker).contains("\\")) {
                System.out.println("we are in the file copying method");
                createDirectories(sharedFile[0], copyDirectory);
            }

            writer = new BufferedWriter(new FileWriter(copyDirectory.toString() + "\\" + getLocalFilePath(filePathTracker)));
            reader = new BufferedReader(new FileReader(sharedFile[0]));

            String line;
            ArrayList<String> lineBase = new ArrayList<>();
            ArrayList<String> line1 = new ArrayList<>();
            ArrayList<String> line2 = new ArrayList<>();

            System.out.println("LineBase " + sharedFile[0]);
            System.out.println("Line1 " + sharedFile[1]);
            System.out.println("Line2 " + sharedFile[2]);
            while ((line = reader.readLine()) != null) {
                lineBase.add(line);
            }
            reader = new BufferedReader(new FileReader(sharedFile[1]));
            while ((line = reader.readLine()) != null) {
                line1.add(line);
            }
            reader = new BufferedReader(new FileReader(sharedFile[2]));
            while ((line = reader.readLine()) != null) {
                line2.add(line);
            }

            int line1Tracker = 0;
            int line2Tracker = 0;
            for (int x = 0; x < lineBase.size(); x++) {
                int insertBlockLimit;

                //All lines are the same
                if (lineBase.get(x).equals(line1.get(line1Tracker)) && lineBase.get(x).equals(line2.get(line2Tracker))) {
                    writer.write(lineBase.get(x));
                    writer.newLine();

                    System.out.println("all lines same " + "linebase " + x + " line1 " + line1Tracker + " line2 " + line2Tracker);
                    line1Tracker++;
                    line2Tracker++;

                    //Line1 is edited/inserted
                } else if (!lineBase.get(x).equals(line1.get(line1Tracker)) && lineBase.get(x).equals(line2.get(line2Tracker))) {
                    System.out.println("line1 different linebase " + x + " line1 " + line1Tracker + " line2 " + line2Tracker);

                    if (line1.size() - line1Tracker > lineBase.size() - x) {
                        System.out.println("Checking for Insertion with " + lineBase.get(x));
                        for (insertBlockLimit = line1Tracker; insertBlockLimit < line1.size(); insertBlockLimit++) {
                            if (line1.get(insertBlockLimit).equals(lineBase.get(x))) {
                                System.out.println("Insertion identified");
                                while (line1Tracker < insertBlockLimit) {
                                    writer.write(line1.get(line1Tracker));
                                    writer.newLine();
                                    System.out.println(line1Tracker);
                                    line1Tracker++;
                                }
                                x--;
                                break;
                            }
                        }
                        if (insertBlockLimit == line1.size()) {
                            System.out.println("Not an insertion: " + line1.get(line1Tracker));
                            writer.write(line1.get(line1Tracker));
                            writer.newLine();

                            line1Tracker++;
                            line2Tracker++;
                        }
                    }
                    else{
                        writer.write(line1.get(line1Tracker));
                        writer.newLine();
                        line1Tracker++;
                        line2Tracker++;
                    }

                    //Line2 is edited/inserted
                } else if (lineBase.get(x).equals(line1.get(line1Tracker)) && !lineBase.get(x).equals(line2.get(line2Tracker))) {
                    System.out.println("line2 different linebase " + x + " line1 " + line1Tracker + " line2 " + line2Tracker);

                    if (line2.size() - line2Tracker > lineBase.size() - x) {
                        System.out.println("Checking for Insertion with " + lineBase.get(x));
                        for (insertBlockLimit = line2Tracker; insertBlockLimit < line2.size(); insertBlockLimit++) {
                            if (line2.get(insertBlockLimit).equals(lineBase.get(x))) {
                                System.out.println("Insertion identified");
                                while (line2Tracker < insertBlockLimit) {
                                    writer.write(line2.get(line2Tracker));
                                    writer.newLine();
                                    line2Tracker++;
                                }
                                x--;
                                break;
                            }
                        }
                        if (insertBlockLimit == line2.size()) {
                            System.out.println("Not an insertion: " + line2.get(line2Tracker));
                            writer.write(line2.get(line2Tracker));
                            writer.newLine();

                            line2Tracker++;
                            line1Tracker++;
                        }
                    }
                    else {
                        writer.write(line2.get(line2Tracker));
                        writer.newLine();
                        line2Tracker++;
                        line1Tracker++;
                    }

                    //Line1 and Line2 are edited
                } else if (!lineBase.get(x).equals(line1.get(line1Tracker)) && line1.get(x).equals(line2.get(line2Tracker))) {
                    System.out.println("line1 and line2 different linebase " + x + " line1 " + line1Tracker + " line2 " + line2Tracker);
                    if ((line1.size() - line1Tracker > lineBase.size() - x) && (line2.size() - line2Tracker > lineBase.size() - x)) {
                        System.out.println("Checking for Insertion with " + lineBase.get(x));
                        int insertBlockLimit1;
                        int insertBlockLimit2 = line2Tracker;
                        for (insertBlockLimit1 = line1Tracker; insertBlockLimit1 < line1.size(); insertBlockLimit1++) {
                            //The change in Line1 and Line2 are the same
                            if (line1.get(insertBlockLimit1).equals(lineBase.get(x)) && line2.get(insertBlockLimit2).equals(lineBase.get(x))) {
                                System.out.println("Insertion identified");
                                while (line1Tracker < insertBlockLimit1) {
                                    writer.write(line1.get(line1Tracker));
                                    writer.newLine();

                                    line1Tracker++;
                                    line2Tracker++;
                                }
                                x--;
                                break;
                            }
                            //The change in Line1 and Line2 are different
                            else if (!line1.get(insertBlockLimit1).equals(line2.get(insertBlockLimit2))) {
                                System.out.println("Merge conflict encountered at LineBase." + (x + 1) + " and Line1." + (line1Tracker + 1) + " and Line2." + (line2Tracker + 1));
                                System.out.println("LineBase." + (x+1) + ": " + lineBase.get(x));
                                System.out.println("Line1." + (line1Tracker+1) + ": " + line1.get(line1Tracker));
                                System.out.println("Line2." + (line2Tracker+1) + ": " + line2.get(line2Tracker));
                                deleteDirectories(new File(String.valueOf(copyDirectory)));
                                return false;
                            }
                            insertBlockLimit2++;
                        }
                        if (insertBlockLimit1 == line1.size()) {
                            System.out.println("Not an insertion: " + line1.get(line1Tracker));
                            writer.write(line1.get(line1Tracker));
                            writer.newLine();

                            line2Tracker++;
                            line1Tracker++;
                        }
                    }
                    else {
                        writer.write(line1.get(line1Tracker));
                        writer.newLine();
                        line2Tracker++;
                        line1Tracker++;
                    }
                } else {
                    System.out.println("Merge conflict encountered at LineBase." + (x+1) + " and Line1." + (line1Tracker+1) + " and Line2." + (line2Tracker+1));
                    System.out.println("LineBase." + (x+1) + ": " + lineBase.get(x));
                    System.out.println("Line1." + (line1Tracker+1) + ": " + line1.get(line1Tracker));
                    System.out.println("Line2." + (line2Tracker+1) + ": " + line2.get(line2Tracker));
                    deleteDirectories(new File(String.valueOf(copyDirectory)));
                    return false;
                }
            }
            System.out.println("File eligible for merging");
            reader.close();
            writer.close();
            filePathTracker++;
        }
        return true;
    }
}
