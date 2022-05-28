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
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.TestFileHandler;
import de.tudo.naantg.parser.ParseKey;

import java.lang.reflect.Method;
import java.util.*;


/**
 * Simple de.tudo.naantg.generator for classes without de.tudo.naantg.annotations like de.tudo.naantg.model classes.
 */
public class SimpleGenerator implements TestGenerator {

    /**
     * contains information for the generation process
     */
    protected GeneratorModel model;

    /**
     * the context of the logger information
     */
    protected String LOG_INFO;

    public SimpleGenerator() {
        LOG_INFO = "[SimpleGenerator] ";
    }


    @Override
    public boolean generateTests(Class<?> tgClass, GeneratorModel model) {
        if (model == null) {
            Logger.logWarning(LOG_INFO + "Error while generating of the test class for "
                    + tgClass.getSimpleName() + "!");
            return false;
        }
        this.model = model;
        return generateClassWithTests(model.getCut(), tgClass, model.getTestClassName());
    }

    /**
     * Generates the test class stub, if it doesn't already exist, and the tests
     * (together with private help methods).
     * The name of the test class to be generated can be the name of cut ending on "Tests".
     * Example: "ErsteKlasse" name of the cut, "ErsteKlasseTests" name of the test class.
     * @param cut the class under test
     * @param tgClass the test generation interface for the cut
     * @param testClassName the name of the test class to be generated
     * @return true if the generation was successful
     */
    public boolean generateClassWithTests(Class<?> cut, Class<?> tgClass, String testClassName) {
        if (cut == null || tgClass == null) return false;
        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath(model.getTestGenPackageUri());
        String content = createTestClass();
        if (content.equals("")) {
            Logger.logWarning(LOG_INFO + "Error while generating the content for the test class "
                    + testClassName + "!");
            return false;
        }
        testFileHandler.createTestFile(testClassName, content);
        testFileHandler.writeImports(testClassName, generateMoreImports());

        Set<String> testCaseModels = model.getTestClassModel().getTestCaseModels().keySet();
        List<String> names = new ArrayList<>();
        for (String testName : testCaseModels) {
            if (testName != null && !testName.isEmpty()) {
                names.add(testName);
            }
        }
        for (String name : names) {
            if (!name.startsWith(ParseKey.GIVEN.getKeyword())
                    && !name.startsWith(ParseKey.WHEN.getKeyword()) && !name.startsWith("generated"))
                continue;

            String methodsContent = createTestMethod(name);
            testFileHandler.writeContent(testClassName, methodsContent, name);
        }
        List<TestCaseModel> privateMethods = model.getTestClassModel().getPrivateHelperMethods();
        for (TestCaseModel helperMethod : privateMethods) {
            ObjectModel objectModel = null;
            String methodsContent = "";
            if (helperMethod.isGiven()) {
                methodsContent = PrivateMethodGenerator.generateHelperMethod(helperMethod);
            }
            else {
                objectModel = helperMethod.getObjectModels().get(0);
            }
            if (!helperMethod.isGiven()) {
                if (helperMethod.getTestMethodName().startsWith("init")) {
                    methodsContent = helperMethod.getHelperMethodContent();
                } else if (objectModel != null) {
                    methodsContent = generateHelperMethod(objectModel);
                }
            }
            if (methodsContent != null && !methodsContent.isEmpty()) {
                testFileHandler.writeContent(testClassName, methodsContent, helperMethod.getTestMethodName());
            }
        }
        return true;
    }

    /**
     * Generates a private helper method for the parent object creation.
     * @param parent the object to create with the helper method
     * @return the private helper method
     */
    public String generateHelperMethod(ObjectModel parent) {
        return PrivateMethodGenerator.generateHelperMethod(model, parent);
    }

    /**
     * Creates the testClassStub (package, imports, class name) depending on the information of the GeneratorModel.
     * @return the class stub
     */
    public String createTestClass() {
        String testClass = "";
        if (model == null) return testClass;

        if (model.getTestClassModel() == null) return testClass;
        TestClassModel tcm = model.getTestClassModel();
        if (tcm.getPackageUri() == null) return testClass;
        String pck = generatePackagePath(tcm);
        if (tcm.getName() == null) return testClass;

        String clas = "class " + tcm.getName();
        String tgClas = model.getTgClass().getSimpleName();
        String imports = generateDefaultImports();
        //String fields = ""; //generateFields();

        testClass = pck +
                "\n\n" +
                imports +
                "\n\n" +
                clas + " /*implements " + tgClas + "*/ {" +
                //fields +
                "\n\n\n" +
                "}";
        return testClass;
    }

    /**
     * Generates the package uri of the test class.
     * @param tcm the information of the test class.
     * @return the package uri
     */
    public String generatePackagePath(TestClassModel tcm) {
        return "package " + tcm.getPackageUri() + ";";
    }

