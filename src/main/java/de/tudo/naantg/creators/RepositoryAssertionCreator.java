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

import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.model.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Extends the SimpleAssertionCreator to select assertion information for repository test classes.
 */
public class RepositoryAssertionCreator extends SimpleAssertionCreator {

    /**
     * Calculates the assert information for the return and returned statement
     * depending on the key words in the assert part of the test generation method name.
     * Optional return types are separat treated for determining their generic.
     * @param method the test generation method
     * @param expected list of key words and assert value
     */
    @Override
    public void calculateReturnAssertions(Method method, List<String> expected) {
        String testName = method.getName();
        if (getModel().getMethodOfCUT(testName) == null) return;

        Assertion assertion = new Assertion();
        String actual = "actual";
        List<Assertion> assertions = getModel().getAssertions(testName);
        assertions.add(assertion);
        assertion.setActual(actual);
        String returnType = getModel().getMethodOfCUT(testName).getReturnType();
        if (Utils.isOptional(returnType)) {
            calculateOptionalReturnAssertions(expected, assertions, method);
        }
        else {
            super.calculateReturnAssertions(method, expected);
        }
    }

    /**
     * Calculates the assert information for the optional return type.
     * @param expected a list of key words and assert value
     * @param assertions a list of containers for the assertion information
     * @param method the test case
     */
    public void calculateOptionalReturnAssertions(List<String> expected, List<Assertion> assertions, Method method) {
        super.calculateOptionalReturnAssertions(expected, assertions, method);
        /*for (Assertion assertion : assertions) {
            assertion.setAssertType(AssertType.THAT);
        }*/
        if (expected.get(0).equals(StateKey.RETURN.toString())) {
            if (expected.get(1).equals(StateKey.VALUE.toString())) {
                ObjectModel generic = Helper.findOptionalObject(getModel(), method.getName());
                if (generic == null || generic.getInstanceFields().isEmpty()) {
                    return;
                }
                for (ObjectModel fieldObject : generic.getInstanceFields()) {
                    calculateFieldAssertion(fieldObject, generic, null, null, assertions);
                }
            }
        }
    }

    /**
     * Calculates recursive assertions for the field of the parent and its children.
     * The created assertions are added to the assertions and the actualGetter and fieldGetter
     * save the getter statement for the calculation of the children.
     * @param fieldObject an object model of a field of the parent
     * @param parent the object model of the parent
     * @param actualGetter the current getter statement for the actual part
     * @param fieldGetter the current getter statement for the expected part
     * @param assertions the list of assertions
     */
    public void calculateFieldAssertion(ObjectModel fieldObject, ObjectModel parent, String actualGetter,
                                        String fieldGetter, List<Assertion> assertions) {
        if (fieldObject.getGetterName() == null || fieldObject.getGetterName().equals("")) return;

        Assertion assertion = new Assertion("", "", AssertType.THAT);

        String actual = actualGetter != null ? actualGetter : "actual.get()";
        actual += "." + fieldObject.getGetterName() + "()";
        String getter = fieldGetter != null ? fieldGetter : parent.getIdentifier();
        getter += "." + fieldObject.getGetterName() + "()";

        actualGetter = actual;
        fieldGetter = getter;

        if (Utils.isPrimitiveType(fieldObject.getDataType())) {
            actual += " == " + getter;
        }
        else if (Utils.isCollectionType(fieldObject.getDataType())) {
            // todo
        }
        else {
            actual += ".equals(" + getter + ")";
        }
        assertion.setActual(actual);
        assertions.add(assertion);

        for (ObjectModel child : fieldObject.getInstanceFields()) {
            calculateFieldAssertion(child, fieldObject, actualGetter, fieldGetter, assertions);
        }
    }


}
