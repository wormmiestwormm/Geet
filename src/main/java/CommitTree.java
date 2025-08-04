import java.util.*;

public class CommitTree {

    private HashMap<String, Branch> branchList;
    private String headCode;
    private String currentBranchName;
    private String previousBranchName;

    public CommitTree(){
        branchList = new HashMap<>();
        branchList.put("master", new Branch("master"));
        currentBranchName = "master";
        previousBranchName = null;
        headCode = null;
    }

    public void addToCurrentBranch(String commitHash, String commitMessage, Set<String> stageList){
        System.out.println(currentBranchName);
        branchList.get(currentBranchName).addNewCommit(commitHash, commitMessage, stageList);
        System.out.println(branchList.get(currentBranchName).getCommit(commitHash));
    }

    public boolean checkBranchForCommit(String commitHash){
        return branchList.get(currentBranchName).hasCommit(commitHash);
    }

    public void createNewBranch(String branchName){
        Branch newBranch = new Branch(branchName);
        if (previousBranchName == null){
            previousBranchName = "master";
        }
        Branch prevBranch = branchList.get(previousBranchName);
        Commit commit;
        if (headCode == null){
            commit = prevBranch.getCommit(prevBranch.getTailHash());
            headCode = commit.hash;
        }
        else {
            commit = prevBranch.getCommit(headCode);
        }
        newBranch.addCommit(commit);

        branchList.put(branchName, newBranch);
        System.out.println(currentBranchName);
        System.out.println(swapBranches(branchName));
        System.out.println(branchList.keySet() + "\n" + currentBranchName);
    }

    public boolean hasBranch(String branchName) {
        return branchList.containsKey(branchName);
    }

    public void swapToPrevBranch() {
        String tempHolder = currentBranchName;
        currentBranchName = previousBranchName;
        previousBranchName = tempHolder;
    }

    public boolean swapBranches(String branchName){
        if (!branchList.containsKey(branchName)){
            return false;
        }
        else {
            previousBranchName = currentBranchName;
            currentBranchName = branchName;
            return true;
        }
    }

    public void setHeadCode(String hashcode) {
        headCode = hashcode;

    }

    public String getHeadCode(){
        return headCode;
    }

    public String getCurrentBranchName(){
        return currentBranchName;
    }

    public HashMap<String, Branch> getBranchList(){
        return branchList;
    }

    public int getBranchListLength(){
        return branchList.size();
    }

    // Returns the collection of all of this graph's vertices
    public void printCurrentCommitLog(){
        Commit commit = branchList.get(currentBranchName).getCommit(branchList.get(currentBranchName).getTailHash());

        while (commit != null){
            System.out.println(commit);
            commit = branchList.get(currentBranchName).getCommit(commit.previousCommit);
        }
    }
}

