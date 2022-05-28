/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.parser;


import de.tudo.naantg.model.StateKey;
import de.tudo.naantg.model.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser for the name of a test generation method annotated with @TG.
 */
public class MethodNameParser {

    /**
     * Extracts the name of the method to test from the tgMethod name.
     * @param tgMethod the name of the test generation method
     * @return the name of the method to test
     */
    public static String getMethodName(String tgMethod) {
        String methodName = "";
        String[] splitted = tgMethod.split("_");
        for (String part : splitted) {
            if (part.startsWith(ParseKey.WHEN.getKeyword())) {
                methodName = part.substring(ParseKey.WHEN.getKeyword().length());
                methodName = Utils.setLowerCaseFirstChar(methodName);
            }
        }
        return methodName;
    }

    /**
     * Returns the information of the assertion type and the expected value of the test case
     * if the given method name contains a valid key word.
     * <p>
     *     Valid key words and corresponding return values are:
     *     <p>"thenReturn" -> {RETURN, ""},</p>
     *     <p>"thenReturn(_)?[value]" -> {RETURN, value},</p>
     *     <p>"thenReturnNeg[value]" -> {RETURN, -value},</p>
     *     <p>"thenReturnValue" -> {RETURN, VALUE},</p>
     *     <p>"thenReturnNull" -> {RETURN, NULL},</p>
     *     <p>"thenReturnList" -> {RETURN, LIST},</p>
     *     <p>"thenReturned_isSmaller(neg)?[value]" -> {RETURNED, SMALLER, (-)value},</p>
     *     <p>"thenReturned_isGreater(neg)?[value]" -> {RETURNED, GREATER, (-)value}</p>
     *     <p>"thenReturned_size_(is(_)?/isNot(_)?/isSmaller/isGreater)(neg)?[value]" ->
     *         {RETURNED, SIZE, IS/NOT/SMALLER/GREATER, (-)value}</p>
     *     <p>"thenReturned_contains(_)?(neg)?[value]" -> {RETURNED, CONTAINS, (-)value}</p>
     *     <p>"thenReturned_notContains(_)?(neg)?[value]" -> {RETURNED, NOT, CONTAINS, (-)value}</p>
     *     <p>"thenReturned_get(_)?[index]_(is(_)?/isNot(_)?/isSmaller/isGreater)(neg)?[value]" ->
     *         {RETURNED, GET, index, IS/NOT/SMALLER/GREATER, (-)value}</p>
     *     <p>combinations with AND and OR</p>
     * </p>
     *
     * @param tgMethod a test generation method
     * @return the information of the assertion type and the expected value of the test case
     */
    public static List<String> getReturnAssertion(String tgMethod) {
        List<String> assertPart = new ArrayList<>();
        String[] splitted = tgMethod.split("_");
        boolean isReturned = false;
        boolean isReturn = false;
        if (splitted.length > 1) {
            for (String part : splitted) {
                if (part.equals(ParseKey.RETURNED.getKeyword())) {
                    assertPart.add(StateKey.RETURNED.toString());
                    isReturned = true;
                }
                else if (isReturn) {
                    assertPart.add(part);
                }
                else if (!part.startsWith(ParseKey.RETURNED.getKeyword()) &&
                        !part.startsWith(ParseKey.RETURN_NEG.getKeyword())
                        && !part.startsWith(ParseKey.RETURN_VALUE.getKeyword())
                        && !part.startsWith(ParseKey.RETURN_NULL.getKeyword())
                        && !part.startsWith(ParseKey.RETURN_LIST.getKeyword())
                        && !part.startsWith(ParseKey.RETURN_EXCEPTION.getKeyword())
                        && Pattern.matches(ParseKey.RETURN.getKeyword() + "[A-Za-z0-9]+", part)) {
                    String value = part.substring(ParseKey.RETURN.getKeyword().length());
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(value);
                }
                else if (Pattern.matches(ParseKey.RETURN_NEG.getKeyword() + "[0-9]+p?[0-9]*", part)) {
                    String value = "-" + part.substring(ParseKey.RETURN_NEG.getKeyword().length());
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(value);
                }
                else if (part.equals(ParseKey.RETURN_VALUE.getKeyword())) {
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(StateKey.VALUE.toString());
                }
                else if (part.equals(ParseKey.RETURN_NULL.getKeyword())) {
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(StateKey.NULL.toString());
                }
                else if (part.equals(ParseKey.RETURN_LIST.getKeyword())) {
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(StateKey.LIST.toString());
                }
                else if (part.equals(ParseKey.RETURN_EXCEPTION.getKeyword())) {
                    assertPart.add(StateKey.RETURN.toString());
                    assertPart.add(StateKey.EXCEPTION.toString());
                }
                else if (part.equals(ParseKey.RETURN.getKeyword())) {
                    assertPart.add(StateKey.RETURN.toString());
                    isReturn = true;
                }
                else if (isReturned) {
                    addReturnedParts(assertPart, part);
                }
            }
        }
        return assertPart;
    }

