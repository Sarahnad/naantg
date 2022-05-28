/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.model;

import java.util.List;

/**
 * Collection of all necessary information for the test generation.
 */
public class GeneratorModel {

    /**
     * the information of the class stub
     */
    private TestClassModel testClassModel;

    /**
     * the test generation interface
     */
    private Class<?> tgClass;

    /**
     * the class under test
     */
    private Class<?> cut;

    /**
     * the name of the test class
     */
    private String testClassName;

    /**
     * test package uri with slashes like "main/java/test"
     * where the test generation classes can be found
     */
    private String testPackageUriWithSlashs;

    /**
     * project package uri with slashes like "main/java"
     * where the classes under test can be found
     */
    private String projectPackageUriWithSlashs;

    /**
     * test package uri with dots like "main.java.test.testGen"
     * for the package declaration in the generated test class
     */
    private String testPackageUriWithDots;

    /**
     * project package uri with dots like "main.java"
     */
    private String projectPackageUriWithDots;

    /**
     * testGen package uri like "src/main/java/test"
     * for the path of the testGen folder
     */
    private String testGenPackageUri;


    /**
     *
     * @return the information of the test class stub
     */
    public TestClassModel getTestClassModel() {
        return testClassModel;
    }

    /**
     *
     * @param testClassModel the information of the test class stub
     */
    public void setTestClassModel(TestClassModel testClassModel) {
        this.testClassModel = testClassModel;
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the information of the test case
     */
    public MethodModel getMethodOfCUT(String methodName) {
        return getTestMethodModel(methodName).getMethodToTest();
    }

    /**
     *
     * @param methodOfCUT the information of the test case
     * @param methodName the name of the test case
     */
    public void setMethodOfCUT(MethodModel methodOfCUT, String methodName) {
        getTestMethodModel(methodName).setMethodToTest(methodOfCUT);
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the assertions of the test case
     */
    public List<Assertion> getAssertions(String methodName) {
        return getTestMethodModel(methodName).getAssertions();
    }

    /**
     *
     * @param assertions the assertions of the test case
     * @param methodName the name of the test case
     */
    public void setAssertions(List<Assertion> assertions, String methodName) {
        getTestMethodModel(methodName).setAssertions(assertions);
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the inputs of the test case
     */
    public List<ObjectModel> getObjectModels(String methodName) {
        return getTestMethodModel(methodName).getObjectModels();
    }

    /**
     *
     * @param objectModels the inputs of the test case
     * @param methodName the name of the test case
     */
    public void setObjectModels(List<ObjectModel> objectModels, String methodName) {
        getTestMethodModel(methodName).setObjectModels(objectModels);
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the parameters of the method to test
     */
    public List<ObjectModel> getParameters(String methodName) {
        return getTestMethodModel(methodName).getParameters();
    }

    /**
     *
     * @param parameters the parameters of the method to test
     * @param methodName the name of the test case
     */
    public void setParameters(List<ObjectModel> parameters, String methodName) {
        getTestMethodModel(methodName).setParameters(parameters);
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the actual value of an inspector method
     */
    public ObjectModel getActualObject(String methodName) {
        return getTestMethodModel(methodName).getActualObject();
    }

    /**
     *
     * @param actualObject the actual value of an inspector method
     * @param methodName the name of the test case
     */
    public void setActualObject(ObjectModel actualObject, String methodName) {
        getTestMethodModel(methodName).setActualObject(actualObject);
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the cut object model
     */
    public ObjectModel getCutObject(String methodName) {
        return getTestMethodModel(methodName).getCutObject();
    }

    /**
     *
     * @param cutObject the cut object model
     * @param methodName the name of the test case
     */
    public void setCutObject(ObjectModel cutObject, String methodName) {
        getTestMethodModel(methodName).setCutObject(cutObject);
    }

    /**
     * Returns the test case model with the given name.
     * If it is null, a new model with the methodName is created.
     * @param methodName the name of the test case
     * @return the information of the test case
     */
    public TestCaseModel getTestMethodModel(String methodName) {
        if (testClassModel == null) this.testClassModel = new TestClassModel();
        TestCaseModel testCaseModel = testClassModel.getTestCaseModels().get(methodName);
        if (testCaseModel == null) {
            testCaseModel = new TestCaseModel();
            setTestMethodModel(testCaseModel, methodName);
        }
        return testCaseModel;
    }

    /**
     *
     * @param methodName the name of the test case
     * @return the given init objects
     */
    public List<ObjectModel> getGivenInitObjects(String methodName) {
        return getTestMethodModel(methodName).getGivenInitObjects();
    }

    /**
     * Returns a unique identifier for the given type and the test case with the given name.
     * @param methodName the name of the test case
     * @param type the type
     * @return a unique identifier
     */
    public String getUniqueIdentifier(String methodName, String type) {
        return getTestMethodModel(methodName).getUniqueIdentifier(type);
    }

    /**
     * Returns a unique identifier for the given type and the test case with the given name.
     * If a name as identifier is given it will be converted to an unique identifier.
     * @param methodName the name of the test case
     * @param type the type
     * @param name an identifier to make unique (can be null)
     * @return a unique identifier
     */
    public String getUniqueIdentifier(String methodName, String type, String name) {
        return getTestMethodModel(methodName).getUniqueIdentifier(type, name);
    }

    /**
     *
     * @param testCaseModel the information of the test case
     * @param methodName the name of the test case
     */
    public void setTestMethodModel(TestCaseModel testCaseModel, String methodName) {
        if (testClassModel == null) return;
        testClassModel.getTestCaseModels().put(methodName, testCaseModel);
    }

    /**
     *
     * @return the test generation interface
     */
    public Class<?> getTgClass() {
        return tgClass;
    }

    /**
     *
     * @param tgClass the test generation interface
     */
    public void setTgClass(Class<?> tgClass) {
        this.tgClass = tgClass;
    }

    /**
     *
     * @return the class under test
     */
    public Class<?> getCut() {
        return cut;
    }

    /**
     *
     * @param cut the class under test
     */
    public void setCut(Class<?> cut) {
        this.cut = cut;
    }

    /**
     *
     * @return the test class name
     */
    public String getTestClassName() {
        return testClassName;
    }

    /**
     *
     * @param testClassName the test class name
     */
    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    /**
     *
     * @return the test package uri where the test generation classes can be found
     */
    public String getTestPackageUriWithSlashs() {
        return testPackageUriWithSlashs;
    }

    /**
     *
     * @param testPackageUriWithSlashs the test package uri where the test generation classes can be found
     */
    public void setTestPackageUriWithSlashs(String testPackageUriWithSlashs) {
        this.testPackageUriWithSlashs = testPackageUriWithSlashs;
    }

    /**
     *
     * @return the project package uri where the classes under test can be found
     */
    public String getProjectPackageUriWithSlashs() {
        return projectPackageUriWithSlashs;
    }

    /**
     *
     * @param projectPackageUriWithSlashs the project package uri where the classes under test can be found
     */
    public void setProjectPackageUriWithSlashs(String projectPackageUriWithSlashs) {
        this.projectPackageUriWithSlashs = projectPackageUriWithSlashs;
    }

    /**
     *
     * @return the test package uri for the package declaration in the generated test class
     */
    public String getTestPackageUriWithDots() {
        return testPackageUriWithDots;
    }

    /**
     *
     * @param testPackageUriWithDots the test package uri for the package declaration in the generated test class
     */
    public void setTestPackageUriWithDots(String testPackageUriWithDots) {
        this.testPackageUriWithDots = testPackageUriWithDots;
    }

    /**
     *
     * @return the project package uri with dots
     */
    public String getProjectPackageUriWithDots() {
        return projectPackageUriWithDots;
    }

    /**
     *
     * @param projectPackageUriWithDots the project package uri with dots
     */
    public void setProjectPackageUriWithDots(String projectPackageUriWithDots) {
        this.projectPackageUriWithDots = projectPackageUriWithDots;
    }

    /**
     *
     * @return the testGen package uri for the path of the testGen folder
     */
    public String getTestGenPackageUri() {
        return testGenPackageUri;
    }

    /**
     *
     * @param testGenPackageUri the testGen package uri for the path of the testGen folder
     */
    public void setTestGenPackageUri(String testGenPackageUri) {
        this.testGenPackageUri = testGenPackageUri;
    }
}
