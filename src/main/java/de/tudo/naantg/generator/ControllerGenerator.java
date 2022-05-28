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

import de.tudo.naantg.annotations.CheckType;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  Generator for the spring boot controller tests
 *  to test classes with a controller annotation.
 */
public class ControllerGenerator extends SimpleGenerator {

    /**
     * The standard constructor. Sets the log info class.
     */
    public ControllerGenerator() {
        this.LOG_INFO = "[ControllerGenerator] ";
    }

    /**
     * Generates the controller test class.
     */
    @Override
    public String createTestClass() {
        String testClass = "";
        if (model == null) return testClass;

        if (model.getTestClassModel() == null) return testClass;
        TestClassModel tcm = model.getTestClassModel();
        if (tcm.getPackageUri() == null) return testClass;
        String pck = generatePackagePath(tcm);
        if (tcm.getName() == null) return testClass;

        String classAnnotations = generateClassAnnotations();
        String clas = "class " + tcm.getName();
        String tgClas = model.getTgClass().getSimpleName();
        String imports = generateControllerImports();
        String fields = generateClassFields();

        testClass = pck +
                "\n\n" +
                imports +
                "\n\n" +
                classAnnotations +
                "public " + clas + " /*implements " + tgClas + "*/ {\n" +
                "\n" +
                fields +
                "\n\n\n" +
                "}";
        return testClass;
    }

    /**
     * Generates the controller specific imports.
     * @return the controller specific imports
     */
    private String generateControllerImports() {
        String cutImport = "import " + model.getCut().getName() + ";\n";
        return cutImport + "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n" +
                "import org.springframework.boot.test.mock.mockito.MockBean;\n" +
                "import org.springframework.test.web.servlet.MockMvc;\n";
    }

    /**
     * Generates the controller specific annotations.
     * @return the controller specific annotations
     */
    private String generateClassAnnotations() {
        String cut = getModel().getCut().getSimpleName();
        return "@WebMvcTest(" + cut + ".class)\n";
    }

    /**
     * Generates the controller specific fields:
     * MockMvc and MockBeans.
     * @return the controller specific fields
     */
    private String generateClassFields() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t@Autowired\n");
        builder.append("\tprivate MockMvc mockMvc;\n");
        for (ObjectModel mock : model.getTestClassModel().getMocks()) {
            builder.append("\n");
            builder.append("\t@MockBean\n");
            builder.append("\tprivate ").append(mock.getDataType()).append(" ");
            builder.append(mock.getIdentifier()).append(";\n");
        }

