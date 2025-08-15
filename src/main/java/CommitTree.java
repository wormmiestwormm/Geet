import java.util.*;

public class CommitTree {

    private HashMap<String, Branch> branchList;
    private String headCode;
    private String currentBranchName;
    private String previousBranchName;
    private String baseMergeCommitCode;

    public CommitTree(){
        branchList = new HashMap<>();
        branchList.put("master", new Branch("master"));
        currentBranchName = "master";
        previousBranchName = null;
        headCode = null;
        baseMergeCommitCode = null;
    }

    public void addToCurrentBranch(String commitHash, String commitMessage, Set<String> stageList, Set<String> commitFiles) {
        System.out.println(currentBranchName);
        branchList.get(currentBranchName).addNewCommit(commitHash, commitMessage, stageList, commitFiles);
    }

    public void addMergedCommit(String branchName2, String commitHash, String commitMessage, Set<String> totalFiles) {
        branchList.get(branchName2).getCommit(branchList.get(branchName2).getTailHash()).setNextCommit(commitHash);
        branchList.get(currentBranchName).getCommit(branchList.get(currentBranchName).getTailHash()).setNextCommit(commitHash);
        branchList.get(currentBranchName).createMergedCommit(commitHash, commitMessage, totalFiles);
    }

    public boolean checkBranchForCommit(String commitHash) {
        return branchList.get(currentBranchName).hasCommit(commitHash);
    }

    public void createNewBranch(String branchName) {
        Branch newBranch = new Branch(branchName);
        if (previousBranchName == null){
            previousBranchName = "master";
        }
        Branch prevBranch = branchList.get(previousBranchName);
        Commit commit;
        if (headCode == null) {
            commit = prevBranch.getCommit(prevBranch.getTailHash());
            headCode = commit.hash;
        }
        else {
            commit = prevBranch.getCommit(headCode);
        }
        newBranch.addNewBranchCommit(commit);
        baseMergeCommitCode = commit.hash;

        branchList.put(branchName, newBranch);
        System.out.println(currentBranchName);
        System.out.println(swapBranches(branchName));
        System.out.println(branchList.keySet() + "\n" + currentBranchName);
    }

    public boolean hasBranch(String branchName) {
        return branchList.containsKey(branchName);
    }

    public String swapToPrevBranch() {
        String tempHolder = currentBranchName;
        currentBranchName = previousBranchName;
        previousBranchName = tempHolder;

        return previousBranchName;
    }

    public String swapBranches(String branchName) {
        if (!branchList.containsKey(branchName)){
            return branchName;
        }
        else {
            previousBranchName = currentBranchName;
            currentBranchName = branchName;
            return previousBranchName;
        }
    }

    public void setHeadCode(String hashcode) {
        headCode = hashcode;

    }

    public String getHeadCode() {
        return headCode;
    }

    public String getBaseMergeCommitCode() {
        return baseMergeCommitCode;
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public HashMap<String, Branch> getBranchList() {
        return branchList;
    }

    public int getBranchListLength() {
        return branchList.size();
    }

    // Returns the collection of all of this graph's vertices
    public void printCurrentCommitLog() {
        System.out.println(currentBranchName + ":");
        System.out.println();

        Branch printBranch = branchList.get(currentBranchName);
        Commit commit = printBranch.getCommit(branchList.get(currentBranchName).getTailHash());

        while (commit != null) {
            if (commit.getHash().equals(headCode)) {
                System.out.println("HEAD");
            }
            System.out.println(commit);

            if (!printBranch.hasCommit(commit.previousCommit) && commit.previousCommit != null) {
                printBranch = hasCommit(commit.previousCommit);
            }
            commit = printBranch.getCommit(commit.previousCommit);
        }
    }

    private Branch hasCommit(String hashCode) {
        System.out.println("\tChecking commit tree for " + hashCode);
        Branch printBranch = null;
        for (Branch branch : branchList.values()) {
            System.out.println("\tchecking " + branch.getBranchName());
            if (branch.hasCommit(hashCode)) {
                System.out.println("\tcommit found in " + branch.getBranchName());
                printBranch = branch;
            }
        }

        if (printBranch == null) {
            System.out.println("\tCommit does not exist in entire commit tree");
        }
        return printBranch;
    }
}

