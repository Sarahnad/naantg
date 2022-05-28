package de.tudo.naantg.model;

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.testproject.ErsteKlasse;
import de.tudo.naantg.testproject.weiter.Dinge;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void setLowerCaseFirstChar() {
        assertEquals("oneFiveOne", Utils.setLowerCaseFirstChar("OneFiveOne"));
        assertEquals("", Utils.setLowerCaseFirstChar(""));
        assertEquals("u", Utils.setLowerCaseFirstChar("u"));
        assertEquals("a", Utils.setLowerCaseFirstChar("A"));
        assertEquals("1", Utils.setLowerCaseFirstChar("1"));
    }

    @Test
    public void matchesDoubleFormatTest() {
        assertTrue(Utils.matchesDoubleFormat("5p6"));
        assertTrue(Utils.matchesDoubleFormat("555p0767"));
        assertFalse(Utils.matchesDoubleFormat("hkjlk"));
        assertFalse(Utils.matchesDoubleFormat("345667"));
        assertTrue(Utils.matchesDoubleFormat("3456.67"));
    }

    @Test
    public void parseTypeTest() {
        parseTest("", "String", "");
        parseTest("fhgdh", "String", "fhgdh");
        parseTest("3768", "int", "3768");
        parseTest("Neg677", "int", "-677");
        parseTest("67p09", "double", "67.09");
        parseTest("Neg0p0001", "double", "-0.0001");
        parseTest("True", "boolean", "true");
        parseTest("False", "boolean", "false");
    }

    private void parseTest(String param, String type, String value) {
        String[] actual = Utils.parse(param);
        assertEquals(type, actual[0]);
        assertEquals(value, actual[1]);
    }

    @Test
    public void parseValueTest() {
        assertEquals("hello", Utils.parseValue("hello"));
        assertEquals("3768", Utils.parseValue("3768"));
        assertEquals("-677", Utils.parseValue("Neg677"));
        assertEquals("67.09", Utils.parseValue("67p09"));
        assertEquals("-0.0001", Utils.parseValue("neg0p0001"));
        assertEquals("true", Utils.parseValue("True"));
        assertEquals("false", Utils.parseValue("false"));
    }

    @Test
    public void isCollectionTypeTest() {
        assertTrue(Utils.isCollectionType("List"));
        assertTrue(Utils.isCollectionType("java.util.List"));
        assertTrue(Utils.isCollectionType("Optional"));
        assertTrue(Utils.isCollectionType("HashMap"));

        assertTrue(Utils.isMapType("Map"));
        assertTrue(Utils.isMapType("HashMap"));
        assertTrue(Utils.isMap("Map"));
        assertTrue(Utils.isHashMap("java.util.HashMap"));
        assertFalse(Utils.isMapType("List"));
    }

    @Test
    public void getRandomStringTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomString(10, "abcdefghijklmnopqrst"));
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomStringAsString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomStringAsString(10, "abcdefghijklmnopqrst"));
        }
    }

    @Test
    public void getRandomCharTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomChar());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomChar("abcdefghijklmnopqrst"));
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomCharAsString());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomCharAsString("abcdefghijklmnopqrst"));
        }
    }

    @Test
    public void getRandomBooleanTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomBoolean());
        }
    }

    @Test
    public void getRandomIntTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomInt());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomInt(10));
        }
        for (int i = 0; i < 10; i++) {
            int val = Utils.getRandomInt(-3, 3);
            System.out.println(val);
            assertTrue(val >= -3);
            assertTrue(val <= 3);
        }
    }

    @Test
    public void getRandomDoubleTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomDouble());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomDoubleAsString());
        }
    }

    @Test
    public void getRandomFloatTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomFloat());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomFloatAsString());
        }
    }

    @Test
    public void getRandomLongTest() {
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomLong());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomLongAsString());
        }
    }

    @Test
    public void getRandomList() {
        System.out.println("int list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(int.class));
        }
        System.out.println("\nshort list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(short.class));
        }
        System.out.println("\nbyte list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(byte.class));
        }
        System.out.println("\nlong list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(long.class));
        }
        System.out.println("\ndouble list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(double.class));
        }
        System.out.println("\nfloat list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(float.class));
        }
        System.out.println("\nchar list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(char.class));
        }
        System.out.println("\nString list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(String.class));
        }
        System.out.println("\nboolean list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(boolean.class));
        }
        System.out.println("\nDinge list\n");
        for (int i = 0; i < 10; i++) {
            System.out.println(Utils.getRandomList(Dinge.class));
        }
    }

    @Test
    public void getSimpleDefaultOrRandomValueTest() {
        assertEquals("0", Utils.getSimpleDefaultOrRandomValue("int", false));
        assertEquals("0L", Utils.getSimpleDefaultOrRandomValue("long", false));
        assertEquals("0.0f", Utils.getSimpleDefaultOrRandomValue("float", false));
        assertEquals("0.0d", Utils.getSimpleDefaultOrRandomValue("double", false));
        assertEquals("false", Utils.getSimpleDefaultOrRandomValue("boolean", false));
        assertEquals("0", Utils.getSimpleDefaultOrRandomValue("char", false));
        assertEquals("\"\"", Utils.getSimpleDefaultOrRandomValue("String", false));

        System.out.println(Utils.getSimpleDefaultOrRandomValue("int", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("long", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("float", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("double", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("boolean", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("char", true));
        System.out.println(Utils.getSimpleDefaultOrRandomValue("String", true));
    }

    @Test
    public void getSimpleNameTest() {
        Optional<Constructor<?>> optConst = Scanner.getDefaultConstructor(ErsteKlasse.class);
        assertTrue(optConst.isPresent());
        assertEquals("ErsteKlasse", Utils.getSimpleName(optConst.get()));
    }

    @Test
    public void convertListValuesTest() {
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "int"));
        assertEquals("(1, 2, 3, 4)", Utils.convertListValues("(1, 2, 3, 4)", "int"));
        assertEquals("[1, 2, 3, 4]", Utils.convertListValues("[1,2,3,4]", "int"));
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "Integer"));
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "short"));
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "Short"));
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "byte"));
        assertEquals("{1, 2, 3, 4}", Utils.convertListValues("{1, 2, 3, 4}", "Byte"));
        assertEquals("{true, false}", Utils.convertListValues("{true,false}", "boolean"));
        assertEquals("{true, false}", Utils.convertListValues("{true, false}", "Boolean"));
        assertEquals("{1L, 2L, 3L, 4L}", Utils.convertListValues("{1, 2, 3, 4}", "long"));
        assertEquals("{1L, 2L, 3L, 4L}", Utils.convertListValues("{1L, 2L, 3, 4}", "long"));
        assertEquals("{1L, 2L, 3L, 4L}", Utils.convertListValues("{1, 2, 3, 4}", "Long"));
        assertEquals("{1f, 2f, 3f, 4f}", Utils.convertListValues("{1,2,3,4}", "float"));
        assertEquals("{1f, 2f, 3f, 4f}", Utils.convertListValues("{1, 2, 3, 4}", "Float"));
        assertEquals("{1f, 2f, 3f, 4f}", Utils.convertListValues("{1, 2, 3f, 4f}", "Float"));
        assertEquals("{1d, 2d, 3d, 4d}", Utils.convertListValues("{1, 2, 3, 4}", "double"));
        assertEquals("{1d, 2d, 3d, 4d}", Utils.convertListValues("{1, 2, 3, 4}", "Double"));
        assertEquals("{1d, 2d, 3d, 4d}", Utils.convertListValues("{1d, 2, 3d, 4}", "Double"));
        assertEquals("{\"1\", \"2\"}", Utils.convertListValues("{1, 2}", "String"));
        assertEquals("{\"1\", \"2\"}", Utils.convertListValues("{\"1\", \"2\"}", "String"));
        assertEquals("{'1', '2', '3', '4'}", Utils.convertListValues("{1, 2, 3, 4}", "char"));
        assertEquals("{'1', '2', '3', '4'}", Utils.convertListValues("{'1', '2', '3', '4'}", "char"));
        assertEquals("{'1', '2', '3', '4'}", Utils.convertListValues("{1, 2, 3, 4}", "Character"));
    }

    @Test
    public void isSimpleTypeTest() {
        assertTrue(Utils.isSimpleType("int"));
        assertTrue(Utils.isSimpleType("Integer"));
        assertTrue(Utils.isSimpleType("byte"));
        assertTrue(Utils.isSimpleType("Byte"));
        assertTrue(Utils.isSimpleType("long"));
        assertTrue(Utils.isSimpleType("Long"));
        assertTrue(Utils.isSimpleType("float"));
        assertTrue(Utils.isSimpleType("Float"));
        assertTrue(Utils.isSimpleType("double"));
        assertTrue(Utils.isSimpleType("Double"));
        assertTrue(Utils.isSimpleType("String"));
        assertTrue(Utils.isSimpleType("char"));
        assertTrue(Utils.isSimpleType("Character"));
        assertTrue(Utils.isSimpleType("boolean"));
        assertTrue(Utils.isSimpleType("Boolean"));
        assertTrue(Utils.isSimpleType("short"));
        assertTrue(Utils.isSimpleType("Short"));
    }

    @Test
    public void getRandomEnumTest() {
        String[] expectedValues = new String[] {Dinge.BECHER.toString(), Dinge.FLASCHE.toString(),
                        Dinge.GABEL.toString(), Dinge.GLAS.toString(),
                        Dinge.MESSER.toString(), Dinge.TELLER.toString()};
        for (int i = 0; i < 10; i ++) {
            String value = Utils.getRandomEnumAsString(Dinge.class.getFields());
            System.out.println(value);
            assertTrue(Arrays.asList(expectedValues).contains(value));
        }
        for (int i = 0; i < 10; i ++) {
            String value = Utils.getRandomEnumAsEnum(Dinge.class.getFields());
            System.out.println(value);
            assertTrue(value.startsWith("Dinge."));
        }
    }

    @Test
    public void getFirstEnumAsEnumTest() {
        assertEquals("Dinge.FLASCHE", Utils.getFirstEnumAsEnum(Dinge.class.getFields()));
        assertEquals("FLASCHE", Utils.getFirstEnumAsString(Dinge.class.getFields()));
    }

    @Test
    public void removeSpacesTest() {
        assertEquals("noSpaces(Between)<-It", Utils.removeSpaces("no Spaces( Between )  <- It"));
    }

    @Test
    public void matchesIntFormatTest() {
        assertTrue(Utils.matchesIntFormat("1"));
        assertTrue(Utils.matchesIntFormat("-2"));
        assertFalse(Utils.matchesIntFormat("no"));
    }

    @Test
    public void matchesBooleanFormatTest() {
        assertTrue(Utils.matchesBooleanFormat("true"));
        assertTrue(Utils.matchesBooleanFormat("false"));
        assertFalse(Utils.matchesBooleanFormat("True"));
    }

    @Test
    public void matchesCharFormatTest() {
        assertTrue(Utils.matchesCharFormat("t"));
        assertTrue(Utils.matchesCharFormat("&"));
        assertTrue(Utils.matchesCharFormat("5"));
        assertFalse(Utils.matchesCharFormat("hg"));
    }

    @Test
    public void isPrimitiveTypeTest() {
        assertTrue(Utils.isPrimitiveType("int"));
        assertFalse(Utils.isPrimitiveType("Integer"));
        assertTrue(Utils.isPrimitiveType("long"));
        assertFalse(Utils.isPrimitiveType("Long"));
        assertFalse(Utils.isPrimitiveType("String"));
        assertTrue(Utils.isPrimitiveType("char"));
    }

    @Test
    public void isObjectTest() {
        assertTrue(Utils.isObject("Object"));
        assertTrue(Utils.isObject("java.util.Object"));
        assertFalse(Utils.isObject("String"));
    }

    @Test
    public void getAlphabetTest() {
        Alphabet[] alphabets = new Alphabet[] {Alphabet.LITTLE_LETTERS};
        String actual = Utils.getAlphabet(alphabets);
        String little = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(little, actual);

        alphabets = new Alphabet[] {Alphabet.BIG_LETTERS};
        actual = Utils.getAlphabet(alphabets);
        String big = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        assertEquals(big, actual);

        alphabets = new Alphabet[] {Alphabet.LITTLE_LETTERS, Alphabet.BIG_LETTERS};
        actual = Utils.getAlphabet(alphabets);
        assertEquals(little + big, actual);

        alphabets = new Alphabet[] {Alphabet.BIG_LETTERS, Alphabet.LITTLE_LETTERS};
        actual = Utils.getAlphabet(alphabets);
        assertEquals(big + little, actual);
    }

    @Test
    public void getRandomStringWithParametersTest() {
        for (int i = 0; i < 10; i++) {
            String alphabet = Utils.getAlphabet(new Alphabet[] {Alphabet.BIG_LETTERS, Alphabet.LITTLE_LETTERS});
            String result = Utils.getRandomString(5, 7, alphabet);
            System.out.println(result);
            assertTrue(result.length() >= 5);
            assertTrue(result.length() <= 7);
        }
    }

    @Test
    public void removeBracketsTest() {
        assertEquals("A string without brackets.", Utils.removeBrackets("A [string] with(out) {brackets}."));
    }

}