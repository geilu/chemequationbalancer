import java.util.HashMap;

public class Main {
    private static HashMap<String, Integer> parseSide(String side) {
        String[] compounds = side.split(" \\+ ");
        HashMap<String, Integer> elementMap = new HashMap<>();

        for (String comp : compounds) {
            parseCompound(comp, elementMap);
        }
        return elementMap;
    }

    private static HashMap<String, Integer> parseCompound(String compound, HashMap<String, Integer> elementMap) {
        if (compound.contains("(") && compound.contains(")")) {
            compound = removeParentheses(compound);
        }

        int[] ionIndices = findIon(compound);

        int prevIdx = 0;
        for (int i = 0; i < compound.length(); i++) {
            char c = compound.charAt(i);

            if (i > prevIdx && Character.isUpperCase(c)) { // if its an uppercase character (or we are at end of string) then that "concludes" previous element and c is the (start of) new element
                char prevChar = compound.charAt(i-1);

                if (Character.isDigit(prevChar)) { // if the prev char is a nr then that is the count of prev element, else just put +1
                    String elem = compound.substring(prevIdx, i-1);
                    int elemIdx = prevChar - '0'; // char into int
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0)+elemIdx);
                } else {
                    String elem = compound.substring(prevIdx, i);
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + 1);
                }

                if (ionIndices != null && ionIndices[0] == i) {
                    String elem = compound.substring(ionIndices[0], ionIndices[1]+1);
                    int elemIdx = 1;

                    if ((ionIndices[1] + 1 < compound.length()) && (Character.isDigit(compound.charAt(ionIndices[1]+1)))) { // if there is a nr at the end of the ion (eg CoNO32, from Co(NO3)2)
                        elemIdx = compound.charAt(ionIndices[1] + 1) - '0';
                        i = ionIndices[1] + 1;
                    } else {
                        i = ionIndices[1];
                    }

                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + elemIdx);
                    prevIdx = i + 1;
                    if (prevIdx < compound.length() && Character.isUpperCase(compound.charAt(prevIdx))) {
                        i = prevIdx - 1;
                    }
                    continue;
                }

                prevIdx = i;
            }

            if (i == compound.length() - 1) {
                char lastChar = compound.charAt(i);

                if (Character.isDigit(lastChar)) {
                    int elemIdx = lastChar - '0';
                    String elem = compound.substring(prevIdx, i);
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + elemIdx);
                } else {
                    String elem = compound.substring(prevIdx);
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + 1);
                }
            }
        }
        return elementMap;
    }

    private static String removeParentheses(String compound) {
        int openParen = compound.indexOf("(");
        int closeParen = compound.indexOf(")");

        String insideParen = compound.substring(openParen + 1, closeParen);
        String beforeParen = compound.substring(0, openParen);
        String afterParen = compound.substring(closeParen + 1);

        return beforeParen + insideParen + afterParen;
    }

    private static int[] findIon (String compound) {
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};
        int[] ionIndices = new int[2];

        for (String ion : ions) {
            if (compound.contains(ion)) {
                int startIdx = compound.indexOf(ion);

                ionIndices[0] = startIdx;
                ionIndices[1] = startIdx + ion.length() - 1; // -1 bc index starts at 0
                return ionIndices;
            }
        }
        return null; // return null if no ion found
    }

    public static void main(String[] args) {
        String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        String eq2 = "C6H12O6 + O2 = CO2 + H2O";
        String[] sides = eq2.split(" = ");
        System.out.println(parseSide(sides[0]));
        System.out.println(parseSide(sides[1]));
    }
}
