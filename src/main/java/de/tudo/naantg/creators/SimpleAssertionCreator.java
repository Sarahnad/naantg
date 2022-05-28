/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.AssertState;
import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.parser.MethodNameParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Selects all necessary information for the assertions creation and adds them to the GeneratorModel.
 */
public class SimpleAssertionCreator implements AssertionCreator {

    /**
     * the container for the information needed for the test generation
     */
    private GeneratorModel model;

    private String testName;



    /**
     * Selects the information about the assertions.
     * @param method the test case
     */
    public void calculateAssertions(Method method) {
        testName = method.getName();
        ArrayList<Assertion> assertions = new ArrayList<>();
        model.setAssertions(assertions, testName);
        List<String> expected = MethodNameParser.getReturnAssertion(testName);
        if (!expected.isEmpty()) {
            calculateReturnAssertions(method, expected);
            return;
        }
        expected = MethodNameParser.getGetAssertion(testName);
        if (!expected.isEmpty()) {
            calculateGetterAssertion(method, expected);
            return;
        }

        calculateAnnotationAssertions(method);

    }

    /**
     * Selects the information about the get assertion.
     * @param testName the name of the test case
     */
    public void calculateSetterGetterAssertion(String testName, String getter, String fieldType) {
        createSetAndGetAssertion(getter, true, testName, Utils.isListType(fieldType));
    }

    /**
     * Calculates assertions for the get inspector method.
     * @param method the test case
     * @param expected the parsed values of the get statement
     */
    private void calculateGetterAssertion(Method method, List<String> expected) {
        boolean isGetter = false;
        boolean is = false;
        boolean isValue = false;
        String value = "";

        for (String part : expected) {
            if (part.equals(StateKey.GET.toString())) {
                isGetter = true;
            }
            else if (part.equals(StateKey.IS.toString())) {
                is = true;
            }
            else if (part.equals(StateKey.VALUE.toString())) {
                if (is) createSetAndGetAssertion(value, true, method.getName(),false);
                else isValue = true;
            }
            else if (part.equals(StateKey.VALUES.toString())) {
                createSetAndGetAssertion(value, is, method.getName(),true);
            }
            else if (isValue) {
                createSetAndGetAssertion(value, part, is, method.getName(), false);
            }
            else if (isGetter) {
                value = "get" + part;
            }
        }
    }

    /**
     * Creates the assertions for the get inspector statement.
     * @param getValue the getter method name
     * @param is true if it is an equal assertion
     * @param testName the name of the test case
     * @param isList true if it is a list assertion
     */
    public void createSetAndGetAssertion(String getValue, boolean is, String testName, boolean isList) {
        createSetAndGetAssertion(getValue, "", is, testName, isList);
    }

    /**
     * Creates the assertions for the get inspector statement.
     * @param getValue the getter method name
     * @param value the expected value of the assertion
     * @param is true if it is an equal assertion
     * @param testName the name of the test case
     * @param isList true if it is a list assertion
     */
    public void createSetAndGetAssertion(String getValue, String value, boolean is, String testName, boolean isList) {
        List<ObjectModel> parameters = model.getParameters(testName);
        List<ObjectModel> objects = model.getObjectModels(testName);
        if (parameters.isEmpty() || objects.isEmpty()) {
            return;
        }
        ObjectModel parameter = null;
        for (ObjectModel param : parameters) {
            if (param.getIdentifier() != null && param.getIdentifier().startsWith("param")) {
                parameter = param;
            }
        }
        if (parameter == null) return;

        Assertion assertion = new Assertion();
        ObjectModel actualObject = new ObjectModel();
        ObjectModel object = objects.get(0);
        String actual = object.getIdentifier() + "." + getValue + "()";
        actualObject.setDataType(parameter.getDataType());
        actualObject.setGenericClasses(parameter.getGenericClasses());
        actualObject.setIdentifier("actual");
        actualObject.setValue(actual);
        model.setActualObject(actualObject, testName);
        actual = "actual";

        if (is) {
            if (isList) {
                if (Utils.isArray(parameter.getDataType())) {
                    assertion.setAssertType(AssertType.ARRAY_EQUALS);
                } else {
                    assertion.setAssertType(AssertType.EQUALS);
                }
            }
            else {
                assertion.setAssertType(AssertType.EQUALS);
            }
            assertion.setActual(actual);
            assertion.setExpected("param1");
        }
        else {
            if (isList) {
                if (Utils.isArray(parameter.getDataType())) {
                    assertion.setExpected("0");
                    assertion.setActual(actual + ".length");
                    assertion.setAssertType(AssertType.NOTEQUALS);
                } else {
                    assertion.setExpected("");
                    assertion.setActual(actual + ".isEmpty()");
                    assertion.setAssertType(AssertType.FALSE);
                }
            }
            else {
                assertion.setAssertType(AssertType.EQUALS);
                assertion.setActual(actual);
                if (!value.equals("")) assertion.setExpected(value);
                else assertion.setExpected("param1");
            }
        }
        model.getAssertions(testName).add(assertion);
    }

