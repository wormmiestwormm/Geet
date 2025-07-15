import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.google.gson.*;

public class StagingArea {
    //constructor
    private static ArrayList<File> stageList = new ArrayList<>();
    private FileOutputStream stageFile;

    private Path repoPath = Paths.get(".geet");

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public StagingArea() throws IOException {
        FileWriter writer = new FileWriter(repoPath + "\\" + "stage.json");
    }

    //adds file to staging area
    public void addFileToStagingArea(File newFile) throws IOException {
        stageList.add(newFile);
        try {
            Reader reader = Files.newBufferedReader(Paths.get("stage.json"));
            stageList = gson.fromJson(reader, (Type) stageList);
            reader.close();


            System.out.println(stageList);

            FileWriter writer = new FileWriter(repoPath + "\\" + "stage.json");
            gson.toJson(stageList, writer);
            writer.close();
        }
        catch(IOException e) {

        }
    }

    //adds files in staging area to repository in storage module
    public void commitChanges(){
        //Files.copy(newFile.toPath(),
        //        new File(repoPath + "\\" + newFile.getName()).toPath(),
        //        StandardCopyOption.REPLACE_EXISTING);
    }
}
