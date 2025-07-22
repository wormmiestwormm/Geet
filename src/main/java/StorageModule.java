import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.time.LocalDate;

public class StorageModule {
    //private Commit root = null;
    //private Commit master = null;

    public void logChanges(String commitMessage, Set<String> stageList) throws IOException {
        int commitHash = commitMessage.hashCode();

        Graph commitLog = readCommitLog();
        if (commitLog == null){
            commitLog = new Graph();
        }
        commitLog.addCommit(commitHash, commitMessage, stageList);
        writeCommitLog(commitLog);
        //1. when commiting, a new folder is created in .geet
        //2. copy file(s) into the folder
        //3. commitLog.json then stores a new commit node containing the necessary information that make up a Commit node
        //4. when new commit is made, a new folder is created in .geet, and cycle continues
        //commitLog.addVertex();

        /*LocalDate commitDate = LocalDate.now();
        Commit newCommit = new Commit(commitHash, commitMessage, stageList, "author will be figured out in a sec", commitDate);

        if (root == null){
            root = newCommit;
            master = newCommit;
        }
        else {
            master.nextCommit = newCommit;
            newCommit.previousCommit = master;0
            master = newCommit;
        }*/

        Path commitFolderPath = Paths.get(".geet\\" + commitHash);
        Files.createDirectory(commitFolderPath);
        for (String fileName: stageList) {
            if (fileName.contains("\\")){
                String[] layeredPath = fileName.split("\\\\");
                String homeRepoPath = commitFolderPath.toString();

                for (String layer : layeredPath) {
                    Path directory = Paths.get(layer);
                    if(Files.isDirectory(directory)){
                        homeRepoPath += "\\" + layer;
                        Files.createDirectory(Paths.get(homeRepoPath));
                        System.out.println(homeRepoPath);
                    }
                    else {
                        File file = new File(fileName);
                        System.out.println(file.getPath());
                        Files.copy(file.toPath(),
                                new File( homeRepoPath + "\\" + layer).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            else {
                File file = new File(fileName);
                System.out.println(file.getPath());

                Files.copy(file.toPath(),
                        new File(commitFolderPath + "\\" + file.getPath()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }

    public Graph readCommitLog() throws IOException{
        try (Reader reader = new FileReader(".geet\\commitLog.json")){
            Type jsonCommitLog = new TypeToken<Graph>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(reader, jsonCommitLog);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void writeCommitLog(Graph newCommitLog) {
        try (Writer writer = new FileWriter(".geet\\commitLog.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(newCommitLog, writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLog(){
        return "Log will be added later";
    }
}
