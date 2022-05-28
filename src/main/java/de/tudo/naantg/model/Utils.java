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

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.helpers.Helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Useful help methods for the de.tudo.naantg.model creations.
 */
public class Utils {

    /**
     * Checks if the value is an element in the enumClass.
     * @param type a possible element of the enumClass
     * @param enumClass a class of an enum
     * @param <E> enum class
     * @return true if the value is an element in the enumClass, otherwise false
     */
    public static  <E extends Enum<E>> boolean isInEnum(String type, Class<E> enumClass) {
        for (E e : enumClass.getEnumConstants()) {
            if(e.name().equalsIgnoreCase(type)) { return true; }
            if (e instanceof CollectionDataType &&
                    ((CollectionDataType) e).getType().equalsIgnoreCase(type)) return true;
            if (e instanceof CollectionDataType &&
                    (e.toString().equalsIgnoreCase(type))) return true;
        }
        return false;
    }

    /**
     * Checks if dataType is equal to "int" or "Integer".
     * @param dataType the data type
     * @return true if dataType is equal to "int" or "Integer", otherwise false
     */
    public static boolean isInt(String dataType) {
        return (dataType.equalsIgnoreCase(SimpleDataType.INT.getType())
                || dataType.equals(SimpleDataType.INTEGER.getType()));
    }

