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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for the information of the test case.
 */
public class TestCaseModel {

    /**
     * the test generation method
     */
    private Method tgMethod;

    /**
     * the method to test of the class under test
     */
    private MethodModel methodToTest;

    /**
     * list of mock calls
     */
    private List<MethodModel> mockCalls;

    /**
     * the class under test object (this)
     */
    private ObjectModel cutObject;

    /**
     * the assertions in the post condition part
     */
    private List<Assertion> assertions;

    /**
     * the objects in the pre condition part
     */
    private List<ObjectModel> objectModels;

    /**
     * the parameters of the method to test
     */
    private List<ObjectModel> parameters;

    /**
     * the return value of an inspector method needed for the assertions
     */
    private ObjectModel actualObject;

    /**
     * the return value of the helper method
     */
    private ObjectModel returnObject;

    /**
     * the name of the test case
     */
    private String testMethodName;

    /**
     * the content of the method if this is a helper method
     */
    private String helperMethodContent;

    /**
     * all used identifiers in the test case,
     * key is the test case name and the value is the index
     */
    private final Map<String, Integer> identifiers;

    /**
     * the given objects of the pre state
     */
    private final List<ObjectModel> givenInitObjects;

    /**
     * random configurations
     */
    private RandomConfigs randomConfigs;

    /**
     * test case annotations
     */
    List<Class<?>> annotations;

    private boolean isGiven;


    /**
     * Creates a new container for the information of the test case.
     */
    public TestCaseModel() {
        this.identifiers = new HashMap<>();
        this.objectModels = new ArrayList<>();
        this.parameters = new ArrayList<>();
        this.assertions = new ArrayList<>();
        this.givenInitObjects = new ArrayList<>();
        this.mockCalls = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    /**
     * Determine an unique identifier for the given data type.
     * @param type a data type
     * @return an unique identifier for the data type
     */
    public String getUniqueIdentifier(String type) {
        return getUniqueIdentifier(type, null);
    }

    /**
     * Determine an unique identifier for the given data type.
     * If a name as identifier is given it will be converted to an unique identifier.
     * @param type a data type
     * @param name an identifier to make unique (can be null)
     * @return an unique identifier for the data type
     */
    public String getUniqueIdentifier(String type, String name) {
        String identifier;
        if (type == null || type.equals("")) return "";

        if (name != null && !name.equals("")) {
            identifier = name;
        }
        else if (Utils.isSimpleType(type)) {
            identifier = getUniqueIdentifierForSimpleType(type);
        }
        else if (Utils.isCollectionType(type) && !Utils.isArray(type)) {
            identifier = "list";
        }
        else if (Utils.isArray(type)) {
            identifier = "array";
        }
        else {
            identifier = Utils.setLowerCaseFirstChar(type);
        }

        if (identifiers.get(identifier) != null) {
            int index = identifiers.get(identifier);
            String oldIdentifier = identifier;
            identifier += index;
            index += 1;
            identifiers.put(oldIdentifier, index);
        }
        else {
            identifiers.put(identifier, 2);
        }
        return identifier;
    }

    /**
     * Determines an unique identifier for a simple data type.
     * @param type a data type
     * @return an unique identifier for the simple data type
     */
    private String getUniqueIdentifierForSimpleType(String type) {
        String identifier = "";
        SimpleDataType dataType = SimpleDataType.valueOf(type.toUpperCase());
        switch (dataType) {
            case INT: return "iVar";
            case LONG: return  "lVar";
            case SHORT: return "sVar";
            case BYTE: return "bVar";
            case CHAR: return "ch";
            case FLOAT: return "fVar";
            case DOUBLE: return "dVar";
            case STRING: return "str";
            case BOOLEAN: return "isValid";
        }
        return identifier;
    }

    /**
     *
     * @return the method to test of the class under test
     */
    public MethodModel getMethodToTest() {
        return methodToTest;
    }

    /**
     *
     * @param methodToTest the method to test of the class under test
     */
    public void setMethodToTest(MethodModel methodToTest) {
        this.methodToTest = methodToTest;
    }

    /**
     *
     * @return the assertions of the test case
     */
    public List<Assertion> getAssertions() {
        return assertions;
    }

    /**
     *
     * @param assertions the assertions of the test case
     */
    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    /**
     *
     * @return the objects of the pre condition part
     */
    public List<ObjectModel> getObjectModels() {
        return objectModels;
    }

    /**
     *
     * @param objectModels the objects of the pre condition part
     */
    public void setObjectModels(List<ObjectModel> objectModels) {
        this.objectModels = objectModels;
    }

    /**
     *
     * @return the parameters of the method to test
     */
    public List<ObjectModel> getParameters() {
        return parameters;
    }

    /**
     *
     * @param parameters the parameters of the method to test
     */
    public void setParameters(List<ObjectModel> parameters) {
        this.parameters = parameters;
    }

    /**
     *
     * @return the actual value of an inspector method
     */
    public ObjectModel getActualObject() {
        return actualObject;
    }

    /**
     *
     * @param actualObject the actual value of an inspector method
     */
    public void setActualObject(ObjectModel actualObject) {
        this.actualObject = actualObject;
    }

    /**
     *
     * @return the name of the test case
     */
    public String getTestMethodName() {
        return testMethodName;
    }

    /**
     *
     * @param testMethodName the name of the test case
     */
    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    /**
     *
     * @return the cut object model
     */
    public ObjectModel getCutObject() {
        return cutObject;
    }

    /**
     *
     * @param cutObject the cut object model
     */
    public void setCutObject(ObjectModel cutObject) {
        this.cutObject = cutObject;
    }

    /**
     *
     * @return the content of the helper method
     */
    public String getHelperMethodContent() {
        return helperMethodContent;
    }

    /**
     *
     * @param helperMethodContent the content of the helper method
     */
    public void setHelperMethodContent(String helperMethodContent) {
        this.helperMethodContent = helperMethodContent;
    }

    /**
     *
     * @return the given init objects of the pre state
     */
    public List<ObjectModel> getGivenInitObjects() {
        return givenInitObjects;
    }

    /**
     *
     * @return random configurations
     */
    public RandomConfigs getRandomConfigs() {
        return randomConfigs;
    }

    /**
     *
     * @param randomConfigs random configurations
     */
    public void setRandomConfigs(RandomConfigs randomConfigs) {
        this.randomConfigs = randomConfigs;
    }

    /**
     *
     * @return the mock calls
     */
    public List<MethodModel> getMockCalls() {
        return mockCalls;
    }

    /**
     *
     * @return the annotations
     */
    public List<Class<?>> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return the test generation method
     */
    public Method getTgMethod() {
        return tgMethod;
    }

    /**
     *
     * @param tgMethod the test generation method
     */
    public void setTgMethod(Method tgMethod) {
        this.tgMethod = tgMethod;
    }

    public ObjectModel getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(ObjectModel returnObject) {
        this.returnObject = returnObject;
    }

    public boolean isGiven() {
        return isGiven;
    }

    public void setGiven(boolean given) {
        isGiven = given;
    }
}