    /**
     * Parses the returned part.
     * @param assertPart the list of parsed assert keys and values
     * @param part the part to parse
     */
    private static void addReturnedParts(List<String> assertPart, String part) {
        if (part.equals(ParseKey.EMPTY.getKeyword())) {
            assertPart.add(StateKey.EMPTY.toString());
        }
        else if (part.equals(ParseKey.SIZE.getKeyword())) {
            assertPart.add(StateKey.SIZE.toString());
        }
        else if (part.equals(ParseKey.GET.getKeyword())) {
            assertPart.add(StateKey.GET.toString());
        }
        else if (part.startsWith(ParseKey.GET.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.GET, StateKey.GET);
        }
        else if (part.equals(ParseKey.CONTAINS.getKeyword())) {
            assertPart.add(StateKey.CONTAINS.toString());
        }
        else if (part.startsWith(ParseKey.CONTAINS.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.CONTAINS, StateKey.CONTAINS);
        }
        else if (part.equals(ParseKey.NOT_CONTAINS.getKeyword())) {
            assertPart.add(StateKey.NOT.toString());
            assertPart.add(StateKey.CONTAINS.toString());
        }
        else if (part.startsWith(ParseKey.NOT_CONTAINS.getKeyword())) {
            assertPart.add(StateKey.NOT.toString());
            addAssertPart(assertPart, part, ParseKey.CONTAINS, StateKey.CONTAINS);
        }
        else if (part.equals(ParseKey.GREATER.getKeyword())) {
            assertPart.add(StateKey.GREATER.toString());
        }
        else if (part.startsWith(ParseKey.GREATER.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.GREATER, StateKey.GREATER);
        }
        else if (part.equals(ParseKey.SMALLER.getKeyword())) {
            assertPart.add(StateKey.SMALLER.toString());
        }
        else if (part.startsWith(ParseKey.SMALLER.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.SMALLER, StateKey.SMALLER);
        }
        else if (part.equals(ParseKey.IS_NOT.getKeyword())) {
            assertPart.add(StateKey.NOT.toString());
        }
        else if (part.startsWith(ParseKey.IS_NOT.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.IS_NOT, StateKey.NOT);
        }
        else if (part.equals(ParseKey.IS.getKeyword())) {
            assertPart.add(StateKey.IS.toString());
        }
        else if (part.startsWith(ParseKey.IS.getKeyword())) {
            addAssertPart(assertPart, part, ParseKey.IS, StateKey.IS);
        }
        else if (part.startsWith(ParseKey.NEG.getKeyword())) {
            assertPart.add(Utils.parseValue(part));
        }
        else if (part.equals(ParseKey.AND.getKeyword())) {
            assertPart.add(StateKey.AND.toString());
        }
        else if (part.equals(ParseKey.OR.getKeyword())) {
            assertPart.add(StateKey.OR.toString());
        }

        else if (assertPart.get(assertPart.size()-1).equals(StateKey.CONTAINS.toString())) {
            assertPart.add(part);
        }
        else if (assertPart.get(assertPart.size()-1).equals(StateKey.IS.toString())) {
            assertPart.add(part);
        }
        else if (assertPart.get(assertPart.size()-1).equals(StateKey.GET.toString())) {
            assertPart.add(part);
        }
        else if (assertPart.get(assertPart.size()-1).equals(StateKey.GREATER.toString())) {
            assertPart.add(part);
        }
        else if (assertPart.get(assertPart.size()-1).equals(StateKey.SMALLER.toString())) {
            assertPart.add(part);
        }
    }

