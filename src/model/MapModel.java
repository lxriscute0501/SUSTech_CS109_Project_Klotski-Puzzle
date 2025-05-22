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

    public MapModel(int[][] matrix, String level) {
        this.matrix = new int[height][width];
        this.initialMatrix = new int[height][width];

        for (int i = 0; i < height; i++)
        {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, width);
            System.arraycopy(matrix[i], 0, this.initialMatrix[i], 0, width);
        }
        this.level = level;
    }

    public void setMatrix(int[][] newMatrix) {
        if (newMatrix == null || newMatrix.length != height || newMatrix[0].length != width) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }

        for (int i = 0; i < height; i++)
            System.arraycopy(newMatrix[i], 0, matrix[i], 0, width);
    }

    public void resetMap() {
        for (int i = 0; i < height; i++) {
            System.arraycopy(initialMatrix[i], 0, matrix[i], 0, width);
        }
    }

    public int getId(int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) return matrix[row][col];
        return 0;
    }

    public void setObstacle(int row, int col) {
        if (getId(row, col) == 0) {
            matrix[row][col] = -1;
        }
    }

    public void removeSoldier(int row, int col) {
        if (getId(row, col) == 4) {
            matrix[row][col] = 0;
        }
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
