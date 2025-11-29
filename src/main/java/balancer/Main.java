package balancer;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        String eq = "Al2O3 + HCl + H2O = [Al(H2O)6]Cl3";

        String[] sides = eq.split(" = ");

        List<String> uniqueElements = new ArrayList<>();

        if (sides.length == 2) {
            // get compound object lists of reactants and products and fill unique elements list
            List<Compound> reactants = SideParser.parseSide(sides[0], uniqueElements);
            List<Compound> products = SideParser.parseSide(sides[1], uniqueElements);

            // create matrix of element appearances
            Fraction[][] elementAppearances = Balancer.matrix(reactants, products, uniqueElements);

            // matrix into rref
            Fraction[][] rrefMatrix = Balancer.matrixToRREF(elementAppearances);

            // get the coeffs
            long[] coeffs = Balancer.getCoefficients(rrefMatrix);


            System.out.println("balanced equation for " + eq + ":");
            System.out.println(buildEquation(coeffs, reactants, products));
        }
    }

    public static String buildEquation(long[] coeffs, List<Compound> reactants, List<Compound> products) {
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