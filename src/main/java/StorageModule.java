import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class StorageModule {
    private CommitTree commitTree;
    private Branch currentBranch;

    public StorageModule(){
        commitTree = readCommitTree();
        if (commitTree != null) {
            currentBranch = commitTree.getBranchList().get(commitTree.getCurrentBranchName());
        }
    }

    public void logChanges(String commitMessage, Set<String> stageList) throws IOException {
        //creates hash key for commit
        String commitHash = UUID.randomUUID().toString();

        Path commitFolderPath = Paths.get(".geet\\" + commitHash);
        Files.createDirectory(commitFolderPath);

        Set<String> commitFiles = new HashSet<>();
        //read commitTree from commitLog.json, initialize new commitTree object if none exists in json file
        if (commitTree == null){
            commitTree = new CommitTree();
        }
        else {
            File prevCommit = new File(".geet\\" + currentBranch.getTailHash());
            System.out.println("getting previous commit files from: " + prevCommit.getPath());
            File[] prevCommitFiles = prevCommit.listFiles();
            System.out.println(Arrays.toString(prevCommitFiles));

            commitFiles = copyFilesFromDirectory(prevCommitFiles, commitFiles, commitFolderPath);
        }

        for (String fileName : stageList) {
            commitFiles.add(".geet\\" + commitHash + "\\" + fileName);
        }

        copyStageFiles(stageList, commitFolderPath);
        //copyFilesToCommit(stageList, commitFolderPath);
        //call commitTree method to add
        commitTree.addToCurrentBranch(commitHash, commitMessage, stageList, commitFiles);
        commitTree.printCurrentCommitLog();
        writeCommitTree(commitTree);
    }

    public void checkOut(String hashCode) throws IOException {
        File file = new File(System.getProperty("user.dir"));
        File[] dirFiles = file.listFiles();
        System.out.println(Arrays.toString(dirFiles));

        for (File f : dirFiles) {
            if (f.getName().equals(".geet")) {
                System.out.println(".geet spared");
            }
            else {
                deleteDirectories(f);
            }
        }

        commitTree.setHeadCode(hashCode);

        String commitPath = ".geet\\" + currentBranch.getCommit(hashCode).hash;
        System.out.println("getting commit files from " + commitPath);

        File commit = new File(commitPath);
        File[] commitFiles = commit.listFiles();

        Set<String> allCommitFiles = new HashSet<>();
        System.out.println("now copying:");
        copyFilesFromDirectory(commitFiles, allCommitFiles, Paths.get(System.getProperty("user.dir")));

        writeCommitTree(commitTree);
    }

    public void initiateMerge(String branchName1, String branchName2, String commitMessage) throws IOException {
        Branch mergeOnto = commitTree.getBranchList().get(branchName1);
        Branch mergeWith = commitTree.getBranchList().get(branchName2);

        System.out.println("commit 1: " + mergeOnto.getCommit(mergeOnto.getTailHash()));
        System.out.println("commit 2: " + mergeWith.getCommit(mergeWith.getTailHash()));

        File commit1 = new File(".geet\\" + mergeOnto.getTailHash());
        File commit2 = new File(".geet\\" + mergeWith.getTailHash());

        System.out.println(Files.exists(commit1.toPath()));
        System.out.println(Files.exists(commit2.toPath()));
        System.out.println(".geet\\" + mergeOnto.getCommit(mergeOnto.getTailHash()));
        System.out.println(".geet\\" + mergeWith.getCommit(mergeWith.getTailHash()));

        Set<String> fileListBaseCommit = mergeOnto.getCommit(commitTree.getBaseMergeCommitCode()).getCommitFiles();
        Set<String> fileListCurrBranch = mergeOnto.getCommit(mergeOnto.getTailHash()).getCommitFiles();
        Set<String> fileListOtherBranch = mergeWith.getCommit(mergeWith.getTailHash()).getCommitFiles();

        System.out.println("Files of commit 1: " + fileListCurrBranch);
        System.out.println("Files of commit 2: " + fileListOtherBranch);

        UniqueFiles uniqueFiles = new UniqueFiles();
        SharedFiles sharedFiles;

        int arraySize = Math.max(fileListCurrBranch.size(), fileListOtherBranch.size());
        sharedFiles = new SharedFiles(arraySize);

        Iterator<String> currBranchIterator = fileListCurrBranch.iterator();
        int i = 0;
        while (currBranchIterator.hasNext()) {
            String x = currBranchIterator.next();
            System.out.println("X File: " + x);

            File xFile = new File(x);
            boolean shared = false;

            for (String y: fileListOtherBranch) {
                System.out.println("Y File" + y);
                File yFile = new File(y);

                if (xFile.getName().equals(yFile.getName())) {
                    System.out.println("File " + xFile.getName() + " exists in both files");

                    Iterator<String> baseCommitIterator = fileListBaseCommit.iterator();
                    boolean sharedByAll = false;
                    String z = baseCommitIterator.next();
                    System.out.println(z);

                    while (baseCommitIterator.hasNext()) {
                        File zFile = new File(z);
                        if (xFile.getName().equals(zFile.getName())) {
                            sharedFiles.addSharedFiles(z, x, y);
                            sharedByAll = true;
                            break;
                        }
                        z = baseCommitIterator.next();
                    }
                    if (!sharedByAll) {
                        sharedFiles.addSharedFiles(z, x, y);
                    }
                    fileListOtherBranch.remove(y);
                    shared = true;
                    i++;
                    break;
                }
            }
            if (!shared) {
                System.out.println("File " + x + " is unique to first branch");
                uniqueFiles.addFilePath(x);
                currBranchIterator.remove();
            }
        }

        uniqueFiles.addRemainingFiles(fileListOtherBranch);

        System.out.println("unique files: " + uniqueFiles);
        System.out.println("shared files: ");

        String commitHash = UUID.randomUUID().toString();
        Path commitFolderPath = Paths.get(".geet\\" + commitHash);
        Files.createDirectory(commitFolderPath);

        boolean mergePossible = sharedFiles.compareSharedFiles(commitFolderPath);
        if (mergePossible) {
            uniqueFiles.copyUniqueFiles(commitFolderPath);
        }

        Set<String> totalFiles = new HashSet<>();
        for (String localFilePath : sharedFiles.getLocalFilePaths()) {
            totalFiles.add(localFilePath);
        }
        for (String localFilePath : uniqueFiles.getUniqueFileArray()) {
            totalFiles.add(localFilePath);
        }

        commitTree.swapBranches(branchName1);
        commitTree.addMergedCommit(branchName2, commitHash, commitMessage, totalFiles);
        commitTree.printCurrentCommitLog();
        writeCommitTree(commitTree);
    }

    private Set<String> copyFilesFromDirectory(File[] fileArray, Set<String> commitFiles, Path copyDirectory) throws IOException {
        for (File file : fileArray){
            System.out.println("looking at file " + file.getName());
            if (file.isDirectory()){
                commitFiles = copyDirectories(file, commitFiles, copyDirectory.toString());
            }
            else {
                Path copyPath = Paths.get(copyDirectory.toString() + "\\" + file.getName());
                System.out.println("Copying file from " + file.getPath() + " to " + copyPath);
                Files.copy(file.toPath(), copyPath);
                System.out.println(file.getName() + " copied");
                commitFiles.add(copyDirectory + "\\" + file.getName());
            }
        }
        return commitFiles;
    }

    private Set<String> copyDirectories(File file, Set<String> commitFiles, String currentCopyPath) throws IOException {
        System.out.println("Source path: " + file.getPath());
        System.out.println("Commit path: " + currentCopyPath);

        if (file.isDirectory()) {
            Files.createDirectory(Paths.get(currentCopyPath + "\\" + file.getName()));
            System.out.println("Directory created at " + currentCopyPath);

            File[] fileArray = file.listFiles();
            System.out.println("Directory found at " + file.getPath());

            for (File x : fileArray) {
                commitFiles = copyDirectories(x, commitFiles,currentCopyPath + "\\" + file.getName());
            }
        }
        else{
            System.out.println("Copying file");
            Files.copy(file.toPath(), Paths.get(currentCopyPath + "\\" + file.getName()));
            commitFiles.add(currentCopyPath + "\\" + file.getName());
        }
        return commitFiles;
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
            System.out.println(fileName);

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
                            Files.createDirectory(Paths.get(commitPath));
                            System.out.println("Creating directory");
                        }
                        currentPath += "\\" + pathLayers[x + 1];
                        commitPath += "\\" + pathLayers[x + 1];
                    }
                    else {
                        Path sourcePath = Paths.get(fileName);
                        Path copyPath = Paths.get(commitPath);

                        System.out.println("Source path: " + sourcePath);
                        System.out.println("Copy path: " + copyPath);

                        Files.copy(sourcePath,
                                copyPath,
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            else {
                Path sourcePath = Paths.get(System.getProperty("user.dir") + "\\" + fileName);
                Path copyPath = Paths.get(copyDirectory + "\\" + fileName);

                System.out.println("Source path: " + sourcePath);
                System.out.println("Copy path: " + copyPath);

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

    public String switchWithPrevBranch() {
        String prevBranchName = commitTree.swapToPrevBranch();
        writeCommitTree(commitTree);
        return prevBranchName;
    }

    public String switchWithSpecifiedBranch(String branchName) {
        String prevBranchName = commitTree.swapBranches(branchName);
        writeCommitTree(commitTree);
        return prevBranchName;
    }

    public String getHeadFromCommitTree() {
        return commitTree.getHeadCode();
    }

    public void getLog() {
        commitTree.printCurrentCommitLog();
    }

    public int getNumBranchesFromCommitTree() {
        return commitTree.getBranchListLength();
    }

    public String getCurrBranchFromCommitTree() {
        return commitTree.getCurrentBranchName();
    }

    public boolean checkHasCommit(String hashCode) {
        return commitTree.checkBranchForCommit(hashCode);
    }

    public boolean checkHasBranch(String branchName) {
        return commitTree.hasBranch(branchName);
    }

    private static CommitTree readCommitTree() {
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
