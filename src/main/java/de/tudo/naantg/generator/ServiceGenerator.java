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
import java.util.stream.Collectors;

/**
 *  Generator for the spring boot service tests
 *  to test classes with an service annotation.
 */
public class ServiceGenerator extends SimpleGenerator {

    public ServiceGenerator() {
        this.LOG_INFO = "[ServiceGenerator] ";
    }

    /**
     * Creates the service test class.
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
        String imports = generateServiceImports();
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
     * Generates the service specific imports.
     * @return the service specific imports
     */
    private String generateServiceImports() {
        String cutImport = "import " + model.getCut().getName() + ";\n";
        return cutImport + "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.junit.jupiter.api.extension.ExtendWith;\n" +
                "import org.mockito.InjectMocks;\n" +
                "import org.mockito.Mock;\n" +
                "import org.mockito.junit.jupiter.MockitoExtension;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n";
    }

    /**
     * Generates the service specific annotations.
     * @return the service specific annotations
     */
    private String generateClassAnnotations() {
        return "@ExtendWith(MockitoExtension.class)\n";
    }

    /**
     * Generates the service specific fields.
     * @return the service specific fields
     */
    private String generateClassFields() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t@InjectMocks\n");
        Class<?> cut = getModel().getCut();
        builder.append("\tprivate ").append(cut.getSimpleName()).append(" ");
        builder.append(Utils.setLowerCaseFirstChar(cut.getSimpleName())).append(";\n");
        for (ObjectModel mock : model.getTestClassModel().getMocks()) {
            builder.append("\n");
            builder.append("\t@Mock\n");
            builder.append("\tprivate ").append(mock.getDataType()).append(" ");
            builder.append(mock.getIdentifier()).append(";\n");
        }

        return builder.toString();
    }

    /**
     * Generates the method to test except one assert type is "THROWN".
     * @param testName the name of the test case
     * @return the method to test
     */
    @Override
    public String generateMethodStatement(String testName) {
        MethodModel methodModel = model.getMethodOfCUT(testName);
        if (methodModel == null) return "";
        List<Assertion> assertions = getModel().getAssertions(testName);
        for (Assertion assertion : assertions) {
            if (assertion.getAssertType() != null &&
                    assertion.getAssertType().equals(AssertType.THROWN)) return "";
        }
        return methodModel.generateMethodStatement();
    }

    /**
     * Generates the mocking statements, objects and parameters of the test case.
     * @param testName the name of the test case
     * @return the mocking statements, objects and parameters
     */
    @Override
    public String generateInputs(String testName) {
        removeSpecialInputs(testName);
        // add parameter inits from mocking
        for (ObjectModel param : model.getParameters(testName)) {
            if (!model.getObjectModels(testName).contains(param)) {
                model.getObjectModels(testName).add(param);
            }
        }
        MockGenerator mockGenerator = new MockGenerator(model, testName);
        return super.generateInputs(testName) +
                mockGenerator.generateMockCalls();
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
     * Generates the assertions.
     * @param testName the name of the test case
     * @return the generated assertions
     */
    @Override
    public String generateAssertions(String testName) {
        StringBuilder builder = new StringBuilder();
        List<Assertion> assertions = getModel().getAssertions(testName);
        MethodModel methodToTest = getModel().getMethodOfCUT(testName);
        if (model.getActualObject(testName) != null) {
            builder.append(model.getActualObject(testName).generateObjectStatement());
        }
        for (Assertion assertion : assertions) {
            if (assertion.getAssertType() == null) continue;
            if (assertion.getAssertType().equals(AssertType.THROWN)) {
                builder.append("\t\t").append(SpringBootKey.ASSERT_THROWN.getKeyword());
                builder.append("( () -> ");
                String methodCall = methodToTest.generateMethodStatement(false);
                methodCall = methodCall.substring(2, methodCall.length() - 2);
                builder.append(methodCall);
                builder.append(" )\n");
                builder.append("\t\t\t\t.isInstanceOf( ").append(assertion.getActual());
                builder.append(".class );\n");
            }
            else {
                builder.append(assertion.generateAssertion());
            }
        }

        return builder.toString();
    }

}
