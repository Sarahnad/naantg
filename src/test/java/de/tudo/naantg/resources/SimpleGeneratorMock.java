package de.tudo.naantg.resources;

import de.tudo.naantg.generator.SimpleGenerator;
import de.tudo.naantg.model.ObjectModel;

import java.util.List;

/**
 * This class replaces random values in the test methods with fixed values
 * for enable comparison of the generated tests.
 */
public class SimpleGeneratorMock extends SimpleGenerator {

    private final List<String> randomMethods;

    public SimpleGeneratorMock(List<String> randomMethods) {
        super();
        this.randomMethods = randomMethods;
    }

    @Override
    public String generateInputs(String testName) {
        String inputs = super.generateInputs(testName);
        if (randomMethods == null) return inputs;
        if (!randomMethods.contains(testName)) return inputs;

        StringBuilder changed = new StringBuilder();
        String[] splitted = inputs.split("\n");
        for (String input : splitted) {
            if (input.startsWith("\t\tint param") || input.startsWith("\t\tint iVar"))
                input = input.replaceAll("= -?(\\d)+", "= 44");
            else if (input.startsWith("\t\tString param")  || input.startsWith("\t\tString str"))
                input = input.replaceAll("= \".*\"", "= \"§Puiu\"");
            else if (input.startsWith("\t\tchar param"))
                input = input.replaceAll("= '.'", "= 'G'");
            else if (input.startsWith("\t\tboolean param"))
                input = input.replaceAll("= false", "= true");
            else if (input.startsWith("\t\tdouble param"))
                input = input.replaceAll("= -?(\\d)+\\.(\\d)+d?", "= -0.4397059479246579d");
            else if (input.startsWith("\t\tfloat param"))
                input = input.replaceAll("= -?(\\d)+\\.(\\d)+f?", "= 0.65997547f");
            else if (input.startsWith("\t\tbyte param"))
                input = input.replaceAll("= -?(\\d)+", "= 119");
            else if (input.startsWith("\t\tlong param"))
                input = input.replaceAll("= -?(\\d)+L?", "= 1628253323520968601L");
            else if (input.startsWith("\t\tshort param"))
                input = input.replaceAll("= -?(\\d)+", "= -808");

            else if (input.contains("int[] param"))
                input = input.replaceAll("\\{(-?(\\d)+(,\\s)?)*}", "{1, -1, 2, -3, 4, -3, 8}");
            else if (input.contains("new int[] {"))
                input = input.replaceAll("\\{(-?(\\d)+(,\\s)?)*}", "{1, -3}");
            else if (input.contains("List<String> param"))
                input = input.replaceAll("\\(((\".*\")(,\\s)?)*\\)", "(\"cat\", \"dog\", \"bird\", \"horse\", \"cow\", \"butterfly\")");
            else if (input.contains("new String[] {"))
                input = input.replaceAll("\\{((\".*\")(,\\s)?)*}", "{\"cat\", \"dog\"}");

            else if (input.contains("List<Dinge> param"))
                input = input.replaceAll("\\(((Dinge\\..+)(,\\s)?)*\\)", "(Dinge.BECHER, Dinge.BECHER)");

            changed.append(input).append("\n");
        }
        return changed.toString();
    }

    @Override
    public String generateHelperMethod(ObjectModel parent) {
        String result = super.generateHelperMethod(parent);
        if (randomMethods == null) return result;
        if (!randomMethods.contains(parent.getIdentifier())) return result;

        if (result.contains("new int[] {"))
            result = result.replaceAll("\\{(-?(\\d)+(,\\s)?)*}", "{1, -3}");
        else if (result.contains("new String[] {"))
            result = result.replaceAll("\\{((\".*\")(,\\s)?)*}", "{\"cat\", \"dog\"}");
        return result;
    }

}
