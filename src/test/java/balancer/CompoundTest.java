package balancer;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompoundTest {
    @Test
    void testCreateElementMap() {
        Compound comp = new Compound("C6H12O6");
        Map<String, Fraction> expected = Map.of("C", new Fraction(6),
                                                                              "H", new Fraction(12),
                                                                              "O", new Fraction(6));
        comp.setElements();
        Map<String, Fraction> elementMap = comp.getElements();

        for(Map.Entry<String, Fraction> entry : expected.entrySet()) {
            assertTrue(elementMap.containsKey(entry.getKey()));
            assertEquals(elementMap.get(entry.getKey()), entry.getValue());
        }
    }
}
