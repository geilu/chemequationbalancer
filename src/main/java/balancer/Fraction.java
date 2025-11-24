package balancer;

public final class Fraction {
    private final long numerator;
    private final long denominator;

    public static final Fraction ZERO = new Fraction(0);

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        long gcd = gcd(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public Fraction(long number) {
        this.numerator = number;
        this.denominator = 1;
    }

    public Fraction multiply(Fraction b) {
        return new Fraction(this.numerator * b.getNumerator(), this.denominator * b.getDenominator());
    }

    public Fraction add(Fraction b) {
        long resultNumerator = this.numerator * b.getDenominator() + b.getNumerator() * this.denominator;
        long resultDenominator = this.denominator * b.getDenominator();
        return new Fraction(resultNumerator, resultDenominator);
    }

    public Fraction negate() {
        return new Fraction(-this.getNumerator(), this.getDenominator());
    }

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a * b) / gcd(a, b);
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        return this.numerator == fraction.getNumerator() && this.denominator == fraction.getDenominator();
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(this.numerator) + Long.hashCode(this.denominator);
    }
}
