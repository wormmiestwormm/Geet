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
    Set<String> stageList;
    public StagingArea() throws IOException {
        stageList = readStage();
    }

    //adds file to staging area
    public boolean addFileToStagingArea(File newFile) throws IOException {
        if (stageList == null){
            stageList = new HashSet<>();
        }
        if (!stageList.add(newFile.getPath())){
            return false;
        }
        writeStage(stageList);
        return true;
    }

    public boolean hasFiles() throws IOException {
        if (stageList == null || stageList.isEmpty()){
            return false;
        }
        else{
            return true;
        }
    }

    //reads arraylist of file names from stagingArea.json
    private static Set<String> readStage() throws IOException{
        try (Reader reader = new FileReader(".geet\\stagingArea.json")){
            Type jsonStageList = new TypeToken<Set<String>>() {}.getType();
            Gson gson = new Gson();
            Set<String> set = gson.fromJson(reader, jsonStageList);
            reader.close();
            return set;
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //adds updated arraylist to stagingArea.json
    private static void writeStage(Set<String> stageList) {
        try (Writer writer = new FileWriter(".geet\\stagingArea.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(stageList, writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //adds files in staging area to repository in storage module
    public Set<String>getStageList() throws IOException {
        Set<String> emptyList = readStage();
        emptyList.clear();
        writeStage(emptyList);
        return stageList;
    }
}
