import java.time.LocalDate;
import java.util.*;

public class Graph {

    private HashMap<Integer, Commit> commitLog;

    private static Commit root = null;
    private static Commit master = null;

    public Graph(){
        commitLog = new HashMap<Integer, Commit>();
    }

    public void addCommit(int commitHash, String commitMessage, Set<String> stageList){
        String commitDate = LocalDate.now().toString();
        Commit newCommit = new Commit(commitMessage, stageList, "author", commitDate);

        commitLog.put(commitHash, newCommit);

        if (root == null){
            root = newCommit;
            master = newCommit;
        }
        else {
            master.nextCommit = newCommit;
            newCommit.previousCommit = master;
            master = newCommit;
        }
    }

    /*
    public Edge addDirectedEdge(Commit fromVertex, Commit toVertex){
        if (hasEdge(fromVertex, toVertex)){
            return null;
        }

        Edge newEdge = new Edge(fromVertex, toVertex);

        return newEdge;
    }
     */

    public Commit getCommit(Commit comparedCommit, int commitHash){
        // Search the collection of vertices for a vertex with a matching label
        Commit commitCheck = comparedCommit;
        if (commitCheck.hash == commitHash){
            return commitCheck;
        }
        else if (commitCheck.nextCommit == null) {
            return null;
        }
        else{
            commitCheck = commitCheck.nextCommit;
            getCommit(commitCheck, commitHash);
            return commitCheck;
        }
    }

    // Returns the collection of all of this graph's vertices
    public Collection<Integer> getFullCommitLog() {
        return commitLog.keySet();
    }

    //depth first search
    /*public void depthFirstSearch(Commit startVertex, VertexVisitor visitor){
        Stack<Commit> vertexStack = new Stack<Commit>();
        HashSet<Commit> visitedSet = new HashSet<Commit>();

        vertexStack.push(startVertex);

        while(vertexStack.size() > 0) {
            Commit currentVertex = vertexStack.pop();
            if (!visitedSet.contains(currentVertex)) {
                visitor.visit(currentVertex);
                visitedSet.add(currentVertex);

                for(Edge edge : getEdgesFrom(currentVertex)) {
                    Commit adjVertex = edge.toVertex;
                    vertexStack.push(adjVertex);
                }
            }
        }
    }
     */
}

