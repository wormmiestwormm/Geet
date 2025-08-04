import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class StorageModule {
    CommitTree commitTree;
    public StorageModule(){
        commitTree = readCommitTree();
    }

    public void logChanges(String commitMessage, Set<String> stageList) throws IOException {
        //creates hash key for commit
        String commitHash = UUID.randomUUID().toString();

        Path commitFolderPath = Paths.get(".geet\\" + commitHash);
        Files.createDirectory(commitFolderPath);

        //read commitTree from commitLog.json, initialize new commitTree object if none exists in json file
        if (commitTree == null){
            commitTree = new CommitTree();
        }
        else {
            File prevCommit = new File(".geet\\" + commitTree.getBranchList().get(commitTree.getCurrentBranchName()).getTailHash());
            System.out.println("getting previous commit files from: " + prevCommit.getPath());
            File[] prevCommitFiles = prevCommit.listFiles();
            System.out.println(Arrays.toString(prevCommitFiles));

            copyFilesFromFileArray(prevCommitFiles, commitFolderPath);
        }

        copyStageFiles(stageList, commitFolderPath);
        //copyFilesToCommit(stageList, commitFolderPath);
        //call commitTree method to add
        commitTree.addToCurrentBranch(commitHash, commitMessage, stageList);
        commitTree.printCurrentCommitLog();
        writeCommitTree(commitTree);
    }

    public void checkOut(String hashCode) throws IOException {
        File file = new File(System.getProperty("user.dir"));
        File[] dirFiles = file.listFiles();
        System.out.println(dirFiles);

        for (File f : dirFiles) {
            if (f.getName().equals(".geet")) {
                System.out.println(".geet spared");
            }
            else {
                deleteDirectories(f);
            }
        }

        commitTree.setHeadCode(hashCode);

        String commitPath = ".geet\\" + commitTree.getBranchList().get(commitTree.getCurrentBranchName()).getCommit(hashCode).hash;
        System.out.println("getting commit files from " + commitPath);

        File commit = new File(commitPath);
        File[] commitFiles = commit.listFiles();

        System.out.println("now copying:");
        copyFilesFromFileArray(commitFiles, Paths.get(System.getProperty("user.dir")));

        writeCommitTree(commitTree);
    }

    private void copyFilesFromFileArray(File[] fileArray, Path copyDirectory) throws IOException {
        for (File file : fileArray){
            if (file.isDirectory()){
                copyDirectories(file, copyDirectory.toString());
            }
            else {
                Path copyPath = Paths.get(copyDirectory.toString() + "\\" + file.getName());
                System.out.println("Copying file from " + file.getPath() + " to " + copyPath);
                Files.copy(file.toPath(), copyPath);
                System.out.println(file.getName() + " copied");
            }
        }
    }

    private void copyDirectories(File file, String currentCopyPath) throws IOException {
        System.out.println("Source path: " + file.getPath());
        System.out.println("Commit path: " + currentCopyPath);
        if (file.isDirectory()) {
            Files.createDirectory(Paths.get(currentCopyPath + "\\" + file.getName()));
            System.out.println("Directory created at " + currentCopyPath);

            File[] fileArray = file.listFiles();
            System.out.println("Directory found at " + file.getPath());

            for (File x : fileArray) {
                copyDirectories(x, currentCopyPath + "\\" + file.getName());
            }
        }
        else{
            System.out.println("Copying file");
            Files.copy(file.toPath(), Paths.get(currentCopyPath + "\\" + file.getName()));
        }
    }

    private void deleteDirectories(File file) throws IOException {
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

    private void copyStageFiles(Set<String> fileSet, Path copyDirectory) throws IOException {
        System.out.println("Source directory: " + System.getProperty("user.dir"));
        System.out.println("Copy directory: " + copyDirectory);
        for (String fileName: fileSet) {
            if (fileName.contains("\\")) {
                String[] pathLayers = fileName.split("\\\\");
                String currentPath = pathLayers[0];
                String commitPath = copyDirectory + "\\" + pathLayers[0];

                for (int x = 0; x < pathLayers.length; x++) {
                    System.out.println(currentPath);
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
                        System.out.println("Getting sourcePath: ");
                        Path sourcePath = Paths.get(fileName);
                        System.out.println("Source path: " + sourcePath);

                        System.out.println("Getting copyPath: ");
                        Path copyPath = Paths.get(commitPath);
                        System.out.println("Source path: " + copyPath);

                        System.out.println("File " + sourcePath + " unlocked");
                        Files.copy(sourcePath,
                                copyPath,
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            else {
                System.out.println("Getting sourcePath: ");
                Path sourcePath = Paths.get(System.getProperty("user.dir") + "\\" + fileName);
                System.out.println("Source path: " + sourcePath);

                System.out.println("Getting copyPath: ");
                Path copyPath = Paths.get(copyDirectory + "\\" + fileName);
                System.out.println("Source path: " + copyPath);

                System.out.println("File " + sourcePath + " unlocked");
                Files.copy(sourcePath,
                        copyPath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public void createNewBranch(String branchName) {
        commitTree.createNewBranch(branchName);
        writeCommitTree(commitTree);
    }

    public void switchWithPrevBranch() {
        commitTree.swapToPrevBranch();
        writeCommitTree(commitTree);
    }

    public void switchWithSpecifiedBranch(String branchName) {
        commitTree.swapBranches(branchName);
        writeCommitTree(commitTree);
    }

    public String getHead() {
        return commitTree.getHeadCode();
    }

    public void getLog() {
        commitTree.printCurrentCommitLog();
    }

    public int getNumBranches() {
        return commitTree.getBranchListLength();
    }

    public boolean checkHasCommit(String hashCode) {
        return commitTree.checkBranchForCommit(hashCode);
    }

    public boolean checkHasBranch(String branchName) {
        return commitTree.hasBranch(branchName);
    }

    private CommitTree readCommitTree() {
        try (Reader reader = new FileReader(".geet\\commitLog.json")) {
            Type jsonCommitLog = new TypeToken<CommitTree>() {}.getType();
            Gson gson = new Gson();
            CommitTree commitTree = gson.fromJson(reader, jsonCommitLog);
            reader.close();
            return commitTree;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeCommitTree(CommitTree newCommitLog) {
        try {
            Writer writer = new FileWriter(".geet\\commitLog.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(newCommitLog, writer);
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
