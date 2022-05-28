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

import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.TestFileHandler;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.MethodNameParser;
import de.tudo.naantg.parser.ParseKey;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  Generator for the spring boot repository tests
 *  to test classes with an repository annotation.
 */
public class RepositoryGenerator extends SimpleGenerator {

    public RepositoryGenerator() {
        this.LOG_INFO = "[RepositoryGenerator] ";
    }

    /**
     * Creates the repository test class.
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
        String imports = generateRepositoryImports();
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
     * Generates the repository specific imports.
     * @return the repository specific imports
     */
    private String generateRepositoryImports() {
        String cutImport = "import " + model.getCut().getName() + ";\n";
        return cutImport + "\n" +
                "import static org.assertj.core.api.Assertions.*;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;\n" +
                "import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;\n";
    }

    /**
     * Generates the repository specific annotations.
     * @return the repository specific annotations
     */
    private String generateClassAnnotations() {
        return "@DataJpaTest\n";
    }

    /**
     * Generates the repository specific fields:
     * TestEntityManager and cut.
     * @return the repository specific fields
     */
    private String generateClassFields() {
        String cut = model.getCut().getSimpleName();
        String cutName = Utils.setLowerCaseFirstChar(cut);
        return "\t@Autowired\n" +
                "\tprivate TestEntityManager entityManager;\n" +
                "\n" +
                "\t@Autowired\n" +
                "\tprivate " + cut + " " + cutName + ";\n";
    }

    /**
     * Generates the objects and parameters of the test case.
     * @param testName the name of the test case
     * @return the objects and parameters
     */
    @Override
    public String generateInputs(String testName) {
        removeSpecialInputs(testName);
        String inputs = super.generateInputs(testName);
        inputs += generateSpecialInputs(testName);
        return inputs;
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
     * Adds the persist statements.
     * @param testName the name of the test case
     * @return the persist statements
     */
    public String generateSpecialInputs(String testName) {
        /*List<Assertion> assertions = model.getAssertions(testName);
        if (assertions.isEmpty()) return "";
        boolean isValue = false;
        for (Assertion assertion : assertions) {
            if (assertion.getActual().contains("isPresent") ||
                    (assertion.getAssertType().equals(AssertType.NEQ) ||
                    assertion.getAssertType().equals(AssertType.NOTEQUALS))
                            && assertion.getExpected().equals("null")) {
                isValue = true;
                break;
            }
        }
        if (!isValue) return "";*/

        List<String> givenState = MethodNameParser.getGivenValue(testName);
        String parsedEntity = "";
        if (givenState.size() == 1) {
            parsedEntity = Utils.setUpperCaseFirstChar(givenState.get(0));
        }
        else if (givenState.size() == 2 && givenState.get(0).equals(StateKey.VALUE.toString())) {
            parsedEntity = Utils.setUpperCaseFirstChar(givenState.get(1));
        }

        if (parsedEntity.isEmpty() || !testName.contains(ParseKey.FIND.getKeyword())) {
            return "";
        }

        ObjectModel objectModel = Helper.findOptionalObject(model, testName);
        if (objectModel == null) return "";

        String value = "\n\t\tentityManager.persist(";
        value += objectModel.getIdentifier();
        value += ");\n" +
                "\t\tentityManager.flush();\n";

        return value;
    }

}
