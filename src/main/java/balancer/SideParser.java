package balancer;

import java.util.*;

public final class SideParser {

    private SideParser(){}

    public static List<Compound> parseSide(String side, List<String> uniqueElements) {
        List<Compound> compList = new ArrayList<>();
        String[] compounds = side.split(" \\+ ");

        for (String comp : compounds) {
            Compound compound = new Compound(comp);

            String changedForm = comp;
            while (changedForm.contains("(") && changedForm.contains(")") || changedForm.contains("[") && changedForm.contains("]")) {
                int[] parenIndices = findParentheses(changedForm); // get innermost parentheses
                int digitIdx = parenIndices[1] + 1;
                digitIdx = changedForm.length() > digitIdx ? digitIdx : -1;

                if (digitIdx != -1 && Character.isDigit(changedForm.charAt(digitIdx))) {
                    int scalar = Integer.parseInt(changedForm.substring(digitIdx, digitIdx+1));
                    changedForm = multiplyInsideParentheses(changedForm, new Fraction(scalar));
                } else if (digitIdx == -1 || !Character.isDigit(changedForm.charAt(digitIdx))) {
                    changedForm = multiplyInsideParentheses(changedForm, new Fraction(1)); // scale by 1 to just remove the parentheses
                }
            }

            changedForm = changedForm.equals(comp) ? null : changedForm;
            compound.setChangedForm(changedForm);

            compound.setElements();
            compList.add(compound);
            getUniqueElements(compound.getElements(), uniqueElements);
        }
        return compList;
    }

    public static String multiplyInsideParentheses(String compound, Fraction scalar) {
        // for cases like Fe(C2H3O2)3 or Co(NO3)2 we'll turn it into FeC6H9O6 or CoN2O6 for easier parsing
        int[] parenIndices = findParentheses(compound); // get the innermost parentheses index
        StringBuilder sb = new StringBuilder();

        sb.append(compound, 0, parenIndices[0]);

        String inParentheses = compound.substring(parenIndices[0]+1, parenIndices[1]);
        Compound inParenthesesComp = new Compound(inParentheses);
        inParenthesesComp.setElements();
        Map<String, Fraction> inParenthesesMap = inParenthesesComp.getElements();

        for (Map.Entry<String, Fraction> entry : inParenthesesMap.entrySet()) {
            String key = entry.getKey();
            Fraction value = entry.getValue();

            value = value.multiply(scalar);
            inParenthesesMap.replace(key, value);

            sb.append(key);
            if (value.getNumerator() > 1) {
                sb.append(value.getNumerator());
            }
        }

        int endCharIdx = compound.length() == parenIndices[1] + 1 ? -1 : parenIndices[1] + 1;
        if(endCharIdx != -1 && Character.isDigit(compound.charAt(parenIndices[1]+1))) {
            sb.append(compound.substring(endCharIdx+1));
        } else if (endCharIdx != -1) {
            sb.append(compound.substring(endCharIdx));
        }
        return sb.toString();
    }

    public static int[] findParentheses(String compound) {
        int[] parenIndices = new int[]{-1, -1};
        char openChar = 0;
        // find the rightmost opening paren
        for (int i = 0; i < compound.length(); i++) {
            char c = compound.charAt(i);
            if (c == '(' || c == '[') {
                parenIndices[0] = i;
                openChar = c;
            }
        }
        if (parenIndices[0] == -1) {
            return parenIndices;
        }
        // find matching closing paren
        char closeChar = openChar == '(' ? ')' : ']';
        for (int i = parenIndices[0]; i < compound.length(); i++) {
            if (compound.charAt(i) == closeChar) {
                parenIndices[1] = i;
                break;
            }
        }
        return parenIndices;
    }

    public static void getUniqueElements(Map<String, Fraction> elementMap, List<String> uniqueElements) {
        for (String element : elementMap.keySet()) {
            if (!uniqueElements.contains(element)) {
                uniqueElements.add(element);
            }
        }
    }

    public static String buildEquation(long[] coeffs, List<Compound> reactants, List<Compound> products) {
        StringBuilder sb = new StringBuilder();

        int idx = 0;
        int compoundCount = reactants.size();
        for (Compound reactant : reactants) {
            appendElement(coeffs[idx], sb, reactant.getOriginalForm(), compoundCount, idx);
            idx++;
        }
        sb.append(" = ");
        compoundCount += products.size();
        for (Compound product : products) {
            appendElement(coeffs[idx], sb, product.getOriginalForm(), compoundCount, idx);
            idx++;
        }
        return sb.toString();
    }

    public static void appendElement(long coeff, StringBuilder sb, String compound, int compoundCount, int idx) {
        if (coeff > 1) {
            sb.append(coeff);
        }
        sb.append(compound);
        if (idx+1 < compoundCount) {
            sb.append(" + ");
        }
    }
}
