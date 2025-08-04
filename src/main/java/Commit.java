import java.util.Set;
import java.util.UUID;

public class Commit {
    //Each commit contains its hash, commit message, set of file names, author, date, and is assigned the previous and next commit in line.
    //Modified portions of pre-existing files was considered, but due to the time constraints, it was not added.
    public String hash;
    public String message;
    public Set<String> commitFiles;
    public String author;
    public String date;

    public String previousCommit;
    public String nextCommit;

    public Commit(String hash, String message, Set<String> stagingAreaFiles, String author, String date) {
        this.hash = hash;
        this.message = message;
        commitFiles = stagingAreaFiles;
        this.author = author;
        this.date = date;

        previousCommit = null;
        nextCommit = null;
    }

    public String toString(){
        String printOut = hash +
                "\nAuthor: " + author +
                "\nDate commited: " + date +
                "\nmessage:" +
                "\n\t" + message +
                "\nfiles:" + "\n\t";

        for (String fileName: commitFiles){
            printOut += fileName + "\n\t";
        }

        return printOut;
    }

    public Set<String> getCommitFiles() {
        return commitFiles;
    }
}