    /**
     * Calculates the assert information for the return and returned statement
     * depending on the key words in the assert part of the test generation method name.
     * @param method the test generation method
     * @param expected list of key words and assert value
     */
    public void calculateReturnAssertions(Method method, List<String> expected) {
        String testName = method.getName();
        if (model.getMethodOfCUT(testName) == null) return;
        if (expected.size() >= 2 && expected.get(1).equals(StateKey.EXCEPTION.toString())) {
            calculateException(method, expected);
        }
        else {
            String returnType = model.getMethodOfCUT(testName).getReturnType();
            calculateReturnAssertions(method, expected, returnType);
        }
    }

    /**
     * Calculates the assert exception statement.
     * @param method the test generation method
     * @param expected list of key words and assert value
     */
    public void calculateException(Method method, List<String> expected) {
        List<Assertion> assertions = getModel().getAssertions(method.getName());
        Assertion assertion = new Assertion();
        assertions.add(assertion);
        MethodModel methodModel = getModel().getMethodOfCUT(method.getName());
        Method methodToTest = methodModel.getMethodToTest();
        String expectedString = "";
        String actualString = "";
        Class<?>[] exs = methodToTest.getExceptionTypes();
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (exs.length == 1) {
                expectedString = exs[0].getSimpleName() + ".class";
                getModel().getTestClassModel().addImport(exs[0].getName());
                actualString = "() -> {\n\t\t" + methodModel.generateMethodStatement(false) + "\t\t\t}";
            }
            assertion.setAssertType(AssertType.THROWS);
            assertion.setExpected(expectedString);
            assertion.setActual(actualString);
        }
    }

    /**
     * Calculates the assert information for the return and returned statement
     * depending on the key words in the assert part of the test generation method name.
     * @param method the test generation method
     * @param expected list of key words and assert value
     * @param returnType the type to use for the assertions
     */
    public void calculateReturnAssertions(Method method, List<String> expected, String returnType) {
        String testName = method.getName();
        //if (model.getMethodOfCUT(testName) == null) return;

        Assertion assertion = new Assertion();
        String actual = "actual";
        List<Assertion> assertions = model.getAssertions(testName);
        assertions.add(assertion);
        assertion.setActual(actual);
        //String returnType = model.getMethodOfCUT(testName).getReturnType();
        if (Utils.isSimpleType(returnType)) {
            if (Utils.isInt(returnType)) {
                calculateIntStatement(expected, assertion, method);
            }
            else if (Utils.isShort(returnType)) {
                calculateShortStatement(expected, assertion, method);
            }
            else if (Utils.isLong(returnType)) {
                calculateLongStatement(expected, assertion, method);
            }
            else if (Utils.isFloat(returnType)) {
                calculateFloatStatement(expected, assertion, method);
            }
            else if (Utils.isDouble(returnType)) {
                calculateDoubleStatement(expected, assertion, method);
            }
            else if (Utils.isBoolean(returnType)) {
                calculateBooleanStatement(expected, assertion, method);
            }
            else if (Utils.isString(returnType)) {
                calculateStringStatement(expected, assertion, method);
            }
            else if (Utils.isChar(returnType)) {
                calculateCharStatement(expected, assertion, method);
            }
            else if (Utils.isByte(returnType)) {
                calculateByteStatement(expected, assertion, method);
            }

        }
        else if (Utils.isCollectionType(returnType)) {
            List<String> types = model.getMethodOfCUT(testName).getGenerics();
            String type = !types.isEmpty()? types.get(0) : "";
            if (Utils.isArray(returnType)) {
                type = returnType.replace("[", "").replace("]", "");
            }

            if (Utils.isOptional(returnType)) {
                calculateOptionalReturnAssertions(expected, assertions, method);
            }
            else if (Utils.isMapType(returnType)) {
                // todo
            }
            else {
                calculateListReturnAssertions(expected, assertions, type, Utils.isArray(returnType));
            }
        }
        else {
            calculateObjectReturnAssertions(expected, assertions);
        }
    }

    /**
     * Calculates the assert information for an object.
     * @param expected a list of key words and assert value
     * @param assertions a list of containers for the assertion information
     */
    public void calculateObjectReturnAssertions(List<String> expected, List<Assertion> assertions) {
        assertions.get(0).setAssertType(AssertType.NEQ);
        assertions.get(0).setExpected("null");

        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (expected.get(1).equals(StateKey.NULL.toString())) {
                assertions.get(0).setAssertType(AssertType.EQ);
            }
        }
    }

    /**
     * Calculates the assert information for the int return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateIntStatement(List<String> expected, Assertion assertion, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                String value = method.getDeclaredAnnotation(AssertState.class).value();
                assertion.setExpected(value);
                if (value.equals("")) {
                    value = calculateRuleAssertion(method);
                    calculateExpectedObject(value, SimpleDataType.INT.getType(), method.getName());
                    assertion.setExpected("expected");
                }
            }
            else if (expected.get(1).equals(StateKey.VALUE.toString())) {
                assertion.setAssertType(AssertType.NOTEQUALS);
                assertion.setExpected("0");
                return;
            }
            else {
                assertion.setExpected(expected.get(1));
            }
            assertion.setAssertType(AssertType.EQUALS);
        } else if (expected.get(0).equals(StateKey.RETURNED.toString())) {
            if (expected.get(1).equals(StateKey.GREATER.toString())) {
                assertion.setExpected(expected.get(2));
                assertion.setAssertType(AssertType.GREATER);
            }
            else if (expected.get(1).equals(StateKey.SMALLER.toString())) {
                assertion.setExpected(expected.get(2));
                assertion.setAssertType(AssertType.SMALLER);
            }
        }
    }

    /**
     * Calculates the assert information for the short return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateShortStatement(List<String> expected, Assertion assertion, Method method) {
        calculateIntStatement(expected, assertion, method);
    }

    /**
     * Calculates the assert information for the long return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateLongStatement(List<String> expected, Assertion assertion, Method method) {
        calculateIntStatement(expected, assertion, method);
    }

    /**
     * Calculates the assert information for the float return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateFloatStatement(List<String> expected, Assertion assertion, Method method) {
        calculateDoubleStatement(expected, assertion, method);
    }

    /**
     * Calculates the assert information for the double return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateDoubleStatement(List<String> expected, Assertion assertion, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                String value = method.getDeclaredAnnotation(AssertState.class).value();
                assertion.setExpected(value);
                if (value.equals("")) {
                    value = calculateRuleAssertion(method);
                    calculateExpectedObject(value, SimpleDataType.DOUBLE.getType(), method.getName());
                    assertion.setExpected("expected");
                }
            }
            else if (expected.get(1).equals(StateKey.VALUE.toString())) {
                assertion.setAssertType(AssertType.NOTEQUALS);
                assertion.setExpected("0.0");
                return;
            }
            else if (Utils.matchesDoubleFormat(expected.get(1))) {
                String value = expected.get(1).replace("p", ".");
                assertion.setExpected(value);
            } else {
                assertion.setExpected(expected.get(1));
            }
            assertion.setAssertType(AssertType.EQUALS);
        }
        else if (expected.get(0).equals(StateKey.RETURNED.toString())) {
            if (Utils.matchesDoubleFormat(expected.get(2))) {
                String value = expected.get(2).replace("p", ".");
                assertion.setExpected(value);
            }
            else {
                assertion.setExpected(expected.get(2));
            }
            if (expected.get(1).equals(StateKey.GREATER.toString())) {
                assertion.setAssertType(AssertType.GREATER);
            }
            else if (expected.get(1).equals(StateKey.SMALLER.toString())) {
                assertion.setAssertType(AssertType.SMALLER);
            }
        }
    }

    /**
     * Calculates the assert information for the boolean return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateBooleanStatement(List<String> expected, Assertion assertion, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                String value = method.getDeclaredAnnotation(AssertState.class).value();
                assertion.setExpected(value);
                assertion.setAssertType(AssertType.EQUALS);
                return;
            }
            else if (expected.get(1).equals(StateKey.VALUE.toString())) {
                assertion.setExpected("true");
                assertion.setAssertType(AssertType.TRUE);
                return;
            }
            assertion.setExpected(expected.get(1));
            if (expected.get(1).equalsIgnoreCase("true")) {
                assertion.setAssertType(AssertType.TRUE);
            } else if (expected.get(1).equalsIgnoreCase("false")) {
                assertion.setAssertType(AssertType.FALSE);
            } else {
                assertion.setAssertType(AssertType.EQUALS);
            }
        }
    }

    /**
     * Calculates the assert information for the string return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test generation method
     */
    private void calculateStringStatement(List<String> expected, Assertion assertion, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                String value = "\"" + method.getDeclaredAnnotation(AssertState.class).value() + "\"";
                assertion.setExpected(value);
                if (value.equals("\"\"")) {
                    value = calculateRuleAssertion(method);
                    calculateExpectedObject(value, SimpleDataType.STRING.getType(), method.getName());
                    assertion.setExpected("expected");
                }
            }
            else if (expected.get(1).equals(StateKey.VALUE.toString())) {
                assertion.setAssertType(AssertType.NOTEQUALS);
                assertion.setExpected("\"\"");
                return;
            }
            else if (expected.get(1).equals(StateKey.NULL.toString())) {
                assertion.setAssertType(AssertType.EQ);
                assertion.setExpected("null");
                return;
            }
            else {
                assertion.setExpected("\"" + expected.get(1) + "\"");
            }
            assertion.setAssertType(AssertType.EQUALS);
        }
    }

    /**
     * Calculates the assert information for the char return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test generation method
     */
    private void calculateCharStatement(List<String> expected, Assertion assertion, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                String value = "'" + method.getDeclaredAnnotation(AssertState.class).value() + "'";
                assertion.setExpected(value);
            }
            else if (expected.get(1).equals(StateKey.VALUE.toString())) {
                assertion.setAssertType(AssertType.GREATER);
                assertion.setExpected("0");
                return;
            }
            else {
                assertion.setExpected("'" + expected.get(1) + "'");
            }
            assertion.setAssertType(AssertType.EQUALS);
        }
    }

    /**
     * Calculates the assert information for the byte return type.
     * @param expected list of key words and assert value
     * @param assertion a container for the assertion information
     * @param method the test case
     */
    private void calculateByteStatement(List<String> expected, Assertion assertion, Method method) {
        calculateIntStatement(expected, assertion, method);
    }

    /**
     * Calculates the assertion information with help of the annotations of the method.
     * @param method the test generation method
     */
    public void calculateAnnotationAssertions(Method method) {
        String testName = method.getName();
        String returnType = model.getMethodOfCUT(testName).getReturnType();
        if (Utils.isCollectionType(returnType) || Utils.isObjectType(returnType)) {
            List<Assertion> assertions = model.getAssertions(testName);
            Assertion assertion = new Assertion();
            String actual = "actual";
            assertion.setActual(actual);
            assertions.add(assertion);

            List<String> types = model.getMethodOfCUT(testName).getGenerics();
            String type = !types.isEmpty()? types.get(0) : Utils.isObjectType(returnType)? returnType : "";
            if (Utils.isArray(returnType)) {
                type = returnType.replace("[", "").replace("]", "");
            }
            calculateListAnnotationsAssertions(assertions, type, method, Utils.isArray(returnType));
        }
    }

    /**
     * Calculates the assert information for the list return type.
     * @param expected a list of key words and assert value
     * @param assertions a list of containers for the assertion information
     * @param type the type of the elements
     * @param isArray if collection is an array
     */
    public void calculateListReturnAssertions(List<String> expected, List<Assertion> assertions, String type, boolean isArray) {
        assertions.get(0).setAssertType(AssertType.NEQ);
        assertions.get(0).setExpected("null");

        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (expected.get(1).equals(StateKey.LIST.toString())) {
                // case: not Empty
                Assertion withElements = new Assertion("", "actual.isEmpty()", AssertType.FALSE);
                assertions.add(withElements);
            }
            else if (expected.get(1).equals(StateKey.NULL.toString())) {
                assertions.get(0).setAssertType(AssertType.EQ);
            }
        }
        else if (expected.get(0).equals(StateKey.RETURNED.toString())) {
            calculateListReturnedAssertions(expected, assertions, type, isArray);
        }
    }

    /**
     * Calculates the assert information for the optional return type.
     * @param expected a list of key words and assert value
     * @param assertions a list of containers for the assertion information
     * @param method the test case method
     */
    public void calculateOptionalReturnAssertions(List<String> expected, List<Assertion> assertions, Method method) {
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            String expectedValue = expected.get(1);
            if (expectedValue.equals(StateKey.VALUE.toString()) ||
                    expectedValue.equals(StateKey.NOT_NULL.toString())) {
                assertions.get(0).setAssertType(AssertType.TRUE);
                assertions.get(0).setExpected("");
                assertions.get(0).setActual("actual.isPresent()");
            }
            else if (expectedValue.equals(StateKey.NULL.toString())) {
                assertions.get(0).setAssertType(AssertType.FALSE);
                assertions.get(0).setExpected("");
                assertions.get(0).setActual("actual.isPresent()");
            }
            else {
                assertions.get(0).setAssertType(AssertType.TRUE);
                assertions.get(0).setExpected("");
                assertions.get(0).setActual("actual.isPresent()");
                calculateOptionalGetAssertions(expected, method);
            }
        }
        else if (expected.get(0).equals(StateKey.RETURNED.toString())) {
            if (expected.get(1).equals(StateKey.EMPTY.toString())) {
                assertions.get(0).setAssertType(AssertType.FALSE);
                assertions.get(0).setExpected("");
                assertions.get(0).setActual("actual.isPresent()");
            }
            else {
                assertions.get(0).setAssertType(AssertType.TRUE);
                assertions.get(0).setExpected("");
                assertions.get(0).setActual("actual.isPresent()");
                calculateOptionalGetAssertions(expected, method);
            }
        }
    }

    /**
     * Calculates the assert information for the optional content.
     * @param expected a list of key words and assert value
     * @param method the test case method
     */
    public void calculateOptionalGetAssertions(List<String> expected, Method method) {
        ObjectModel generic = Helper.findOptionalObject(model, method.getName());
        if (generic == null) {
            return;
        }
        List<Assertion> assertions = model.getAssertions(method.getName());
        if (assertions.isEmpty()) return;

        Assertion saveAssertion = assertions.get(0);
        assertions.clear();

        calculateReturnAssertions(method, expected, generic.getDataType());

        assertions = model.getAssertions(method.getName());
        if (assertions.get(0).getExpected().equals("null")) {
            assertions.remove(0);
        }
        for (Assertion assertion : assertions) {
            String actual = assertion.getActual();
            actual = actual.replaceAll("actual", "actual.get()");
            assertion.setActual(actual);
        }
        assertions.add(0, saveAssertion);
    }

    /**
     * Calculates the expected value of a rule assertion.
     * @param method the test case
     * @return the expected assertion value
     */
    private String calculateRuleAssertion(Method method) {
        String value = "";
        String rule = method.getDeclaredAnnotation(AssertState.class).rule();
        if (!rule.equals("")) {
            String[] ruleParts = AnnotationParser.parseAssertStateRule(rule);
            if (ruleParts.length > 1) {
                value = ruleParts[ruleParts.length-1];
                for (int i = 0; i < ruleParts.length - 1; i++) {
                    value = value.replace(ruleParts[i], "param" + (i+1));
                }
            }
        }
        return value;
    }

    /**
     * Calculates a new instance for the expected value.
     * @param value the expected value
     * @param dataType the data type of the expected value
     * @param testName the test case name
     */
    private void calculateExpectedObject(String value, String dataType, String testName) {
        ObjectModel objectModel = new ObjectModel();
        objectModel.setNewInstance(true);
        objectModel.setDataType(dataType);
        objectModel.setIdentifier("expected");
        objectModel.setValue(value);
        model.getObjectModels(testName).add(objectModel);
    }

    /**
     * Calculates the assertion information for Lists with help of the annotations of the method.
     * @param assertions the assertions
     * @param type the type of the elements or object type
     * @param method the test generation method
     * @param array list is an array if it is true
     */
    private void calculateListAnnotationsAssertions(List<Assertion> assertions, String type, Method method, boolean array) {
        assertions.get(0).setAssertType(AssertType.NEQ);
        assertions.get(0).setExpected("null");

        String sizeValue = null;
        String containsValue = null;
        String getValue = null;
        boolean isEmpty = false;
        if (Scanner.hasAssertStateAnnotation(method)) {
            sizeValue = method.getDeclaredAnnotation(AssertState.class).returnedSize();
            containsValue = method.getDeclaredAnnotation(AssertState.class).returnedContains();
            getValue = method.getDeclaredAnnotation(AssertState.class).returned();
            isEmpty = method.getDeclaredAnnotation(AssertState.class).returnedIsEmpty();
        }
        if (sizeValue != null && !sizeValue.equals("")) {
            List<String> expected = AnnotationParser.parseAssertStateReturnedSize(sizeValue);
            calculateListReturnedAssertions(expected, assertions, type, array);
        }
        if (getValue != null && !getValue.equals("")) {
            List<String> expected = AnnotationParser.parseAssertStateReturned(getValue);
            calculateListReturnedAssertions(expected, assertions, type, array);
        }
        if (containsValue != null && !containsValue.equals("")) {
            List<String> expected = AnnotationParser.parseAssertStateReturnedContains(containsValue);
            calculateListReturnedAssertions(expected, assertions, type, array);
        }
        if (isEmpty) {
            List<String> expected = new ArrayList<>();
            expected.add(StateKey.EMPTY.toString());
            calculateListReturnedAssertions(expected, assertions, type, array);
        }
    }

    /**
     * Calculates the assert information for the list returned assertions.
     * @param expected a list of key words and assert value
     * @param assertions a list of containers for the assertion information
     * @param type the type of the elements
     * @param isArray if collection is an array
     */
    public void calculateListReturnedAssertions(List<String> expected, List<Assertion> assertions, String type, boolean isArray) {
        boolean isSize = false;
        boolean isContains = false;
        boolean isGet = false;
        boolean is = false;
        boolean isNot = false;
        boolean isGreater = false;
        boolean isSmaller = false;
        boolean isOr = false;
        boolean isParam = false;
        String getIndex = "";
        String param = "";

        for (String part : expected) {
            if (part.equals(StateKey.EMPTY.toString())) {
                String actual = isArray? "actual.length" : "actual.isEmpty()";
                Assertion assertion = new Assertion("", actual, AssertType.TRUE);
                assertions.add(assertion);
            }
            else if (part.equals(StateKey.SIZE.toString())) {
                isSize = true;
            }
            else if (part.equals(StateKey.CONTAINS.toString())) {
                isContains = true;
            }
            else if (part.equals(StateKey.GET.toString())) {
                isGet = true;
            }
            else if (part.equals(StateKey.PARAM.toString())) {
                isParam = true;
            }
            else if (part.equals(StateKey.IS.toString())) {
                is = true;
            }
            else if (part.equals(StateKey.NOT.toString())) {
                isNot = true;
            }
            else if (part.equals(StateKey.GREATER.toString())) {
                isGreater = true;
            }
            else if (part.equals(StateKey.SMALLER.toString())) {
                isSmaller = true;
            }
            else if (part.equals(StateKey.AND.toString())) {
                isOr = false;
                isSize = false;
                isContains = false;
                isGet = false;
                isSmaller = false;
                isGreater = false;
                is = false;
                isNot = false;
                isParam = false;
                param = "";
            }
            else if (part.equals(StateKey.OR.toString())) {
                isOr = true;
                isGet = false;
                isSmaller = false;
                isGreater = false;
                is = false;
                isNot = false;
            }
            else if (isParam) {
                param = part;
                isParam = false;
            }
            else if (isSize) {
                calculateSizeAssertion(part, assertions, is, isNot, isOr, isGreater, isSmaller, isArray);
            }
            else if (isContains) {
                calculateContainsAssertion(part, type, assertions, isNot, isOr, isArray);
            }
            else if (isGet) {
                if (is || isNot || isGreater || isSmaller) {
                    if (getIndex.isEmpty()) {
                        calculateOptionalObjectMapAssertion(part, type, assertions, isOr, is, isNot, isGreater, isSmaller, param);
                    }
                    else {
                        calculateGetAssertion(part, type, assertions, isOr, is, isNot, isGreater, isSmaller, isArray, getIndex);
                    }
                }
                else {
                    getIndex = part;
                }
            }
        }
    }

    /**
     * Calculates the information for the size assertion.
     * @param expected the expected values
     * @param assertions the assertions
     * @param is if the context is "is"
     * @param isNot if the context is "not"
     * @param isOr if the context is "or"
     * @param isGreater if the context is "isGreater"
     * @param isSmaller if the context is "isSmaller"
     * @param isArray if collection is an array
     */
    public void calculateSizeAssertion(String expected, List<Assertion> assertions, boolean is, boolean isNot,
                                       boolean isOr, boolean isGreater, boolean isSmaller, boolean isArray) {
        if (!expected.matches("\\d+")) return;

        String sizeLabel = "actual.";
        sizeLabel += isArray? "length" : "size()";
        String type = "";

        if (is) {
            calculateIsAssertion(assertions, type, sizeLabel, expected, isOr, false);
        }
        else if (isNot) {
            calculateIsAssertion(assertions, type, sizeLabel, expected, isOr, true);
        }
        else if (isGreater) {
            calculateCompareAssertion(assertions, sizeLabel, expected, isOr, true);
        }
        else if (isSmaller) {
            calculateCompareAssertion(assertions, sizeLabel, expected, isOr, false);
        }
    }

    /**
     * Calculates the information for the contains assertion.
     * @param expected the expected values
     * @param type the type of the elements
     * @param assertions the assertions
     * @param isNot if the context is "not"
     * @param isOr if the context is "or"
     * @param isArray if collection is an array
     */
    public void calculateContainsAssertion(String expected, String type, List<Assertion> assertions, boolean isNot, boolean isOr, boolean isArray) {
        boolean isString = false;

        String exp = expected;
        if ((Utils.isInt(type) || Utils.isFloat(type) || Utils.isDouble(type) || Utils.isShort(type)
                || Utils.isLong(type) || Utils.isByte(type))
                && exp.matches("(Neg)?(-)?\\d+p?.?\\d*")) {
            exp = exp.replace("Neg", "-").replace("p", ".");
        }
        else if (Utils.isBoolean(type) &&
                (exp.equalsIgnoreCase(StateKey.TRUE.toString())
                        || exp.equalsIgnoreCase(StateKey.FALSE.toString()))) {
            exp = exp.toLowerCase();
        }
        else if (Utils.isString(type) || Utils.isChar(type)) {
            exp = "\"" + exp + "\"";
            isString = true;
        }

        String containsLabel = isArray? isString? "Arrays.asList(actual).contains("
                : "Arrays.stream(actual).anyMatch(x -> x == " : "actual.contains(";

        if (isOr) {
            Assertion assertion = assertions.get(assertions.size()-1);
            String firstActual = assertion.getActual();
            if (assertion.getAssertType().equals(AssertType.FALSE)) {
                assertion.setAssertType(AssertType.TRUE);
                firstActual = "!" + assertion.getActual();
            }
            String actual = firstActual + " || " + (isNot? "!" : "") + containsLabel + exp + ")";
            assertion.setExpected(actual);
        }
        else if (isNot) {
            String actual = containsLabel + exp + ")";
            Assertion assertion = new Assertion("", actual, AssertType.FALSE);
            assertions.add(assertion);
        }
        else {
            String actual = containsLabel + exp + ")";
            Assertion assertion = new Assertion("", actual, AssertType.TRUE);
            assertions.add(assertion);
        }
    }

    /**
     * Calculates the information for the get assertion.
     * @param expected the expected values
     * @param type the type of the elements
     * @param assertions the assertions
     * @param isOr if the context is "or"
     * @param is if the context is "is"
     * @param isNot if the context is "not"
     * @param isGreater if the context is "isGreater"
     * @param isSmaller if the context is "isSmaller"
     * @param isArray if collection is an array
     * @param getIndex the get index
     */
    public void calculateGetAssertion(String expected, String type, List<Assertion> assertions, boolean isOr, boolean is,
                                      boolean isNot, boolean isGreater, boolean isSmaller, boolean isArray,
                                      String getIndex) {
        String getLabel = "actual";
        getLabel += isArray? "[" : ".get(";
        getLabel += getIndex;
        getLabel += isArray? "]" : ")";

        calculateSimpleComparison(expected, type, assertions, isOr, is, isNot, isGreater, isSmaller, getLabel);
    }

    /**
     * Calculates the information for the assertion with an optional map type.
     * @param expected the expected values
     * @param type the type of the elements
     * @param assertions the assertions
     * @param isOr if the context is "or"
     * @param is if the context is "is"
     * @param isNot if the context is "not"
     * @param isGreater if the context is "isGreater"
     * @param isSmaller if the context is "isSmaller"
     * @param param the parameter of the optional type.
     */
    public void calculateOptionalObjectMapAssertion(String expected, String type, List<Assertion> assertions, boolean isOr,
                                                    boolean is, boolean isNot, boolean isGreater,
                                                    boolean isSmaller, String param) {
        Field field = null;
        List<String> generics = model.getMethodOfCUT(testName).getGenerics();
        ObjectModel returnObject = model.getMethodOfCUT(testName).getReturnObject();
        if (returnObject == null) {
            Logger.logWarning("[SimpleAssertionCreator] No returnObject found!");
            return;
        }
        if (!Utils.isMapType(returnObject.getDataType())) {
            try {
                if (Utils.isOptional(returnObject.getDataType()) && generics.size() == 1 && returnObject.getGenericClasses().size() == 1) {
                    field = returnObject.getGenericClasses().get(0).getDeclaredField(param);
                }
                else {
                    field = returnObject.getObjectClass().getDeclaredField(param);
                }
            } catch (NoSuchFieldException e) {
                Logger.logWarning("[SimpleAssertionCreator] No field with name " + param + " found!");
                return;
            }

            if (!Utils.isSimpleType(field.getType().getSimpleName()) && (isGreater || isSmaller)) {
                Logger.logWarning("[SimpleAssertionCreator] The " + param + " has no primitive type! " +
                        "No comparison possible!");
                return;
            }

            Class<?> parent = Utils.isOptional(returnObject.getDataType()) && returnObject.getGenericClasses().size() == 1?
                    returnObject.getGenericClasses().get(0) : returnObject.getObjectClass();

            if (parent != null && !Scanner.hasGetterMethod(parent, field)) {
                Logger.logWarning("[SimpleAssertionCreator] No getter method for " + param + " found!");
                return;
            }
        }

        String getLabel = "actual.get";
        if (Utils.isOptional(returnObject.getDataType())) {
            getLabel += "().get" + Utils.setUpperCaseFirstChar(param) + "()";
            assertions.get(0).setAssertType(AssertType.TRUE);
            assertions.get(0).setExpected("");
            assertions.get(0).setActual("actual.isPresent()");
        }
        else if (Utils.isMapType(returnObject.getDataType())) {
            if (Utils.isString(type)) {
                param = "\"" + param + "\"";
            }
            getLabel += "(" + param + ")";
        }
        else {
            getLabel += Utils.setUpperCaseFirstChar(param) + "()";
        }

        if (generics.size() == 2) {
            type = generics.get(1);
        }
        else {
            type = field != null ? field.getType().getSimpleName() : type;
        }

        calculateSimpleComparison(expected, type, assertions, isOr, is, isNot, isGreater, isSmaller, getLabel);

    }

    /**
     * Calculates the comparison type.
     * @param expected the expected value.
     * @param type the data type for the comparison.
     * @param assertions the list of all assertions.
     * @param isOr whether the assertion is combined with or.
     * @param is whether the comparison type is equal.
     * @param isNot whether the comparison type is not equal.
     * @param isGreater whether the comparison type is greater.
     * @param isSmaller whether the comparison type is smaller.
     * @param getLabel the actual label.
     */
    private void calculateSimpleComparison(String expected, String type, List<Assertion> assertions, boolean isOr, boolean is,
                                           boolean isNot, boolean isGreater, boolean isSmaller, String getLabel) {
        String value = expected;
        if ((Utils.isInt(type) || Utils.isFloat(type) || Utils.isDouble(type) || Utils.isShort(type)
                || Utils.isLong(type) || Utils.isByte(type))
                && value.matches("(Neg)?(-)?\\d+p?.?\\d*")) {
            value = value.replace("Neg", "-").replace("p", ".");
        }
        else if (Utils.isString(type) || Utils.isChar(type)) {
            value = "\"" + value + "\"";
        }

        if (is) {
            calculateIsAssertion(assertions, type, getLabel, value, isOr, false);
        }
        if (isNot) {
            calculateIsAssertion(assertions, type, getLabel, value, isOr, true);
        }
        else if (isGreater) {
            calculateCompareAssertion(assertions, getLabel, value, isOr, true);
        }
        else if (isSmaller) {
            calculateCompareAssertion(assertions, getLabel, value, isOr, false);
        }
    }

    /**
     * Calculates the assertion information for the "is" part.
     * @param assertions the assertions
     * @param type the type of the elements
     * @param actual the actual label
     * @param part the value for the "is" part
     * @param isOr add or part if it is true
     * @param not assert negation of the value if it is true
     */
    public void calculateIsAssertion(List<Assertion> assertions, String type, String actual, String part, boolean isOr, boolean not) {
        if (isOr) {
            Assertion assertion = assertions.get(assertions.size()-1);

            if (Utils.isBoolean(type)) {
                boolean partNot = part.equalsIgnoreCase(StateKey.FALSE.toString());
                boolean actualNot = !(not == partNot);
                if (assertion.getAssertType().equals(AssertType.TRUE)
                        && assertion.getActual().contains("||")) {
                    assertion.setActual(assertion.getActual() + " || " + (actualNot? "!" : "") + actual);
                }
                else {
                    boolean firstNot = assertion.getAssertType().equals(AssertType.FALSE);
                    String firstActual = firstNot ? "!" : "";
                    firstActual += assertion.getActual();

                    String newActual = firstActual + " || " + (actualNot? "!" : "") + actual;
                    assertion.setAssertType(AssertType.TRUE);
                    assertion.setActual(newActual);
                }
            }
            else {
                boolean firstNot = assertion.getAssertType().equals(AssertType.NOTEQUALS);
                boolean isObj = Utils.isString(type);
                String firstCompSymb = isObj? ".equals(" : firstNot? " != " : " == ";
                String compSymbol = isObj? ".equals(" : not? " != " : " == ";

                if (!assertion.getAssertType().equals(AssertType.TRUE) && !assertion.getExpected().equals("")) {
                    String firstActual = firstNot && isObj? "!" : "";
                    firstActual += assertion.getActual() + firstCompSymb + assertion.getExpected();
                    firstActual += isObj? ")" : "";
                    String newActual = firstActual + " || " + (not && isObj? "!" : "") + actual
                            + compSymbol + part + (isObj? ")" : "");
                    assertion.setActual(newActual);
                    assertion.setExpected("");
                    assertion.setAssertType(AssertType.TRUE);
                } else {
                    assertion.setActual(assertion.getActual() + " || " + actual + compSymbol + part + (isObj? ")" : ""));
                }
            }
        }
        else {
            if (Utils.isBoolean(type)) {
                boolean partNot = part.equalsIgnoreCase(StateKey.FALSE.toString());
                boolean actualNot = !(not == partNot);
                AssertType assertType = actualNot? AssertType.FALSE : AssertType.TRUE;
                Assertion assertion = new Assertion("", actual, assertType);
                assertions.add(assertion);
            }
            else {
                AssertType assertType = not ? AssertType.NOTEQUALS : AssertType.EQUALS;
                Assertion assertion = new Assertion(part, actual, assertType);
                assertions.add(assertion);
            }
        }
    }

    /**
     * Calcualtes the "is greater" or "is smaller" part.
     * @param assertions the assertions
     * @param actual the actual label
     * @param part the value for the comparison
     * @param isOr add or part if it is true
     * @param greater assert actual is greater as the part if it is true, otherwise smaller
     */
    public void calculateCompareAssertion(List<Assertion> assertions, String actual, String part, boolean isOr, boolean greater) {
        String compSymbol = greater? " > " : " < ";
        if (isOr) {
            Assertion assertion = assertions.get(assertions.size()-1);
            assertion.setActual(assertion.getActual() + " || " + actual + compSymbol + part);
        }
        else {
            String newActual = actual + compSymbol + part;
            Assertion assertion = new Assertion("", newActual, AssertType.TRUE);
            assertions.add(assertion);
        }
    }

    /**
     *
     * @return the container for the generator information
     */
    public GeneratorModel getModel() {
        return model;
    }

    /**
     *
     * @param model the container for the generator information
     */
    public void setModel(GeneratorModel model) {
        this.model = model;
    }

    /**
     *
     * @return the test name.
     */
    public String getTestName() {
        return testName;
    }

    /**
     *
     * @param testName the test name.
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }
}
