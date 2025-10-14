import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SideParser {

    public static HashMap<String, HashMap<String, Integer>> parseSide(String side, List<String> uniqueElements) {
        String[] compounds = side.split(" \\+ ");
        HashMap<String, HashMap<String, Integer>>  sideMap = new HashMap<>();

        for (String comp : compounds) {
            String foundIon = containsIon(comp);
            if (foundIon != null) {
                comp = addParentheses(comp, foundIon);
            }
            HashMap<String, Integer> elementMap = parseCompound(comp);
            sideMap.put(comp, elementMap);
            getUniqueElements(elementMap, uniqueElements);
        }
        return sideMap;
    }

    public static HashMap<String, Integer> parseCompound(String compound) {
        HashMap<String, Integer> elementMap = new HashMap<>();

        Pattern pattern = Pattern.compile("([A-Z][a-z]?|\\([A-Za-z0-9]+\\))(\\d*)"); // find uppercase letter with optional lowercase letter or parenthesized group with optional digit
        Matcher matcher = pattern.matcher(compound);

        while (matcher.find()) {
            String element = matcher.group(1);
            String elemIdx = matcher.group(2);

            int count = elemIdx.isEmpty() ? 1 : Integer.parseInt(elemIdx);

            elementMap.put(element, elementMap.getOrDefault(element, 0) + count);
        }

        return elementMap;
    }

    public static String addParentheses(String compound, String ion) {
        // for cases like NaNO3 when there's an ion without parentheses, add parentheses for easier parsing later
        if (compound.contains(ion) && !compound.contains("(") && !compound.contains(")")) { // make sure there are no parentheses already too
            int startIdx = compound.indexOf(ion);
            int endIdx = startIdx + ion.length() - 1;

            return compound.substring(0, startIdx) + "(" + compound.substring(startIdx, endIdx+1) + ")" + compound.substring(endIdx+1);
        }
        return compound;
    }

    public static String containsIon(String compound) {
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};

        for (String ion : ions) {
            if (compound.contains(ion)) {
                return ion;
            }
        }
        return null;
    }

    public static void getUniqueElements(HashMap<String, Integer> elementMap, List<String> uniqueElements) {
        for (String element : elementMap.keySet()) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
    }
}
