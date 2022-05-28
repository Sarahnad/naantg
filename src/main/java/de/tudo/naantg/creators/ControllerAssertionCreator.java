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

import de.tudo.naantg.annotations.AnnoType;
import de.tudo.naantg.annotations.CheckType;
import de.tudo.naantg.annotations.Expect;
import de.tudo.naantg.annotations.StatusType;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.parser.ParseKey;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Extends the SimpleAssertCreator to create assertions for the controller classes.
 */
public class ControllerAssertionCreator extends SimpleAssertionCreator {

    /**
     * Selects the information about the assertions.
     * Checks therefor if the generation method has an Expect annotation.
     * @param method the test case
     */
    public void calculateAssertions(Method method) {
        String testName = method.getName();
        ArrayList<Assertion> assertions = new ArrayList<>();
        getModel().setAssertions(assertions, testName);
        if (Scanner.hasAnnotation(method, AnnoType.EXPECT.getType())) {
            CheckType[] checkTypes = method.getDeclaredAnnotation(Expect.class).checkTypes();
            for (CheckType checkType : checkTypes) {
                calculateAssertion(method, checkType);
            }
        }
        else {
            calculateAssertion(method, CheckType.ALL);
        }
    }

    /**
     * Selects the calculation method for the given checkType.
     * @param method the test case
     * @param checkType the check type
     */
    public void calculateAssertion(Method method, CheckType checkType) {
        switch (checkType) {
            case PRINT:
                calculatePrint(method.getName());
                break;
            case STATUS:
                calculateStatus(method);
                break;
            case VIEW:
                calculateView(method, false);
                break;
            case REDIRECT:
                calculateView(method, true);
                break;
            case CONTENT_CONTAINS:
                calculateContentContains(method);
                break;
            case ALL:
                calculateAll(method);
                break;
        }
    }

    /**
     * Calculates the print statement.
     * @param testName the name of the test case
     */
    private void calculatePrint(String testName) {
        Assertion assertion = new Assertion(CheckType.PRINT.toString(), "", AssertType.TRUE);
        getModel().getAssertions(testName).add(assertion);
        String[] imports = CheckType.PRINT.getImportPaths().split(",");
        getModel().getTestClassModel().addImports(imports);
    }

    /**
     * Calculates all check statements.
     * @param method the test case
     */
    private void calculateAll(Method method) {
        calculatePrint(method.getName());
        calculateStatus(method);
        calculateView(method, false);
        calculateView(method, true);
        calculateContentContains(method);
        calculateModelChecks(method);
    }

    /**
     * Calculates the status statement.
     * @param method the test case
     */
    private void calculateStatus(Method method) {
        Assertion assertion = new Assertion(CheckType.STATUS.toString(), "", AssertType.TRUE);
        if (method.getDeclaredAnnotation(Expect.class) != null) {
            StatusType statusType = method.getDeclaredAnnotation(Expect.class).statusType();
            assertion.setActual(statusType.getType());
        }
        else {
            assertion.setActual(StatusType.OK.getType());
        }
        getModel().getAssertions(method.getName()).add(assertion);
        String[] imports = CheckType.STATUS.getImportPaths().split(",");
        getModel().getTestClassModel().addImports(imports);
    }

    /**
     * Calculates the view statement.
     * @param method the test case
     * @param isRedirect whether the view is redirected
     */
    private void calculateView(Method method, boolean isRedirect) {
        String expected = isRedirect ? CheckType.REDIRECT.toString() : CheckType.VIEW.toString();
        Assertion assertion = new Assertion(expected, "", AssertType.TRUE);
        boolean isFound = false;
        if (method.getDeclaredAnnotation(Expect.class) != null) {
            String value = "";
            if (isRedirect) value = method.getDeclaredAnnotation(Expect.class).redirectToView();
            else value = method.getDeclaredAnnotation(Expect.class).getView();
            if (!value.equals("")) {
                isFound = true;
                assertion.setActual(value);
                getModel().getAssertions(method.getName()).add(assertion);
                String[] imports = isRedirect ? CheckType.REDIRECT.getImportPaths().split(",") :
                        CheckType.VIEW.getImportPaths().split(",");
                getModel().getTestClassModel().addImports(imports);
            }
        }
        if(!isFound) {
            String name = method.getName();
            String[] splitted = name.split("_");
            String keyWord = isRedirect ? ParseKey.REDIRECT.getKeyword() : ParseKey.VIEW.getKeyword();
            for (String part : splitted) {
                if (part.startsWith(keyWord)) {
                    String value = part.substring(keyWord.length());
                    value = Utils.setLowerCaseFirstChar(value);
                    assertion.setActual(value);
                    getModel().getAssertions(method.getName()).add(assertion);
                    String[] imports = isRedirect ? CheckType.REDIRECT.getImportPaths().split(",") :
                            CheckType.VIEW.getImportPaths().split(",");
                    getModel().getTestClassModel().addImports(imports);
                    return;
                }
            }
        }
    }

