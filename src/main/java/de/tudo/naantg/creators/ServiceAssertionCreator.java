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

import de.tudo.naantg.model.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Extends the SimpleAssertionCreator to to create assertions for the service classes.
 */
public class ServiceAssertionCreator extends SimpleAssertionCreator {

    /**
     * Calculates the assert information for the return and returned statement
     * depending on the key words in the assert part of the test generation method name.
     * Handle Exceptions separat.
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
        Method methodToTest = getModel().getMethodOfCUT(method.getName()).getMethodToTest();
        if (expected.size() == 2 && expected.get(1).equals(StateKey.EXCEPTION.toString())) {
            assertion.setExpected("");
            Class<?>[] exs = methodToTest.getExceptionTypes();
            if (exs.length != 0) {
                assertion.setActual(exs[0].getSimpleName());
                getModel().getTestClassModel().addImport(exs[0].getName());
            }
            else {
                assertion.setActual("Exception");
            }
            assertion.setAssertType(AssertType.THROWN);
            getModel().getTestClassModel().addImport(SpringBootKey.ASSERT_THROWN.getImportPath());
        }
        else {
            super.calculateReturnAssertions(method, expected);
        }
    }

}
