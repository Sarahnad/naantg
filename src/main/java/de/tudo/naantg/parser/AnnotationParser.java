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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the expression given in an annotation.
 */
public class AnnotationParser {

    /**
     * Parses and returns the variables of the rule and the rule part.
     * The rule has to be a lambda expression. For example: "x, y -> x+y".
     * @param rule a rule in lambda notation
     * @return the variables and the rule part
     */
    public static String[] parseAssertStateRule(String rule) {
        String[] splitted = rule.split("->");
        if (splitted.length != 2) {
            return new String[]{};
        }
        String[] variables = splitted[0].split(",");
        String rulePart = splitted[1];
        String[] result = new String[variables.length+1];
        for (int i = 0; i < variables.length; i++) {
            // remove white space characters
            result[i] = variables[i].replaceAll("\\s", "");
        }
        rulePart = removeBeginningSpaceCharacters(rulePart);
        if (rulePart.contains("%str")) {
            rulePart = parseStringRule(rulePart);
        }
        result[variables.length] = rulePart;
        return result;
    }

    /**
     * Makes writing of rules with strings easier.
     * For example:
     * <p>instead of</p>
     * <p>"funny(6, 7, \"yeah!\", 8, \"with \" + t + \" and \" + w + \"\", 9)"</p>
     * <p>it is allowed to write</p>
     * <p>"funny(6, 7, %str(yeah!), 8, %str(with %t and %w), 9)"</p>
     * @param stringRule a rule part with strings
     * @return the converted rule
     */
    public static String parseStringRule(String stringRule) {
        String result = stringRule;
        Pattern pattern = Pattern.compile("%str\\([^()]+\\)");
        Matcher matcher = pattern.matcher(stringRule);
        while (matcher.find()) {
            String stringPart = matcher.group();
            String replacement = "\"" + stringPart.substring(5, stringPart.length()-1) + "\"";
            result = result.replace(stringPart, replacement);
        }
        pattern = Pattern.compile("%\\w+");
        matcher = pattern.matcher(result);
        while (matcher.find()) {
            String stringPart = matcher.group();
            String replacement = "\" + " + stringPart.substring(1) + " + \"";
            result = result.replace(stringPart, replacement);
        }
        return result;
    }

    /**
     * Parses the returnedSize value of the AssertState annotation.
     * @param returned the returnedSize value
     * @return a list of parsed assert keys and values
     */
    public static List<String> parseAssertStateReturnedSize(String returned) {
        return parseAssertStateReturnedStatement(returned, true, false, false);
    }

    /**
     * Parses the returned value of the AssertState annotation for get statements.
     * @param returned the returned value
     * @return a list of parsed assert keys and values
     */
    public static List<String> parseAssertStateReturned(String returned) {
        return parseAssertStateReturnedStatement(returned, false, true, false);
    }

    /**
     * Parses the returnedContains value of the AssertState annotation.
     * @param returned the returnedContains value
     * @return a list of parsed assert keys and values
     */
    public static List<String> parseAssertStateReturnedContains(String returned) {
        return parseAssertStateReturnedStatement(returned, false, false, true);
    }

