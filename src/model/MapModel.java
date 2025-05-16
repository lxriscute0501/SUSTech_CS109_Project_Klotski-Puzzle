package model;

/**
 * This class is to record the map of one game. For example:
 */

public class MapModel {
    private final int[][] matrix;
    private final int[][] initialMatrix;
    private final int width = 5;
    private final int height = 4;
    private String level;
    private int[][] exitPositions;

    public MapModel(int[][] matrix, String level, int[][] exit) {
        this.matrix = matrix;
        this.initialMatrix = matrix;
        this.level = level;
        this.exitPositions = exit;
    }

    public void setMatrix(int[][] newMatrix) {
        if (newMatrix == null || newMatrix.length != height || newMatrix[0].length != width) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }

        for (int i = 0; i < height; i++)
            System.arraycopy(newMatrix[i], 0, matrix[i], 0, width);
    }

    // restore from initial matrix
    public void resetMap() {
        for (int i = 0; i < height; i++) {
            System.arraycopy(initialMatrix[i], 0, matrix[i], 0, width);
        }
    }

    public int[][] getExitPositions() {
        return exitPositions;
    }

    public void setExitPositions(int[][] exitPositions) {
        this.exitPositions = exitPositions;
    }

    public void setId(int row, int col, int id) {
        matrix[row][col] = id;
    }

    public int getId(int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) return matrix[row][col];
        return 0;
    }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int[][] getMatrix() { return matrix; }

    public String getLevel() { return level; }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < matrix[0].length;
    }

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < matrix.length;
    }
}
