import dataobjects.Compound;
import dataobjects.Fraction;

import java.util.*;

public final class SideParser {

    private SideParser(){}

    public static List<Compound> parseSide(String side, List<String> uniqueElements) {
        List<Compound> compList = new ArrayList<>();
        String[] compounds = side.split(" \\+ ");

        for (String comp : compounds) {
            Compound compound = new Compound(comp);

            String foundIon = containsIon(comp);
            if (foundIon != null) {
                String compChangedForm = addParentheses(comp, foundIon);
                compound.setChangedForm(compChangedForm);
            }

            compound.setElements(); // create the element map when changed form is set
            compList.add(compound);
            getUniqueElements(compound.getElements(), uniqueElements); // update unique elements list
        }
        return compList;
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

    public static void getUniqueElements(Map<String, Fraction> elementMap, List<String> uniqueElements) {
        for (String element : elementMap.keySet()) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
    }
}
