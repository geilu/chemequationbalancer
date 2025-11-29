package balancer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SideParserTest {
    @Test
    void testGetUniqueElements() {
        List<String> result = new ArrayList<>();
        Map<String, Fraction> elementMap = Map.of("C", new Fraction(6),
                                                                                    "H", new Fraction(5),
                                                                                    "N", new Fraction(3));

        Map<String, Fraction> elementMap2 = Map.of("H", new Fraction(6),
                                                                                    "N", new Fraction(5),
                                                                                    "O", new Fraction(3));
        List<String> expectedResult = List.of("C", "H", "N", "O");
        SideParser.getUniqueElements(elementMap, result);
        SideParser.getUniqueElements(elementMap2, result);

        Collections.sort(result);

        assertEquals(expectedResult, result);
    }

    @Test
    void testMultiplyInsideParentheses_ironAcetate() {
        // Fe(C2H3O2)3
        String input = "Fe(C2H3O2)";
        Fraction scalar = new Fraction(3);

        String result = SideParser.multiplyInsideParentheses(input, scalar);

        assertTrue(result.startsWith("Fe"));
        assertTrue(result.contains("C6"));
        assertTrue(result.contains("H9"));
        assertTrue(result.contains("O6"));
    }

    @Test
    void testMultiplyInsideParentheses_magnesiumHydroxide() {
        // Mg(OH)2
        String input = "Mg(OH)";
        Fraction scalar = new Fraction(2);

        String result = SideParser.multiplyInsideParentheses(input, scalar);

        assertTrue(result.startsWith("Mg"));
        assertTrue(result.contains("O2"));
        assertTrue(result.contains("H2"));
    }

    @Test
    void testMultiplyInsideParentheses_ammoniumSulfate() {
        // (NH4)2SO4
        String input = "(NH4)SO4";
        Fraction scalar = new Fraction(2);

        String result = SideParser.multiplyInsideParentheses(input, scalar);

        assertTrue(result.contains("N2"));
        assertTrue(result.contains("H8"));
        assertTrue(result.endsWith("SO4"));
        assertFalse(result.startsWith("N2H82") || result.startsWith("H8N22")); // make sure the 2 wasnt readded, order can be random
    }

    @Test
    void testMultiplyInsideParentheses_coordinationComplexWithRoundBrackets() {
        // (Co(NH3)4CO3)2SO4
        String input = "(Co(NH3)CO3)2SO4";

        // final expected: Co2N8H24C2O6SO4
        String result = SideParser.multiplyInsideParentheses(input, new Fraction(4)); // first call: handles (NH3)4
        assertTrue(result.contains("N4"));
        assertTrue(result.contains("H12"));
        result = SideParser.multiplyInsideParentheses("(CoN4H12CO3)SO4", new Fraction(2)); // second call: handles (CoN4H12CO3)2
        assertTrue(result.contains("Co2"));
        assertTrue(result.contains("N8"));
        assertTrue(result.contains("H24"));
        assertTrue(result.contains("C2"));
        assertTrue(result.contains("O6"));
        assertTrue(result.endsWith("SO4"));
    }

    @Test
    void testMultiplyInsideParentheses_coordinationComplexWithSquareBrackets() {
        // [Co(NH3)4CO3]2SO4
        String input = "[Co(NH3)CO3]2SO4";

        // final expected: Co2N8H24C2O6SO4
        String result = SideParser.multiplyInsideParentheses(input, new Fraction(4)); // first call: handles (NH3)4
        assertTrue(result.contains("N4"));
        assertTrue(result.contains("H12"));
        result = SideParser.multiplyInsideParentheses("[CoN4H12CO3]SO4", new Fraction(2)); // second call: handles (CoN4H12CO3)2
        assertTrue(result.contains("Co2"));
        assertTrue(result.contains("N8"));
        assertTrue(result.contains("H24"));
        assertTrue(result.contains("C2"));
        assertTrue(result.contains("O6"));
        assertTrue(result.endsWith("SO4"));
    }

    @Test
    void testParseSide() {
        String side = "C6H12O6 + O2";
        List<Compound> result = SideParser.parseSide(side, new ArrayList<>());
        assertTrue(result.size() == 2);
        assertTrue(result.get(0).getOriginalForm().equals("C6H12O6"));
        assertTrue(result.get(1).getOriginalForm().equals("O2"));
    }

    @Test
    void testFindParentheses_noParentheses() {
        String input = "C6H12O6";
        int[] result = SideParser.findParentheses(input);

        assertArrayEquals(new int[]{-1, -1}, result);
    }

    @Test
    void testFindParentheses_roundBrackets() {
        String input = "Na(OH)2";
        int[] result = SideParser.findParentheses(input);

        assertArrayEquals(new int[]{2, 5}, result);
    }

    @Test
    void testFindParentheses_squareBrackets() {
        String input = "[AlH2O6]Cl3"; // the right formula is [Al(H2O)6]Cl3 but i removed the round brackets for this test
        int[] result = SideParser.findParentheses(input);

        assertArrayEquals(new int[]{0, 7}, result);
    }

    @Test
    void testFindParentheses_doubleBrackets() {
        // should find innermost brackets first
        String input = "[Al(H2O)6]Cl3";
        int[] result = SideParser.findParentheses(input);

        assertArrayEquals(new int[]{3, 7}, result);
    }

    @Test
    void testBuildEquation_alreadyBalanced() {
        List<Compound> reactants = List.of(new Compound("Br"), new Compound("O"));
        List<Compound> products = List.of(new Compound("BrO"));
        long[] coeffs = {1, 1, 1};

        String result = SideParser.buildEquation(coeffs, reactants, products);
        assertEquals("Br + O = BrO", result);
    }

    @Test
    void testBuildEquation() {
        List<Compound> reactants = List.of(new Compound("C6H12O6"), new Compound("O2"));
        List<Compound> products = List.of(new Compound("CO2"), new Compound("H2O"));
        long[] coeffs = {1, 6, 6, 6};

        String result = SideParser.buildEquation(coeffs, reactants, products);
        assertEquals("C6H12O6 + 6O2 = 6CO2 + 6H2O", result);
    }
}