import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

/**
 * I, Maksim Pavlicic, attest that this code is my original work and was written in compliance with the class Academic
 * Integrity and Collaboration Policy found in the syllabus.
 */

public class BoggleSolver {

    private final fastTrie dictionary;
    private boolean[][] marked;
    private ArrayList<String> validWords;
    private int rows, cols;
    private BoggleBoard board;

    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionary cannot be null");
        }

        this.dictionary = new fastTrie();

        for (String s : dictionary) {
            this.dictionary.add(s);
        }

    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }

        rows = board.rows();
        cols = board.cols();
        this.board = board;
        marked = new boolean[rows][cols];
        validWords = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                enumeratePath("", r, c);
            }
        }

        return validWords;
    }

    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word cannot be null");
        }

        if (dictionary.contains(word) && word.length() > 2) {
            if (word.length() <= 4) {
                return 1;
            } else if (word.length() == 5) {
                return 2;
            } else if (word.length() == 6) {
                return 3;
            } else if (word.length() == 7) {
                return 5;
            } else {
                return 11;
            }
        }

        return 0;
    }

    private void enumeratePath(String path, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || marked[r][c] || dictionary.containsKeyWithPrefix(path)) {
            return;
        }

        char letter = board.getLetter(r, c);

        if (letter == 'Q') {
            path = path.concat("QU");
        } else {
            path = path.concat(String.valueOf(letter));
        }
        if (path.length() > 2 && dictionary.contains(path) && !validWords.contains(path)) {
            validWords.add(path);
        }

        marked[r][c] = true;
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                enumeratePath(path, r + y, c + x);
            }
        }
        marked[r][c] = false;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }


    /**
     * I found writing my own trie with a faster keysWithPrefix to be the most difficult, or really, the most
     * frustrating. I wrote the above code thinking I was done only to realize that, of course, keysWithPrefix
     * is way too slow, and now I need to somehow think of a way to make it faster. Ruined my happiness.
     * Fortunately, algs4 does have its own TrieSET that is pretty similar, and is a good place to start making
     * a new one.
     */
    private class fastTrie {

        private Node root;

        private class Node {
            private boolean isWord;
            private Node[] next = new Node[26];
        }

        public void add(String key) {
            root = add(root, key, 0);
        }

        private Node add(Node x, String key, int d) {
            if (x == null) {
                x = new Node();
            }
            if (d == key.length()) {
                x.isWord = true;
            } else {
                char letter = key.charAt(d);
                x.next[letter-'A'] = add(x.next[letter-'A'], key, d+1);
            }

            return x;
        }

        public boolean contains(String key) {
            Node x = get(root, key, 0);
            if (x == null) {
                return false;
            }

            return x.isWord;
        }

        public boolean containsKeyWithPrefix(String key) {
            Node x = get(root, key, 0);

            return x == null;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) {
                return null;
            }

            if (d == key.length()) {
                return x;
            }

            char letter = key.charAt(d);

            return get(x.next[letter-'A'], key, d+1);
        }
    }

}
