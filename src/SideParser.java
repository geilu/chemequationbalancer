import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SideParser {

    public static LinkedHashMap<String, LinkedHashMap<String, Fraction>> parseSide(String side, List<String> uniqueElements) {
        String[] compounds = side.split(" \\+ ");
        LinkedHashMap<String, LinkedHashMap<String, Fraction>> sideMap = new LinkedHashMap<>();

        for (String comp : compounds) {
            String foundIon = containsIon(comp);
            if (foundIon != null) {
                comp = addParentheses(comp, foundIon);
            }
            LinkedHashMap<String, Fraction> elementMap = parseCompound(comp);
            sideMap.put(comp, elementMap);
            getUniqueElements(elementMap, uniqueElements);
        }
        return sideMap;
    }

    private static LinkedHashMap<String, Fraction> parseCompound(String compound) {
        LinkedHashMap<String, Fraction> elementMap = new LinkedHashMap<>();

        Pattern pattern = Pattern.compile("([A-Z][a-z]?|\\([A-Za-z0-9]+\\))(\\d*)"); // find uppercase letter with optional lowercase letter or parenthesized group with optional digit
        Matcher matcher = pattern.matcher(compound);

        while (matcher.find()) {
            String element = matcher.group(1);
            String elemIdx = matcher.group(2);

            Fraction count = elemIdx.isEmpty() ? new Fraction(1) : new Fraction(Integer.parseInt(elemIdx));

            elementMap.put(element, elementMap.getOrDefault(element, new Fraction(0)).add(count));
        }

        return elementMap;
    }

    private static String addParentheses(String compound, String ion) {
        // for cases like NaNO3 when there's an ion without parentheses, add parentheses for easier parsing later
        if (compound.contains(ion) && !compound.contains("(") && !compound.contains(")")) { // make sure there are no parentheses already too
            int startIdx = compound.indexOf(ion);
            int endIdx = startIdx + ion.length() - 1;

            return compound.substring(0, startIdx) + "(" + compound.substring(startIdx, endIdx+1) + ")" + compound.substring(endIdx+1);
        }
        return compound;
    }

    private static String containsIon(String compound) {
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};

        for (String ion : ions) {
            if (compound.contains(ion)) {
                return ion;
            }
        }
        return null;
    }
    // checks for unnecessary parentheses in a compound from parsing, eg Na(NO3) when it could be NaNO3
    // when an ion with unnecessary parentheses is found, itll return compound with the parentheses removed, otherwise unchanged
    public static String handleParentheses(String compound) {
        String ion = containsIon(compound);
        if (ion != null) {
            int startIdx = compound.indexOf(ion);
            int endIdx = startIdx + ion.length() - 1;
            char afterParentheses = 'a';
            if (endIdx+2 < compound.length()) {
                afterParentheses = compound.charAt(endIdx+2);
            }
            if (!Character.isDigit(afterParentheses)) {
                return compound.substring(0, startIdx-1) + ion + compound.substring(endIdx+2);
            }
        }
        return compound;
    }

    public static void getUniqueElements(LinkedHashMap<String, Fraction> elementMap, List<String> uniqueElements) {
        for (String element : elementMap.keySet()) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
    }
}
