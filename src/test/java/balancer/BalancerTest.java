package balancer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalancerTest {

    @Test
    void testNullMatrix() {
        assertThrows(IllegalArgumentException.class, () -> Balancer.matrixToRREF(null),
                "method matrixToRREF failed to give error for invalid matrix");

        assertThrows(IllegalArgumentException.class, () -> Balancer.getCoefficients(null),
                "method getCoefficients failed to give error for invalid matrix");
    }

    @Test
    void testEmptyMatrix() {
        Fraction[][] emptyMatrix = new Fraction[][]{};

        assertThrows(IllegalArgumentException.class, () -> Balancer.matrixToRREF(emptyMatrix),
                "method matrixToRREF failed to give error for invalid matrix");

        assertThrows(IllegalArgumentException.class, () -> Balancer.getCoefficients(emptyMatrix),
                "method getCoefficients failed to give error for invalid matrix");
    }

    @Test
    void testRowsWithNoCols() {
        Fraction[][] zeroColMatrix = new Fraction[][] {
                {},
                {}
        };

        assertThrows(IllegalArgumentException.class, () -> Balancer.matrixToRREF(zeroColMatrix),
                "method matrixToRREF failed to give error for invalid matrix");

        assertThrows(IllegalArgumentException.class, () -> Balancer.getCoefficients(zeroColMatrix),
                "method getCoefficients failed to give error for invalid matrix");
    }

    @Test
    void testGetCoefficients_nullRowInMatrix() {
        Fraction[][] matrixWithNullRow = new Fraction[][] { {new Fraction(1), new Fraction(0)},
                                                                                             null,
                                                                                            {new Fraction(0), new Fraction(1)} };

        assertThrows(IllegalArgumentException.class, () -> Balancer.getCoefficients(matrixWithNullRow),
                "method getCoefficients failed to give error for invalid matrix");
        assertThrows(IllegalArgumentException.class, () -> Balancer.matrixToRREF(matrixWithNullRow),
                "method matrixToRREF failed to give error for invalid matrix");
    }

    @Test
    void testInconsistentRowLengthMatrix() {
        Fraction[][] inconsistentMatrix = new Fraction[][] { {new Fraction(1), new Fraction(0), new Fraction(5)},
                                                                                           {new Fraction(0), new Fraction(1)} };

        try {
            assertThrows(IllegalArgumentException.class, () -> Balancer.getCoefficients(inconsistentMatrix));
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("method getCoefficients crashed with IndexOutOfBounds instead of validating row length consistency"); // giving a result with partial data is also a fail
        }

        try {
            assertThrows(IllegalArgumentException.class, () -> Balancer.matrixToRREF(inconsistentMatrix));
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("method matrixToRREF crashed with IndexOutOfBounds instead of validating row length consistency");
        }
    }

    @Test
    void testGetCoefficients_leadingZeroes() {
        long[][] data = { {0, 1, 0, -2},
                                   {0, 0, 1,  5} };
        Fraction[][] matrix = createMatrix(data);
        long[] coeffs = Balancer.getCoefficients(matrix);

        assertArrayEquals(new long[]{0, 2, 5, 1}, coeffs);
    }

    @Test
    void testGetCoefficients_gappedRows() {
        long[][] data = { {1, 0, 0, -2},
                                   {0, 0, 1, -1} };

        Fraction[][] matrix = createMatrix(data);
        long[] coeffs = Balancer.getCoefficients(matrix);

        assertArrayEquals(new long[]{2, 0, 1, 1}, coeffs);
    }

    @Test
    void testGetCoefficients_overflow() {
        long prime1 = 982451653L;
        long prime2 = 961748941L;
        long prime3 = 928374929L;

        long[][] data = { {1, 0, 1, 0},
                                   {0, 1, 0, 0},
                                   {0, 0, 1, 0} };
        Fraction[][] matrix = createMatrix(data);
        matrix[0][3] = new Fraction(1, prime1);
        matrix[1][3] = new Fraction(1, prime2);
        matrix[2][3] = new Fraction(1, prime3);

        assertThrows(ArithmeticException.class, () -> Balancer.getCoefficients(matrix),
                "failed to catch overflow error");
    }

    @Test
    void testGetCoefficients_matrixWithZeroes() {
        Fraction[][] matrix = { {Fraction.ZERO, Fraction.ZERO},
                                            {Fraction.ZERO, Fraction.ZERO} };

        long[] coeffs = Balancer.getCoefficients(matrix);
        assertArrayEquals(new long[]{0, 0}, coeffs);
    }

    @Test
    void testNullElementInMatrix() {
        Fraction[][] matrix = {{new Fraction(1), null},
                                           {null, new Fraction(1)}};

        Balancer.matrixToRREF(matrix);

        Fraction[][] expected = {{new Fraction(1), Fraction.ZERO},
                                                {Fraction.ZERO, new Fraction(1)}};

        assertArrayEquals(expected, matrix, "failed to replace null elements in matrix");
    }

    @Test
    void testMatrixToRREF() {
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
    void testMatrix() {
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

    @Test
    void testMatrix_nullArguments() {
        List<Compound> products = List.of(new Compound("CO2"));
        List<Compound> reactants = List.of(new Compound("C"), new Compound("O2"));
        List<String> uniqueElements = List.of("C", "O");
        // null reactants
        assertThrows(IllegalArgumentException.class, () ->
                Balancer.matrix(null, products, uniqueElements),
                "reactants list as null argument failed to throw error");
        // null products
        assertThrows(IllegalArgumentException.class, () ->
                Balancer.matrix(reactants, null, uniqueElements),
                "products list as null argument failed to throw error");
        // null unique elements
        assertThrows(IllegalArgumentException.class, () ->
                Balancer.matrix(reactants, products, null),
                "unique elements list as null argument failed to throw error");
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