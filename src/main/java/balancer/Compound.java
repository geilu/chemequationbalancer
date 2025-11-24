package main.java.balancer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compound {
    private final String originalForm;
    private String changedForm;
    private Map<String, Fraction> elements;

    public Compound(String comp) {
        this.originalForm = comp;
    }

    public String getOriginalForm() {
        return originalForm;
    }

    public Map<String, Fraction> getElements() {
        return new HashMap<>(elements);
    }

    public void setChangedForm(String changedForm) {
        this.changedForm = changedForm;
    }

    public void setElements() {
        this.elements = createElementMap();
    }
    public void setElements(Map<String, Fraction> elements) {
        this.elements = elements;
    }

    private Map<String, Fraction> createElementMap() {
        String comp = (changedForm == null || changedForm.isEmpty()) ? originalForm : changedForm;

        Map<String, Fraction> elementMap = new HashMap<>();

        Pattern pattern = Pattern.compile("([A-Z][a-z]?|\\([A-Za-z0-9]+\\))(\\d*)"); // find uppercase letter with optional lowercase letter or parenthesized group with optional digit
        Matcher matcher = pattern.matcher(comp);

        while (matcher.find()) {
            String element = matcher.group(1);
            String elemIdx = matcher.group(2);

            Fraction count = elemIdx.isEmpty() ? new Fraction(1) : new Fraction(Integer.parseInt(elemIdx));

            elementMap.put(element, elementMap.getOrDefault(element, new Fraction(0)).add(count));
        }

        return elementMap;
    }

    @Override
    public String toString() {
        return originalForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Compound compound = (Compound) o;
        return Objects.equals(originalForm, compound.originalForm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalForm, changedForm, elements);
    }
}
