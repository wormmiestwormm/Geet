import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class StagingArea {
    private FileOutputStream stageFile;
    private static Path repoPath = Paths.get(".geet");

    public StagingArea() throws IOException {
    }

    //adds file to staging area
    public boolean addFileToStagingArea(File newFile) throws IOException {
        Set<String> stageList = readStage();

        if (stageList == null){
            stageList = new HashSet<>();
        }
        if (!stageList.add(newFile.getName())){
            return false;
        }
        writeStage(stageList, newFile);
        return true;
    }

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

    public static void writeStage(Set<String> stageList, File newFile) {
        try (Writer writer = new FileWriter(".geet\\stagingArea.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(stageList, writer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //adds files in staging area to repository in storage module
    public void commitChanges(){
        //Files.copy(newFile.toPath(),
        //        new File(repoPath + "\\" + newFile.getName()).toPath(),
        //        StandardCopyOption.REPLACE_EXISTING);
    }
}
