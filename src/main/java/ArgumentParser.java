import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ArgumentParser {
    private final String[] args;
    private static final Repository newRepo = new Repository();
    private static StorageModule storage;
    private static StagingArea stage;

    public ArgumentParser(String[] args) {
        this.args = args;
    }

    public void getCommand() throws IOException {
        if (args.length < 1) {
            System.out.println("-----------------------------------------------------------------------------------" +
                    "\nCommand list:" +
                    "\n\ninit\t\t\t\t\tInitialize geet repository in current directory" +
                    "\nadd <your_file>\t\t\t\tadd listed file to staging area" +
                    "\ncommit -m \"<your commit message>\"\trecords files in staging area in the repository" +
                    "\nlog\t\t\t\t\tview commit history" +
                    "\ncheckout" +
                    "\n\t-f <specified_commit_hash>\tmove the HEAD pointer to the specified commit" +
                    "\n\t-b <your_branch_name>\t\tcreates a new geet branch from HEAD. New commits will now be made on this branch" +
                    "\nswitch\t\t\t\t\tswitch current branch to previous branch" +
                    "\n\t -b <branch_name>\t\tswitch current branch to specified branch" +
                    "\nmerge <branch_name_1> <branch_name_2> " +
                    "\t-m \"<your commit message>\" merges two branches together. The branch listed first becomes the current branch." +
                    "\n-----------------------------------------------------------------------------------");
            return;
        }

        switch(args[0]){
            //Initializes geet repository in current directory if one doesn't already exist
            case "init":
                initializeFunction();
                break;

            //Adds a specified file to the staging area if the file exists
            case "add":
                addFunction();
                break;

            //Adds files from staging area to the repository with a commit message.
            case "commit":
                commitFunction();
                break;

            //displays the commit history
            case "log":
                storage = new StorageModule();
                storage.getLog();
                break;

            case "checkout":
                checkoutFunction();
                break;

            case "switch":
                switchFunction();
                break;

            case "merge":
                mergeFunction();
                break;

            default:
                System.out.println("Error: Command not found");
                System.out.println("-----------------------------------------------------------------------------------" +
                        "\nCommand list:" +
                        "\n\ninit\t\t\t\t\tInitialize geet repository in current directory" +
                        "\nadd <your_file>\t\t\t\tadd listed file to staging area" +
                        "\ncommit -m \"<your commit message>\"\trecords files in staging area in the repository" +
                        "\nlog\t\t\t\t\tview commit history" +
                        "\ncheckout" +
                        "\n\t-f <specified_commit_hash>\tmove the HEAD pointer to the specified commit" +
                        "\n\t-b <your_branch_name>\t\tcreates a new geet branch from HEAD. New commits will now be made on this branch" +
                        "\nswitch\t\t\t\t\tswitch current branch to previous branch" +
                        "\n\t -b <branch_name>\t\tswitch current branch to specified branch" +
                        "\nmerge <branch_name_1> <branch_name_2> -m \"<your commit message>\" merges two branches together. The branch listed first becomes the current branch." +
                        "\n-----------------------------------------------------------------------------------");
                break;
        }
    }

    //Checks for command errors, then calls Repository class which initializes the repository and repository files.
    private void initializeFunction() throws IOException {
        if (newRepo.initializeRepository()) {
            newRepo.initializeRepository();
            System.out.println("Initialized geet repository in " + newRepo.getRepoPath());
        } else {
            System.out.println("Error: geet repository already exists in " + newRepo.getParentPath());
        }
    }

    //Checks for command errors, then calls StagingArea Class where files are adds to the staging area file.
    private void addFunction() {
        stage = new StagingArea();
        storage = new StorageModule();

        if (args.length < 2) {
            System.out.println("Error: No file specified" +
                    "\nPlease specify the file name: geet add <your file>");
        }
        File newFile = new File(args[1]);

        if (!newFile.getAbsoluteFile().exists()) {
            System.out.println("Error: file name doesn't exist");
        }
        else {
            if (!stage.addFileToStagingArea(newFile)){
                System.out.println("Error: file already added to staging area");
            }
            else {
                System.out.println("Added " + newFile.getName() + " to staging area");
            }
        }
    }

    //Checks for command errors, then calls StagingArea and StorageModule classes which commits the files.
    private void commitFunction() throws IOException {
        stage = new StagingArea();
        storage = new StorageModule();

        if (!args[1].equals("-m") || args.length < 3) {
            System.out.println("Error: Invalid commit" +
                    "\nPlease add commit comment: geet commit -m \"<your commit message>\"");
        }
        else {
            Set<String> stageList = stage.getStageList();

            if (stageList == null || stageList.isEmpty()){
                System.out.println("Error: No files present in staging area for commit");
            } else {
                storage.logChanges(args[2], stageList);
                System.out.println("Files have been commited");
            }
        }
    }

    //Checks for command errors, then calls StorageModule class to create new commit branch.
    private void checkoutFunction() throws IOException {
        stage = new StagingArea();
        storage = new StorageModule();

        if (args.length < 2) {
            System.out.println("Error: No command specified" +
                    "\n\tTo move HEAD pointer: checkout -f <specified commit hash>" +
                    "\n\tTo create new branch from HEAD pointer: -b <your branch name>");
            return;
        }
        else if (stage.hasFiles()) {
            System.out.println("Error: Staging Area still contains files that have not been commited. " +
                    "Please commit them before moving the Head pointer.");
            return;
        }

        switch (args[1]) {
            case "-f":
                if (args.length < 3) {
                    System.out.println("Error: Specify the commit you want the HEAD pointer to move to" +
                            "\nTo move HEAD pointer: geet checkout -f <specified commit hash>");
                } else if (!storage.checkHasCommit(args[2])) {
                    System.out.println("Error: No commit found");
                } else {
                    storage.checkOut(args[2]);
                    System.out.println("Head pointer moved to " + args[2]);
                }
                break;

            case "-b":
                if (args.length < 3) {
                    System.out.println("Error: Specify name your new branch" +
                            "\nTo create new branch from HEAD pointer: geet checkout -b <your branch name>");
                }
                else {
                    storage.createNewBranch(args[2]);
                    System.out.println("New branch " + args[2] + " created, splitting from HEAD: " + storage.getHeadFromCommitTree() +
                            "\nNew commits will be added to this branch.");
                }
                break;
            default:
                System.out.println("Error: Unknown command" +
                        "\n\tTo move HEAD pointer: geet checkout -f <specified commit hash>" +
                        "\n\tTo create new branch from HEAD pointer: geet checkout -b <your branch name>");
        }
    }

    private void switchFunction() {
        storage = new StorageModule();
        if (args.length < 2){
            if (storage.getNumBranchesFromCommitTree() < 2){
                System.out.println("Error: No other branch exists to switch to");
            }
            else {
                String prevBranchName = storage.switchWithPrevBranch();
                String currBranchName = storage.getCurrBranchFromCommitTree();
                System.out.println("Pointer switched from " + prevBranchName + " to " + currBranchName);
            }
        }
        else{
            if (!storage.checkHasBranch(args[1])) {
                System.out.println("Error: No branch found");
            }
            else {
                String prevBranchName = storage.switchWithSpecifiedBranch(args[1]);
                System.out.println("Pointer switched from " + prevBranchName + " to " + args[1]);
            }
        }
    }

    private void mergeFunction() throws IOException {
        storage = new StorageModule();

        if (args.length != 5) {
            System.out.println("Error: Invalid merge command" +
                    "\nmerge <branch_name_1> <branch_name_2> -m \"<your commit message>\" merges two branches together. The branch listed first becomes the current branch.");
        }
        else if (!storage.checkHasBranch(args[1])) {
            System.out.println("Error: Branch " + args[1] + " is not recognized");
        }
        else if (!storage.checkHasBranch(args[2])) {
            System.out.println("Error: Branch " + args[2] + " is not recognized");
        }
        else {
            storage.initiateMerge(args[1], args[2], args[4]);
            System.out.println("Branches " + args[1] + " and " + args[2] + " have been merged");
        }
    }
}
