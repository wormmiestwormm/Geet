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
        commitLog = new HashMap<>();
    }

    public void addNewCommit(String commitHash, String commitMessage, Set<String> stageList, Set<String> commitFiles) {
        String commitDate = LocalDate.now().toString();
        Commit newCommit = new Commit(commitHash, commitMessage, stageList, commitFiles, commitDate);

        commitLog.put(commitHash, newCommit);

        if (rootHash == null){
            rootHash = newCommit.hash;
        }
        else {
            commitLog.get(tailHash).nextCommit = newCommit.hash;
            newCommit.previousCommit = tailHash;
        }
        tailHash = newCommit.hash;
    }

    public void addNewBranchCommit(Commit commit) {
        commitLog.put(commit.hash, commit);
        if (rootHash == null){
            rootHash = commit.hash;
        }
        else {
            commitLog.get(tailHash).nextCommit = commit.hash;
            commit.previousCommit = tailHash;
        }
        tailHash = commit.hash;
    }

    public void createMergedCommit(String commitHash, String commitMessage, Set<String> totalFiles) {
        String commitDate = LocalDate.now().toString();

        Commit mergeCommit = new Commit(commitHash, commitMessage, totalFiles, totalFiles, commitDate);
        commitLog.put(commitHash, mergeCommit);
        mergeCommit.previousCommit = tailHash;
        tailHash = commitHash;
    }

    public boolean hasCommit(String commitHash) {
        boolean hasCommitConfirm = commitLog.containsKey(commitHash);
        return hasCommitConfirm;
    }

    public Commit getCommit(String commitHash) {
        return commitLog.get(commitHash);
    }

    public String getBranchName () {
        return branchName;
    }

    public String getTailHash() {
        return tailHash;
    }

    public String getRootHash(){
        return rootHash;
    }

    public HashMap<String, Commit> getCommitLog() {
        return commitLog;
    }
}
