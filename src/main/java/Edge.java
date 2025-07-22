public class Edge {
    public Commit fromVertex;
    public Commit toVertex;

    public Edge(Commit from, Commit to) {
        fromVertex = from;
        toVertex = to;
    }

    public String toString(){
        return fromVertex + " " + toVertex;
    }
}