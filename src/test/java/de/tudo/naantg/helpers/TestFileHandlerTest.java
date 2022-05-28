package de.tudo.naantg.helpers;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFileHandlerTest {

    private final String content = "package de.tudo.naantg.testproject.test.gen.testGen;\n\n" +
            "import de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n\n" +
            "class ErsteKlasseTests {\n\n\n" +
            "}\n";

    private final String contentForImportTest = "package de.tudo.naantg.testproject.test.gen.testGen;\n\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n\n" +
            "class ErsteKlasseTests {\n\n\n" +
            "}\n";

    private final String contentForImportTest_2 = "package de.tudo.naantg.testproject.test.gen.testGen;\n\n" +
            "import de.tudo.naantg.testproject.DritteKlasse;\n" +
            "import de.tudo.naantg.testproject.ErsteKlasse;\n" +
            "import de.tudo.naantg.testproject.ZweiteKlasse;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.List;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "import static org.junit.jupiter.api.Assertions.*;\n\n\n" +
            "class ErsteKlasseTests {\n\n\n" +
            "}\n";


    private void createTestFile(String content) {
        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath("src/main/java/de/tudo/naantg/testproject/test/gen");
        testFileHandler.createTestFile("ErsteKlasseTests", content);
    }

    @Test
    public void readTestFile() {
        createTestFile(content);
        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath("src/main/java/de/tudo/naantg/testproject/test/gen/testGen");

        StringBuilder actual = testFileHandler.readTestFile("ErsteKlasseTests");
        assertEquals(content, actual.toString());

        assertTrue(testFileHandler.deleteTestFile("ErsteKlasseTests"));
        assertTrue(testFileHandler.deleteFolder());
    }

    @Test
    public void writeImportsTest() {
        List<String> imports = new ArrayList<>();
        imports.add("import de.tudo.naantg.testproject.ErsteKlasse;\n");

        createTestFile(contentForImportTest);
        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath("src/main/java/de/tudo/naantg/testproject/test/gen/testGen");

        testFileHandler.writeImports("ErsteKlasseTests", imports);
        StringBuilder actual = testFileHandler.readTestFile("ErsteKlasseTests");
        assertEquals(content, actual.toString());

        imports.add("import de.tudo.naantg.testproject.ErsteKlasse;\n");
        imports.add("import de.tudo.naantg.testproject.ZweiteKlasse;\n");
        imports.add("import de.tudo.naantg.testproject.DritteKlasse;\n");
        imports.add("import java.util.List;\n");
        imports.add("import java.util.List;\n");
        imports.add("import java.util.ArrayList;\n");

        testFileHandler.writeImports("ErsteKlasseTests", imports);
        actual = testFileHandler.readTestFile("ErsteKlasseTests");
        assertEquals(contentForImportTest_2, actual.toString());

        assertTrue(testFileHandler.deleteTestFile("ErsteKlasseTests"));
        assertTrue(testFileHandler.deleteFolder());
    }

    @Test
    public void containsTestCaseTest() {
        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath("src/test/java/de/tudo/naantg/resources/testGen");

        boolean found = testFileHandler.containsTestCase("ErsteKlasseTests", "whenGetFivePointSix_thenReturnValue_1");
        assertTrue(found);

        found = testFileHandler.containsTestCase("ErsteKlasseTests", "whenGetFivePointSix_thenNotFound");
        assertFalse(found);
    }
}