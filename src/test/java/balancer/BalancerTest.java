package balancer;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BalancerTest {

    @Test
    public void testGetCoefficients_matrixWithFractionElement() {
        // set up a RREF matrix like
        //      1    0   0   -1/6
        //      0    1   0     -1
        //      0    0   1     -1
        long[][] matrixData = {{1, 0, 0, -1},
                                            {0, 1, 0, -1},
                                            {0, 0, 1, -1}};
        Fraction[][] matrix = createMatrix(matrixData);
        matrix[0][3] = new Fraction(-1, 6);

        long[] coeffs = Balancer.getCoefficients(matrix);
        // expected coeffs: 1, 6, 6, 6
        assertArrayEquals(new long[]{1, 6, 6, 6}, coeffs);
    }

    @Test
    public void testGetCoefficients_matrixWithNoFractions() {
        // set up RREF matrix like
        // 1  0  -1
        // 0  1  -2
        // 0  0  0
        long[][] matrixData = {{1, 0, -1},
                                            {0, 1, -2},
                                            {0, 0, 0}};
        Fraction[][] matrix = createMatrix(matrixData);

        long[] coeffs = Balancer.getCoefficients(matrix);
        // expected coeffs: 1, 2, 1
        assertArrayEquals(new long[]{1, 2, 1}, coeffs);
    }

    @Test
    public void testMatrixToRREF() {
        // set up matrix like
        // 6  0  -1  0
        // 12  0  0  -2
        // 6  2  -2  -1
        long[][] matrixData = {{6, 0, -1, 0},
                                             {12, 0, 0, -2},
                                             {6, 2, -2, -1}};
        Fraction[][] matrix = createMatrix(matrixData);

        Fraction[][] rrefMatrix = Balancer.matrixToRREF(matrix);
        // expected RREF matrix:
        // 1  0  0  -1/6
        // 0  1  0  -1
        // 0  0  1  -1
        long[][] expected = {{1, 0, 0, -1},
                                          {0, 1, 0, -1},
                                          {0, 0, 1, -1}};
        Fraction[][] expectedMatrix = createMatrix(expected);
        expectedMatrix[0][3] = new Fraction(-1, 6);

        assertArrayEquals(expectedMatrix, rrefMatrix);
    }

    @Test
    public void testMatrix() {
        Compound c6h12o6 = new Compound("C6H12O6");
        c6h12o6.setElements();
        Compound o2 = new Compound("O2");
        o2.setElements();
        Compound co2 = new Compound("CO2");
        co2.setElements();
        Compound h2o = new Compound("H2O");
        h2o.setElements();
        List<Compound> reactants = List.of(c6h12o6, o2);
        List<Compound> products = List.of(co2, h2o);
        List<String> uniqueElements = List.of("C", "H", "O");

        Fraction[][] resultMatrix = Balancer.matrix(reactants, products, uniqueElements);
        long[][] expectedData = {{6, 0, -1, 0},
                                                  {12, 0, 0, -2},
                                                  {6, 2, -2, -1}};
        Fraction[][] expectedMatrix = createMatrix(expectedData);
        assertArrayEquals(expectedMatrix, resultMatrix);
    }

    private Fraction[][] createMatrix(long[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        Fraction[][] matrix = new Fraction[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                long value = data[i][j];
                if (value == 0) {
                    matrix[i][j] = Fraction.ZERO;
                } else {
                    matrix[i][j] = new Fraction(value);
                }
            }
        }
        return matrix;
    }
}