package balancer;

import java.util.*;

public final class Balancer {

    private Balancer(){}

    public static long[] getCoefficients(Fraction[][] matrix) {
        // we have a matrix in RREF with elements as fractions, we can turn those into linear equations:
        // from a matrix like this where a column represents x_(col)
        //      1    0   0   -1/6
        //      0    1   0     -1
        //      0    0   1     -1
        // the equations are
        // x_1 = -1/6 * x_4
        // x_2 = -1 * x_4
        // x_3 = -1 * x_4
        // here, lowest value for x_4 to make x_1 an integer needs to be 6, from where all others can also be solved
        validateMatrix(matrix);

        int rows = matrix.length;
        int cols = matrix[0].length;

        long[] coeffs = new long[cols];

        long commonDenominator = 1;
        // find the variable corresponding to the last column
        for (int i = 0; i < rows; i++) {
            long currentDenominator = matrix[i][cols - 1].getDenominator();
            commonDenominator = Fraction.lcm(commonDenominator, currentDenominator);
        }
        coeffs[cols-1] = commonDenominator;
        Fraction commDenomFrac = new Fraction(commonDenominator);

        // find the rest of the variables
        for (int i = 0; i < cols-1; i++) {
            int idx = i;
            if (i < rows) {
                // handle offset in case of rows with extra leading zeroes
                while (matrix[i][idx].equals(Fraction.ZERO)) {
                    if (idx == (cols-1)) break;
                    idx++;
                }
                Fraction coeff = matrix[i][cols-1].multiply(commDenomFrac);
                coeffs[idx] = Math.abs(coeff.getNumerator());
            }
        }

        return coeffs;
    }
    public static Fraction[][] matrixToRREF(Fraction[][] matrix) {
        validateMatrix(matrix);

        int rows = matrix.length;
        int cols = matrix[0].length;

        int currentColumn = 0;
        int currentRow;

        for (int i = 0; i < rows; i++) {
            // skip the 0s above
            currentRow = i;

            while (matrix[currentRow][currentColumn].equals(Fraction.ZERO)) {
                currentRow++;
                if (rows == currentRow) {
                    currentRow = i;
                    currentColumn++;
                    if (cols <= currentColumn) break; // end if we're at the very "bottom right corner" of matrix
                }
            }
            if (cols <= currentColumn) break;

            swapRow(matrix, currentRow, i); // swapping the first row with nonzero value in column to the 1 diagonal
            if (!matrix[i][currentColumn].equals(Fraction.ZERO)) {
                Fraction frac = new Fraction(matrix[i][currentColumn].getDenominator(), matrix[i][currentColumn].getNumerator());
                multiplyRow(matrix, i, frac);
            }
            for (int j = 0; j < rows; j++) {
                if (i != j) { // skip the 1
                    addMultipliedRow(matrix, j, i, matrix[j][currentColumn].negate());
                }
            }
            currentColumn++;
        }
        return matrix;
    }

    public static Fraction[][] matrix(List<Compound> reactants, List<Compound> products, List<String> uniqueElements) {
        // we're gonna use this to create a matrix of how many times each unique element appears in a side
        // for the reactants we'll use positive numbers and for the products, negative numbers
        // for example with equation C6H12O6 + O2 -> CO2 + H2O:
        //
        // element          C6H12O6         O2         CO2         H2O
        //        C              +6          0          -1          0
        //        H             +12          0           0         -2
        //        O              +6         +2          -2         -1
        if (reactants == null || reactants.isEmpty()) {
            throw new IllegalArgumentException("Parsed list of reactants is invalid");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Parsed list of products is invalid");
        }
        if (uniqueElements == null || uniqueElements.isEmpty()) {
            throw new IllegalArgumentException("Parsed list of unique elements is invalid");
        }

        int rowCount = uniqueElements.size();
        int colCount = reactants.size() + products.size();

        Fraction[][] matrix = new Fraction[rowCount][colCount];

        int col = 0;
        for (Compound reactant : reactants) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row);
                Map<String, Fraction> compElementMap = reactant.getElements();
                matrix[row][col] = compElementMap.getOrDefault(element, Fraction.ZERO);
            }
            col++;
        }

        for (Compound product : products) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row);
                Map<String, Fraction> compElementMap = product.getElements();
                matrix[row][col] = compElementMap.getOrDefault(element, Fraction.ZERO).negate();
            }
            col++;
        }

        return matrix;
    }

    // adding row2*scale to row1
    private static void addMultipliedRow(Fraction[][] matrix, int rowIdx1, int rowIdx2, Fraction scale) {
        int cols = matrix[0].length;
        Fraction[] multipliedRow = new Fraction[matrix[rowIdx2].length];
        for (int i = 0; i < cols; i++) {
            multipliedRow[i] = matrix[rowIdx2][i].multiply(scale);
            matrix[rowIdx1][i] = matrix[rowIdx1][i].add(multipliedRow[i]);
        }
    }

    // multiplying a row by a scale
    private static void multiplyRow(Fraction[][] matrix, int rowIdx, Fraction scale) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIdx][i] = matrix[rowIdx][i].multiply(scale);
        }
    }

    // swapping position of two rows
    private static void swapRow(Fraction[][] matrix, int rowIdx1, int rowIdx2) {
        Fraction[] row1 = matrix[rowIdx1];
        matrix[rowIdx1] = matrix[rowIdx2];
        matrix[rowIdx2] = row1;
    }

    private static void validateMatrix(Fraction[][] matrix) {

        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException("Invalid matrix given");
        }

        // in case of null elements, replace with zero
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] == null || matrix[i].length == 0) throw new IllegalArgumentException("Invalid matrix: Matrix contains null or empty row");
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == null) {
                    matrix[i][j] = Fraction.ZERO;
                }
            }
        }

        int len = matrix[0].length;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length != len)
                throw new IllegalArgumentException("Invalid matrix: Matrix has inconsistent row lengths");
        }
    }
}
