public class Main {
    public static void main(String[] args) {
        String eq = "Co(NO3)2 + Na2S = CoS + NaNO3";
        String eq2 = "C6H12O6 + O2 = CO2 + H2O";
        String[] sides = eq.split(" = ");
        System.out.println(SideParser.parseSide(sides[0]));
        System.out.println(SideParser.parseSide(sides[1]));
    }
}
