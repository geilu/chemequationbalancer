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
        // Na2SO4
        // Co(NO3)2
        String[] ions = {"NO2", "NO3", "SO3", "SO4", "CO3", "PO4", "OH", "NH4"};

        if (!(compound.indexOf("(") == -1) && !(compound.indexOf(")") == -1)) {
            compound = removeParentheses(compound);
        }

        int prevIdx = 0;
        for (int i = 1; i < compound.length(); i++) { // i=1 bc first letter is always uppercase
            char c = compound.charAt(i);
            if (Character.isUpperCase(c)) { // if its an uppercase character (or we are at end of string) then that "concludes" previous element and c is the (start of) new element
                char prevChar = compound.charAt(i-1);
                if (Character.isDigit(prevChar)) { // if the prev char is a nr then that is the count of prev element, else just put +1
                    String elem = compound.substring(prevIdx, i-1);
                    int count = prevChar - '0'; // char into int

                    elementMap.put(elem, elementMap.getOrDefault(elem, 0)+count);
                } else {
                    String elem = compound.substring(prevIdx, i);
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + 1);
                }
                prevIdx = i;
            }
            if (i == compound.length() - 1) {
                if (Character.isLowerCase(c)) { // lowercase letter need to also include prev (theres never more than one lowercase letter in an element
                    String elem = compound.substring(i - 1);
                    elementMap.put(elem, elementMap.getOrDefault(elem, 0) + 1);
                } else if (Character.isDigit(c)) {
                    // todo when its sth like H2 or Na2
                } else { // else can just add elem by itself
                    String elem = compound.substring(i);
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

    public static void main(String[] args) {
        String eq = "Co(NO3)2 + Na2S + Ba + H2 = CoS + NaNO3";
        String[] sides = eq.split(" = ");
        System.out.println(parseSide(sides[0]));
    }
}
