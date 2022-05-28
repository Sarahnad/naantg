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

/**
 * Container for the information of an assertion statement in the post condition.
 */
public class Assertion {

    /**
     * the assertion type
     */
    private AssertType assertType;

    /**
     * the expected behavior
     */
    private String expected;

    /**
     * the actual behavior
     */
    private String actual;

    /**
     * comment to generate for additional explanation
     */
    private String comment;


    /**
     * Constructs an Assertion.
     */
    public Assertion() {

    }

    /**
     * Constructs an Assertion with the expected and actual behavior and the assertion type
     * @param expected the expected behavior
     * @param actual the actual behavior
     * @param assertType the assertion type
     */
    public Assertion(String expected, String actual, AssertType assertType) {
        this.expected = expected;
        this.actual = actual;
        this.assertType = assertType;
    }

    /**
     * Generates the assertion statement based on the stored information in this Assertion object.
     * @return the assertion statement
     */
    public String generateAssertion() {
        if (assertType == null || actual == null || expected == null) return "";

        String assertion = "\t\t";
        if (isSimple()) {
            assertion += "assert " + actual + " " + assertType.getType() + " " + expected + ";\n";
        }
        else {
            assertion += assertType.getType() + "(";
            if (isOneValue()) {
                assertion += actual;
            }
            else {
                assertion += expected + ", " + actual;
            }
            assertion += ");\n";
        }
        return assertion;
    }

    /**
     *
     * @return the assertion type
     */
    public AssertType getAssertType() {
        return assertType;
    }

    /**
     *
     * @param assertType the assertion type
     */
    public void setAssertType(AssertType assertType) {
        this.assertType = assertType;
    }

    /**
     * Checks if if the assertion type is simple like "assert objA == objB" or "assert objA != objB".
     * @return true if the assertion type is simple
     */
    private boolean isSimple() {
        if (assertType == null) return false;
        return assertType.equals(AssertType.EQ) || assertType.equals(AssertType.NEQ)
                || assertType.equals(AssertType.GREATER) || assertType.equals(AssertType.SMALLER);
    }

    /**
     *
     * @return the expected behavior
     */
    public String getExpected() {
        return expected;
    }

    /**
     *
     * @param expected the expected behavior
     */
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     *
     * @return the actual behavior
     */
    public String getActual() {
        return actual;
    }

    /**
     *
     * @param actual the actual behavior
     */
    public void setActual(String actual) {
        this.actual = actual;
    }

    /**
     * Checks if the assertion type needs only the actual behavior like "assertTrue" or "assertFalse"
     * @return true if the assertion type needs only the actual behavior
     */
    private boolean isOneValue() {
        if (assertType == null) return false;
        return assertType.equals(AssertType.TRUE) || assertType.equals(AssertType.FALSE) ||
                assertType.equals(AssertType.THAT);
    }

    /**
     *
     * @return a comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @param comment a comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
