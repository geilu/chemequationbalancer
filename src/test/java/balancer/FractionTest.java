package balancer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FractionTest {
    @Test
    void testMultiply() {
        Fraction a = new Fraction(3, 4);
        Fraction b = new Fraction(5, 8);

        Fraction result = a.multiply(b);
        assertEquals(new Fraction(15, 32), result);
    }

    @Test
    void testSimplifying() {
        Fraction frac = new Fraction(2, 4);
        assertEquals(new Fraction(1, 2), frac);
    }

    @Test
    void testAdd() {
        Fraction a = new Fraction(3, 4);
        Fraction b = new Fraction(5, 8);

        Fraction result = a.add(b);
        assertEquals(new Fraction(11, 8), result);
    }

    @Test
    void testNegate() {
        Fraction a = new Fraction(-3, 8);
        Fraction result = a.negate();
        assertEquals(new Fraction(3, 8), result);
    }

    @Test
    void testGcd() {
        long a = 24;
        long b = 16;
        long gcd = Fraction.gcd(a, b);

        assertEquals(8, gcd);
    }

    @Test
    void testLcm() {
        long a = 24;
        long b = 16;
        long lcm = Fraction.lcm(a, b);

        assertEquals(48, lcm);
    }
}