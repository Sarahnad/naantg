/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.generator;

import de.tudo.naantg.model.*;

import java.util.List;

/**
 * Generates the mocking statements.
 */
public class MockGenerator {

    /**
     * the generator model
     */
    private final GeneratorModel model;

    /**
     * the name of the test case
     */
    private final String testName;

    /**
     * Creates the MockGenerator.
     * @param model the generator model
     * @param testName the name of the test case
     */
    public MockGenerator(GeneratorModel model, String testName) {
        this.model = model;
        this.testName = testName;
    }

    /**
     * Generates mock calls for the test case with the given name.
     * @return the mock calls
     */
    public String generateMockCalls() {
        StringBuilder builder = new StringBuilder();
        List<MethodModel> mockCalls = model.getTestMethodModel(testName).getMockCalls();
        for (MethodModel mockCall : mockCalls) {
            String returnIdentifier = mockCall.getReturnIdentifier();
            if (returnIdentifier == null) continue;
            builder.append("\t\t");

            builder.append(returnIdentifier).append("(");

            if (returnIdentifier.equals(SpringBootKey.THROW.getKeyword())) {
                ObjectModel returnObject = mockCall.getReturnObject();
                if (returnObject != null) {
                    builder.append(ObjectGenerator.generateObjectStatement(returnObject, true, false));
                }
                builder.append(")\n");
                builder.append("\t\t\t\t.when(").append(mockCall.getObject()).append(").");
                builder.append(mockCall.getName()).append("(");
            }
            else if (returnIdentifier.equals(SpringBootKey.WHEN.getKeyword())) {
                builder.append(mockCall.getObject()).append(".");
                builder.append(mockCall.getName()).append("(");
            }
            if (!mockCall.getParameterObjects().isEmpty()) {
                for (ObjectModel param : mockCall.getParameterObjects()) {
                    builder.append(Utils.removeSpaces(param.getIdentifier())).append(", ");
                }
                builder.delete(builder.length()-2, builder.length());
            }
            builder.append(")");
            if (returnIdentifier.equals(SpringBootKey.WHEN.getKeyword())) {
                builder.append(").thenReturn(");
                String returnValue = mockCall.getReturnObject() != null ?
                        ObjectGenerator.generateObjectStatement(mockCall.getReturnObject(),
                                true, false) : mockCall.getReturnType();
                builder.append(returnValue).append(")");
            }
            builder.append(";\n");
        }
        return builder.toString();
    }

}