    /**
     * Parses the different returned values of the AssertState annotation.
     * @param returned the value to parse
     * @param size if it is a size statement
     * @param get if it is a get statement
     * @param contains if it is a contains statement
     * @return a list of parsed assert keys and values
     */
    private static List<String> parseAssertStateReturnedStatement(String returned, boolean size,
                                                                  boolean get, boolean contains) {
        String value = returned.replaceAll("\\s", "");
        List<String> result = new ArrayList<>();
        if (size) result.add(StateKey.SIZE.toString());

        if (value.contains("and")) {
            for (String part : value.split("and")) {
                if (size) {
                    if (!result.get(result.size()-1).equals(StateKey.SIZE.toString())) {
                        result.add(StateKey.SIZE.toString());
                    }
                    parseAssertStateReturnedSizePart(result, part, true, false);
                }
                else if (get) parseAssertStateReturendGetPart(result, part, true, false);
                else if (contains) parseAssertStateReturendContainsPart(result, part, true, false);
            }
        }
        else if (value.contains(",")) {
            for (String part : value.split(",")) {
                if (size) parseAssertStateReturnedSizePart(result, part, true, false);
                else if (get) parseAssertStateReturendGetPart(result, part, true, false);
                else if (contains) parseAssertStateReturendContainsPart(result, part, true, false);
            }
        }
        else if (value.contains("or")) {
            for (String part : value.split("or")) {
                if (size) parseAssertStateReturnedSizePart(result, part, false, true);
                else if (get) parseAssertStateReturendGetPart(result, part, false, true);
                else if (contains) parseAssertStateReturendContainsPart(result, part, false, true);
            }
        }
        else {
            if (size) parseAssertStateReturnedSizePart(result, value, false, false);
            else if (get) parseAssertStateReturendGetPart(result, value, false, false);
            else if (contains) parseAssertStateReturendContainsPart(result, value, false, false);
        }

        int index = result.size() - 1;
        if (index > -1 && (result.get(index).equals(StateKey.AND.toString()) ||
                result.get(index).equals(StateKey.OR.toString()))) {
            result.remove(index);
        }
        return result;
    }

    /**
     * Parses the part value of the contains statement
     * and adds the parsed assert keys and values to the result list.
     * @param result the list of parsed assert keys and values
     * @param part the value to parse
     * @param isAnd if it is an and statement
     * @param isOr if it is an or statement
     */
    private static void parseAssertStateReturendContainsPart(List<String> result, String part, boolean isAnd, boolean isOr) {
        result.add(StateKey.CONTAINS.toString());
        if (part.startsWith("!")) {
            result.add(StateKey.NOT.toString());
            part = part.replace("!", "");
        }
        result.add(part);
        if (isAnd) {
            result.add(StateKey.AND.toString());
        }
        else if (isOr) {
            result.add(StateKey.OR.toString());
        }
    }

    /**
     * Parses the part value of the get statement
     * and adds the parsed assert keys and values to the result list.
     * @param result the list of parsed assert keys and values
     * @param value the value to parse
     * @param isAnd if it is an and statement
     * @param isOr if it is an or statement
     */
    private static void parseAssertStateReturendGetPart(List<String> result, String value, boolean isAnd, boolean isOr) {
        boolean isBoolean = false;

        if (value.startsWith("[")) {
            if (value.endsWith("]")) {
                result.add(StateKey.GET.toString());
                result.add(value.replace("[", "").replace("]", ""));
                result.add(StateKey.IS.toString());
                result.add(StateKey.TRUE.toString());
                isBoolean = true;
            }
            else {
                String[] splitted = value.split("]");
                if (splitted.length != 2) return;
                result.add(StateKey.GET.toString());
                result.add(splitted[0].replace("[", ""));

                String part = splitted[1].replace("=", "");
                parseAssertStateReturnedSizePart(result, part, isAnd, isOr);
            }
        }
        else if (value.startsWith("![")) {
            result.add(StateKey.GET.toString());
            result.add(value.replace("![", "").replace("]", ""));
            result.add(StateKey.IS.toString());
            result.add(StateKey.FALSE.toString());
            isBoolean = true;
        }
        else if (value.contains("=")) {
            result.add(StateKey.GET.toString());
            String[] objAndField = value.split("=");
            if (objAndField.length == 2) {
                result.add(StateKey.PARAM.toString());
                result.add(Utils.removeSpaces(objAndField[0]));
                String val = removeBeginningSpaceCharacters(objAndField[1]);
                result.add(StateKey.IS.toString());
                result.add(val);
            }
        }

        if (isAnd && !result.get(result.size()-1).equals(StateKey.AND.toString())) {
            result.add(StateKey.AND.toString());
        }
        else if (isOr) {
            if (!isBoolean && result.get(result.size()-1).equals(StateKey.OR.toString())) {
                int n = result.size() - 1;
                int index = 0;
                while (n >= 0) {
                    if (result.get(n).equals(StateKey.GET.toString())) {
                        index = n + 1;
                        break;
                    }
                    n--;
                }
                result.add(StateKey.GET.toString());
                result.add(result.get(index));
            }
            else if (!isBoolean) {
                parseAssertStateReturnedSizePart(result, value.replace("=", ""), isAnd, isOr);
            }
            else {
                result.add(StateKey.OR.toString());
            }
        }

    }

