package balancer;

import java.util.*;

public final class SideParser {

    private SideParser(){}

    public static List<Compound> parseSide(String side, List<String> uniqueElements) {
        List<Compound> compList = new ArrayList<>();
        String[] compounds = side.split(" \\+ ");

        for (String comp : compounds) {
            Compound compound = new Compound(comp);

            if (comp.contains("(") && comp.contains(")")) {
                String reverse = new StringBuilder(comp).reverse().toString();
                int digitIdx = comp.length() - reverse.indexOf(")");
                digitIdx = comp.length() > digitIdx ? digitIdx : -1;

                if (digitIdx != -1 && Character.isDigit(comp.charAt(digitIdx))) {
                    int digit = Integer.parseInt(comp.substring(digitIdx, digitIdx + 1));
                    compound.setChangedForm(multiplyInsideParentheses(comp, new Fraction(digit)));
                }
            }

            compound.setElements();
            compList.add(compound);
            getUniqueElements(compound.getElements(), uniqueElements);
        }
        return compList;
    }

    public static String multiplyInsideParentheses(String compound, Fraction scalar) {
        // for cases like Fe(C2H3O2)3 or Co(NO3)2 we'll turn it into FeC6H9O6 or CoN2O6 for easier parsing
        if (compound.contains("(") && compound.contains(")")) {
            int beginParentheses = compound.indexOf("(");
            String reverse = new StringBuilder(compound).reverse().toString();
            int endParentheses = compound.length() - reverse.indexOf(")") - 1; // get the outermost end parentheses
            StringBuilder sb = new StringBuilder();
            sb.append(compound, 0, beginParentheses);

            String inParentheses = compound.substring(beginParentheses+1, endParentheses);

            if (inParentheses.contains("(") && inParentheses.contains(")")) { // if theres double parentheses, eg in (Co(NH3)4CO3)2SO4
                int digitIdx = inParentheses.indexOf(")") + 1;
                digitIdx = inParentheses.length() > digitIdx ? digitIdx : -1;

                if (digitIdx != -1 && Character.isDigit(inParentheses.charAt(digitIdx))) {
                    int digit = Integer.parseInt(inParentheses.substring(digitIdx, digitIdx + 1));
                    inParentheses = multiplyInsideParentheses(inParentheses, new Fraction(digit));
                }
            }

            Compound inParenthesesComp = new Compound(inParentheses);
            inParenthesesComp.setElements();

            Map<String, Fraction> inParenthesesMap = inParenthesesComp.getElements();

            for (Map.Entry<String, Fraction> entry : inParenthesesMap.entrySet()) {
                String key = entry.getKey();
                Fraction value = entry.getValue();

                value = value.multiply(scalar);
                inParenthesesMap.replace(key, value);

                sb.append(key);
                sb.append(value.getNumerator());
            }
            int endCharIdx = compound.length() == endParentheses + 1 ? -1 : endParentheses + 1;
            if(endCharIdx != -1 && Character.isDigit(compound.charAt(endParentheses+1))) {
                sb.append(compound.substring(endCharIdx+1));
            } else if (endCharIdx != -1) {
                sb.append(compound.substring(endCharIdx));
            }
            return sb.toString();
        }
        return compound;
    }

    public static void getUniqueElements(Map<String, Fraction> elementMap, List<String> uniqueElements) {
        for (String element : elementMap.keySet()) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
    }
}
