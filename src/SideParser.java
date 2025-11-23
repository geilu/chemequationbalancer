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

            //String foundIon = containsIon(comp);
            if (comp.contains("(") && comp.contains(")")) { // does it still have parentheses? eg. Fe(C2H3O3)3
                int digitIdx = comp.indexOf(")") + 1;
                digitIdx = comp.length() > digitIdx ? digitIdx : -1;

                if (digitIdx != -1 && Character.isDigit(comp.charAt(digitIdx))) {
                    int digit = Integer.parseInt(comp.substring(digitIdx, digitIdx + 1));
                    compound.setChangedForm(multiplyInsideParentheses(comp, new Fraction(digit)));
                }
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

    private static String multiplyInsideParentheses(String compound, Fraction scalar) {
        // for cases like Fe(C2H3O2)3 or Co(NO3)2 we'll turn it into FeC6H9O6 or CoN2O6 for easier parsing
        if (compound.contains("(") && compound.contains(")")) {
            int beginParentheses = compound.indexOf("(");
            int endParentheses = compound.indexOf(")");

            StringBuilder sb = new StringBuilder();
            sb.append(compound, 0, beginParentheses); // append Fe

            String inParentheses = compound.substring(beginParentheses+1, endParentheses); // get the C2H3O2

            Compound inParenthesesComp = new Compound(inParentheses);
            inParenthesesComp.setElements(); // create initial map

            Map<String, Fraction> inParenthesesMap = inParenthesesComp.getElements();

            for (Map.Entry<String, Fraction> entry : inParenthesesMap.entrySet()) {
                String key = entry.getKey();
                Fraction value = entry.getValue();

                value = value.multiply(scalar);
                inParenthesesMap.replace(key, value);

                sb.append(key);
                sb.append(value.getNumerator());
            }
            return sb.toString();
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
