import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * I, Maksim Pavlicic, attest that this code is my original work and was written in compliance with the class Academic
 * Integrity and Collaboration Policy found in the syllabus.
 */

public class BaseballElimination {

    private final String[] teams;
    private final int[] w;
    private final int[] l;
    private final int[] r;
    private final int[][] g;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        String[] lines = in.readAllLines();
        teams = new String[Integer.parseInt(lines[0])];
        w = new int[teams.length];
        l = new int[teams.length];
        r = new int[teams.length];
        g = new int[teams.length][teams.length];
        for (int i = 1; i < lines.length; i++) {
            String[] vals = lines[i].trim().split("\\s+");
            teams[i - 1] = vals[0];
            w[i - 1] = Integer.parseInt(vals[1]);
            l[i - 1] = Integer.parseInt(vals[2]);
            r[i - 1] = Integer.parseInt(vals[3]);
            for (int j = 4, c = 0; j < vals.length; j++, c++) {
                g[i - 1][c] = Integer.parseInt(vals[j]);
            }
        }
    }

    public int numberOfTeams() {
        return teams.length;
    }

    public Iterable<String> teams() {
        return new ArrayList<String>(Arrays.asList(teams));
    }

    public int wins(String team) {
        checkTeam(team);

        return w[teamIndex(team)];
    }

    public int losses(String team) {
        checkTeam(team);

        return l[teamIndex(team)];
    }

    public int remaining(String team) {
        checkTeam(team);

        return r[teamIndex(team)];
    }

    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);

        return g[teamIndex(team1)][teamIndex(team2)];
    }

    public boolean isEliminated(String team) {
        checkTeam(team);

        return (certificateOfElimination(team) != null);
    }

    /**
     * This was the most challenging because it was difficult to take the nice illustration
     * of the FlowNetwork from the specifications and translate it into code.
     */
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);

        ArrayList<String> R = new ArrayList<>();

        if (w[teamIndex(team)] + r[teamIndex(team)] < w[0]) {
            R.add(teams[0]);
            return R;
        }

        int numGameVertices = 0;
        Stack<int[]> gamePairs = new Stack<>();
        for (int i = teams.length - 2; i >= 0; i--) {
            if (!teams[i].equals(team)) {
                for (int j = teams.length - 1; j > i; j--) {
                    if (!teams[j].equals(team)) {
                        numGameVertices++;
                        gamePairs.push(new int[]{i, j});
                    }
                }
            }
        }

        FlowNetwork maxFlow = new FlowNetwork(teams.length + numGameVertices + 2);
        
        for (int i = 1; i < numGameVertices + 1; i++) {
            int[] gameVertex = gamePairs.pop();
            int gameVertexT1 = gameVertex[0];
            int gameVertexT2 = gameVertex[1];
            maxFlow.addEdge(new FlowEdge(0, i, g[gameVertexT1][gameVertexT2]));
            maxFlow.addEdge(new FlowEdge(i, i + (numGameVertices - i + 1) + gameVertexT1, Double.POSITIVE_INFINITY));
            maxFlow.addEdge(new FlowEdge(i, i + (numGameVertices - i + 1) + gameVertexT2, Double.POSITIVE_INFINITY));
        }

        for (int j = 1; j <= teams.length; j++) {
            if (!teams[j - 1].equals(team)) {
                int capacity = w[teamIndex(team)] + r[teamIndex(team)] - w[j - 1];
                if (capacity < 0) {
                    R.add(teams[j - 1]);
                    return R;
                } else {
                    maxFlow.addEdge(new FlowEdge(numGameVertices + j, maxFlow.V() - 1, w[teamIndex(team)] +
                            r[teamIndex(team)] - w[j - 1]));
                }
            }
        }

        FordFulkerson minCut = new FordFulkerson(maxFlow, 0, maxFlow.V() - 1);
        for (int v = numGameVertices + 1; v < maxFlow.V() - 1; v++) {
            //System.out.println("v: " + v);
            if (minCut.inCut(v) && !teams[v - numGameVertices - 1].equals(team)) {
                R.add(teams[v - numGameVertices - 1]);
            }
        }

        if (!R.isEmpty()) {
            return R;
        }

        return null;
    }

    private int teamIndex(String team) {
        for (int i = 0; i < teams.length; i++) {
            if (teams[i].equals(team)) {
                return i;
            }
        }

        return -1;
    }

    private void checkTeam(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }

        boolean found = false;
        for (int i = 0; i < teams.length; i++) {
            if (teams[i].equals(team)) {
                found = true;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Team " + team + " not found");
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