        return builder.toString();
    }

    /**
     * Creates the controller test cases named with testName
     * depending on the information of the GeneratorModel.
     * @param testName the name of the test
     * @return the generated test
     */
    @Override
    public String createTestMethod(String testName) {
        String testMethod = "";
        if (model == null) return  testMethod;

        String inputs = generateInputs(testName);
        String assertions = generateExpected(testName);
        String annos = generateAnnotations(testName);

        testMethod = "\t@Test\n" +
                annos +
                "\tpublic void " + testName + "() throws Exception {\n" +
                inputs + "\n" +
                assertions +
                "\t}\n";
        return testMethod;
    }

    private boolean shouldNotGenerateInput(ObjectModel input) {
        return input.getIdentifier().startsWith("param") ||
                input.getParentIdentifier() != null &&
                        input.getParentIdentifier().startsWith("param");
    }

    /**
     * Generates the objects and parameters and mock calls of the test case.
     * @param testName the name of the test case
     * @return the objects and parameters
     */
    @Override
    public String generateInputs(String testName) {
        StringBuilder builder = new StringBuilder();
        for (ObjectModel objectModel : getModel().getParameters(testName)) {
            if (shouldNotGenerateInput(objectModel)) continue;
            super.generateInput(objectModel, builder);
        }
        for (ObjectModel objectModel : getModel().getParameters(testName)) {
            if (shouldNotGenerateInput(objectModel)) continue;
            generateCalls(objectModel, builder);
        }
        MockGenerator mockGenerator = new MockGenerator(model, testName);
        String mocking = mockGenerator.generateMockCalls();
        builder.append("\n").append(mocking);
        if (!mocking.isEmpty()) builder.append("\n");
        builder.append("\t\tthis.mockMvc.perform(\n");
        removeSpecialInputs(testName);
        builder.append(generateGetStatement(testName));
        builder.append(generatePostStatement(testName));
        if (model.getCutObject(testName) != null) {
            for (ObjectModel objectModel : model.getCutObject(testName).getInstanceParameters()) {
                builder.append(generateParam(objectModel));
            }
        }
        builder.append("\t\t)");

        return builder.toString();
    }

    /**
     * Removes objects with GeneratedValue annotation.
     * @param testName the name of the test case
     */
    public void removeSpecialInputs(String testName) {
        List<ObjectModel> objectModels = getModel().getObjectModels(testName);
        List<ObjectModel> toDelete = objectModels.stream()
                .filter(objectModel -> objectModel.getAnnotations().contains(SpringBootKey.GENERATED.getKeyword()))
                .collect(Collectors.toList());
        objectModels.removeAll(toDelete);
    }

    /**
     * Generates the post mapping statement.
     * @param testName the name of the test case
     * @return the post mapping statement
     */
    public String generatePostStatement(String testName) {
        MethodModel methodModel = model.getMethodOfCUT(testName);
        if (methodModel == null) return "";

        List<String> getValues = methodModel.getAnnotationValues().get(SpringBootKey.POST.getKeyword());
        if (getValues == null || getValues.size() != 1) return "";

        StringBuilder builder = new StringBuilder();
        builder.append("\t\t\t\tpost(");
        builder.append(getValues.get(0));
        builder.append(")\n");

        return builder.toString();
    }

    /**
     * Generates the get mapping statement.
     * @param testName the name of the test case
     * @return the get mapping statement
     */
    public String generateGetStatement(String testName) {
        MethodModel methodModel = model.getMethodOfCUT(testName);
        if (methodModel == null) return "";

        List<String> getValues = methodModel.getAnnotationValues().get(SpringBootKey.GET.getKeyword());
        if (getValues == null || getValues.size() != 1) return "";

        return  "\t\t\t\tget(" +
                getValues.get(0) +
                ")\n";
    }

    /**
     * Generates the controller model parameter statements.
     * @param param model parameter object
     * @return the controller model parameter statements
     */
    public String generateParam(ObjectModel param) {
        StringBuilder builder = new StringBuilder();
        builder.append("\t\t\t\t\t\t.param(");
        builder.append("\"").append(param.getIdentifier()).append("\"");
        String value = param.getValue();
        if (Utils.isCollectionType(param.getDataType())) {
            value = Utils.removeBrackets(value);
            value = Utils.removeSpaces(value);
            String[] parts = value.split(",");
            for (String part : parts) {
                if (!part.contains("\"")) {
                    part = "\"" + part + "\"";
                }
                builder.append(", ").append(part);
            }
        }
        else {
            builder.append(", ");
            if (value != null && !value.contains("\"")) {
                value = "\"" + value + "\"";
            }
            builder.append(value);
        }
        builder.append(")\n");

        return builder.toString();
    }

    /**
     * Generates the expected part.
     * @param testName the name of the test case
     * @return the expected part
     */
    public String generateExpected(String testName) {
        StringBuilder builder = new StringBuilder();
        List<Assertion> assertions = model.getAssertions(testName);
        if (assertions.isEmpty()) return "";

        for (Assertion assertion : assertions) {
            builder.append("\t\t\t\t");
            if (assertion.getComment() != null && !assertion.getComment().equals("")) {
                builder.append("// ").append(assertion.getComment()).append("\n\t\t\t\t");
            }
            if (assertion.getExpected().equals(CheckType.PRINT.toString())) {
                builder.append(".andDo(print())");
            }
            else if (assertion.getExpected().equals(CheckType.STATUS.toString())) {
                builder.append(".andExpect(status().").append(assertion.getActual()).append("())");
            }
            else if (assertion.getExpected().equals(CheckType.VIEW.toString())) {
                builder.append(".andExpect(view().name(\"").append(assertion.getActual()).append("\"))");
            }
            else if (assertion.getExpected().equals(CheckType.REDIRECT.toString())) {
                builder.append(".andExpect(redirectedUrl(\"");
                String actual = assertion.getActual();
                if (!actual.contains("/")) {
                    actual = "/" + actual;
                }
                builder.append(actual).append("\"))");
            }
            else if (assertion.getExpected().equals(CheckType.CONTENT_CONTAINS.toString())) {
                builder.append(".andExpect(content().string(containsString(\"")
                        .append(assertion.getActual()).append("\")))");
            }
            else if (assertion.getExpected().equals(StateKey.EXIST.toString())) {
                builder.append(".andExpect(model().attributeExists(\"")
                        .append(assertion.getActual()).append("\"))");
            }
            else if (assertion.getExpected().equals(StateKey.NOT_EXIST.toString())) {
                builder.append(".andExpect(model().attributeDoesNotExist(\"")
                        .append(assertion.getActual()).append("\"))");
            }
            else if (assertion.getExpected().equals(StateKey.ATTRIBUTE.toString())) {
                String[] attrFieldValue = assertion.getActual().split(",");
                for (int i = 0; i < attrFieldValue.length; i++) {
                    String val = attrFieldValue[i];
                    if (!val.startsWith("\"")) {
                        attrFieldValue[i] = "\"" + val + "\"";
                    }
                }
                if (attrFieldValue.length == 1) {
                    builder.append(".andExpect(model().attributeHasNoErrors(")
                            .append(attrFieldValue[0]).append("))");
                }
                else if (attrFieldValue.length == 2) {
                    builder.append(".andExpect(model().attribute(")
                            .append(attrFieldValue[0]).append(", ")
                            .append(attrFieldValue[1]).append("))");
                }
                else if (attrFieldValue.length == 3) {
                    builder.append(".andExpect(model().attribute(")
                            .append(attrFieldValue[0]).append(", ")
                            // hasProperty("field", is("value"))
                            .append("hasProperty(")
                            .append(attrFieldValue[1]).append(", is(")
                            .append(attrFieldValue[2]).append("))))");
                }
                else {
                    Logger.logWarning("Error while processing ATTRIBUTE with " + assertion.getActual());
                }
            }
            else if (assertion.getExpected().equals(StateKey.ERROR.toString())) {
                String[] attrFieldValue = assertion.getActual().split(",");
                for (int i = 0; i < attrFieldValue.length; i++) {
                    String val = attrFieldValue[i];
                    if (!val.startsWith("\"")) {
                        attrFieldValue[i] = "\"" + val + "\"";
                    }
                }
                if (attrFieldValue.length == 1) {
                    builder.append(".andExpect(model().attributeHasErrors(")
                            .append(attrFieldValue[0]).append("))");
                }
                else if (attrFieldValue.length == 2) {
                    builder.append(".andExpect(model().attributeHasFieldErrors(")
                            .append(attrFieldValue[0]).append(", ")
                            .append(attrFieldValue[1]).append("))");
                }
                else if (attrFieldValue.length == 3) {
                    builder.append(".andExpect(model().attributeHasFieldErrorCode(")
                            .append(attrFieldValue[0]).append(", ")
                            .append(attrFieldValue[1]).append(", ")
                            .append(attrFieldValue[2]).append("))");
                }
                else {
                    Logger.logWarning("Error while processing ERROR with " + assertion.getActual());
                }
            }
            else {
                Logger.logWarning("Error while processing Assertion with " + assertion.getExpected() + ", " + assertion.getActual());
            }
            builder.append("\n");
        }
        builder.replace(builder.length()-1, builder.length(), ";\n");

        return builder.toString();
    }

}