    /**
     * Parses the part value of the size statement
     * and adds the parsed assert keys and values to the result list.
     * @param result the list of parsed assert keys and values
     * @param value the value to parse
     * @param isAnd if it is an and statement
     * @param isOr if it is an or statement
     */
    private static void parseAssertStateReturnedSizePart(List<String> result, String value, boolean isAnd, boolean isOr) {
        if (value.contains("!")) {
            value = value.replace("!", "").replace("=", "");
            result.add(StateKey.NOT.toString());
            result.add(value);
        }
        else if (value.contains(">")) {
            value = value.substring(1);
            result.add(StateKey.GREATER.toString());
            result.add(value);
        }
        else if (value.contains("<")) {
            value = value.substring(1);
            result.add(StateKey.SMALLER.toString());
            result.add(value);
        }
        else {
            result.add(StateKey.IS.toString());
            result.add(value);
        }
        if (isAnd) {
            result.add(StateKey.AND.toString());
        }
        else if (isOr) {
            result.add(StateKey.OR.toString());
        }
    }

    /**
     * Parses a list of controller params separated with ";".
     * @param value the value to parse
     * @return the parsed values in the form attribute name, value, attribute name, value, ...
     */
    public static List<String> parseInitStateControllerParams(String value) {
        ArrayList<String> result = new ArrayList<>();
        value = Utils.removeSpaces(value);
        String[] parts = value.split(";");
        for (String part : parts) {
            String[] nameAndValue = part.split("=");
            if (nameAndValue.length == 2) {
                result.addAll(Arrays.asList(nameAndValue));
            }
        }
        return result;
    }

