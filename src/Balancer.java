import java.util.*;

public class Balancer {
    public static int[] getCoefficients(Fraction[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // first getting the matrix into RREF
        // by column
        // top 1
        // get rows-1 bottom nums to be 0
        // cont with other 2 rows same thing next col
        // top row num 1
        // rows-2 bottom nums 0

        return null;
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

    // adding two rows, row2 = row2+row1
    private Fraction[][] addRow(Fraction[][] matrix, int rowIdx1, int rowIdx2) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIdx2][i] = matrix[rowIdx2][i].add(matrix[rowIdx1][i]);
        }
        return matrix;
    }

    // multiplying a row by a scale
    private Fraction[][] multiplyRow(Fraction[][] matrix, int rowIdx, Fraction scale) {
        int cols = matrix[0].length;
        for (int i = 0; i < cols; i++) {
            matrix[rowIdx][i] = matrix[rowIdx][i].multiply(scale);
        }
        return matrix;
    }

    // swapping position of two rows
    private Fraction[][] swapRow(Fraction[][] matrix, int rowIdx1, int rowIdx2) {
        Fraction[] row1 = matrix[rowIdx1];
        matrix[rowIdx1] = matrix[rowIdx2];
        matrix[rowIdx2] = row1;

        return matrix;
    }
}
