import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Balancer {
    public static HashMap<String, HashMap<String, Integer>> balance(HashMap<String, HashMap<String, Integer>> firstSide, HashMap<String, HashMap<String, Integer>> secondSide, List<String> uniqueElements) {
        HashMap<String, List<List<String>>> elementVariables = getElementVariables(firstSide, secondSide, uniqueElements);
        findVariables(elementVariables);
        return null;
    }

    private static void findVariables(HashMap<String, List<List<String>>> elementVariables) {
        // map = {C=[[6a], [1d]], H=[[12a], [2c]], O=[[2b, 6a], [1c, 2d]]}
        // for C, 6a = 1d
        // for H, 12a = 2c
        // for O, 2b+6a=1c+2d
        // todo: solve for a, b, c, d
    }

    private static HashMap<String, List<List<String>>> getElementVariables(HashMap<String, HashMap<String, Integer>> firstSide, HashMap<String, HashMap<String, Integer>> secondSide, List<String> uniqueElements) {
        HashMap<Character, String> variables = assignVariables(firstSide, secondSide);

        HashMap<String, List<String>> elementVariablesLeft = new HashMap<>(); // key: multiplier x variable, value: corresponding element, eg. 2c = O
        HashMap<String, List<String>> elementVariablesRight = new HashMap<>(); // one for each side

        for (char variable :variables.keySet()) {
            String comp = variables.get(variable);
            if (firstSide.containsKey(comp)) {
                addElementVariable(firstSide, elementVariablesLeft, variable, comp);
            } else {
                addElementVariable(secondSide, elementVariablesRight, variable, comp);
            }
        }

        HashMap<String, List<List<String>>> elementEquation = new HashMap<>();
        for (String element : uniqueElements) {
            List<String> equationVariablesLeft = new ArrayList<>();
            List<String> equationVariablesRight = new ArrayList<>();

            for (String key : elementVariablesLeft.keySet()) {
                if (elementVariablesLeft.get(key).contains(element)) {
                    equationVariablesLeft.add(key);
                }
            }

            for (String key : elementVariablesRight.keySet()) {
                if (elementVariablesRight.get(key).contains(element)) {
                    equationVariablesRight.add(key);
                }
            }

            List<List<String>> equation = List.of(equationVariablesLeft, equationVariablesRight);
            elementEquation.put(element, equation);
        }
        return elementEquation;
    }

    private static void addElementVariable(HashMap<String, HashMap<String, Integer>> side, HashMap<String, List<String>> elementVariables, char variable, String comp) {
        HashMap<String, Integer> elementMap;
        elementMap = side.get(comp);

        for (String elem : elementMap.keySet()) {
            String func = String.valueOf(elementMap.get(elem)) + variable;

            if (!elementVariables.containsKey(func)) { // if it isnt there already
                List<String> elemList = new ArrayList<>(); // new list with element
                elemList.add(elem);
                elementVariables.put(func, elemList);
            } else { // if we already have func in map
                elementVariables.get(func).add(elem); // add elem to list
            }
        }
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

    public static HashMap<Character, String> assignVariables(HashMap<String, HashMap<String, Integer>> firstSide, HashMap<String, HashMap<String, Integer>> secondSide) {
        HashMap<Character, String> elementVariables = new HashMap<>();
        char[] variables = {'a', 'b', 'c', 'd', 'e'};
        int idx = 0;
        for (String comp : firstSide.keySet()) {
            elementVariables.put(variables[idx], comp);
            idx++;
        }
        for (String comp : secondSide.keySet()) {
            elementVariables.put(variables[idx], comp);
            idx++;
        }
        return elementVariables;
    }
}
