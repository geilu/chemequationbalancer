import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        String eq2 = "C6H12O6 + O2 = CO2 + H2O";
        String eq3 = "Br + O = BrO";
        String[] sides = eq2.split(" = ");

        List<String> uniqueElements = new ArrayList<>();

        if (sides.length == 2) {
            HashMap<String, HashMap<String, Integer>> firstSide = new HashMap<>(SideParser.parseSide(sides[0], uniqueElements));
            HashMap<String, HashMap<String, Integer>> secondSide = new HashMap<>(SideParser.parseSide(sides[1], uniqueElements));

//            System.out.println(firstSide);
//            System.out.println(secondSide);

            System.out.println(Balancer.balance(firstSide, secondSide, uniqueElements));
        }
    }
}
