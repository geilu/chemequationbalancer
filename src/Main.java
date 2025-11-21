import java.util.*;

public class Main {
    public static void main(String[] args) {
        String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        String eq2 = "C6H12O6 + O2 = CO2 + H2O";
        String eq3 = "Br + O = BrO";
        String[] sides = eq2.split(" = ");

        List<String> uniqueElements = new ArrayList<>();

        if (sides.length == 2) {
            // get maps of both sides and fill unique elements list
            // LinkedHashMap to preserve insertion order
            LinkedHashMap<String, LinkedHashMap<String, Fraction>> reactants = new LinkedHashMap<>(SideParser.parseSide(sides[0], uniqueElements));
            LinkedHashMap<String, LinkedHashMap<String, Fraction>> products = new LinkedHashMap<>(SideParser.parseSide(sides[1], uniqueElements));

            // create matrix of element appearances
            Fraction[][] elementAppearances = Balancer.matrix(reactants, products, uniqueElements);
            // matrix into rref
            Fraction[][] rrefMatrix = Balancer.RREFmatrix(elementAppearances);
            // get the coeffs
            long[] coeffs = Balancer.getCoefficients(rrefMatrix);

            System.out.println("balanced equation:");
            System.out.println(buildEquation(coeffs, reactants, products));
        }
    }

    private static String buildEquation(long[] coeffs, LinkedHashMap<String, LinkedHashMap<String, Fraction>> reactants, LinkedHashMap<String, LinkedHashMap<String, Fraction>> products) {
        StringBuilder sb = new StringBuilder();

        int idx = 0;
        int compoundCount = reactants.size();
        for (String reactant : reactants.keySet()) {
            appendElement(coeffs[idx], sb, reactant, compoundCount, idx);
            idx++;
        }
        sb.append(" = ");
        compoundCount += products.size();
        for (String product : products.keySet()) {
            appendElement(coeffs[idx], sb, product, compoundCount, idx);
            idx++;
        }
        return sb.toString();
    }

    private static void appendElement(long coeff, StringBuilder sb, String compound, int compoundCount, int idx) {
        compound = SideParser.handleParentheses(compound);
        if (coeff > 1) {
            sb.append(coeff);
        }
        sb.append(compound);
        if (idx+1 < compoundCount) {
            sb.append(" + ");
        }
    }
}