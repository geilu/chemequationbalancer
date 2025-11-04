import java.util.*;

public class Balancer {
    public static int[] getCoefficients(double[][] A) {
        // todo
        return null;
    }

    public static double[][] matrix(LinkedHashMap<String, LinkedHashMap<String, Integer>> reactants, LinkedHashMap<String, LinkedHashMap<String, Integer>> products, List<String> uniqueElements) {
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

        double[][] matrix = new double[rowCount][colCount];

        int col = 0;
        for (LinkedHashMap<String, Integer> compound : reactants.values()) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row); // get element from unique elements list
                matrix[row][col] = compound.getOrDefault(element, 0);
            }
            col++;
        }

        for (LinkedHashMap<String, Integer> compound : products.values()) {
            for (int row = 0; row < rowCount; row++) {
                String element = uniqueElements.get(row);
                matrix[row][col] = -compound.getOrDefault(element, 0);
            }
            col++;
        }

        return matrix;
    }
}
