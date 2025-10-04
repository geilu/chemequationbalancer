import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SideParser {

    public static HashMap<String, Integer> parseSide(String side) {
        String[] compounds = side.split(" \\+ ");
        HashMap<String, Integer> elementMap = new HashMap<>();

        for (String comp : compounds) {
            comp = addParentheses(comp);
            parseCompound(comp, elementMap);
        }
        return elementMap;
    }

    public static HashMap<String, Integer> parseCompound(String compound, HashMap<String, Integer> elementMap) {

        Pattern basePattern = Pattern.compile("([A-Z][a-z]?)(\\d*)"); // find uppercase letter with optional lowercase letter and optional digit
        Pattern ionPattern = Pattern.compile("([A-Z][a-z]?|\\([A-Za-z0-9]+\\))(\\d*)"); // find element or parenthesized group with optional digit
        Matcher matcher = basePattern.matcher(compound);

        if (containsIon(compound)) {
            matcher = ionPattern.matcher(compound);
        }

        while (matcher.find()) {
            String element = matcher.group(1);
            String elemIdx = matcher.group(2);

            int count = elemIdx.isEmpty() ? 1 : Integer.parseInt(elemIdx);

            elementMap.put(element, elementMap.getOrDefault(element, 0) + count);
        }

        return elementMap;
    }

    public static String addParentheses(String compound) {
        // for cases like NaNO3 when there's an ion without parentheses, add parentheses for easier parsing later
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};

        for (String ion : ions) {
            if (compound.contains(ion) && !compound.contains("(") && !compound.contains(")")) { // make sure there are no parentheses already too
                int startIdx = compound.indexOf(ion);
                int endIdx = startIdx + ion.length() - 1;

                return compound.substring(0, startIdx) + "(" + compound.substring(startIdx, endIdx+1) + ")" + compound.substring(endIdx+1);
            }
        }
        return compound;
    }

    public static boolean containsIon(String compound) {
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};

        for (String ion : ions) {
            if (compound.contains(ion)) {
                return true;
            }
        }
        return false;
    }
}
