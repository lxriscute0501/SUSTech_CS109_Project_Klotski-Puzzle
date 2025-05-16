package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This class is to record the map of one game. For example:
 */
public class MapModel {
    private final int[][] matrix;
    private final int[][] initialMatrix;
    private final int width = 5;
    private final int height = 4;
    private String level;

    public MapModel(int[][] matrix) {
        this.matrix = matrix;
        this.initialMatrix = matrix;
        // this.level = level;
    }

    public void setMatrix(int[][] newMatrix) {
        // I think it's optional, this case not gonna happen
        if (newMatrix == null || newMatrix.length != height || newMatrix[0].length != width) {
            throw new IllegalArgumentException("Invalid matrix dimensions");
        }

        // deep copy the new matrix
        for (int i = 0; i < height; i++)
            System.arraycopy(newMatrix[i], 0, matrix[i], 0, width);
    }

    // restore from initial matrix
    public void resetMap() {
        for (int i = 0; i < height; i++) {
            System.arraycopy(initialMatrix[i], 0, matrix[i], 0, width);
        }
    }

    public void setId(int row, int col, int id) {
        matrix[row][col] = id;
    }

    public int getId(int row, int col) {
        if (row >= 0 && row < height && col >= 0 && col < width) return matrix[row][col];
        return 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public String getLevel() {
        return level;
    }

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
