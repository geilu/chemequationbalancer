package balancer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SideParserTest {
    @Test
    void testSideParser_doubleDigitSubscript() {
        String input = "(NH4)12PO4";

        List<String> emptyList = new ArrayList<>();
        List<Compound> list = SideParser.parseSide(input, emptyList);

        String result = list.getFirst().getChangedForm();

        verifyCompoundChangedForm(new String[]{"H48", "N12", "PO4"}, result);
    }

    @Test
    void testNestedParentheses() {
        String input = "((A))";
        List<String> emptyList = new ArrayList<>();

        List<Compound> list = SideParser.parseSide(input, emptyList);

        assertEquals("A", list.getFirst().getChangedForm());
    }

    @Test
    void testParenthesesAtEnd() {
        String input = "Ca(OH)";
        List<String> emptyList = new ArrayList<>();

        List<Compound> list = SideParser.parseSide(input, emptyList);

        String result = list.getFirst().getChangedForm();
        assertFalse(result.contains("(OH)"));

        assertTrue(result.equals("CaOH") || result.equals("CaHO"));
    }

    @Test
    void testSideParser_nullArguments() {
        String input = "Ca(OH)";
        List<String> uniqueElements = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () ->
                SideParser.parseSide(null, uniqueElements),
                "Null side input failed to throw an error");

        assertThrows(IllegalArgumentException.class, () ->
                        SideParser.parseSide(input, null),
                "Null uniqueElements input failed to throw an error");
    }

    @Test
    void testFindParentheses_mismatchedBracket() {
        String input = "Fe(OH]";
        assertArrayEquals(new int[]{-1, -1}, SideParser.findParentheses(input));
    }

    @Test
    void testMultiplyInsideParentheses_inconsistentBrackets() {
        String input = "Co(NH3)5[CN]";

        input = SideParser.multiplyInsideParentheses(input, new Fraction(1));
        assertTrue(input.equals("Co(NH3)5CN") || input.equals("Co(NH3)5NC")); // first run, order doesnt matter
        input = SideParser.multiplyInsideParentheses(input, new Fraction(5));
        assertTrue(input.equals("CoN5H15CN") || input.equals("CoH15N5CN") ||
                           input.equals("CoN5H15NC") || input.equals("CoH15N5NC")); // second run
    }

    private void verifyCompoundChangedForm(String[] parts, String result) {
        int partsLength = 0;
        for (String part : parts) {
            assertTrue(result.contains(part));
            partsLength += part.length();
        }
        assertEquals(result.length(), partsLength, "result length doesn't match length of expected value (extra or missing characters)");
    }
}