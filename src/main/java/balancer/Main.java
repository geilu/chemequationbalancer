package balancer;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // todo 1: turn these into tests
        String[] eqs = {"Co(NO3)2 + Na2S = CoS + NaNO3",
                                "C6H12O6 + O2 = CO2 + H2O",
                                "Br + O = BrO",
                                "Fe + C2H3O2 = Fe(C2H3O2)3",
                                "Mg + OH = Mg(OH)2",
                                "Be + NaOH + H2O = Na2[Be(OH)4] + H2", // potentially problematic
                                "Al2(SO4)3 + Na[Al(OH)4] = Al(OH)3 + Na2SO4", // potentially problematic
                                "Al2O3 + HCl + H2O = [Al(H2O)6]Cl3", // todo 2: problematic
                                "Zn + OH + H2O = [Zn(OH)4] + H2"}; // potentially problematic

        for (String eq : eqs) {
            String[] sides = eq.split(" = ");

            List<String> uniqueElements = new ArrayList<>();

            if (sides.length == 2) {
                // get compound object lisrs of reactants and products and fill unique elements list
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
                System.out.println("-------------");
            }
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