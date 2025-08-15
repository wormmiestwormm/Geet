import java.util.Set;

public class Commit {
    //Each commit contains its hash, commit message, set of file names, date, and is assigned the previous and next commit in line.
    //Modified portions of pre-existing files was considered, but due to the time constraints, it was not added.
    public String hash;
    public String message;
    public Set<String> updatedFiles;
    public Set<String> allFiles;
    public String date;

    public String previousCommit;
    public String nextCommit;

    public Commit(String hash, String message, Set<String> stagingAreaFiles, Set<String> commitFiles, String date) {
        this.hash = hash;
        this.message = message;
        updatedFiles = stagingAreaFiles;
        allFiles = commitFiles;
        this.date = date;

        previousCommit = null;
        nextCommit = null;
    }

    public String toString() {
        StringBuilder printOut = new StringBuilder(hash +
                "\nDate commited: " + date +
                "\nmessage:" +
                "\n\t" + message +
                "\nfiles:" + "\n\t");

        for (String fileName: updatedFiles) {
            printOut.append(fileName).append("\n\t");
        }
        return printOut.toString();
    }

    public void setNextCommit(String nextCommit){
        this.nextCommit = nextCommit;
    }

    public String getHash() {
        return hash;
    }

    public Set<String> getCommitFiles() {
        return allFiles;
    }
}