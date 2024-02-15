import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

/**
 * I, Maksim Pavlicic, attest that this code is my original work and was written in compliance with the class Academic
 * Integrity and Collaboration Policy found in the syllabus.
 */

public class WordNet {

    private final Digraph graph;
    private final Map<String, Bag<Integer>> nounsToIDs;
    private final Map<Integer, String> IDsToSynsets;
    private String[] synsets;
    private String[] hypernyms;
    private SAP sap;

    /**
     * I found that this constructor was especially difficult. I have almost no experience with reading and parsing
     * files from the command line, so that confused me quite a bit. And then after that, I was super lost on what
     * this constructor actually needed to do. Only after reading some of Hong's tips, and looking at the rest
     * of the methods in this class, did I understand what this constructor should do. And then implementation was
     * also tricky, because of the use of regular expressions.
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("Null argument(s)");
        }

        nounsToIDs = new HashMap<>();
        IDsToSynsets = new HashMap<>();

        this.hypernyms = new In(hypernyms).readAllLines();
        this.synsets = new In(synsets).readAllLines();

        graph = new Digraph(this.hypernyms.length);

        hyp();
        syn();

        sap = new SAP(graph);
    }

    public Iterable<String> nouns() {
        return nounsToIDs.keySet();
    }

    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word is null");
        }

        if (nounsToIDs.containsKey(word)) {
            return true;
        }

        return false;
    }

    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Not a WordNet noun");
        }

        return sap.length(nounsToIDs.get(nounA), nounsToIDs.get(nounB));
    }

    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Not a WordNet noun");
        }

        return IDsToSynsets.get(sap.ancestor(nounsToIDs.get(nounA), nounsToIDs.get(nounB)));
    }

    //Creates graph using hypernyms
    private void hyp() {
        for (String line : this.hypernyms) {
            String[] vertices = line.split(",");
            for (int i = 1; i < vertices.length; i++) {
                graph.addEdge(Integer.parseInt(vertices[0]), Integer.parseInt(vertices[i]));
            }
        }

        checkDAG();
    }

    //Creates two maps for distance() and sap()
    private void syn() {
        for (String line : this.synsets) {
            String[] components = line.split(",");
            IDsToSynsets.put(Integer.parseInt(components[0]), components[1]);
            String[] nouns = components[1].split(" ");
            for (String noun : nouns) {
                if (nounsToIDs.containsKey(noun)) {
                    nounsToIDs.get(noun).add(Integer.parseInt(components[0]));
                } else {
                    Bag<Integer> newID = new Bag<>();
                    newID.add(Integer.parseInt(components[0]));
                    nounsToIDs.put(noun, newID);
                }
            }
        }
    }

    //Checks if the graph is rooted or not
    private void checkDAG() {
        DirectedCycle cycle = new DirectedCycle(graph);
        if (cycle.hasCycle()) {
            throw new IllegalArgumentException("Not an acyclic digraph");
        }

        int count = 0;
        for (int i = 0; i < graph.V(); i++) {
            if (graph.outdegree(i) == 0 && graph.indegree(i) > 0) {
                count++;
            }
        }
        if (count > 1) {
            throw new IllegalArgumentException("Multiple roots");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);

        System.out.println(wordnet.distance("lower_limit", "AWOL"));
        System.out.println(wordnet.isNoun("AWOL"));
    }
}