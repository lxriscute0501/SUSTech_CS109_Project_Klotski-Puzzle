package model;

/**
 * This class is to record the map of one game. For example:
 */
public class MapModel {
    private final int[][] matrix;
    private final int[][] initialMatrix;
    private final int width = 5;
    private final int height = 4;

    public MapModel(int[][] matrix) {
        this.matrix = matrix;
        this.initialMatrix = new int[height][width];
        defaultMap();
    }

    private void defaultMap() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
            {
                matrix[i][j] = 0;
                initialMatrix[i][j] = 0;
            }

        // CaoCao block (2x2, ID=1)
        matrix[0][1] = 1;
        matrix[0][2] = 1;
        matrix[1][1] = 1;
        matrix[1][2] = 1;

        // GuanYu block (2x1, ID=2)
        matrix[0][0] = 2;
        matrix[1][0] = 2;

        // General blocks (1x2, ID=3)
        matrix[2][0] = 3;
        matrix[2][1] = 3;
        matrix[2][2] = 3;
        matrix[2][3] = 3;

        // Soldier blocks (1x1, ID=4)
        matrix[0][3] = 4;
        matrix[1][3] = 4;
        matrix[3][1] = 4;
        matrix[3][2] = 4;

        // copy initial state
        for (int i = 0; i < height; i++)
            System.arraycopy(matrix[i], 0, initialMatrix[i], 0, width);
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

    public void resetMap() {
        // restore from initial matrix
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

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < matrix[0].length;
    }

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < matrix.length;
    }
}
