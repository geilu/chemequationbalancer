import dataobjects.Compound;
import dataobjects.Fraction;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        //String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        //String eq2 = "C6H12O6 + O2 = CO2 + H2O";
        //String eq3 = "Br + O = BrO";
        String eq4 = "Fe + C2H3O2 = Fe(C2H3O2)3"; // problematic
        String eq5 = "Mg + OH = Mg(OH)2"; // problematic
        String[] sides = eq4.split(" = ");

        List<String> uniqueElements = new ArrayList<>();

        if (sides.length == 2) {
            // get maps of both sides and fill unique elements list
            // LinkedHashMap to preserve insertion order
            List<Compound> reactants = SideParser.parseSide(sides[0], uniqueElements);
            List<Compound> products = SideParser.parseSide(sides[1], uniqueElements);

            // create matrix of element appearances
            Fraction[][] elementAppearances = Balancer.matrix(reactants, products, uniqueElements);
            // matrix into rref
            Fraction[][] rrefMatrix = Balancer.matrixToRREF(elementAppearances);
            // get the coeffs
            long[] coeffs = Balancer.getCoefficients(rrefMatrix);

            System.out.println("balanced equation:");
            System.out.println(buildEquation(coeffs, reactants, products));
        }
    }

    private static String buildEquation(long[] coeffs, List<Compound> reactants, List<Compound> products) {
        StringBuilder sb = new StringBuilder();

        int idx = 0;
        int compoundCount = reactants.size();
        for (Compound reactant : reactants) {
            appendElement(coeffs[idx], sb, reactant.getOriginalForm(), compoundCount, idx);
            idx++;
        }
        sb.append(" = ");
        compoundCount += products.size();
        for (Compound product : products) {
            appendElement(coeffs[idx], sb, product.getOriginalForm(), compoundCount, idx);
            idx++;
        }
        return sb.toString();
    }

    private static void appendElement(long coeff, StringBuilder sb, String compound, int compoundCount, int idx) {
        if (coeff > 1) {
            sb.append(coeff);
        }
        sb.append(compound);
        if (idx+1 < compoundCount) {
            sb.append(" + ");
        }
    }
}