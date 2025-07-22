class PrintVertexVisitor implements VertexVisitor {
    public void visit(Commit vertex) {
        System.out.print(vertex.hash + " ");
    }
}