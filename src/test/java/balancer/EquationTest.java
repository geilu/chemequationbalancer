package balancer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquationTest {
    @Test
    void testEquation1() {
        String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        String expectedEq = "Co(NO3)2 + Na2S = CoS + 2NaNO3";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation2() {
        String eq = "C6H12O6 + O2 = CO2 + H2O";
        String expectedEq = "C6H12O6 + 6O2 = 6CO2 + 6H2O";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_alreadyBalanced() {
        String eq = "Br + O = BrO";
        String expectedEq = "Br + O = BrO";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_parenthesizedGroup() {
        String eq = "Fe + C2H3O2 = Fe(C2H3O2)3";
        String expectedEq = "Fe + 3C2H3O2 = Fe(C2H3O2)3";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_parenthesizedGroup2() {
        String eq = "Mg + OH = Mg(OH)2";
        String expectedEq = "Mg + 2OH = Mg(OH)2";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_coordinationComplex() {
        String eq = "Be + NaOH + H2O = Na2[Be(OH)4] + H2";
        String expectedEq = "Be + 2NaOH + 2H2O = Na2[Be(OH)4] + H2";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_coordinationComplex2() {
        String eq = "Al2(SO4)3 + Na[Al(OH)4] = Al(OH)3 + Na2SO4";
        String expectedEq = "Al2(SO4)3 + 6Na[Al(OH)4] = 8Al(OH)3 + 3Na2SO4";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_coordinationComplex3() {
        String eq = "Al2O3 + HCl + H2O = [Al(H2O)6]Cl3";
        String expectedEq = "Al2O3 + 6HCl + 9H2O = 2[Al(H2O)6]Cl3";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_parenthesizedGroupInFront() {
        String eq = "KOH + (NH4)2SO4 = H2O + K2SO4 + NH3";
        String expectedEq = "2KOH + (NH4)2SO4 = 2H2O + K2SO4 + 2NH3";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    @Test
    void testEquation_coordinationComplexWithIndex() {
        String eq = "CoSO4 + (NH4)2CO3 + NH3 + O2 = (Co(NH3)4CO3)2SO4 + (NH4)2SO4 + H2O";
        String expectedEq = "4CoSO4 + 4(NH4)2CO3 + 12NH3 + O2 = 2(Co(NH3)4CO3)2SO4 + 2(NH4)2SO4 + 2H2O";

        String result = getBalancedEquation(eq);

        assertEquals(expectedEq, result);
    }

    private String getBalancedEquation(String eq) {
        String[] sides = eq.split(" = ");
        List<String> uniqueElements = new ArrayList<>();

        assertEquals(2, sides.length);

        List<Compound> reactants = SideParser.parseSide(sides[0], uniqueElements);
        List<Compound> products = SideParser.parseSide(sides[1], uniqueElements);

        Fraction[][] matrix = Balancer.matrix(reactants, products, uniqueElements);
        Fraction[][] rrefMatrix = Balancer.matrixToRREF(matrix);

        long[] coeffs = Balancer.getCoefficients(rrefMatrix);
        return Main.buildEquation(coeffs, reactants, products);
    }
}