    /**
     * Generates the default imports of the test class.
     * @return the default imports
     */
    public String generateDefaultImports() {
        return "import " + "org.junit.jupiter.api.Test" + ";\n" +
                "import " + "static org.junit.jupiter.api.Assertions.*" + ";\n";
    }

    /**
     * Generates all the needed imports, collected in the generator model.
     * @return a list of import statements
     */
    public List<String> generateMoreImports() {
        List<String> imports = new ArrayList<>();
        if (model.getTestClassModel().getImports() != null) {
            for (String imp : model.getTestClassModel().getImports()) {
                imports.add("import " + imp + ";\n");
            }
        }
        return imports;
    }

    /**
     * Creates the test named with testName depending on the information of the GeneratorModel.
     * @param testName the name of the test
     * @return the generated test
     */
    public String createTestMethod(String testName) {
        if (model.getTestMethodModel(testName) == null) return "";

        String testMethod = "";
        if (model == null) return  testMethod;

        String inputs = generateInputs(testName);
        String methodStatement = generateMethodStatement(testName);
        if (!methodStatement.equals("")) {
            methodStatement += "\n";
        }
        String assertions = generateAssertions(testName);
        String annos = generateAnnotations(testName);

        testMethod = "\t@Test\n" +
                annos +
                "\tpublic void " + testName + "() {\n" +
                inputs + "\n" +
                methodStatement +
                assertions +
                "\t}\n";
        return testMethod;
    }

    public String generateAnnotations(String testName) {
        StringBuilder builder = new StringBuilder();
        if (model.getTestMethodModel(testName).getAnnotations().isEmpty()) return "";

        for (Class<?> anno : model.getTestMethodModel(testName).getAnnotations()) {
            builder.append("\t@");
            builder.append(anno.getSimpleName()).append("\n");
        }
        return builder.toString();
    }

    /**
     * Generates the objects and parameters of the test case.
     * @param testName the name of the test case
     * @return the objects and parameters
     */
    public String generateInputs(String testName) {
        List<ObjectModel> objectModels = model.getObjectModels(testName);
        if (objectModels == null) return "";
        StringBuilder inputs = new StringBuilder();
        for (ObjectModel input : objectModels) {
            generateInput(input, inputs);
        }
        for (ObjectModel input : objectModels) {
            generateCalls(input, inputs);
        }
        return inputs.toString();
    }

    /**
     * Generates the given input and adds it to the inputs.
     * @param input the input to generate
     * @param inputs the generated inputs
     */
    public void generateInput(ObjectModel input, StringBuilder inputs) {
        if (input.isPrivate()) return ;
        if (!input.isNotToGenerate()) {
            String statement = ObjectGenerator.generateObjectStatement(input, model);
            inputs.append(statement);
        }
        TestCaseModel helperMethod = PrivateMethodGenerator.generateFields(model, input, inputs);
        if (helperMethod != null) {
            String content = PrivateMethodGenerator.generatePrivateInitMethod(model, input);
            helperMethod.setHelperMethodContent(content);
        }
    }

    /**
     * Generates method and field calls the given input and adds it to the inputs.
     * @param input the input to generate
     * @param inputs the generated inputs
     */
    public void generateCalls(ObjectModel input, StringBuilder inputs) {
        if (input.isPrivate()) return ;

        for (ObjectModel calling : input.getFieldCalls()) {
            String fieldCall = ObjectGenerator.generateFieldCalls(calling);
            inputs.append(fieldCall);
        }
        for (MethodModel methodModel : input.getMethodCalls()) {
            String methodCall = methodModel.generateMethodStatement();
            inputs.append(methodCall);
        }

    }

    /**
     * Generates the method to test.
     * @param testName the name of the test case
     * @return the method to test
     */
    public String generateMethodStatement(String testName) {
        MethodModel methodModel = model.getMethodOfCUT(testName);
        if (methodModel == null) return "";
        List<Assertion> assertions = getModel().getAssertions(testName);
        for (Assertion assertion : assertions) {
            if (assertion.getAssertType() != null &&
                    assertion.getAssertType().equals(AssertType.THROWS)) return "";
        }
        return methodModel.generateMethodStatement();
    }

    /**
     * Generates the assertions of the test case.
     * @param testName the name of the test case
     * @return the assertions
     */
    public String generateAssertions(String testName) {
        List<Assertion> assertionList = model.getAssertions(testName);
        if (assertionList == null) return "";
        StringBuilder assertions = new StringBuilder();
        if (model.getActualObject(testName) != null) {
            assertions.append(model.getActualObject(testName).generateObjectStatement());
        }
        for (Assertion assertion : assertionList) {
            assertions.append(assertion.generateAssertion());
        }
        return assertions.toString();
    }

    /**
     * Returns the GeneratorModel.
     * @return the GeneratorModel
     */
    public GeneratorModel getModel() {
        return model;
    }

    /**
     * Sets the GeneratorModel.
     * @param model the GeneratorModel
     */
    public void setModel(GeneratorModel model) {
        this.model = model;
    }


}
