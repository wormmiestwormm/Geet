import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ArgumentParser {
    private String[] args;
    private static Repository newRepo = new Repository();
    private static StorageModule storage = new StorageModule();
    private static StagingArea stage = new StagingArea();
    public ArgumentParser(String[] args) throws FileNotFoundException {
        this.args = args;
    }

    //
    public void getCommand() throws IOException {
        if (args.length < 1){
            System.out.println("-----------------------------------------------------------------------------------" +
                    "\nCommand list:" +
                    "\n\ninit - Initialize geet repository in current directory" +
                    "\nadd <your file> - add listed file to staging area" +
                    "\ncommit <\"your commit message\"> - records files in staging area in the repository" +
                    "\nlog - view commit history" +
                    "\nbranch" +
                    "\n\n-----------------------------------------------------------------------------------");
            return;
        }

        switch(args[0]){
            //Initializes geet repository in current directory if one doesn't already exist
            case "init":
                if (newRepo.initializeRepository()){
                    newRepo.initializeRepository();
                    System.out.println("Initialized geet repository in " + newRepo.getRepoPath());
                    break;
                }
                else {
                    System.out.println("Error: geet repository already exists in " + newRepo.getParentPath());
                    break;
            }

            //Adds a specified file to the staging area if the file exists
            case "add":
                if (args.length < 2){
                    System.out.println("Error: No file specified" +
                                     "\nPlease specify the file name: geet add <your file>");
                    break;
                }
                File newFile = new File(args[1]);

                if (!newFile.getAbsoluteFile().exists()) {
                    System.out.println("Error: file name doesn't exist");
                    break;
                }
                else {
                    if (!stage.addFileToStagingArea(newFile)){
                        System.out.println("Error: file already added to staging area");
                        break;
                    }
                    else {
                        System.out.println("Added " + newFile.getName() + " to staging area");
                        break;
                    }
                }

            //Adds files from staging area to the repository with a commit message.
            case "commit":
                if (args.length < 2){
                    System.out.println("Error: Invalid commit" +
                            "\nPlease add commit comment: geet commit <your commit message>");
                    break;
                }
                else {
                    if (!stage.commitChanges(args[1])){
                        System.out.println("Error: No files present in staging area for commit");
                        break;
                    }
                    else{
                        System.out.println("Files have been commited");
                        break;
                    }
                }

            //displays the commit history
            case "log":
                System.out.println(storage.getLog());
                break;

            case "branch":
                System.out.println("branching will be added later");
                break;
            default:
                System.out.println("Error: Command not found");
                break;
        }
    }
}
