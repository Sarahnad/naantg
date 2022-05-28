package de.tudo.naantg.parser;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFileParserTest {

    private final String classStub = "package main.java.de.tudo.naantg.testproject.test.testGen;\n\n" +
            "import main.java.de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import org.junit.jupiter.api.Test;\n" +"" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n" +
            "class ErsteKlasseTests {\n" +"" +
            "}\n";

    private final String actMethod = "\t@Test\n"+
            "\tvoid whenAct_thenNoError() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tersteKlasse.act();\n" +
            "\t}\n";

    private final String fiveMethod = "\t@Test\n"+
            "\tvoid whenGetFive_thenReturn5() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tint actual = ersteKlasse.getFive();\n" +
            "\t}\n";

    private final String classWithAct = "package main.java.de.tudo.naantg.testproject.test.testGen;\n\n" +
            "import main.java.de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import org.junit.jupiter.api.Test;\n" +"" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n" +
            "class ErsteKlasseTests {\n" +"" +
            "\t@Test\n" +
            "\tvoid whenAct_thenNoError() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tersteKlasse.act();\n" +
            "\t}\n\n\n}";

    private final String classWithActAndFive = "package main.java.de.tudo.naantg.testproject.test.testGen;\n\n" +
            "import main.java.de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import org.junit.jupiter.api.Test;\n" +"" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n" +
            "class ErsteKlasseTests {\n" +"" +
            "\t@Test\n" +
            "\tvoid whenAct_thenNoError() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tersteKlasse.act();\n" +
            "\t}\n\n\n"+
            "\t@Test\n"+
            "\tvoid whenGetFive_thenReturn5() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tint actual = ersteKlasse.getFive();\n" +
            "\t}\n" +
            "\n\n}";

    private final String classWithMethodWithException =
            "package main.java.de.tudo.naantg.testproject.test.testGen;\n\n" +
            "import main.java.de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import org.junit.jupiter.api.Test;\n" +"" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n" +
            "class ErsteKlasseTests {\n" +"" +
            "\t@Test\n" +
            "\tvoid whenAct_thenNoError() throws Exception {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tersteKlasse.act();\n" +
            "\t}\n\n\n"+
            "\t@Test\n"+
            "\tvoid whenGetFive_thenReturn5() {\n" +
            "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n" +
            "\t\tint actual = ersteKlasse.getFive();\n" +
            "\t}\n" +
            "\n\n}";

    private final String classWithPrivateMethods = "package de.tudo.naantg.testproject.test.testGen;\n" +
            "\n" +
            "import de.tudo.naantg.testproject.DritteKlasse;\n" +
            "import de.tudo.naantg.testproject.weiter.Box;\n" +
            "import de.tudo.naantg.testproject.weiter.Komplex;\n" +
            "import de.tudo.naantg.testproject.weiter.Komplex3;\n" +
            "import de.tudo.naantg.testproject.weiter.Person;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n" +
            "\n" +
            "\n" +
            "class DritteKlasseTests /*implements DritteKlasseTG*/ {\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void whenGetKomplex_thenReturnNull() {\n" +
            "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
            "\n" +
            "\t\tKomplex actual = dritteKlasse.getKomplex();\n" +
            "\n" +
            "\t\tassert actual == null;\n" +
            "\t}\n" +
            "\n" +
            "\t\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue() {\n" +
            "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
            "\t\tList<Person> list = createNewPersonList();\n" +
            "\t\tList<Box> list2 = createNewBoxList();\n" +
            "\t\tKomplex3 param1 = new Komplex3(list, list2);\n" +
            "\n" +
            "\t\tdritteKlasse.setKomplex3(param1);\n" +
            "\n" +
            "\t\tKomplex3 actual = dritteKlasse.getKomplex3();\n" +
            "\t\tassertEquals(param1, actual);\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\tprivate List<Person> createNewPersonList() {\n" +
            "\t\tList<Person> list = new ArrayList<>();\n" +
            "\t\tString[] personStrs = new String[] {\"\", \"a1/z5+(UF5r6qYugE$mILUt9\"};\n" +
            "\t\tfor (String elem : personStrs) {\n" +
            "\t\t\tlist.add(new Person(elem));\n" +
            "\t\t}\n" +
            "\t\treturn list;\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\tprivate List<Box> createNewBoxList() {\n" +
            "\t\tList<Box> list2 = new ArrayList<>();\n" +
            "\t\tint[] boxIVars = new int[] {2, 2};\n" +
            "\t\tfor (int elem : boxIVars) {\n" +
            "\t\t\tlist2.add(new Box(elem));\n" +
            "\t\t}\n" +
            "\t\treturn list2;\n" +
            "\t}";

    private final String classWithMissingPrivateMethods = "package de.tudo.naantg.testproject.test.testGen;\n" +
            "\n" +
            "import de.tudo.naantg.testproject.DritteKlasse;\n" +
            "import de.tudo.naantg.testproject.weiter.Box;\n" +
            "import de.tudo.naantg.testproject.weiter.Komplex;\n" +
            "import de.tudo.naantg.testproject.weiter.Komplex3;\n" +
            "import de.tudo.naantg.testproject.weiter.Person;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n" +
            "\n" +
            "\n" +
            "class DritteKlasseTests /*implements DritteKlasseTG*/ {\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void whenGetKomplex_thenReturnNull() {\n" +
            "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
            "\n" +
            "\t\tKomplex actual = dritteKlasse.getKomplex();\n" +
            "\n" +
            "\t\tassert actual == null;\n" +
            "\t}\n" +
            "\n" +
            "\t\n" +
            "\n" +
            "\t@Test\n" +
            "\tpublic void whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue() {\n" +
            "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
            "\t\tList<Person> list = createNewPersonList();\n" +
            "\t\tList<Box> list2 = createNewBoxList();\n" +
            "\t\tKomplex3 param1 = new Komplex3(list, list2);\n" +
            "\n" +
            "\t\tdritteKlasse.setKomplex3(param1);\n" +
            "\n" +
            "\t\tKomplex3 actual = dritteKlasse.getKomplex3();\n" +
            "\t\tassertEquals(param1, actual);\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\tprivate List<Person> createNewPersonList() {\n" +
            "\t\tList<Person> list = new ArrayList<>();\n" +
            "\t\tString[] personStrs = new String[] {\"\", \"a1/z5+(UF5r6qYugE$mILUt9\"};\n" +
            "\t\tfor (String elem : personStrs) {\n" +
            "\t\t\tlist.add(new Person(elem));\n" +
            "\t\t}\n" +
            "\t\treturn list;\n" +
            "\t}\n" +
            "\n" +
            "\n" +
            "\t}";

    @Test
    public void addMethodTest() {
        String content = classStub;
        String method = actMethod;
        String methodName = "whenAct_thenNoError";
        String expected = classWithAct;

        String result = TestFileParser.addMethod(methodName, method, content);
        assertEquals(expected, result);

        // add next Method
        method = fiveMethod;
        methodName = "whenGetFive_thenReturn5";
        expected = classWithActAndFive;

        result = TestFileParser.addMethod(methodName, method, result+"\n");
        assertEquals(expected, result);
    }

    @Test
    public void methodExistsTest() {
        String methodName = "whenAct_thenNoError";
        assertFalse(TestFileParser.methodExists(methodName, classStub));
        assertTrue(TestFileParser.methodExists(methodName, classWithAct));
        assertTrue(TestFileParser.methodExists(methodName, classWithMethodWithException));
    }

    @Test
    public void privateMethodExistsTest() {
        String publicMethodName = "whenGetKomplex_thenReturnNull";
        String privateMethodName = "createNewBoxList";
        assertTrue(TestFileParser.methodExists(publicMethodName, classWithPrivateMethods));
        assertTrue(TestFileParser.methodExists(privateMethodName, classWithPrivateMethods));
        assertFalse(TestFileParser.methodExists(privateMethodName, classWithMissingPrivateMethods));
    }

    @Test
    public void importExistsTest() {
        String importName = "de.tudo.naantg.testproject.DritteKlasse";
        String importName2 = "main.java.de.tudo.naantg.testproject.ErsteKlasse";
        assertFalse(TestFileParser.importExists(importName, classStub));
        assertTrue(TestFileParser.importExists(importName2, classStub));
    }

    @Test
    public void findFirstPositionTest() {
        List<String> list = Arrays.asList("hello", "de.tudo", "de.tudo.naantg", "main.java");
        assertEquals(1, TestFileParser.findFirstPosition(list, "de"));

        list = Arrays.asList("hello", "tudo", "de.tudo.naantg", "main.java");
        assertEquals(2, TestFileParser.findFirstPosition(list, "de"));

        list = Arrays.asList("hello", "main.java");
        assertEquals(-1, TestFileParser.findFirstPosition(list, "de"));
    }

    @Test
    public void findLastPositionTest() {
        List<String> list = Arrays.asList("hello", "de.tudo", "de.tudo.naantg", "main.java");
        assertEquals(2, TestFileParser.findLastPosition(list, "de"));

        list = Arrays.asList("hello", "tudo", "de.evo", "de.tudo.naantg");
        assertEquals(3, TestFileParser.findLastPosition(list, "de"));

        list = Arrays.asList("hello", "tudo", "es.de.evo", "de.tudo.naantg");
        assertEquals(3, TestFileParser.findLastPosition(list, "de"));

        list = Arrays.asList("de.evo", "tudo.naantg");
        assertEquals(0, TestFileParser.findLastPosition(list, "de"));

        list = Arrays.asList("hello", "main.java");
        assertEquals(-1, TestFileParser.findLastPosition(list, "de"));
    }

    @Test
    public void importAddTest() {
        String importName = "main.java.de.tudo.naantg.testproject.DritteKlasse";
        String importName2 = "main.java.de.tudo.naantg.testproject.ErsteKlasse";
        String result = "package main.java.de.tudo.naantg.testproject.test.testGen;\n\n" +
                "import main.java.de.tudo.naantg.testproject.DritteKlasse;\n" +
                "import main.java.de.tudo.naantg.testproject.ErsteKlasse;\n" +
                "import org.junit.jupiter.api.Test;\n" +"" +
                "import static org.junit.jupiter.api.Assertions.*;\n\n" +
                "class ErsteKlasseTests {\n" +"" +
                "}\n";
        assertEquals(result, TestFileParser.addImport(importName,
                "import " + importName + ";\n", classStub));

        assertEquals(classStub, TestFileParser.addImport(importName2,
                "import " + importName + ";\n", classStub));

    }

}