    /**
     * Removes the parseKey from the part and adds the parseKeys
     * and the new value to the assertPart list
     * @param assertPart the list of parsed assert keys and values
     * @param part the value to parse
     * @param parseKey the parse key
     * @param stateKey the assert keys to add
     */
    private static void addAssertPart(List<String> assertPart, String part, ParseKey parseKey,
                                          StateKey... stateKey) {
        String value = part.substring(parseKey.getKeyword().length());
        for (StateKey key : stateKey) {
            assertPart.add(key.toString());
        }
        assertPart.add(Utils.parseValue(value));
    }

    /**
     * Returns a list of state keys and the parameter value or method calls
     * if the test generation name contains the keyword "with" or "after".
     * @param tgMethod a test generation method
     * @return a list of state keys and the parameter value or method calls
     */
    public static List<String> getPreValue(String tgMethod) {
        List<String> result = new ArrayList<>();
        String[] splitted = tgMethod.split("_");
        boolean isWithPart = false;
        boolean isValue = false;
        int index = 1;
        if (splitted.length <= 1) return result;
        for (String part : splitted) {
            if ((part.equals(ParseKey.WITH_VALUES.getKeyword()))) {
                isWithPart = true;
                isValue = true;
                result.add(StateKey.VALUES.toString());
            }
            else if (part.equals(ParseKey.WITH_VALUE.getKeyword())) {
                isWithPart = true;
                isValue = true;
                result.add(StateKey.VALUE.toString());
            }
            else if (part.equals(ParseKey.WITH.getKeyword())) {
                isWithPart = true;
            }
            else if (part.startsWith(ParseKey.WITH.getKeyword())) {
                isWithPart = true;
                String value = part.substring(ParseKey.WITH.getKeyword().length());
                result.add(Utils.parseValue(value));
            }
            else if ((part.equals(ParseKey.VALUES.getKeyword()))) {
                result.add(StateKey.VALUES.toString());
            }
            else if (isWithPart && isValue && part.startsWith(ParseKey.OF.getKeyword())) {
                result.add("param" + index);
                index++;
                result.add(StateKey.OF.toString());
                String value = part.substring(ParseKey.OF.getKeyword().length());
                result.add(Utils.setLowerCaseFirstChar(value));
                isValue = false;
            }
            else if (part.startsWith("then")) {
                break;
            }
            else if (isWithPart){
                result.add(part);
            }
        }
        return result;
    }