    /**
     * Parses the value of the params annotation.
     * The value must be a list with strings without "="
     * or like "p1 = valX, p5 = valY".
     * @param params the value of the params annotation
     * @param paramCounts the number of parameters of the method to test
     * @return an array with parameter values while
     * the index of the array corresponds with the parameter index
     */
    public static String[] parseParams(String params, int paramCounts) {
        if (params.contains("[") && params.contains("]")) {
            Pattern pattern = Pattern.compile("\\[.*]+");
            Matcher matcher = pattern.matcher(params);
            while (matcher.find()) {
                String listPart = matcher.group();
                String replacement = listPart.replace(",", ";")
                        .replace("[", "{")
                        .replace("]", "}");
                params = params.replace(listPart, replacement);
            }
        }
        String[] splitted = params.split(",");
        if (splitted.length == 0) return null;
        if (splitted.length > paramCounts) {
            paramCounts = splitted.length;
        }
        String[] result = new String[paramCounts + 1];
        for (int i = 0; i < paramCounts + 1; i++) {
            result[i] = "";
        }

        int[] indexArray = new int[splitted.length];
        String[] valueArray = new String[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].contains("=")) {
                String value = removeBeginningSpaceCharacters(splitted[i]);
                result[i+1] = value;
            }
            else {
                String[] parts = splitted[i].split("=");
                String paramIndex = parts[0].replaceAll("\\s", "").replace("p", "");
                String paramValue = parts[1];
                if (!paramIndex.matches("\\d+")) return null;
                paramValue = removeBeginningSpaceCharacters(paramValue);
                indexArray[i] = Integer.parseInt(paramIndex);
                valueArray[i] = paramValue;
            }
        }
        for (int i = 0; i < valueArray.length; i++) {
            result[indexArray[i]] = valueArray[i];
        }
        return result;
    }

    /**
     * Parses the constructor definition.
     * @param constructors a constructor definition
     * @param parameters the parameters of the method to test
     * @return returns the parsed result
     */
    public static List<String> parseConstructors(String constructors, Class<?>[] parameters) {
        List<String> result = new ArrayList<>();
        if (constructors == null || constructors.equals("")) return result;

        String[] definitions = constructors.split(";");
        for (String definition : definitions) {
            definition = Utils.removeSpaces(definition);
            String[] parts = definition.split("=");
            if (parts.length == 1) {
                StringBuilder builder = new StringBuilder();
                for (Class<?> param : parameters) {
                    builder.append(param.getSimpleName()).append(",");
                }
                parseConstParts(parts[0], builder.toString(), result);
            }
            else if (parts.length == 2){
                String strIndex = parts[0].substring(1);
                String objectType = "";
                if (Utils.matchesIntFormat(strIndex)) {
                    int index = Integer.parseInt(strIndex) - 1;
                    if (index >= 0 && index < parameters.length) {
                        result.add(StateKey.INDEX.toString());
                        result.add(strIndex);
                        objectType = parameters[index].getSimpleName();
                    }
                }
                parseConstParts(parts[1], objectType, result);
            }
        }
        return result;
    }

    /**
     * Parses the constructor definition part and adds the parsed constructor and its
     * parameters to the result list.
     * @param part the constructor definition part
     * @param result the parsed result
     */
    private static void parseConstParts(String part, String objectType, List<String> result) {
        if (!part.contains("(") && !part.contains(")")) return;

        String[] constAndParams = part.split(Pattern.quote("("));
        if (constAndParams.length != 2) return;

        // check correct type
        String constructor = constAndParams[0];
        if (objectType.contains(",")) {
            String[] types = objectType.split(",");
            if (!Arrays.asList(types).contains(constructor)) return;

            String index = Arrays.asList(types).indexOf(constructor) + 1 + "";
            result.add(StateKey.INDEX.toString());
            result.add(index);
        }
        else if (!objectType.equals(constructor)) return;

        result.add(StateKey.CONSTRUCTOR.toString());
        result.add(constructor);

        String[] params = constAndParams[1].replace(")", "").split(",");
        for (String param : params) {
            result.add(StateKey.PARAM.toString());
            result.add(param);
        }
    }

    /**
     * Removes all beginning space characters of the word.
     * @param word a word
     */
    public static String removeBeginningSpaceCharacters(String word) {
        while (word.length() > 0 && word.startsWith(" ")) {
            word = word.substring(1);
        }
        return word;
    }

    /**
     * Parses the value of the InitState annotation.
     * @param value the value of the InitState annotation
     * @return the parsed values
     */
    public static List<String> parseInitStateValue(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.equals("")) return result;

        String[] parts = value.split(";");
        for (String part : parts) {
            String def = "";
            String val = "";
            String type = "";
            if (part.contains("=")) {
                String[] defAndVal = part.split("=");
                if (defAndVal.length == 2) {
                    def = defAndVal[0];
                    val = defAndVal[1];
                }
            }
            else {
                def = part;
            }
            if (def.contains(".")) {
                def = Utils.removeSpaces(def);
                String[] callingParts = def.split(Pattern.quote("."));
                if (callingParts.length >= 2) {
                    result.add(StateKey.FROM.toString());
                    String caller = callingParts[0];
                    if (caller.matches("p\\d+"))
                        caller = caller.replace("p", "param");
                    else if (caller.equals(ParseKey.THIS.getKeyword()))
                        caller = StateKey.THIS.toString();
                    else if (Character.isUpperCase(caller.toCharArray()[0])) {
                        result.add(StateKey.TYPE.toString());
                    }
                    result.add(caller);

                    for (int i = 1; i < callingParts.length; i++) {
                        result.add(StateKey.FIELD.toString());
                        result.add(callingParts[i]);
                    }
                }
            }
            else {
                while (def.startsWith(" ")) {
                    def = def.substring(1);
                }
                String[] typeAndName = def.split(" ");

                if (typeAndName.length == 2) {
                    result.add(StateKey.TYPE.toString());
                    type = Utils.removeSpaces(typeAndName[0]);
                    result.add(type);
                    result.add(StateKey.PARAM.toString());
                    result.add(Utils.removeSpaces(typeAndName[1]));
                }
            }
            if (!val.equals("")) {
                result.add(StateKey.VALUE.toString());
                if (type.equals("String")) {
                    while (val.startsWith(" ")) {
                        val = val.substring(1);
                    }
                }
                else {
                    val = Utils.removeSpaces(val);
                }
                if (val.equals(ParseKey.NULL.getKeyword())) {
                    val = StateKey.NULL.toString();
                }
                result.add(val);
            }
        }

        return result;
    }

    /**
     * Parses the methods of the InitState annotation.
     * <p>For example:</p>
     * <p>"initBuy()" is parsed to </p>
     * <p>"VALUE,VOID,OF,initBuy" and </p>
     * <p>"this.init()" is parsed to </p>
     * <p>"VALUE,VOID,FROM,THIS,OF,init" and </p>
     * <p>"p1.init()" is parsed to </p>
     * <p>"VALUE,VOID,FROM,param1,OF,init" and</p>
     * <p>"this.field.field2.init()" is parsed to </p>
     * <p>"VALUE,VOID,FROM,THIS,FIELD,field,FIELD,field2,OF,init" and</p>
     * <p>"p1 = createPersonWithBoxes(boxes); boxes = createBoxes()" is parsed to</p>
     * <p>"VALUE,param1,OF,createPersonWithBoxes,PARAM,boxes,VALUE,boxes,OF,createBoxes".</p>
     * @param initValue the methods of the InitState annotation
     * @return the parsed values
     */
    public static List<String> parseInitStateMethods(String initValue) {
        List<String> result = new ArrayList<>();
        if (initValue == null || initValue.isEmpty()) return  result;

        String[] definitions = initValue.split(";");
        for (String def : definitions) {
            result.add(StateKey.VALUE.toString());
            result.addAll(parseDefinition(def));
        }
        return result;
    }

    private static List<String> parseDefinition(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        part = Utils.removeSpaces(part);
        String methodPart;
        if (part.contains("=")) {
            String[] objectAndMethod = part.split("=");
            result.addAll(parseObjectPart(objectAndMethod[0]));
            methodPart = objectAndMethod[1];
        }
        else {
            result.add(StateKey.VOID.toString());
            methodPart = part;
        }
        result.addAll(parseMethodCallPart(methodPart));
        return result;
    }

    private static List<String> parseObjectPart(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        if (part.equals(ParseKey.THIS.getKeyword()))
            result.add(StateKey.THIS.toString());
        else if (part.matches("p\\d+"))
            result.add(part.replace("p", "param"));
        else if (Character.isUpperCase(part.toCharArray()[0])) {
            result.add(StateKey.TYPE.toString());
            result.add(part);
        }
        else result.add(part);
        return result;
    }

    private static List<String> parseMethodCallPart(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        String methodPart;
        if (part.contains(".")) {
            String[] callerAndMethod = part.split(Pattern.quote("."));
            methodPart = callerAndMethod[callerAndMethod.length-1];
            result.add(StateKey.FROM.toString());
            result.addAll(parseObjectPart(callerAndMethod[0]));

            for (int i = 1; i < callerAndMethod.length - 1; i++) {
                result.add(StateKey.FIELD.toString());
                result.add(callerAndMethod[i]);
            }
        }
        else {
            methodPart = part;
        }
        result.add(StateKey.OF.toString());
        result.addAll(parseMethodPart(methodPart));
        return result;
    }

    private static List<String> parseMethodPart(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        if (part.contains("(")) {
            String[] nameAndParams = part.split(Pattern.quote("("));
            result.add(nameAndParams[0]);
            result.addAll(parseParamsPart(nameAndParams[1].replace(")", "")));
        }
        else {
            result.add(part);
        }
        return result;
    }

    private static List<String> parseParamsPart(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        if (part.contains(",")) {
            String[] params = part.split(",");
            for (String param : params) {
                result.addAll(parseParamPart(param));
            }
        }
        else {
            result.addAll(parseParamPart(part));
        }
        return result;
    }

    private static List<String> parseParamPart(String part) {
        List<String> result = new ArrayList<>();
        if (part.isEmpty()) return result;

        result.add(StateKey.PARAM.toString());
        if (part.matches("p\\d+"))
            part = part.replace("p", "param");
        else if (part.equals(ParseKey.THIS.getKeyword()))
            part = StateKey.THIS.toString();
        result.add(part);
        return result;
    }

    /**
     * Parses the mocking value of the Mocking annotation.
     * @param mockingDef the mocking value of the Mocking annotation
     * @return the parsed values
     */
    public static List<String> parseMockingValue(String mockingDef) {
        List<String> result = new ArrayList<>();
        if (mockingDef == null || mockingDef.equals("")) return result;

        String[] definitions = mockingDef.split(";");
        for (String definition : definitions) {
            definition = Utils.removeSpaces(definition);
            if (definition.contains(":")) {
                String[] classAndDef = definition.split(":");
                if (classAndDef.length == 2) {
                    result.add(StateKey.MOCK_CLASS.toString());
                    result.add(classAndDef[0]);
                    parseMockingDefinition(classAndDef[1], result);
                }
            }
            else {
                result.add(StateKey.MOCK_CLASS.toString());
                result.add(StateKey.DEFAULT.toString());
                parseMockingDefinition(definition, result);
            }
        }

        return result;
    }

    /**
     * Parses the definition part of the mocking definition.
     * @param definition the definition part of the mocking definition
     * @param result the list of parsed values
     */
    private static void parseMockingDefinition(String definition, List<String> result) {
        String[] methodAndValue = definition.split("=");
        if (methodAndValue.length != 2) return;

        String method = methodAndValue[0];
        if (!method.contains("(") && !method.contains(")")) return;

        String[] methodAndParams = method.split(Pattern.quote("("));
        if (methodAndParams.length != 2) return;

        result.add(StateKey.MOCK_METHOD.toString());
        result.add(methodAndParams[0]);

        String[] params = methodAndParams[1].replace(")", "").split(",");
        for (String param : params) {
            result.add(StateKey.PARAM.toString());
            if (param.equals(ParseKey.NOT_NULL.getKeyword())) {
                param = StateKey.NOT_NULL.toString();
            }
            result.add(param);
        }

        String value = methodAndValue[1];
        result.add(StateKey.VALUE.toString());
        if (value.equalsIgnoreCase(StateKey.EXCEPTION.toString())) {
            result.add(StateKey.EXCEPTION.toString());
        }
        else {
            result.add(value);
        }
    }

    /**
     * Parses the modelAttrExist value of the Expect annotation.
     * @return the parsed values
     */
    public static List<String> parseExpectModelAttrExist(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.equals("")) return result;

        String[] parts = Utils.removeSpaces(value).split(";");
        for (String part : parts) {
            if (part.startsWith("!")) {
                result.add(StateKey.NOT.toString());
                part = part.substring(1);
            }
            result.add(part);
        }
        return result;
    }

    /**
     * Parses the modelAttr or modelAttrErrors value of the Expect annotation.
     * Examples:
     * "attr1 = value1; attr2.field = value2;
     * attr3 = *; attr4.field = *; attr5: all = *" (set value = get value)
     * "attr7" (no error)
     * "attr1; attr2.field; attr3.field = error message"
     * @return the parsed values
     */
    public static List<String> parseExpectModelAttrAndErrors(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.equals("")) return result;

        String[] parts = value.split(";");
        for (String part : parts) {
            if (part.contains(":")) {
                StateKey[] keys = new StateKey[]{StateKey.ATTRIBUTE, StateKey.TYPE, StateKey.VALUE};
                Map<String, String> replaceMap = new HashMap<>();
                replaceMap.put(ParseKey.ALL.getKeyword(), StateKey.ALL.toString());
                result.addAll(parseTypeNameValue(part, keys, replaceMap, true));
                continue;
            }

            String[] attrAndValue = part.split("=");
            if (attrAndValue.length < 1 || attrAndValue.length > 2) continue;

            String attr = Utils.removeSpaces(attrAndValue[0]);
            String[] attrAndField = attr.split(Pattern.quote("."));
            if (attrAndField.length > 0) {
                result.add(StateKey.ATTRIBUTE.toString());
                result.add(attrAndField[0]);
            }
            if (attrAndField.length == 2) {
                String field = attrAndField[1];
                result.add(StateKey.FIELD.toString());
                result.add(field);
            }

            if (attrAndValue.length == 1) continue;

            String attrValue = attrAndValue[1];
            if (attrValue.startsWith(" ")) {
                attrValue = attrValue.substring(1);
            }
            result.add(StateKey.VALUE.toString());
            result.add(attrValue);
        }
        return result;
    }

    /**
     * Parses the modelPath value of the Expect annotation.
     * @return the parsed values
     */
    public static List<String> parseExpectPath(String value) {
        StateKey[] keys = new StateKey[]{StateKey.TYPE, StateKey.PATH, StateKey.VALUE};
        Map<String, String> replaceMap = new HashMap<>();
        return parseTypeNameValue(value, keys, replaceMap, false);
    }

    /**
     * Parses the pattern "type : name = value".
     * @param value the value to parse
     * @param keys three keywords for type, name and value
     * @param replaceMap map with all keywords to replace
     * @param spaceSensitive if true, than spaces in value must not be removed
     * @return the parsed values
     */
    private static List<String> parseTypeNameValue(String value, StateKey[] keys,
                                                   Map<String, String> replaceMap, boolean spaceSensitive) {
        List<String> result = new ArrayList<>();
        if (value == null || value.equals("")) return result;
        if (keys.length != 3) return result;

        String[] parts = value.split(";");
        for (String part : parts) {
            String[] defAndValue = part.split("=");
            if (defAndValue.length != 2) continue;

            String def = Utils.removeSpaces(defAndValue[0]);
            String defValue = defAndValue[1];
            if (spaceSensitive && defValue.startsWith(" ")) {
                defValue = defValue.substring(1);
            }
            else {
                defValue = Utils.removeSpaces(defValue);
            }
            String name;
            if (def.contains(":")) {
                String[] typeAndName = def.split(":");
                if (typeAndName.length == 2) {
                    result.add(keys[0].toString());
                    result.add(checkAndReplace(typeAndName[0], replaceMap));
                    name = typeAndName[1];
                }
                else {
                    name = def;
                }
            }
            else {
                name = def;
            }
            result.add(keys[1].toString());
            result.add(checkAndReplace(name, replaceMap));

            result.add(keys[2].toString());
            result.add(checkAndReplace(defValue, replaceMap));
        }
        return result;
    }

    /**
     * If the replaceMap contains the key, than the mapped value is returned,
     * otherwise the the given key.
     * @param key the key word
     * @param replaceMap the map with words to replace
     * @return the mapped value or the key
     */
    private static String checkAndReplace(String key, Map<String, String> replaceMap) {
        if (replaceMap.get(key) != null) {
            return replaceMap.get(key);
        }
        return key;
    }

    /**
     * Parses the value of the MappingValue annotation.
     * @param value the value of the MappingValue annotation
     * @return the parsed values
     */
    public static List<String> parseMappingValue(String value) {
        List<String> result = new ArrayList<>();
        if (value == null || value.equals("")) return result;

        String[] parts = value.split(";");
        for (String part : parts) {
            String def = "";
            String val = "";
            if (part.contains("=")) {
                String[] defAndVal = part.split("=");
                if (defAndVal.length == 2) {
                    def = defAndVal[0];
                    val = defAndVal[1];
                }
            }
            else {
                def = part;
            }
            while (def.startsWith(" ")) {
                def = def.substring(1);
            }
            String[] typeAndName = def.split(" ");
            String type = "";
            if (typeAndName.length == 2) {
                result.add(StateKey.TYPE.toString());
                type = Utils.removeSpaces(typeAndName[0]);
                result.add(type);
                result.add(StateKey.PARAM.toString());
                result.add(Utils.removeSpaces(typeAndName[1]));
            }
            if (!val.equals("")) {
                result.add(StateKey.VALUE.toString());
                if (type.equals("String")) {
                    while (val.startsWith(" ")) {
                        val = val.substring(1);
                    }
                }
                else {
                    val = Utils.removeSpaces(val);
                }
                result.add(val);
            }
        }

        return result;
    }

}
