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
            LinkedHashMap<String, LinkedHashMap<String, Integer>> reactants = new LinkedHashMap<>(SideParser.parseSide(sides[0], uniqueElements));
            LinkedHashMap<String, LinkedHashMap<String, Integer>> products = new LinkedHashMap<>(SideParser.parseSide(sides[1], uniqueElements));

            // create matrix of element appearances
            double[][] elementAppearances = Balancer.matrix(reactants, products, uniqueElements);

            // get list of coefficients
            int[] coeffs = Balancer.getCoefficients(elementAppearances);
            System.out.println(Arrays.toString(coeffs));
        }
    }
}