    /**
     * Calculates the content contains statement.
     * @param method the test case
     */
    private void calculateContentContains(Method method) {
        Assertion assertion = new Assertion(CheckType.CONTENT_CONTAINS.toString(), "", AssertType.TRUE);
        if (method.getDeclaredAnnotation(Expect.class) != null) {
            String content = method.getDeclaredAnnotation(Expect.class).contentContains();
            if (!content.equals("")) {
                assertion.setActual(content);
                getModel().getAssertions(method.getName()).add(assertion);
                String[] imports = CheckType.CONTENT_CONTAINS.getImportPaths().split(",");
                getModel().getTestClassModel().addImports(imports);
            }
        }
    }

    /**
     * Calculates the model check statements.
     * @param method the test case
     */
    public void calculateModelChecks(Method method) {
        if (method.getDeclaredAnnotation(Expect.class) != null) {
            String exists = method.getDeclaredAnnotation(Expect.class).modelAttrExists();
            String attr = method.getDeclaredAnnotation(Expect.class).modelAttr();
            String errors = method.getDeclaredAnnotation(Expect.class).modelAttrErrors();

            if (!exists.equals("")) {
                calculateModelExistCheck(exists, method.getName());
            }
            if (!attr.equals("")) {
                calculateModelAttrCheck(attr, method.getName(), false);
            }
            if (!errors.equals("")) {
                calculateModelAttrCheck(errors, method.getName(), true);
            }
        }
    }

    /**
     * Calculates the model attribute and attribute error checks.
     * @param attr the given attribute statement of the annotation
     * @param testName the name of the test case
     * @param isError whether it is an error check
     */
    public void calculateModelAttrCheck(String attr, String testName, boolean isError) {
        List<String> parsed = AnnotationParser.parseExpectModelAttrAndErrors(attr);
        List<Assertion> assertions = getModel().getAssertions(testName);
        String errorOrAttr = isError? StateKey.ERROR.toString() : StateKey.ATTRIBUTE.toString();
        boolean isAttr = false;
        boolean isField = false;
        boolean isType = false;
        boolean isValue = false;
        boolean isAll = false;
        String actual = "";
        String field = "";
        String val = "";
        String expected = "";
        Optional<Class<?>> optAttrClass = Optional.empty();
        String comment = "";

        for (String part : parsed) {
            if (part.equals(StateKey.ATTRIBUTE.toString())) {
                isAttr = true;
                if (!actual.equals("")) {
                    if (expected.equals("")) {
                        expected = errorOrAttr;
                    }
                    if (!field.equals("")) {
                        actual += "," + field;
                    }
                    if (!val.equals("")) {
                        actual += "," + val;
                    }
                    Assertion assertion = new Assertion(expected, actual, AssertType.TRUE);
                    assertion.setComment(comment);
                    assertions.add(assertion);
                }
                actual = "";
                expected = "";
            }
            else if (part.equals(StateKey.FIELD.toString())) {
                //expected = part;
                isField = true;
            }
            else if (part.equals(StateKey.TYPE.toString())) {
                isType = true;
            }
            else if (part.equals(StateKey.VALUE.toString())) {
                isValue = true;
            }
            else if (isAttr) {
                actual = part;
                optAttrClass = Scanner.findEntityClass(Utils.setUpperCaseFirstChar(part),
                        getModel().getProjectPackageUriWithDots());
                if (!optAttrClass.isPresent()) {
                    optAttrClass = Scanner.findCorrespondingField(part, getModel().getProjectPackageUriWithDots());
                    if (!optAttrClass.isPresent()) {
                        comment = "The attribute class with the name " + part + " can not be found in the project package.";
                    }
                }
                isAttr = false;
            }
            else if (isField) {
                field = part;
                Optional<Class<?>> optFieldClass = Scanner.findCorrespondingField(part, getModel().getProjectPackageUriWithDots());
                if (!optFieldClass.isPresent()) {
                    comment += "The field with the name " + part + " can not be found in the project package.";
                    Logger.logWarning(comment);
                }
                else {
                    String finalActual = actual;
                    optAttrClass.ifPresent(aClass -> checkFieldOfAttribute(aClass, finalActual, part));
                }
                isField = false;
            }
            else if (isType && part.equals(StateKey.ALL.toString())) {
                isAll = true;
            }
            else if (isValue && isAll) {
                checkAllFieldValues(part, actual, testName, isError);
                expected = "";
                actual = "";
            }
            else if (isValue) {
                val = checkFieldValue(part, field, actual, testName);
                expected = errorOrAttr;
                isValue = false;
            }
        }
        if (!actual.equals("")) {
            if (expected.equals("")) {
                expected = errorOrAttr;
            }
            if (!field.equals("")) {
                actual += "," + field;
                getModel().getTestClassModel().addImport(SpringBootKey.HAS_PROPERTY.getImportPath());
            }
            if (!val.equals("")) {
                actual += "," + val;
            }
            Assertion assertion = new Assertion(expected, actual, AssertType.TRUE);
            assertion.setComment(comment);
            assertions.add(assertion);
        }

    }

