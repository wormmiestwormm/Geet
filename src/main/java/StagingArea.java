import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class StagingArea {

    public StagingArea(){}

    //adds file to staging area
    public boolean addFileToStagingArea(File newFile) throws IOException {
        Set<String> stageList = readStage();

        if (stageList == null){
            stageList = new HashSet<>();
        }
        if (!stageList.add(newFile.getPath())){
            return false;
        }
        writeStage(stageList);
        return true;
    }

    //reads arraylist of file names from stagingArea.json
    public static Set<String> readStage() throws IOException{
        try (Reader reader = new FileReader(".geet\\stagingArea.json")){
            Type jsonStageList = new TypeToken<Set<String>>() {}.getType();
            Gson gson = new Gson();
            return gson.fromJson(reader, jsonStageList);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //adds updated arraylist to stagingArea.json
    public static void writeStage(Set<String> stageList) {
        try (Writer writer = new FileWriter(".geet\\stagingArea.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(stageList, writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //adds files in staging area to repository in storage module
    public boolean commitChanges(String commitMessage) throws IOException {
        Set<String> stageList = readStage();
        if (stageList == null) {
            return false;
        }

        StorageModule s = new StorageModule();
        s.logChanges(commitMessage, stageList);

        stageList.clear();
        writeStage(stageList);
        return true;
    }
}