    /**
     * Checks if dataType is equal to "short" or "Short".
     * @param dataType the data type
     * @return true if dataType is equal to "short" or "Short", otherwise false
     */
    public static boolean isShort(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.SHORT.getType());
    }

    /**
     * Checks if dataType is equal to "long" or "Long".
     * @param dataType the data type
     * @return true if dataType is equal to "long" or "Long", otherwise false
     */
    public static boolean isLong(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.LONG.getType());
    }

    /**
     * Checks if dataType is equal to "float" or "Float".
     * @param dataType the data type
     * @return true if dataType is equal to "float" or "Float", otherwise false
     */
    public static boolean isFloat(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.FLOAT.getType());
    }

    /**
     * Checks if dataType is equal to "double" or "Double".
     * @param dataType the data type
     * @return true if dataType is equal to "double" or "Double", otherwise false
     */
    public static boolean isDouble(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.DOUBLE.getType());
    }

    /**
     * Checks if dataType is equal to "byte" or "Byte".
     * @param dataType the data type
     * @return true if dataType is equal to "byte" or "Byte", otherwise false
     */
    public static boolean isByte(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.BYTE.getType());
    }

    /**
     * Checks if dataType is equal to "boolean" or "Boolean".
     * @param dataType the data type
     * @return true if dataType is equal to "boolean" or "Boolean", otherwise false
     */
    public static boolean isBoolean(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.BOOLEAN.getType());
    }

    /**
     * Checks if dataType is equal to "String".
     * @param dataType the data type
     * @return true if dataType is equal to "String", otherwise false
     */
    public static boolean isString(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.STRING.getType());
    }

    /**
     * Checks if dataType is equal to "char" or "Character".
     * @param dataType the data type
     * @return true if dataType is equal to "char" or "Character", otherwise false
     */
    public static boolean isChar(String dataType) {
        return dataType.equalsIgnoreCase(SimpleDataType.CHAR.getType()) ||
                dataType.equals(SimpleDataType.CHARACTER.getType());
    }

    /**
     * Sets the first char of the word to lower case.
     * @param word a word which first char should be lower case
     * @return the word which first char is lower case
     */
    public static String setLowerCaseFirstChar(String word) {
        if (word.length() == 0) return word;
        String first = word.substring(0,1);
        first = first.toLowerCase();
        String ending = word.substring(1);
        return first + ending;
    }

    /**
     * Sets the first char of the word to upper case.
     * @param word a word which first char should be upper case
     * @return the word which first char is upper case
     */
    public static String setUpperCaseFirstChar(String word) {
        if (word.length() == 0) return word;
        String first = word.substring(0,1);
        first = first.toUpperCase();
        String ending = word.substring(1);
        return first + ending;
    }

    /**
     * Checks if the data type is a simple type.
     * @param dataType a data type
     * @return true if it is a simple type
     */
    public static boolean isSimpleType(String dataType) {
        return isInEnum(dataType, SimpleDataType.class);
    }

    /**
     * Checks if the data type is a primitive type (no object),
     * for example "long" instead of "Long".
     * @param dataType a data type
     * @return true if it is a primitive type
     */
    public static boolean isPrimitiveType(String dataType) {
        return dataType.equals(SimpleDataType.INT.getType()) ||
                dataType.equals(SimpleDataType.SHORT.getType()) ||
                dataType.equals(SimpleDataType.LONG.getType()) ||
                dataType.equals(SimpleDataType.BOOLEAN.getType()) ||
                dataType.equals(SimpleDataType.FLOAT.getType()) ||
                dataType.equals(SimpleDataType.DOUBLE.getType()) ||
                dataType.equals(SimpleDataType.BYTE.getType()) ||
                dataType.equals(SimpleDataType.CHAR.getType());
    }

    /**
     * Checks if the data type is not a simple or void type.
     * @param dataType a data type
     * @return true if it is an object type
     */
    public static boolean isObjectType(String dataType) {
        return !(isSimpleType(dataType) || isVoid(dataType));
    }

    public static boolean isObject(String dataType) {
        return dataType.equals("Object") || dataType.equals("java.util.Object");
    }

    /**
     * Checks if the data type is a void type.
     * @param dataType a data type
     * @return true if it is a void type
     */
    public static boolean isVoid(String dataType) {
        return dataType != null && dataType.equals("void");
    }

    /**
     * Checks if the data type is a collection type.
     * @param dataType a data type
     * @return true if it is a collection type
     */
    public static boolean isCollectionType(String dataType) {
        if (dataType.contains("[]")) return true;
        return isInEnum(dataType, CollectionDataType.class);
    }

    /**
     * Checks if the data type is a list type: List, ArrayList or LinkedList.
     * @param dataType a data type
     * @return true if it is a list type
     */
    public static boolean isListType(String dataType) {
        return isList(dataType) || isArrayList(dataType) || isLinkedList(dataType);
    }

    /** Checks if the value matches the format "-?(\\d)+p(\\d)+".
     * @param value a value
     * @return true if the value matches the format, otherwise false
     */
    public static boolean matchesDoubleFormat(String value) {
        return value.matches("-?(\\d)+[p|.](\\d)+");
    }

    /** Checks if the value matches the format "-?(\\d)+".
     * @param value a value
     * @return true if the value matches the format, otherwise false
     */
    public static boolean matchesIntFormat(String value) {
        return value.matches("-?(\\d)+");
    }

    /** Checks if the value matches "false" or "true".
     * @param value a value
     * @return true if the value matches the format, otherwise false
     */
    public static boolean matchesBooleanFormat(String value) {
        return value.equals("false") || value.equals("true");
    }

    /** Checks if the value matches the format "\\S".
     * @param value a value
     * @return true if the value matches the format, otherwise false
     */
    public static boolean matchesCharFormat(String value) {
        return value.matches("\\S");
    }

    /**
     * Returns the last element of the given list.
     * @param list list of objects
     * @return the last element
     */
    public static Object getLast(List<?> list) {
        if (list.isEmpty()) return null;
        return list.get(list.size()-1);
    }

    /**
     * Returns a default value for a primitive type or, if random is true, a random value.
     * @param type the data type
     * @param random true if the value should be a random value
     * @return a default or a random value of the given type
     */
    public static String getSimpleDefaultOrRandomValue(String type, boolean random) {
        if (isInt(type) || isShort(type) || isByte(type)) {
            if (random) return getRandomInt(100) + "";
            return 0 + "";
        }
        if (isLong(type)) {
            if (random) return getRandomLongAsString();
            return 0 + "L";
        }
        if (isFloat(type)) {
            if (random) return getRandomFloatAsString();
            return 0.0 + "f";
        }
        if (isDouble(type)) {
            if (random) return getRandomDoubleAsString();
            return 0.0 + "d";
        }
        if (isBoolean(type)) {
            if (random) return getRandomBoolean() + "";
            return "false";
        }
        if (isChar(type)) {
            if (random) return getRandomCharAsString();
            return 0 + "";
        }
        if (isString(type)) {
            if (random) return getRandomStringAsString();
            return "\"\"";
        }
        return "";
    }

    /**
     * Returns the type "int" for a digit sequence,
     * the type "boolean" for "true" or "false",
     * the type "double" for floating point,
     * the type "String" otherwise and the value.
     * @param paramValue a parameter value
     * @return the data type and the value
     */
    public static String[] parse(String paramValue) {
        String dataType;
        String value;
        if (paramValue.equalsIgnoreCase("true")) {
            dataType = SimpleDataType.BOOLEAN.getType();
            value = "true";
        }
        else if (paramValue.equalsIgnoreCase("false")) {
            dataType = SimpleDataType.BOOLEAN.getType();
            value = "false";
        }
        else if (paramValue.matches("(-)?(Neg)?(neg)?(\\d)+")) {
            dataType = SimpleDataType.INT.getType();
            value = paramValue.replace("Neg", "-")
                    .replace("neg", "-");
        }
        else if (matchesDoubleFormat(paramValue.replace("Neg", "-")
                .replace("neg", "-"))) {
            dataType = SimpleDataType.DOUBLE.getType();
            value = paramValue.replace("p", ".")
                    .replace("Neg", "-").replace("neg", "-");
        }
        else {
            dataType = SimpleDataType.STRING.getType();
            value = paramValue;
        }
        return new String[] {dataType, value};
    }

    /**
     * Replaces keywords like "neg" and "p" in the value with "-" and "."
     * and "True" or "False" with "true" or "false".
     * @param value a word with potential neg or p
     * @return the word with replaced keywords
     */
    public static String parseValue(String value) {
        String[] parsed = parse(value);
        if (parsed.length != 2) return "";
        return parsed[1];
    }

    /**
     * Checks if the collection is a List.
     * @param collection a collection type
     * @return true if it is a List otherwise false
     */
    public static boolean isList(String collection) {
        return (collection.equals(CollectionDataType.LIST.getType()) ||
                collection.equals(CollectionDataType.LIST.toString()));
    }

    /**
     * Checks if the collection is a Collection.
     * @param collection a collection type
     * @return true if it is a Collection otherwise false
     */
    public static boolean isCollection(String collection) {
        return (collection.equals(CollectionDataType.COLLECTION.getType()) ||
                collection.equals(CollectionDataType.COLLECTION.toString()));
    }

    /**
     * Checks if the collection is an ArrayList.
     * @param collection a collection type
     * @return true if it is a ArrayList otherwise false
     */
    public static boolean isArrayList(String collection) {
        return (collection.equals(CollectionDataType.ARRAY_LIST.getType()) ||
                collection.equals(CollectionDataType.ARRAY_LIST.toString()));
    }

    /**
     * Checks if the collection is a LinkedList.
     * @param collection a collection type
     * @return true if it is a LinkedList otherwise false
     */
    public static boolean isLinkedList(String collection) {
        return (collection.equals(CollectionDataType.LINKED_LIST.getType()) ||
                collection.equals(CollectionDataType.LINKED_LIST.toString()));
    }

    /**
     * Checks if the collection is an Optional.
     * @param collection a collection type
     * @return true if it is an Optional otherwise false
     */
    public static boolean isOptional(String collection) {
        return (collection.equals(CollectionDataType.OPTIONAL.getType()) ||
                collection.equals(CollectionDataType.OPTIONAL.toString()));
    }

    /**
     * Checks if the given map is a Map or an HashMap.
     * @param map a map type
     * @return true if it is a map otherwise false
     */
    public static boolean isMapType(String map) {
        return (map.equals(CollectionDataType.MAP.getType()) ||
                map.equals(CollectionDataType.MAP.toString()) ||
                map.equals(CollectionDataType.HASH_MAP.getType()) ||
                map.equals(CollectionDataType.HASH_MAP.toString()));
    }

    /**
     * Checks if the given map is a Map.
     * @param map a Map
     * @return true if it is a Map otherwise false
     */
    public static boolean isMap(String map) {
        return (map.equals(CollectionDataType.MAP.getType()) ||
                map.equals(CollectionDataType.MAP.toString()));
    }

    /**
     * Checks if the given map is an HashMap.
     * @param map an HashMap
     * @return true if it is an HashMap otherwise false
     */
    public static boolean isHashMap(String map) {
        return (map.equals(CollectionDataType.HASH_MAP.getType()) ||
                map.equals(CollectionDataType.HASH_MAP.toString()));
    }

    /**
     * Checks if the collection is an Array.
     * @param collection a collection type
     * @return true if it is an Array otherwise false
     */
    public static boolean isArray(String collection) {
        return (collection.contains("["));
    }

    /**
     * Returns a random String in String style with maximal word length = 50
     * and uses a default alphabet.
     * @return a random String in string style
     */
    public static String getRandomStringAsString() {
        return  "\"" + getRandomString() + "\"";
    }

    /**
     * Returns a random String in string style with the given maximal word length and uses
     * the chars of the alphabet.
     * @param maxWordLength the maximal word length
     * @param alphabet the chars to use
     * @return a random String in string style
     */
    public static String getRandomStringAsString(int maxWordLength, String alphabet) {
        return getRandomStringAsString(0, maxWordLength, alphabet);
    }

    /**
     * Returns a random String in string style with the given minimal and maximal word length and uses
     * the chars of the alphabet.
     * @param maxWordLength the maximal word length
     * @param minWordLength the minimal word length
     * @param alphabet the chars to use
     * @return a random String in string style
     */
    public static String getRandomStringAsString(int minWordLength, int maxWordLength, String alphabet) {
        return  "\"" + getRandomString(minWordLength, maxWordLength, alphabet) + "\"";
    }

    /**
     * Returns a random String with maximal word length = 50
     * and uses a default alphabet.
     * @return a random String
     */
    public static String getRandomString() {
        return getRandomString(50, null);
    }

    /**
     * Returns a random String with the given maximal word length and uses
     * the chars of the alphabet.
     * @param maxWordLength the maximal word length
     * @param alphabet the chars to use
     * @return a random String
     */
    public static String getRandomString(int maxWordLength, String alphabet) {
        return getRandomString(0, maxWordLength, alphabet);
    }

    /**
     * Returns a random String with the given minimal and maximal word length and uses
     * the chars of the alphabet.
     * @param maxWordLength the maximal word length
     * @param minWordLength the minimal word length
     * @param alphabet the chars to use
     * @return a random String
     */
    public static String getRandomString(int minWordLength, int maxWordLength,
                                         String alphabet) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(maxWordLength + 1);
        while (length < minWordLength) {
            length = random.nextInt(maxWordLength);
        }
        for (int i = 0; i < length; i++) {
            if (alphabet == null) {
                sb.append(getRandomChar());
            }
            else {
                sb.append(getRandomChar(alphabet));
            }
        }
        return sb.toString();
    }

    public static String getAlphabet(Alphabet[] alphabets) {
        StringBuilder builder = new StringBuilder();
        for (Alphabet alphabet : alphabets) {
            switch (alphabet) {
                case ALL:
                    builder.append(allAlphabet());
                    break;
                case BIG_LETTERS:
                    builder.append(bigLettersAlphabet());
                    break;
                case LITTLE_LETTERS:
                    builder.append(littleLettersAlphabet());
                    break;
                case NUMBERS:
                    builder.append(numberAlphabet());
                    break;
                case SYMBOLS:
                    builder.append(symbolAlphabet());
                    break;
                case SPACES:
                    builder.append(spaceAlphabet());
                    break;
            }
        }
        return builder.toString();
    }

    public static String littleLettersAlphabet() {
        return "abcdefghijklmnopqrstuvwxyz";
    }

    public static String bigLettersAlphabet() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    public static String numberAlphabet() {
        return "1234567890";
    }

    public static String symbolAlphabet() {
        return "!§$%&/()=?*+-_";
    }

    public static String spaceAlphabet() {
        return " \t";
    }

    public static String allAlphabet() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "1234567890" +
                "!§$%&/()=?*+-_ \t";
    }

    /**
     * Returns a random char in char style and uses a default alphabet.
     * @return a random char in char style
     */
    public static String getRandomCharAsString() {
        return "'" + getRandomChar() + "'";
    }

    /**
     * Returns a random char in char style and uses the given alphabet.
     * @param alphabet the chars to use
     * @return a random char in char style
     */
    public static String getRandomCharAsString(String alphabet) {
        return "'" + getRandomChar(alphabet) + "'";
    }

    /**
     * Returns a random char and uses a default alphabet.
     * @return a random char
     */
    public static char getRandomChar() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "1234567890" +
                "!§$%&/()=?*+-_ ";
        return getRandomChar(alphabet);
    }

    /**
     * Returns a random char and uses the given alphabet.
     * @param alphabet the chars to use
     * @return a random char
     */
    public static char getRandomChar(String alphabet) {
        Random random = new Random();
        int index = random.nextInt(alphabet.length());
        return alphabet.charAt(index);
    }

    /**
     * Returns a random boolean.
     * @return a random boolean
     */
    public static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    /**
     * Returns a random positive or negative integer with bound as maximal value = 999.
     * @return a random positive or negative integer
     */
    public static int getRandomInt() {
        return getRandomInt(1000);
    }

    /**
     * Returns a random positive or negative integer with bound as maximal value (excluded).
     * @param bound the maximal value (excluded)
     * @return a random positive or negative integer
     */
    public static int getRandomInt(int bound) {
        Random random = new Random();
        int value = random.nextInt(bound);
        boolean isNeg = random.nextBoolean();
        if (isNeg) {
            value *= -1;
        }
        return value;
    }

    /**
     * Returns a random positive or negative integer within the given bounds (included).
     * @param min the minimal value (included)
     * @param max the maximal value (included)
     * @return a random integer within the bounds
     */
    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        int value = 0;
        do {
            value = random.nextInt(max + 1);
            if (min < 0) {
                boolean isNeg = random.nextBoolean();
                if (isNeg) {
                    value *= -1;
                }
            }
        } while (value < min);
        return value;
    }

    /**
     * Returns a random positive or negative double.
     * @return a random positive or negative double
     */
    public static double getRandomDouble() {
        Random random = new Random();
        double value = random.nextDouble();
        boolean isNeg = random.nextBoolean();
        if (isNeg) {
            value *= -1;
        }
        return value;
    }

    /**
     * Returns a random positive or negative double in double style.
     * @return a random positive or negative double in double style
     */
    public static String getRandomDoubleAsString() {
        return getRandomDouble() + "d";
    }

    /**
     * Returns a random positive or negative float.
     * @return a random positive or negative float
     */
    public static float getRandomFloat() {
        Random random = new Random();
        float value = random.nextFloat();
        boolean isNeg = random.nextBoolean();
        if (isNeg) {
            value *= -1;
        }
        return value;
    }

    /**
     * Returns a random positive or negative float in float style.
     * @return a random positive or negative float in float style
     */
    public static String getRandomFloatAsString() {
        return getRandomFloat() + "f";
    }

    /**
     * Returns a random positive or negative long.
     * @return a random positive or negative long
     */
    public static long getRandomLong() {
        Random random = new Random();
        long value = random.nextLong();
        boolean isNeg = random.nextBoolean();
        if (isNeg) {
            value *= -1;
        }
        return value;
    }

    /**
     * Returns a random positive or negative long in long style.
     * @return a random positive or negative long in long style
     */
    public static String getRandomLongAsString() {
        return getRandomLong() + "L";
    }

    /**
     * Returns a random list with elements of the given type.
     * The minimal list size can be 0 and the maximal size can be 20.
     * The maximal value for int, short or byte is 99 (minimal -99).
     * @param dataType the type of the list elements
     * @return a random list with elements of the given type
     */
    public static String getRandomList(Class<?> dataType) {
        return getRandomList(dataType, 0, 20);
    }

    /**
     * Returns a random list with elements of the given type.
     * The maximal value for int, short or byte is 99 (minimal -99).
     * The minimal list size can be 0.
     * @param dataType the type of the list elements
     * @param maxSize the maximum size of the list
     * @return a random list with elements of the given type
     */
    public static String getRandomList(Class<?> dataType, int maxSize) {
        return getRandomList(dataType, 0, maxSize);
    }

    /**
     * Returns a random list with elements of the given type.
     * The maximal value for int, short or byte is 99 (minimal -99).
     * @param dataTypeClass the type of the list elements
     * @param minSize the minimum size of the list
     * @param maxSize the maximum size of the list
     * @return a random list with elements of the given type
     */
    public static String getRandomList(Class<?> dataTypeClass, int minSize, int maxSize) {
        if (dataTypeClass == null) return "";
        String dataType = dataTypeClass.getSimpleName();
        Random random = new Random();
        int length = random.nextInt(maxSize + 1);
        if (length < minSize) length = minSize;
        StringBuilder value = new StringBuilder();
        value.append("{");
        for (int i = 0; i < length; i++) {
            if (Utils.isInt(dataType) || Utils.isShort(dataType) || Utils.isByte(dataType)) {
                value.append(Utils.getRandomInt(100));
            }
            else if (Utils.isLong(dataType)) {
                value.append(Utils.getRandomLong());
            }
            else if (Utils.isDouble(dataType)) {
                value.append(Utils.getRandomDouble());
            }
            else if (Utils.isFloat(dataType)) {
                value.append(Utils.getRandomFloat());
            }
            else if (Utils.isBoolean(dataType)) {
                value.append(Utils.getRandomBoolean());
            }
            else if (Utils.isString(dataType)) {
                value.append(Utils.getRandomStringAsString());
            }
            else if (Utils.isChar(dataType)) {
                value.append(Utils.getRandomCharAsString());
            }
            else if (dataTypeClass.isEnum()) {
                value.append(getRandomEnumAsEnum(dataTypeClass.getFields()));
            }
            value.append(", ");
        }
        if (value.length() >= 2) value.delete(value.length()-2, value.length());
        value.append("}");
        return value.toString();
    }

    /**
     * Returns the simple name of the given constructor.
     * @param constructor a constructor
     * @return the simple name of the constructor
     */
    public static String getSimpleName(Constructor<?> constructor) {
        String[] splitted = constructor.getName().split("\\.");
        if (splitted.length == 0) return "";
        return splitted[splitted.length - 1];
    }

    /**
     * Converts simple values to the specific format:
     * add "f", "d", "L" "\"" or "'" to the value for the corresponding type.
     * @param value the value to v´convert
     * @param type the data type of the value
     * @return the specific format
     */
    public static String convertListValues(String value, String type) {
        return convertListValues(value, type, new ArrayList<>());
    }

    /**
     * Converts simple values to the specific format:
     * add "f", "d", "L" "\"" or "'" to the value for the corresponding type.
     * @param value the value to v´convert
     * @param type the data type of the value
     * @return the specific format
     */
    public static String convertListValues(String value, String type, List<ObjectModel> inits) {
        StringBuilder builder = new StringBuilder();
        if (!value.contains(", ")) value = value.replaceAll(",", ", ");
        if (!isSimpleType(type) || isInt(type) || isShort(type) || isByte(type) || isBoolean(type)) return value;
        boolean isRound = value.contains(")");
        boolean isBox = value.contains("]");
        value = value.replaceAll("\\s+", "");
        value = value.replace("{", "").replace("}", "");
        value = value.replace("(", "").replace(")", "");
        value = value.replace("[", "").replace("]", "");
        for (String part : value.split(",")) {
            if (Helper.findObjectByIdentifier(part, inits) != null) builder.append(part);
            else if (isFloat(type) && !part.contains("f")) builder.append(part).append("f");
            else if (isDouble(type) && !part.contains("d")) builder.append(part).append("d");
            else if (isLong(type) && !part.contains("L")) builder.append(part).append("L");
            else if (isChar(type) && !part.contains("'")) builder.append("'").append(part).append("'");
            else if (isString(type) && !part.contains("\"")) builder.append("\"").append(part).append("\"");
            else builder.append(part);
            builder.append(", ");
        }
        String result = builder.toString();
        result = result.substring(0, result.length()-2);
        if (isRound) return "(" + result + ")";
        if (isBox) return  "[" + result + "]";
        return "{" + result + "}";
    }

    /**
     * Returns per random a value as String from the given array.
     * @param enumValues the enum values
     * @return a random value from the given array
     */
    public static String getRandomEnumAsString(Field[] enumValues) {
        Random random = new Random();
        int index = random.nextInt(enumValues.length);
        Field enumValue = enumValues[index];
        return enumValue.getName();
    }

    /**
     * Returns the first value as String from the given array.
     * @param enumValues the enum values
     * @return the first value from the given array
     */
    public static String getFirstEnumAsString(Field[] enumValues) {
        return enumValues[0].getName();
    }

    /**
     * Returns per random a value as enum (class name + value) from the given array.
     * @param enumValues the enum values
     * @return a random value from the given array
     */
    public static String getRandomEnumAsEnum(Field[] enumValues) {
        Random random = new Random();
        int index = random.nextInt(enumValues.length);
        Field value = enumValues[index];
        return value.getType().getSimpleName() + "." + value.getName();
    }

    /**
     * Returns the first value as enum (class name + value) from the given array.
     * @param enumValues the enum values
     * @return the first value from the given array
     */
    public static String getFirstEnumAsEnum(Field[] enumValues) {
        Field value = enumValues[0];
        return value.getType().getSimpleName() + "." + value.getName();
    }

    /**
     * Removes all white space characters of the input.
     * @param input a string
     * @return the input string without white space characters
     */
    public static String removeSpaces(String input) {
        return input.replaceAll("\\s", "");
    }

    /**
     * Removes all types of brackets in the given value.
     * @param value a string
     * @return the value without brackets
     */
    public static String removeBrackets(String value) {
        value = value.replace("[", "").replace("]", "");
        value = value.replace("{", "").replace("}", "");
        value = value.replace("(", "").replace(")", "");
        return value;
    }

}
