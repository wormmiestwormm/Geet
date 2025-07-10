import java.io.File;

public class ArgumentParser {
    private String[] args;
    public ArgumentParser(String[] args) {
        this.args = args;
    }

    public void getCommand(){
        switch(args[0]){
            case "init":
                File directory = new File("");
                String userDirectory = directory.getAbsolutePath();
                //Initialize geet repository using userDictionary in StagingArea or StorageModule
                System.out.println("Initialized geet repository in " + userDirectory);
                break;

            case "add":
                if (args.length < 2){
                    System.out.println("Error: No file specified" +
                            "\nPlease specify the file name: geet.bat add <file>");
                    break;
                }
                File newFile = new File(args[1]);

                if (newFile.exists()) {
                    System.out.println("Error: file name doesn't exist");
                    break;
                }
                else {
                    //add to repository in StagingArea
                    System.out.println("Added " + newFile.getName() + " to commit");
                    break;
                }

            case "commit":
                if (args.length < 2){
                    System.out.println("Error: Invalid commit" +
                            "\nPlease add commit comment: geet.bat commit <\"commit message\">");
                    break;
                }
                else {
                    System.out.println("Commit will be added later");
                    break;
                }

            case "log":
                System.out.println("log will be added later");
                break;

            case "":
                System.out.println("-----------------------------------------------------------------------------------" +
                                   "\nCommand list:" +
                        "\n\ninit - Initialize geet repository in current directory" +
                        "\nadd <file> - add listed file to staging area" +
                        "\ncommit -m <\"commit message\"> - records files in staging area in the repository" +
                        "\nlog - view commit history" +
                        "\n\n-----------------------------------------------------------------------------------");
                break;

            default:
                System.out.println("Error: Command not found");
                break;
        }
    }
}
