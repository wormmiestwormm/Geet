import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

public class Branch {
    private final HashMap<String, Commit> commitLog;

    private String rootHash;
    private String tailHash;
    private String branchName;

    public Branch(String branchName) {
        this.branchName = branchName;
        commitLog = new HashMap<String, Commit>();
    }

    public void addNewCommit(String commitHash, String commitMessage, Set<String> stageList){
        String commitDate = LocalDate.now().toString();
        Commit newCommit = new Commit(commitHash, commitMessage, stageList, "author", commitDate);

        commitLog.put(commitHash, newCommit);

        if (rootHash == null){
            rootHash = newCommit.hash;
            tailHash = newCommit.hash;
        }
        else {
            commitLog.get(tailHash).nextCommit = newCommit.hash;
            newCommit.previousCommit = tailHash;
            tailHash = newCommit.hash;
        }
    }

    public void addCommit(Commit commit){
        commitLog.put(commit.hash, commit);
        tailHash = commit.hash;
    }

    public boolean hasCommit(String commitHash){
        return commitLog.containsKey(commitHash);
    }

    public Commit getCommit(String commitHash){
        return commitLog.get(commitHash);
    }


    public String getBranchName (){
        return branchName;
    }

    public String getRootHash(){
        return rootHash;
    }

    public String getTailHash() {
        return tailHash;
    }
}
