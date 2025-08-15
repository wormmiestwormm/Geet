import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class StagingArea {
    Set<String> stageList;
    public StagingArea() {
        stageList = readStage();
    }

    //adds file to staging area
    public boolean addFileToStagingArea(File newFile) {
        if (stageList == null) {
            stageList = new HashSet<>();
        }
        if (!stageList.add(newFile.getPath())){
            return false;
        }
        writeStage(stageList);
        return true;
    }

    public boolean hasFiles() {
        return stageList != null && !stageList.isEmpty();
    }

    //reads arraylist of file names from stagingArea.json
    private static Set<String> readStage() {
        try (Reader reader = new FileReader(".geet\\stagingArea.json")) {
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
        try (Writer writer = new FileWriter(".geet\\stagingArea.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(stageList, writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //adds files in staging area to repository in storage module
    public Set<String>getStageList() {
        Set<String> emptyList = readStage();
        emptyList.clear();
        writeStage(emptyList);
        return stageList;
    }
}
