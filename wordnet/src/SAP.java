import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * I, Maksim Pavlicic, attest that this code is my original work and was written in compliance with the class Academic
 * Integrity and Collaboration Policy found in the syllabus.
 */

public class SAP {

    private final Digraph graph;
    private final int[] shortestAncestor;

    public SAP(Digraph G) {
        graph = new Digraph(G);
        shortestAncestor = new int[1];
        shortestAncestor[0] = -1;
    }

    public int length(int v, int w) {
        if ((v < 0 || v > graph.V()) || (w < 0 || w > graph.V())) {
            throw new IllegalArgumentException("Vertex outside prescribed range");
        }

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int ancestor = 0;
        int distance = Integer.MAX_VALUE;
        int temp = 0;
        for (int i = 0; i < graph.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                temp = bfsV.distTo(i) + bfsW.distTo(i);
                if (temp < distance) {
                    distance = temp;
                    ancestor = i;
                }
            }
        }

        if (distance == Integer.MAX_VALUE) {
            shortestAncestor[0] = -1;
            return -1;
        }

        shortestAncestor[0] = ancestor;
        return distance;
    }

    public int ancestor(int v, int w) {
        if ((v < 0 || v > graph.V()) || (w < 0 || w > graph.V())) {
            throw new IllegalArgumentException("Vertex outside prescribed range");
        }

        length(v, w);

        return shortestAncestor[0];
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Iterable is null");
        }

        int checkV = checkIterableVertices(v);
        int checkW = checkIterableVertices(w);
        if (checkV == -1 || checkW == -1) {
            return -1;
        }

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);

        int ancestor = 0;
        int distance = Integer.MAX_VALUE;
        int temp = 0;
        for (int i = 0; i < graph.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                temp = bfsV.distTo(i) + bfsW.distTo(i);
                if (temp < distance) {
                    distance = temp;
                    ancestor = i;
                }
            }
        }

        if (distance == Integer.MAX_VALUE) {
            shortestAncestor[0] = -1;
            return -1;
        }

        shortestAncestor[0] = ancestor;
        return distance;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Iterable is null");
        }

        length(v, w);

        return shortestAncestor[0];
    }

    //Performs all checks on iterable vertices
    private int checkIterableVertices(Iterable<Integer> vertices) {
        int count = 0;
        for (Integer vertex : vertices) {
            count++;
            if (vertex == null) {
                throw new IllegalArgumentException("A vertex is null");
            }
        }

        if (count == 0) {
            return -1;
        }

        return 0;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}