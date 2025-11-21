import java.util.*;

public class Balancer {
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
        long[] coeffs = new long[matrix[0].length];
        int rows = matrix.length;
        int cols = matrix[0].length;

        // find the variable corresponding to the last column
        for (int i = 0; i < rows; i++) {
            if (matrix[i][cols-1].getDenominator() != 1) {
                coeffs[cols-1] = matrix[i][cols-1].getDenominator();
            }
        }

        // find the rest of the variables
        for (int i = 0; i < coeffs.length-1; i++) {
            coeffs[i] = Math.abs(matrix[i][cols-1].multiply(new Fraction(coeffs[cols-1])).getNumerator());
        }

        return coeffs;
    }
    public static Fraction[][] RREFmatrix(Fraction[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int currentColumn = 0;
        int currentRow = 0;

        for (int i = 0; i < rows; i++) {
            if (cols <= currentColumn) {
                break;
            }
            // skip the 0s above
            currentRow = i;
            while (currentColumn < cols && matrix[currentRow][currentColumn].equals(new Fraction(0))) {
                currentRow++;
                if (rows == currentRow) {
                    currentRow = i;
                    currentColumn++;
                }
            }

            if (cols <= currentColumn) {
                break;
            }

            swapRow(matrix, currentRow, i); // swapping the first row with nonzero value in column to the 1 diagonal
            if (!matrix[i][currentColumn].equals(new Fraction(0))) {
                multiplyRow(matrix, i, getScalarFromFraction(matrix[i][currentColumn], 2)); // ver 2: multiplying by the inverse of the current element (eg. element 6 multiplied by 1/6)
            }
            for (int j = 0; j < rows; j++) {
                if (i != j) { // skip the 1
                    addMultipliedRow(matrix, j, i, getScalarFromFraction(matrix[j][currentColumn], 1)); // ver 1: multiplying by the negative of the current element (eg. element 12, adding row with 1 in col multiplied by -12)
                }
            }
            currentColumn++;
        }
        return matrix;
    }

    public static Fraction[][] matrix(LinkedHashMap<String, LinkedHashMap<String, Fraction>> reactants, LinkedHashMap<String, LinkedHashMap<String, Fraction>> products, List<String> uniqueElements) {
        // we're gonna use this to create a matrix of how many times each unique element appears in a side
        // for the reactants we'll use positive numbers and for the products, negative numbers
        // for example with equation C6H12O6 + O2 -> CO2 + H2O:
        //
        // element          C6H12O6         O2         CO2         H2O
        //        C              +6          0          -1          0
        //        H             +12          0           0         -2
        //        O              +6         +2          -2         -1
        int rowCount = uniqueElements.size();
        int colCount = reactants.size() + products.size();

        Fraction[][] matrix = new Fraction[rowCount][colCount];

        int col = 0;
        for (LinkedHashMap<String, Fraction> compound : reactants.values()) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row);
                matrix[row][col] = compound.getOrDefault(element, new Fraction(0));
            }
            col++;
        }

        for (LinkedHashMap<String, Fraction> compound : products.values()) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row);
                matrix[row][col] = compound.getOrDefault(element, new Fraction(0)).negate();
            }
            col++;
        }

        return matrix;
    }

    // adding row2*scale to row1
    private static Fraction[][] addMultipliedRow(Fraction[][] matrix, int rowIdx1, int rowIdx2, Fraction scale) {
        int cols = matrix[0].length;
        Fraction[] multipliedRow = new Fraction[matrix[rowIdx2].length];
        for (int i = 0; i < cols; i++) {
            multipliedRow[i] = matrix[rowIdx2][i].multiply(scale);
            matrix[rowIdx1][i] = matrix[rowIdx1][i].add(multipliedRow[i]);
        }
        return matrix;
    }

    // adding two rows, row2 = row2+row1
    private static Fraction[][] addRow(Fraction[][] matrix, int rowIdx1, int rowIdx2) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIdx2][i] = matrix[rowIdx2][i].add(matrix[rowIdx1][i]);
        }
        return matrix;
    }

    // multiplying a row by a scale
    private static Fraction[][] multiplyRow(Fraction[][] matrix, int rowIdx, Fraction scale) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIdx][i] = matrix[rowIdx][i].multiply(scale);
        }
        return matrix;
    }

    // swapping position of two rows
    private static Fraction[][] swapRow(Fraction[][] matrix, int rowIdx1, int rowIdx2) {
        Fraction[] row1 = matrix[rowIdx1];
        matrix[rowIdx1] = matrix[rowIdx2];
        matrix[rowIdx2] = row1;

        return matrix;
    }

    // for ver = 1, return negative fraction
    // for ver = 2, return reciprocal of fraction
    private static Fraction getScalarFromFraction(Fraction frac, int ver) {
        if (ver == 1) {
            return frac.negate();
        } else if (ver == 2) {
            return new Fraction(frac.getDenominator(), frac.getNumerator());
        } else {
            throw new IllegalArgumentException("Invalid ver: " + ver);
        }
    }
}