    public static List<String> getGivenValue(String tgMethod) {
        List<String> result = new ArrayList<>();
        String[] splitted = tgMethod.split("_");
        boolean isGiven = false;
        boolean isStatic = false;
        boolean isWith = false;

        for (String part : splitted) {
            if (part.equals(ParseKey.GIVEN.getKeyword())) {
                isGiven = true;
            }
            else if (part.startsWith(ParseKey.GIVEN.getKeyword()) &&
                    !(part.startsWith(ParseKey.GIVEN_THIS.getKeyword()) ||
                            part.startsWith(ParseKey.GIVEN_TEST.getKeyword()) ||
                            part.startsWith(ParseKey.GIVEN_PARAM.getKeyword()) ||
                            part.startsWith(ParseKey.GIVEN_STATIC.getKeyword()))) {
                String value = part.substring(ParseKey.GIVEN.getKeyword().length());
                value = Utils.setLowerCaseFirstChar(value);
                result.add(value);
            }
            else if (part.startsWith(ParseKey.GIVEN.getKeyword())) {
                result.add(StateKey.VALUE.toString());
                result.add(StateKey.VOID.toString());
                if (!(part.startsWith(ParseKey.GIVEN_TEST.getKeyword()))) {
                    result.add(StateKey.FROM.toString());
                }
                String value = "";
                if (part.startsWith(ParseKey.GIVEN_THIS.getKeyword())) {
                    result.add(StateKey.THIS.toString());
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.GIVEN_THIS.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.GIVEN_TEST.getKeyword())) {
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.GIVEN_THIS.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.GIVEN_PARAM.getKeyword())) {
                    result.add("param1");
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.GIVEN_PARAM.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.GIVEN_STATIC.getKeyword())) {
                    result.add(StateKey.TYPE.toString());
                    value = part.substring(ParseKey.GIVEN_STATIC.getKeyword().length());
                    isStatic = true;
                }
                if (!part.startsWith(ParseKey.GIVEN_STATIC.getKeyword())) {
                    value = Utils.setLowerCaseFirstChar(value);
                }
                result.add(value);
            }
            else if (part.startsWith(ParseKey.OF.getKeyword())) {
                if (!(part.startsWith(ParseKey.OF_TEST.getKeyword()))) {
                    result.add(StateKey.FROM.toString());
                }
                String value = "";
                if (part.startsWith(ParseKey.OF_THIS.getKeyword())) {
                    result.add(StateKey.THIS.toString());
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.OF_THIS.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.OF_TEST.getKeyword())) {
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.OF_THIS.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.OF_PARAM.getKeyword())) {
                    result.add("param1");
                    result.add(StateKey.OF.toString());
                    value = part.substring(ParseKey.OF_PARAM.getKeyword().length());
                }
                else if (part.startsWith(ParseKey.OF_STATIC.getKeyword())) {
                    result.add(StateKey.TYPE.toString());
                    value = part.substring(ParseKey.OF_STATIC.getKeyword().length());
                    isStatic = true;
                }
                if (!part.startsWith(ParseKey.OF_STATIC.getKeyword())) {
                    value = Utils.setLowerCaseFirstChar(value);
                }
                result.add(value);
            }
            else if (part.equals(ParseKey.WITH.getKeyword())) {
                isWith = true;
            }
            else if (part.startsWith(ParseKey.WITH.getKeyword())) {
                result.add(StateKey.PARAM.toString());
                String value = part.substring(ParseKey.WITH.getKeyword().length());
                value = Utils.setLowerCaseFirstChar(value);
                result.add(value);
            }
            else if (part.startsWith(ParseKey.WHEN.getKeyword())) {
                break;
            }
            else if (isStatic) {
                result.add(StateKey.OF.toString());
                result.add(part);
                isStatic = false;
            }
            else if (isWith) {
                result.add(StateKey.PARAM.toString());
                if (part.equals(ParseKey.THIS.getKeyword())) {
                    result.add(StateKey.THIS.toString());
                }
                else if (part.equals(ParseKey.PARAM.getKeyword())) {
                    result.add("param1");
                }
                else {
                    result.add(part);
                }
                isWith = false;
            }
            else if (isGiven) {
                result.add(StateKey.VALUE.toString());
                if (part.equals(ParseKey.THIS.getKeyword())) {
                    result.add(StateKey.THIS.toString());
                }
                else if (part.equals(ParseKey.PARAM.getKeyword())) {
                    result.add("param1");
                }
                else {
                    result.add(part);
                }
            }
        }
        return result;
    }

    /**
     * Parses the get assertion.
     * @param tgMethod the test generation method
     * @return the list of parsed assert keys and values
     */
    public static List<String> getGetAssertion(String tgMethod) {
        List<String> assertPart = new ArrayList<>();
        String[] splitted = tgMethod.split("_");
        if (splitted.length <= 1) return assertPart;
        for (String part : splitted) {
            if (part.startsWith(ParseKey.THEN_GET.getKeyword())) {
                assertPart.add(StateKey.GET.toString());
                assertPart.add(part.substring(ParseKey.THEN_GET.getKeyword().length()));
            }
            else if (part.startsWith(ParseKey.HAS_VALUE.getKeyword()) &&
                    !part.equals(ParseKey.HAS_VALUES.getKeyword())) {
                assertPart.add(StateKey.VALUE.toString());
                String value = part.substring(ParseKey.HAS_VALUE.getKeyword().length());
                assertPart.add(Utils.parseValue(value));
            }
            else if (part.equals(ParseKey.HAS_VALUES.getKeyword())) {
                assertPart.add(StateKey.VALUES.toString());
            }
            else if (part.equals(ParseKey.HAS_EQUAL_VALUE.getKeyword())) {
                assertPart.add(StateKey.IS.toString());
                assertPart.add(StateKey.VALUE.toString());
            }
            else if (part.equals(ParseKey.HAS_EQUAL_VALUES.getKeyword())) {
                assertPart.add(StateKey.IS.toString());
                assertPart.add(StateKey.VALUES.toString());
            }
        }
        return assertPart;
    }

}
