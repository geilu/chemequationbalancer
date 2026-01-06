package balancer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquationTest {

    @ParameterizedTest
    @MethodSource("getParameters")
    void testEquation(String eq, String expectedEq) {

        String[] sides = eq.split(" = ");
        List<String> uniqueElements = new ArrayList<>();

        assertEquals(2, sides.length);

        List<Compound> reactants = SideParser.parseSide(sides[0], uniqueElements);
        List<Compound> products = SideParser.parseSide(sides[1], uniqueElements);

        Fraction[][] matrix = Balancer.matrix(reactants, products, uniqueElements);
        Fraction[][] rrefMatrix = Balancer.matrixToRREF(matrix);

        long[] coeffs = Balancer.getCoefficients(rrefMatrix);
        String result = SideParser.buildEquation(coeffs, reactants, products);

        assertEquals(expectedEq, result);
    }

    private static Stream<Arguments> getParameters() {
        return Stream.of(
                Arguments.of("Fe + C2H3O2 = Fe(C2H3O2)3", "Fe + 3C2H3O2 = Fe(C2H3O2)3"),
                Arguments.of("Br + O = BrO", "Br + O = BrO"),
                Arguments.of("Be + NaOH + H2O = Na2[Be(OH)4] + H2", "Be + 2NaOH + 2H2O = Na2[Be(OH)4] + H2"),
                Arguments.of("Al2O3 + HCl + H2O = [Al(H2O)6]Cl3", "Al2O3 + 6HCl + 9H2O = 2[Al(H2O)6]Cl3"),
                Arguments.of("KOH + (NH4)2SO4 = H2O + K2SO4 + NH3", "2KOH + (NH4)2SO4 = 2H2O + K2SO4 + 2NH3"),
                Arguments.of("CoSO4 + (NH4)2CO3 + NH3 + O2 = (Co(NH3)4CO3)2SO4 + (NH4)2SO4 + H2O", "4CoSO4 + 4(NH4)2CO3 + 12NH3 + O2 = 2(Co(NH3)4CO3)2SO4 + 2(NH4)2SO4 + 2H2O")
        );
    }
}
