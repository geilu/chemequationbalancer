import java.util.HashMap;

public class Balancer {
    public static HashMap<String, HashMap<String, Integer>> balance(HashMap<String, HashMap<String, Integer>> firstSide, HashMap<String, HashMap<String, Integer>> secondSide) {

        // firstSide {O2={O=2}, C6H12O6={C=6, H=12, O=6}}
        // secondSide {H2O={H=2, O=1}, CO2={C=1, O=2}}

        String lastChanged = ""; // remember the last changed element so we dont look at it twice
        while (!isBalanced(firstSide, secondSide)) { // run until the sides are balanced

        }
        return null;
    }

    public static boolean isBalanced(HashMap<String, HashMap<String, Integer>> firstSide, HashMap<String, HashMap<String, Integer>> secondSide) {
        HashMap<String, Integer> firstSideElements = new HashMap<>();
        HashMap<String, Integer> secondSideElements = new HashMap<>();

        // get total elements one side
        for (String comp : firstSide.keySet()) {
            HashMap<String, Integer> elems = firstSide.get(comp);
            for (String elem : elems.keySet()) {
                firstSideElements.put(elem, firstSideElements.getOrDefault(elem, 0) + elems.get(elem));
            }
        }
        // get total elements other side
        for (String comp : secondSide.keySet()) {
            HashMap<String, Integer> elems = secondSide.get(comp);
            for (String elem : elems.keySet()) {
                secondSideElements.put(elem, secondSideElements.getOrDefault(elem, 0) + elems.get(elem));
            }
        }
        return firstSideElements.equals(secondSideElements);
    }

    //todo: foolproof this more
    public static int findMultiplier(double x, double y) {
        if (x > y) {
            if (x % y == 0) { // division results in int
                int result = (int) Math.floor(x/y);
                return result;
            } else { // division results in fraction
                return -1;
            }
        } else {
            if (y % x == 0) {
                int result = (int) Math.floor(y/x);
                return result;
            } else {
                return -1;
            }
        }
    }
}