    /**
     * Checks all fields of the given actual attribute.
     * @param part the expected value (can be * for random values)
     * @param actual the attribute
     * @param testName the name of the test case
     * @param isError whether it is an error check
     */
    private void checkAllFieldValues(String part, String actual, String testName, boolean isError) {
        ObjectModel attrObject = null;
        for (ObjectModel param : getModel().getParameters(testName)) {
            if (param.getIdentifier().equals(actual)) {
                attrObject = param;
                break;
            }
        }
        if (attrObject == null) {
            Optional<Class<?>> optAttrClass;
            optAttrClass = Scanner.findCorrespondingClass(Utils.setUpperCaseFirstChar(actual),
                    getModel().getProjectPackageUriWithDots());
            if (!optAttrClass.isPresent()) {
                Logger.logWarning("[ControllerAssertionCreator] " +
                        "No attrObject for " + actual + " found! Generation of all fields not possible!");
                return;
            }
            List<Assertion> assertions = getModel().getAssertions(testName);
            for (Field field : optAttrClass.get().getDeclaredFields()) {
                String attrAndValue = field.getName();
                if (isError) {
                    assertions.add(new Assertion(StateKey.ERROR.toString(), attrAndValue, AssertType.TRUE));
                } else {
                    assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), attrAndValue, AssertType.TRUE));
                }
            }
        }
        else {
            List<Assertion> assertions = getModel().getAssertions(testName);
            for (ObjectModel field : attrObject.getInstanceFields()) {
                String attrAndValue = field.getIdentifier() + ",";
                if (part.equals("*")) {
                    attrAndValue += field.getValue();
                } else {
                    attrAndValue += part;
                }
                if (isError) {
                    assertions.add(new Assertion(StateKey.ERROR.toString(), attrAndValue, AssertType.TRUE));
                } else {
                    assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), attrAndValue, AssertType.TRUE));
                }
            }
        }
    }

    /**
     * Checks the field of the given actual attribute and returns the value to check.
     * If it is not random (*), than the given part is returned.
     * @param part the expected value (can be * for random values)
     * @param field a field of the attribute
     * @param actual the attribute
     * @param testName the name of the test case
     * @return the expected value
     */
    private String checkFieldValue(String part, String field, String actual, String testName) {
        if (part.equals("*")) {
            if (field.equals("")) {
                field = actual;
            }
            if (field.equals("")) {
                return "";
            }
            for (ObjectModel param : getModel().getParameters(testName)) {
                if (param.getIdentifier().equals(field) && param.getValue() != null) {
                    return param.getValue();
                }
            }
        }
        return part;
    }

    /**
     * Calculates the model exist check.
     * @param exists the exist statement of the Expect annotation
     * @param testName the name of the test case
     */
    public void calculateModelExistCheck(String exists, String testName) {
        List<String> parsed = AnnotationParser.parseExpectModelAttrExist(exists);
        boolean isNot = false;
        for (String part : parsed) {
            if (part.equals(StateKey.NOT.toString())) {
                isNot = true;
            }
            else if (isNot) {
                Assertion assertion = new Assertion(StateKey.NOT_EXIST.toString(), part, AssertType.TRUE);
                getModel().getAssertions(testName).add(assertion);
                isNot = false;
            }
            else {
                Assertion assertion = new Assertion(StateKey.EXIST.toString(), part, AssertType.TRUE);
                getModel().getAssertions(testName).add(assertion);
            }
        }
    }

    /**
     * Checks if the field class of the attribute exists in the project.
     * @param attr the attribute class
     * @param attrName the attribute name
     * @param field the field to search
     * @return a comment if the field class can not be found
     */
    public String checkFieldOfAttribute(Class<?> attr, String attrName, String field) {
        String comment = "";
        Field[] fields = attr.getDeclaredFields();
        List<String> fieldClass = new ArrayList<>();
        for (Field f : fields) {
            fieldClass.add(f.getName());
        }
        if (!fieldClass.contains(field)) {
            Class<?> superClass = attr.getSuperclass();
            if (superClass == null) {
                comment += "The attribute " + attrName
                        + " does not contain a field with name "
                        + field + ".";
                return comment;
            }
            else {
                return checkFieldOfAttribute(superClass, attrName, field);
            }
        }
        return comment;
    }

}
