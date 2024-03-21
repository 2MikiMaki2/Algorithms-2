import edu.princeton.cs.algs4.Picture;
import java.lang.Math;

/**
 * I, Maksim Pavlicic, attest that this code is my original work and was written in compliance with the class Academic
 * Integrity and Collaboration Policy found in the syllabus.
 */

/**
 * The most challenging part of SeamCarver was understanding how to make the algo work without using an actual
 * DAG, since that is how Bob Sedgwick's videos taught us. But, the algo was super cool so that's ok.
 */

public class SeamCarver {

    private Picture picture;
    private double[][] energyGraph;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture argument is null");
        }
        this.picture = new Picture(picture);
        energyGraph = new double[picture.height()][picture.width()];

        calculateEnergyGraph();
    }

    public Picture picture() {
        return new Picture(this.picture);
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int x, int y) {
        checkIndices(x, y);

        return getPixelEnergy(x, y);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] path = new int[energyGraph.length];
        double[][] distTo = new double[energyGraph.length][energyGraph[0].length];
        int[][] edgeTo = new int[energyGraph.length][energyGraph[0].length];

        for (int i = 0; i < distTo.length; i++) {
            for (int j = 0; j < distTo[0].length; j++) {
                distTo[i][j] = Integer.MAX_VALUE;
            }
        }

        for (int r = 0; r < energyGraph.length; r++) {
            for (int c = 0; c < energyGraph[0].length; c++) {
                if (r == 0 || c == 0 || c == energyGraph[0].length - 1) {
                    edgeTo[r][c] = c;
                    continue;
                }

                if (r == 1) {
                    edgeTo[r][c] = c;
                    distTo[r][c] = energyGraph[r][c];
                }

                double sumLeft = energyGraph[r][c] + distTo[r - 1][c - 1];
                double sumCenter = energyGraph[r][c] + distTo[r - 1][c];
                double sumRight = energyGraph[r][c] + distTo[r - 1][c + 1];

                if (sumLeft < distTo[r][c]) {
                    distTo[r][c] = sumLeft;
                    edgeTo[r][c] = c - 1;
                }

                if (sumCenter < distTo[r][c]) {
                    distTo[r][c] = sumCenter;
                    edgeTo[r][c] = c;
                }

                if (sumRight < distTo[r][c]) {
                    distTo[r][c] = sumRight;
                    edgeTo[r][c] = c + 1;
                }
            }
        }

        int shortestPathIndex = 0;
        for (int j = 1; j < distTo[0].length; j++) {
            if (distTo[distTo.length - 1][shortestPathIndex] > distTo[distTo.length - 1][j]) {
                shortestPathIndex = j;
            }
        }

        path[path.length - 1] = shortestPathIndex;
        int columnIndex = shortestPathIndex;
        for (int p = edgeTo.length - 1; p > 0; p--) {
            path[p - 1] = edgeTo[p][columnIndex];
            columnIndex = edgeTo[p][columnIndex];
        }

        return path;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();

        int[] result = findVerticalSeam();

        transpose();

        return result;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        verticalCheck(seam);
        Picture newP = new Picture(width() - 1, height());

        for (int r = 0; r < height(); r++) {
            int newPIndex = 0;
            for (int c = 0; c < width(); c++) {
                if (c != seam[r]) {
                    newP.setRGB(newPIndex, r, picture.getRGB(c, r));
                    newPIndex++;
                }
            }
        }

        picture = newP;
        energyGraph = new double[picture.height()][picture.width()];
        calculateEnergyGraph();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        horizontalCheck(seam);
        transpose();

        removeVerticalSeam(seam);

        transpose();
    }

    private void transpose() {
        double[][] transposedGraph = new double[energyGraph[0].length][energyGraph.length];
        Picture transposedPicture = new Picture(height(), width());
        for (int r = 0; r < energyGraph.length; r++) {
            for (int c = 0; c < energyGraph[0].length; c++) {
                transposedGraph[c][r] = energyGraph[r][c];
                transposedPicture.setRGB(r, c, picture.getRGB(c, r));
            }
        }

        picture = transposedPicture;
        energyGraph = transposedGraph;
    }

    private void calculateEnergyGraph() {
        for (int r = 0; r < energyGraph.length; r++) {
            for (int c = 0; c < energyGraph[0].length; c++) {
                if (r == 0 || c == 0 || r == energyGraph.length - 1 || c == energyGraph[0].length - 1) {
                    energyGraph[r][c] = 1000.0;
                } else {
                    energyGraph[r][c] = getPixelEnergy(c, r);
                }
            }
        }
    }

    private double getPixelEnergy(int x, int y) {
        if (x == 0 || x == picture.width() - 1 || y == 0 || y == picture.height() - 1) {
            return 1000.0;
        }

        double xRed = getPixelColor(x - 1, y)[0] - getPixelColor(x + 1, y)[0];
        double xGreen = getPixelColor(x - 1, y)[1] - getPixelColor(x + 1, y)[1];
        double xBlue = getPixelColor(x - 1, y)[2] - getPixelColor(x + 1, y)[2];

        double yRed = getPixelColor(x, y + 1)[0] - getPixelColor(x, y - 1)[0];
        double yGreen = getPixelColor(x, y + 1)[1] - getPixelColor(x, y - 1)[1];
        double yBlue = getPixelColor(x, y + 1)[2] - getPixelColor(x, y - 1)[2];

        double xChange = (xRed * xRed) + (xGreen * xGreen) + (xBlue * xBlue);
        double yChange = (yRed * yRed) + (yGreen * yGreen) + (yBlue * yBlue);

        return Math.sqrt(xChange + yChange);

    }

    private double[] getPixelColor(int x, int y) {
        int colorBits = picture.getRGB(x, y);
        double[] color = new double[3];
        color[0] = (colorBits >> 16) & 0xFF;
        color[1] = (colorBits >> 8) & 0xFF;
        color[2] = colorBits & 0xFF;

        return color;
    }

    private void checkIndices(int x, int y) {
        if (x < 0 || x >= picture.width()) {
            throw new IllegalArgumentException("x index outside prescribed range");
        }

        if (y < 0 || y >= picture.height()) {
            throw new IllegalArgumentException("y index outside prescribed range");
        }
    }

    private void horizontalCheck(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Horizontal seam is null");
        }
        if (energyGraph.length <= 1) {
            throw new IllegalArgumentException("Picture height is less than or equal to 1");
        }
        if (seam.length != energyGraph[0].length) {
            throw new IllegalArgumentException("Horizontal seam of incorrect length");
        }

        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("Invalid seam");
            }
        }
    }

    private void verticalCheck(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Vertical seam is null");
        }
        if (energyGraph[0].length <= 1) {
            throw new IllegalArgumentException("Picture width is less than or equal to 1");
        }
        if (seam.length != energyGraph.length) {
            throw new IllegalArgumentException("Horizontal seam of incorrect length");
        }

        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("Invalid seam");
            }
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

}