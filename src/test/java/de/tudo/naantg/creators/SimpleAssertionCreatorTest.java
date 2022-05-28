package de.tudo.naantg.creators;

import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.MethodNameParser;
import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.scheinboot.Hauptschein;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinJob;
import de.tudo.naantg.testproject.test.*;
import de.tudo.naantg.testproject.weiter.Box;
import de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse;
import de.tudo.naantg.testproject.weiter.Komplex;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleAssertionCreatorTest {

    @Test
    public void testAssertions() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        GeneratorModel generatorModel = simpleModelCreator.createGeneratorModel(ErsteKlasseTG.class);
        testReturnAssertionForErsteKlasseTG(generatorModel);

        generatorModel = simpleModelCreator.createGeneratorModel(ZweiteKlasseTG.class);
        testReturnAssertionForZweiteKlasseTG(generatorModel);

        generatorModel = simpleModelCreator.createGeneratorModel(InnereKlasseTG.class);
        testReturnAssertionForInnereKlasseTG(generatorModel);

        generatorModel = simpleModelCreator.createGeneratorModel(NochEineKlasseTG.class);
        testReturnAssertionForNochEineKlasseTG(generatorModel);

    }

    private void testReturnAssertionForErsteKlasseTG(GeneratorModel generatorModel) {
        testReturnAssertion(generatorModel, "whenGetFive_thenReturn5", "5", AssertType.EQUALS);
        List<Assertion> assertions = generatorModel.getTestMethodModel("whenAct_thenNoError").getAssertions();
        assertTrue(assertions.isEmpty());
        testReturnAssertion(generatorModel, "whenGetFive_thenReturned_isGreater4", "4", AssertType.GREATER);
        testReturnAssertion(generatorModel, "whenGetFive_thenReturned_isSmaller6", "6", AssertType.SMALLER);
        testReturnAssertion(generatorModel, "whenGetFivePointSix_thenReturn5p6", "5.6", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetFivePointSix_thenReturnValue", "0.0", AssertType.NOTEQUALS);
        testReturnAssertion(generatorModel, "whenGetFivePointSix_thenReturnValue_1", "5.6", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetFive_thenReturnValue", "0", AssertType.NOTEQUALS);
        testReturnAssertion(generatorModel, "whenGetFive_thenReturnValue_1", "5", AssertType.EQUALS);
    }

    private void testReturnAssertionForZweiteKlasseTG(GeneratorModel generatorModel) {
        testReturnAssertion(generatorModel, "whenGetText_thenReturnValue", "\"simple Method\"", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetText_thenReturnValue_1", "\"\"", AssertType.NOTEQUALS);
        testReturnAssertion(generatorModel, "whenGetCharS_thenReturn_s", "'s'", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetCharS_thenReturnValue", "'s'", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetCharS_thenReturnValue_1", "0", AssertType.GREATER);
        testReturnAssertion(generatorModel, "whenGetShortNegTen_thenReturnNeg10", "-10", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetAFloat_thenReturnNeg10p5", "-10.5", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetLongMillion_thenReturn1000000", "1000000", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetOneByte_thenReturn7", "7", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenGetAFloat_thenReturnValue", "0.0", AssertType.NOTEQUALS);
        testReturnAssertion(generatorModel, "whenGetAFloat_thenReturnValue_1", "-10.5", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenNoText_thenReturnNull", "null", AssertType.EQ);
    }

    private void testReturnAssertionForInnereKlasseTG(GeneratorModel generatorModel) {
        testReturnAssertion(generatorModel, "whenIsNice_thenReturnTrue", "True", AssertType.TRUE);
        testReturnAssertion(generatorModel, "whenIsNice_thenReturnValue", "true", AssertType.TRUE);
        testReturnAssertion(generatorModel, "whenIsNice_thenReturnValue_1", "true", AssertType.EQUALS);
    }

    private void testReturnAssertionForNochEineKlasseTG(GeneratorModel generatorModel) {
        testReturnAssertion(generatorModel, "whenSum_thenReturnValue", "expected", AssertType.EQUALS);
        testReturnAssertion(generatorModel, "whenHaveFun_thenReturnValue", "expected", AssertType.EQUALS);
    }

    private void testReturnAssertion(GeneratorModel generatorModel, String method, String expected, AssertType assertType) {
        Assertion assertion = generatorModel.getTestMethodModel(method).getAssertions().get(0);
        assertEquals("actual", assertion.getActual());
        assertEquals(expected, assertion.getExpected());
        assertEquals(assertType, assertion.getAssertType());
    }

    @Test
    public void testReturnAssertionForKlasseMitInnererKlasseTG() throws NoSuchMethodException {
        String testName = "whenGetNames_thenReturnList";
        String expected = "assertFalse(actual.isEmpty())";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_thenReturned_size_is10";
        expected = "assertEquals(10, actual.size())";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_thenReturned_size_isGreater9_and_size_isSmaller11";
        expected = "assertTrue(actual.size() > 9)";
        String expected2 = "assertTrue(actual.size() < 11)";
        calculateListAssertionTests(testName, "String", false, expected, expected2);

        testName = "whenGetNames_thenReturned_size_is10_or_is20";
        expected = "assertTrue(actual.size() == 10 || actual.size() == 20)";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_thenReturned_get_5_isName5";
        expected = "assertEquals(\"Name5\", actual.get(5))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetBooleans_thenReturned_get8_isTrue";
        expected = "assertTrue(actual.get(8))";
        calculateListAssertionTests(testName, "boolean", false, expected);

        testName = "whenGetNames_thenReturned_get6_is_Name6_and_get_9_is_Name9";
        expected = "assertEquals(\"Name6\", actual.get(6))";
        expected2 = "assertEquals(\"Name9\", actual.get(9))";
        calculateListAssertionTests(testName, "String", false, expected, expected2);

        testName = "whenGetBooleans_thenReturned_get4_isTrue_and_get7_isFalse";
        expected = "assertTrue(actual.get(4))";
        expected2 = "assertFalse(actual.get(7))";
        calculateListAssertionTests(testName, "boolean", false, expected, expected2);

        testName = "whenGetNames_thenReturned_get1_isName1_or_get1_isName2";
        expected = "assertTrue(actual.get(1).equals(\"Name1\") || actual.get(1).equals(\"Name2\"))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetBooleans_thenReturned_get5_isTrue_or_get7_isFalse";
        expected = "assertTrue(actual.get(5) || !actual.get(7))";
        calculateListAssertionTests(testName, "boolean", false, expected);

        testName = "whenGetNames_thenReturned_contains_Name0";
        expected = "assertTrue(actual.contains(\"Name0\"))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNumbers_thenReturned_contains_8";
        expected = "assertTrue(actual.contains(8))";
        calculateListAssertionTests(testName, "int", false, expected);

        testName = "whenGetDoubles_thenReturned_contains_5p5";
        expected = "assertTrue(actual.contains(5.5))";
        calculateListAssertionTests(testName, "double", false, expected);

        testName = "whenGetList_thenReturned_isEmpty";
        expected = "assertTrue(actual.isEmpty())";
        calculateListAssertionTests(testName, "int", false, expected);

        testName = "whenGetNames_thenReturned_size_isNot5";
        expected = "assertNotEquals(5, actual.size())";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNumbers_thenReturned_notContains_neg7";
        expected = "assertFalse(actual.contains(-7))";
        calculateListAssertionTests(testName, "int", false, expected);

        testName = "whenGetDoubles_thenReturned_notContains_neg5p5";
        expected = "assertFalse(actual.contains(-5.5))";
        calculateListAssertionTests(testName, "double", false, expected);

        testName = "whenGetIntArray_thenReturned_get2_isNot_neg3";
        expected = "assertNotEquals(-3, actual[2])";
        calculateListAssertionTests(testName, "int", true, expected);

        testName = "whenGetCollection_with5_thenReturned_size_is5";
        expected = "assertEquals(5, actual.size())";
        calculateListAssertionTests(testName, "double", false, expected);
    }

    @Test
    public void calculateAnnotationAssertionsTest() throws NoSuchMethodException {
        String testName = "whenGetNames_test1";
        String expected = "assertEquals(10, actual.size())";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_test2";
        expected = "assertTrue(actual.size() > 9)";
        String expected2 = "assertTrue(actual.size() < 11)";
        calculateListAssertionTests(testName, "", false, expected, expected2);

        testName = "whenGetNames_test3";
        expected = "assertTrue(actual.size() == 10 || actual.size() == 20)";
        calculateListAssertionTests(testName, "", false, expected);

        testName = "whenGetNames_test33";
        expected = "assertTrue(actual.size() == 5 || actual.size() == 20 || actual.size() == 10)";
        calculateListAssertionTests(testName, "", false, expected);

        testName = "whenGetNames_test34";
        expected = "assertNotEquals(5, actual.size())";
        calculateListAssertionTests(testName, "", false, expected);

        testName = "whenGetNames_test4";
        expected = "assertEquals(\"Name5\", actual.get(5))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_test5";
        expected = "assertEquals(\"Name6\", actual.get(6))";
        expected2 = "assertEquals(\"Name9\", actual.get(9))";
        calculateListAssertionTests(testName, "String", false, expected, expected2);

        testName = "whenGetNames_test6";
        expected = "assertTrue(actual.get(1).equals(\"Name1\") || actual.get(1).equals(\"Name2\"))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNames_test7";
        expected = "assertTrue(actual.contains(\"Name0\"))";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetList_test1";
        expected = "assertTrue(actual.isEmpty())";
        calculateListAssertionTests(testName, "String", false, expected);

        testName = "whenGetNumbers_test1";
        expected = "assertTrue(actual.contains(8))";
        calculateListAssertionTests(testName, "int", false, expected);

        testName = "whenGetDoubles_test1";
        expected = "assertTrue(actual.contains(5.5))";
        calculateListAssertionTests(testName, "double", false, expected);

        testName = "whenGetBooleans_test1";
        expected = "assertTrue(actual.get(4))";
        expected2 = "assertFalse(actual.get(7))";
        calculateListAssertionTests(testName, "boolean", false, expected, expected2);

        testName = "whenGetBooleans_test2";
        expected = "assertTrue(actual.get(5) || !actual.get(7))";
        calculateListAssertionTests(testName, "boolean", false, expected);

        testName = "whenGetBooleans_test3";
        expected = "assertTrue(actual.get(8))";
        calculateListAssertionTests(testName, "boolean", false, expected);

        testName = "whenGetIntArray_test1";
        expected = "assertEquals(7, actual.length)";
        expected2 = "assertTrue(Arrays.stream(actual).anyMatch(x -> x == 7))";
        calculateListAssertionTests(testName, "int", true, expected, expected2);

        testName = "whenGetIntArray_test2";
        expected = "assertEquals(3, actual[2])";
        calculateListAssertionTests(testName, "int", true, expected);
    }

    private SimpleAssertionCreator initCreator(String testName, boolean isArray, String type) {
        GeneratorModel model = new GeneratorModel();
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        model.setAssertions(new ArrayList<>(), testName);
        model.setMethodOfCUT(new MethodModel(), testName);
        if (isArray) model.getMethodOfCUT(testName).setReturnType(type + "[]");
        else model.getMethodOfCUT(testName).setReturnType("List");
        model.getMethodOfCUT(testName).getGenerics().add(type);
        return simpleAssertionCreator;
    }

    @Test
    public void calucalteAssertionTest() throws NoSuchMethodException {
        String testName = "whenSetKomplex_thenGetKomplex_hasEqualValue";
        GeneratorModel model = new GeneratorModel();
        model.setTestClassModel(new TestClassModel());
        model.setAssertions(new ArrayList<>(), testName);
        model.setMethodOfCUT(new MethodModel(), testName);
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator("", "");
        simpleModelCreator.setModel(model);
        model.setCut(DritteKlasse.class);
        model.setTestClassModel(new TestClassModel());
        model.getTestClassModel().setImports(new ArrayList<>());
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(model);
        Method method = DritteKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(testName);
        simpleModelCreator.calculateObjects(method);

        simpleAssertionCreator.calculateAssertions(method);

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tKomplex actual = dritteKlasse.getKomplex();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        Assertion assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertEquals(param1, actual);\n", assertion.generateAssertion());
    }

    private void calculateListAssertionTests(String testName, String type,
                                         boolean isArray, String... expectedStatement) throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = initCreator(testName, isArray, type);
        Method method = KlasseMitInnererKlasseTG.class.getMethod(testName);
        simpleAssertionCreator.calculateAssertions(method);

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(expectedStatement.length + 1, assertions.size());
        int i = 1;
        for (String expected : expectedStatement) {
            assertEquals("\t\t" + expected + ";\n", assertions.get(i).generateAssertion());
            i++;
        }
    }

    @Test
    public void testArrayAssertions() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        GeneratorModel generatorModel = simpleModelCreator.createGeneratorModel(KlasseMitInnererKlasseTG.class);
        testArrayAssertionForKlasseMitInnererKlasseTG(generatorModel);
    }

    private void testArrayAssertionForKlasseMitInnererKlasseTG(GeneratorModel generatorModel) {
        testReturnMultiAssertions(generatorModel,
                "whenGetIntArray_thenReturned_size_is7_and_contains_7",
                "actual,actual.length,Arrays.stream(actual).anyMatch(x -> x == 7)", "null,7,*",
                AssertType.NEQ, AssertType.EQUALS, AssertType.TRUE);

        testReturnMultiAssertions(generatorModel,
                "whenGetIntArray_thenReturned_get2_is3",
                "actual,actual[2]", "null,3",
                AssertType.NEQ, AssertType.EQUALS);

        testReturnMultiAssertions(generatorModel,
                "whenSetSeven_with_7_values_thenGetSeven_hasValues",
                "actual.length", "0",
                AssertType.NOTEQUALS);

        testReturnMultiAssertions(generatorModel,
                "whenSetSeven_with_7_values_thenGetSeven_hasEqualValues",
                "actual", "param1",
                AssertType.ARRAY_EQUALS);

        testReturnMultiAssertions(generatorModel,
                "whenSetAnimals_thenGetAnimals_hasValues",
                "actual.isEmpty()", "",
                AssertType.FALSE);

        testReturnMultiAssertions(generatorModel,
                "whenSetAnimals_thenGetAnimals_hasEqualValues",
                "actual", "param1",
                AssertType.EQUALS);

        testReturnMultiAssertions(generatorModel,
                "whenSetIndex_with55_thenGetIndex_hasValue55",
                "actual", "55",
                AssertType.EQUALS);
    }

    private void testReturnMultiAssertions(GeneratorModel generatorModel, String method,
                                           String actuals, String expecteds, AssertType... assertTypes) {
        List<Assertion> assertions = generatorModel.getTestMethodModel(method).getAssertions();
        assert  assertions != null;
        String[] actualsSplitted = actuals.split(",");
        String[] expectedSplitted = expecteds.split(",");
        for (int i = 0; i < assertions.size(); i++) {
            assertEquals(actualsSplitted[i].replace("*", ""), assertions.get(i).getActual());
            assertEquals(expectedSplitted[i].replace("*", ""), assertions.get(i).getExpected());
            assertEquals(assertTypes[i], assertions.get(i).getAssertType());
        }

    }

    @Test
    public void calculateIsAssertionTest() {
        List<Assertion> assertions = new ArrayList<>();
        testAssertionStatements("5", "int", false, false,
                "assertEquals(5, actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("3", "int", false, true,
                "assertNotEquals(3, actual)", assertions, true);
        testAssertionStatements("5", "int", true, false,
                "assertTrue(actual != 3 || actual == 5)", assertions, true);
        testAssertionStatements("7", "int", true, true,
                "assertTrue(actual != 3 || actual == 5 || actual != 7)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("true", "boolean", false, false,
                "assertTrue(actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("false", "boolean", false, false,
                "assertFalse(actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("true", "boolean", false, true,
                "assertFalse(actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("false", "boolean", false, true,
                "assertTrue(actual)", assertions, true);
        testAssertionStatements("true", "boolean", true, false,
                "assertTrue(actual || actual)", assertions, true);
        testAssertionStatements("false", "boolean", true, true,
                "assertTrue(actual || actual || actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("true", "boolean", false, true,
                "assertFalse(actual)", assertions, true);
        testAssertionStatements("true", "boolean", true, false,
                "assertTrue(!actual || actual)", assertions, true);
        testAssertionStatements("false", "boolean", true, true,
                "assertTrue(!actual || actual || actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("true", "boolean", false, true,
                "assertFalse(actual)", assertions, true);
        testAssertionStatements("false", "boolean", true, false,
                "assertTrue(!actual || !actual)", assertions, true);
        testAssertionStatements("false", "boolean", true, true,
                "assertTrue(!actual || !actual || actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("\"Text\"", "String", false, false,
                "assertEquals(\"Text\", actual)", assertions, true);

        assertions = new ArrayList<>();
        testAssertionStatements("\"Text\"", "String", false, true,
                "assertNotEquals(\"Text\", actual)", assertions, true);
    }

    @Test
    public void calculateCompareAssertionTest() {
        List<Assertion> assertions = new ArrayList<>();
        testAssertionStatements("5", "int", false, false,
                "assertTrue(actual < 5)", assertions, false);

        assertions = new ArrayList<>();
        testAssertionStatements("3", "int", false, true,
                "assertTrue(actual > 3)", assertions, false);
        testAssertionStatements("5", "int", true, false,
                "assertTrue(actual > 3 || actual < 5)", assertions, false);
        testAssertionStatements("7", "int", true, true,
                "assertTrue(actual > 3 || actual < 5 || actual > 7)", assertions, false);
    }

    private void testAssertionStatements(String part, String type, boolean isOr, boolean isNotOrGreater, String expected,
                                         List<Assertion> assertions, boolean isAssertion) {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        String actual = "actual";
        if (isAssertion) simpleAssertionCreator.calculateIsAssertion(assertions, type, actual, part, isOr, isNotOrGreater);
        else simpleAssertionCreator.calculateCompareAssertion(assertions, actual, part, isOr, isNotOrGreater);
        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());
    }

    @Test
    public  void calculateSizeAssertionTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());

        List<Assertion> assertions = new ArrayList<>();
        String expected = "assertEquals(10, actual.size())";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, true, false, false,
                false, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertNotEquals(10, actual.size())";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, true, false,
                false, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertEquals(10, actual.length)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, true, false, false,
                false, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertNotEquals(10, actual.length)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, true, false,
                false, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.size() > 10)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, false, false,
                true, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.size() < 10)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, false, false,
                false, true, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.length > 10)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, false, false,
                true, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.length < 10)";
        simpleAssertionCreator.calculateSizeAssertion("10", assertions, false, false, false,
                false, true, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        simpleAssertionCreator.calculateSizeAssertion("-10", assertions, false, false, false,
                false, true, true);
        assertTrue(assertions.isEmpty());

        assertions = new ArrayList<>();
        simpleAssertionCreator.calculateSizeAssertion("1.0", assertions, false, false, false,
                false, true, true);
        assertTrue(assertions.isEmpty());

        assertions = new ArrayList<>();
        simpleAssertionCreator.calculateSizeAssertion("hello", assertions, false, false, false,
                false, true, true);
        assertTrue(assertions.isEmpty());
    }

    @Test
    public void calculateGetAssertionTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());

        List<Assertion> assertions = new ArrayList<>();
        String expected = "assertEquals(6, actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertNotEquals(6, actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, true,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.get(0) > 6)";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, false,
                true, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.get(0) < 6)";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, false,
                false, true, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertEquals(6, actual[0])";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, true, false,
                false, false, true, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertNotEquals(6, actual[0])";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, true,
                false, false, true, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual[0] > 6)";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, false,
                true, false, true, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual[0] < 6)";
        simpleAssertionCreator.calculateGetAssertion("6", "int", assertions, false, false, false,
                false, true, true, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertEquals(\"hello\", actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("hello", "String", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("true", "boolean", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertFalse(actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("false", "boolean", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertEquals(\"77\", actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("77", "String", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertEquals(\"false\", actual.get(0))";
        simpleAssertionCreator.calculateGetAssertion("false", "String", assertions, false, true, false,
                false, false, false, "0");

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());
    }

    @Test
    public  void calculateContainsAssertionTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());

        List<Assertion> assertions = new ArrayList<>();
        String expected = "assertTrue(actual.contains(3))";
        simpleAssertionCreator.calculateContainsAssertion("3", "int", assertions, false, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.contains(\"children\"))";
        simpleAssertionCreator.calculateContainsAssertion("children", "String", assertions, false, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(actual.contains(true))";
        simpleAssertionCreator.calculateContainsAssertion("true", "boolean", assertions, false, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertFalse(actual.contains(2.0))";
        simpleAssertionCreator.calculateContainsAssertion("2p0", "float", assertions, true, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertFalse(actual.contains(-2.0))";
        simpleAssertionCreator.calculateContainsAssertion("Neg2p0", "double", assertions, true, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertFalse(actual.contains(-2.0))";
        simpleAssertionCreator.calculateContainsAssertion("-2.0", "double", assertions, true, false, false);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(Arrays.stream(actual).anyMatch(x -> x == 1))";
        simpleAssertionCreator.calculateContainsAssertion("1", "short", assertions, false, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertFalse(Arrays.stream(actual).anyMatch(x -> x == 1))";
        simpleAssertionCreator.calculateContainsAssertion("1", "long", assertions, true, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(Arrays.asList(actual).contains(\"yes!\"))";
        simpleAssertionCreator.calculateContainsAssertion("yes!", "String", assertions, false, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());

        assertions = new ArrayList<>();
        expected = "assertTrue(Arrays.asList(actual).contains(\"1\"))";
        simpleAssertionCreator.calculateContainsAssertion("1", "String", assertions, false, false, true);

        assertEquals(1, assertions.size());
        assertEquals("\t\t" + expected + ";\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void createSetAndGetAssertionTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        simpleAssertionCreator.getModel().setCut(KlasseMitInnererKlasse.class);
        ObjectModel parameter = new ObjectModel(int[].class, "param1", true);
        parameter.getGenericClasses().add(int.class);
        ObjectModel object = new ObjectModel();
        object.setIdentifier("klasseMitInnererKlasse");

        String testName = "whenSetSeven_thenGetSeven_hasEqualValues";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getSeven", true, testName, true);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tint[] actual = klasseMitInnererKlasse.getSeven();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        Assertion assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertArrayEquals(param1, actual);\n", assertion.generateAssertion());

        testName = "whenSetSeven_thenGetSeven_hasValues";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getSeven", false, testName, true);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tint[] actual = klasseMitInnererKlasse.getSeven();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertNotEquals(0, actual.length);\n",
                assertion.generateAssertion());

        simpleAssertionCreator.getModel().getParameters(testName).get(0).setDataType("ArrayList");
        simpleAssertionCreator.getModel().getParameters(testName).get(0).getGenericClasses().clear();
        simpleAssertionCreator.getModel().getParameters(testName).get(0).getGenericClasses().add(String.class);
        testName = "whenSetAnimals_thenGetAnimals_hasValues";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getAnimals", false, testName, true);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tArrayList<String> actual = klasseMitInnererKlasse.getAnimals();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertFalse(actual.isEmpty());\n",
                assertion.generateAssertion());

        testName = "whenSetAnimals_thenGetAnimals_hasEqualValues";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getAnimals", true, testName, true);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tArrayList<String> actual = klasseMitInnererKlasse.getAnimals();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertEquals(param1, actual);\n",
                assertion.generateAssertion());

        simpleAssertionCreator.getModel().getParameters(testName).get(0).setDataType("int");
        testName = "whenSetIndex_thenGetIndex_hasEqualValue";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getIndex", true, testName, false);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tint actual = klasseMitInnererKlasse.getIndex();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertEquals(param1, actual);\n",
                assertion.generateAssertion());

        testName = "whenSetIndex_thenGetIndex_hasValue55";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getIndex", "55", false, testName, false);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tint actual = klasseMitInnererKlasse.getIndex();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertEquals(55, actual);\n",
                assertion.generateAssertion());
    }

    @Test
    public void createSetAndGetAssertionObjectsTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        simpleAssertionCreator.getModel().setCut(DritteKlasse.class);
        ObjectModel parameter = new ObjectModel(Komplex.class, "param1", true);
        ObjectModel object = new ObjectModel();
        object.setIdentifier("dritteKlasse");

        String testName = "whenSetKomplex_with_2_values_thenGetKomplex_hasEqualValue";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.createSetAndGetAssertion("getKomplex", true, testName, true);

        assertNotNull(simpleAssertionCreator.getModel().getActualObject(testName));
        assertEquals("\t\tKomplex actual = dritteKlasse.getKomplex();\n",
                simpleAssertionCreator.getModel().getActualObject(testName).generateObjectStatement());
        Assertion assertion = simpleAssertionCreator.getModel().getAssertions(testName).get(0);
        assertEquals("\t\tassertEquals(param1, actual);\n", assertion.generateAssertion());
    }

    @Test
    public void calculateObjectReturnAssertionsTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String methodName = "whenGetKomplex_thenReturnNull";
        List<Assertion> assertions = new ArrayList<>();
        assertions.add(new Assertion("", "actual", null));
        simpleAssertionCreator.calculateObjectReturnAssertions(MethodNameParser.getReturnAssertion(methodName), assertions);
        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassert actual == null;\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void calculateOptionalReturnAssertionsTest() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        List<Assertion> assertions = new ArrayList<>();
        assertions.add(new Assertion("", "actual", null));
        List<String> expected = Arrays.asList("RETURN", "VALUE");
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        Method method = ScheinRepositoryTG.class.getMethod(testName);

        simpleAssertionCreator.calculateOptionalReturnAssertions(expected, assertions, method);

        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());

        assertions.clear();
        assertions.add(new Assertion("", "actual", null));
        expected = Arrays.asList("RETURNED", "EMPTY");

        simpleAssertionCreator.calculateOptionalReturnAssertions(expected, assertions, method);

        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertFalse(actual.isPresent());\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void calculateReturnAssertionsTestWithOptional() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        Method method = ScheinRepositoryTG.class.getMethod(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        List<String> expected = Arrays.asList("RETURN", "VALUE");

        simpleAssertionCreator.calculateReturnAssertions(method, expected);

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());

        testName = "whenFindByScheinId_withScheinEntity_thenReturnNull";
        method = ScheinRepositoryTG.class.getMethod(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        expected = Arrays.asList("RETURN", "NULL");

        simpleAssertionCreator.calculateReturnAssertions(method, expected);

        assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertFalse(actual.isPresent());\n", assertions.get(0).generateAssertion());

        testName = "whenFindByScheinId_withScheinEntity_thenReturnNotNull";
        method = ScheinRepositoryTG.class.getMethod(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        expected = Arrays.asList("RETURN", "NOT_NULL");

        simpleAssertionCreator.calculateReturnAssertions(method, expected);

        assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());

        testName = "whenFindByScheinId_withScheinEntity_thenReturned_isEmpty";
        method = ScheinRepositoryTG.class.getMethod(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        expected = Arrays.asList("RETURNED", "EMPTY");

        simpleAssertionCreator.calculateReturnAssertions(method, expected);

        assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertFalse(actual.isPresent());\n", assertions.get(0).generateAssertion());

        testName = "whenTest_thenReturnList";
        method = HauptscheinTG.class.getMethod(testName);
        methodModel.getGenerics().clear();
        methodModel.getGenerics().add("List");
        simpleAssertionCreator.getModel().getParameters(testName).add(new ObjectModel("List", "list", true));
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        expected = Arrays.asList("RETURN", StateKey.LIST.toString());

        simpleAssertionCreator.calculateReturnAssertions(method, expected);

        assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(2, assertions.size());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());
        assertEquals("\t\tassertFalse(actual.get().isEmpty());\n", assertions.get(1).generateAssertion());
    }

    @Test
    public void calculateExceptionTest() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("String");
        String testName = "whenTest_thenReturnException";
        Method method = HauptscheinTG.class.getMethod(testName);
        Method toTest = Hauptschein.class.getMethod("calc", String.class, String.class);
        methodModel.setMethodToTest(toTest);
        methodModel.setName("calc");
        methodModel.setParameters(toTest.getParameterTypes());
        methodModel.setArgList(new String[] {"param1", "param2"});
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        List<String> expected = Arrays.asList("RETURN", "EXCEPTION");

        simpleAssertionCreator.calculateException(method, expected);

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertFalse(assertions.isEmpty());
        assertEquals(AssertType.THROWS, assertions.get(0).getAssertType());
        assertEquals("ScheinEntityException.class", assertions.get(0).getExpected());
        assertEquals("() -> {\n\t\t\t\tcalc(param1, param2);\n\t\t\t}", assertions.get(0).getActual());
        assertTrue(simpleAssertionCreator.getModel().getTestClassModel().getImports()
                .contains("de.tudo.naantg.testproject.scheinboot.ScheinEntityException"));

    }

    @Test
    public void calculateOptionalObjectMapAssertionTestWithObject() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenTest_thenReturnedObject_isValue";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("Box");
        methodModel.setReturnObject(new ObjectModel(Box.class, "", false));
        List<Assertion> assertions = new ArrayList<>();
        Assertion assertion = new Assertion();
        assertions.add(assertion);

        simpleAssertionCreator.calculateOptionalObjectMapAssertion("1", "Box", assertions, false,
                true, false, false, false, "boxId");

        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertEquals(1, actual.getBoxId());\n", assertions.get(1).generateAssertion());
    }

    @Test
    public void calculateOptionalObjectMapAssertionTestWithOptional() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenTest_thenReturnedOptional_isValue";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("Optional");
        methodModel.setReturnObject(new ObjectModel(Optional.class, "", false));
        methodModel.getGenerics().add("Box");
        methodModel.getReturnObject().getGenericClasses().add(Box.class);
        List<Assertion> assertions = new ArrayList<>();
        Assertion assertion = new Assertion();
        assertions.add(assertion);

        simpleAssertionCreator.calculateOptionalObjectMapAssertion("1", "Optional", assertions, false,
                true, false, false, false, "boxId");

        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertEquals(1, actual.get().getBoxId());\n", assertions.get(1).generateAssertion());
    }

    @Test
    public void calculateOptionalObjectMapAssertionTestWithMap() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenTest_thenReturnedMap_isValue";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("Map");
        methodModel.setReturnObject(new ObjectModel(Map.class, "", false));
        methodModel.getGenerics().add("Integer");
        methodModel.getGenerics().add("String");
        methodModel.getReturnObject().getGenericClasses().add(Integer.class);
        methodModel.getReturnObject().getGenericClasses().add(String.class);
        List<Assertion> assertions = new ArrayList<>();
        Assertion assertion = new Assertion();
        assertions.add(assertion);

        simpleAssertionCreator.calculateOptionalObjectMapAssertion("three", "Integer", assertions, false,
                true, false, false, false, "3");

        assertFalse(assertions.isEmpty());
        assertEquals("\t\tassertEquals(\"three\", actual.get(3));\n", assertions.get(1).generateAssertion());
    }

    @Test
    public void calculateAssertionTestWithObject() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenFindGoodEntity";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("ScheinEntity");
        methodModel.setReturnObject(new ObjectModel(ScheinEntity.class, "", false));

        simpleAssertionCreator.calculateAssertions(HauptscheinTG.class.getMethod(testName));

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(4, assertions.size());
        assertEquals("\t\tassert actual != null;\n", assertions.get(0).generateAssertion());
        assertEquals("\t\tassertEquals(\"good\", actual.getName());\n", assertions.get(1).generateAssertion());
        assertEquals("\t\tassertEquals(\"dtrfzujhil\", actual.getPassword());\n", assertions.get(2).generateAssertion());
        assertEquals("\t\tassertEquals(7, actual.getScheinId());\n", assertions.get(3).generateAssertion());
    }

    @Test
    public void calculateAssertionTestWithOptional() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenFindOptGoodEntity";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("Optional");
        methodModel.setReturnObject(new ObjectModel(Optional.class, "", false));
        methodModel.getGenerics().add("ScheinEntity");
        methodModel.getReturnObject().getGenericClasses().add(ScheinEntity.class);

        simpleAssertionCreator.calculateAssertions(HauptscheinTG.class.getMethod(testName));

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(4, assertions.size());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());
        assertEquals("\t\tassertEquals(\"good\", actual.get().getName());\n", assertions.get(1).generateAssertion());
        assertEquals("\t\tassertEquals(\"dtrfzujhil\", actual.get().getPassword());\n", assertions.get(2).generateAssertion());
        assertEquals("\t\tassertEquals(7, actual.get().getScheinId());\n", assertions.get(3).generateAssertion());
    }

    @Test
    public void calculateAssertionTestWithMap() throws NoSuchMethodException {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        String cutUri = "de.tudo.naantg.testproject";
        simpleAssertionCreator.getModel().setProjectPackageUriWithDots(cutUri);
        MethodModel methodModel = new MethodModel();
        String testName = "whenCreateGoodMap";
        simpleAssertionCreator.setTestName(testName);
        simpleAssertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        methodModel.setReturnType("Map");
        methodModel.setReturnObject(new ObjectModel(Map.class, "", false));
        methodModel.getGenerics().add("String");
        methodModel.getGenerics().add("Integer");
        methodModel.getReturnObject().getGenericClasses().add(String.class);
        methodModel.getReturnObject().getGenericClasses().add(Integer.class);

        simpleAssertionCreator.calculateAssertions(HauptscheinTG.class.getMethod(testName));

        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(2, assertions.size());
        assertEquals("\t\tassert actual != null;\n", assertions.get(0).generateAssertion());
        assertEquals("\t\tassertEquals(7, actual.get(\"good\"));\n", assertions.get(1).generateAssertion());
    }

    @Test
    public void calculateSetterGetterAssertionTest() {
        SimpleAssertionCreator simpleAssertionCreator = new SimpleAssertionCreator();
        simpleAssertionCreator.setModel(new GeneratorModel());
        simpleAssertionCreator.getModel().setTestClassModel(new TestClassModel());
        simpleAssertionCreator.getModel().setCut(ScheinJob.class);
        ObjectModel parameter = new ObjectModel(String.class, "param1", true);
        ObjectModel object = new ObjectModel();
        object.setIdentifier("scheinJob");

        String testName = "generatedSetGetTestOfJob";
        simpleAssertionCreator.getModel().getParameters(testName).add(parameter);
        simpleAssertionCreator.getModel().getObjectModels(testName).add(object);

        simpleAssertionCreator.calculateSetterGetterAssertion(testName, "getJob", "String");
        List<Assertion> assertions = simpleAssertionCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals("\t\tassertEquals(param1, actual);\n", assertions.get(0).generateAssertion());
    }

}