package model;

/**
 * This class is to record the map of one game. For example:
 */
public class MapModel {
    int[][] matrix;
    private int exitRow;
    private int exitCol;


        public void setExitPosition(int row, int col) {
        this.exitRow = row;
        this.exitCol = col;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }


    public MapModel(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getWidth() {
        return this.matrix[0].length;
    }

    public int getHeight() {
        return this.matrix.length;
    }

    public int getId(int row, int col) {
        return matrix[row][col];
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < matrix[0].length;
    }

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < matrix.length;
    }

    public void setId(int row, int col, int value) {
        matrix[row][col] = value;
    }

}
