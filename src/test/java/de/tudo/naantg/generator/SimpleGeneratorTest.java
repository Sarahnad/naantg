package de.tudo.naantg.generator;


import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.annotations.Params;
import de.tudo.naantg.creators.SimpleAssertionCreator;
import de.tudo.naantg.creators.SimpleModelCreator;
import de.tudo.naantg.creators.ValueCreator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.resources.SimpleGeneratorMock;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.scheinboot.ScheinJob;
import de.tudo.naantg.testproject.test.*;

import de.tudo.naantg.helpers.TestFileHandler;
import de.tudo.naantg.testproject.weiter.Dinge;
import de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse;
import de.tudo.naantg.testproject.weiter.Komplex3;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleGeneratorTest {

    private final String projectUri = "de/tudo/naantg/testproject";
    private final String testUri = "main/java/de/tudo/naantg/testproject/test/gen/test";

    @Test
    public void createTestClass() {
        GeneratorModel generatorModel = new GeneratorModel();
        TestClassModel testClassModel = new TestClassModel();
        testClassModel.setPackageUri("de.tudo.naantg.teststructure.test.testGen");
        testClassModel.setName("ErsteKlasseTests");
        List<String> imp = new ArrayList<>();
        imp.add("de.tudo.naantg.testproject.test.ErsteKlasseTG");
        testClassModel.setImports(imp);
        generatorModel.setTestClassModel(testClassModel);
        generatorModel.setTgClass(ErsteKlasseTG.class);
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(generatorModel);

        String expected = "package de.tudo.naantg.teststructure.test.testGen;" +
                "\n\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n\n" +
                "class ErsteKlasseTests /*implements ErsteKlasseTG*/ {" +
                "\n\n\n" +
                "}";

        String actual = simpleGenerator.createTestClass();

        assertEquals(expected, actual);
    }

    @Test
    public void generateErsteKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        SimpleGenerator simpleGenerator = new SimpleGenerator();
        boolean success = simpleGenerator.generateTests(ErsteKlasseTG.class, simpleModelCreator.createGeneratorModel(ErsteKlasseTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(ErsteKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("ErsteKlasseTests", methods);
    }

    @Test
    public void generateZweiteKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        SimpleGenerator simpleGenerator = new SimpleGenerator();
        boolean success = simpleGenerator.generateTests(ZweiteKlasseTG.class, simpleModelCreator.createGeneratorModel(ZweiteKlasseTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(ZweiteKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("ZweiteKlasseTests", methods);
    }

    @Test
    public void generateInnereKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        SimpleGenerator simpleGenerator = new SimpleGenerator();
        boolean success = simpleGenerator.generateTests(InnereKlasseTG.class, simpleModelCreator.createGeneratorModel(InnereKlasseTG.class));
        assert success;

        String[] methods = new String[] {"whenIsNice_thenReturnTrue", "whenIsNice_thenReturnValue",
                "whenIsNice_thenReturnValue_1"};
        checkTestFile("InnereKlasseTests", methods);
    }

    @Test
    public void generateNochEineKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("whenSum_thenReturnValue");
        randomMethods.add("whenIsOne_thenReturnFalse");
        randomMethods.add("whenHaveFun_thenReturnValue");
        randomMethods.add("whenHaveFun_thenReturnValue_1");
        randomMethods.add("whenHaveFun_thenReturnValue_2");
        randomMethods.add("whenHaveFun_thenReturnValue_3");
        SimpleGeneratorMock simpleGeneratorMock = new SimpleGeneratorMock(randomMethods);
        boolean success = simpleGeneratorMock.generateTests(NochEineKlasseTG.class, simpleModelCreator.createGeneratorModel(NochEineKlasseTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(NochEineKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("NochEineKlasseTests", methods);
    }

    @Test
    public void generateKlasseMitInnererKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("whenSetSeven_thenGetSeven_hasValues");
        randomMethods.add("whenSetAnimals_thenGetAnimals_hasValues");
        randomMethods.add("whenSetAnimals_thenGetAnimals_hasEqualValues");
        randomMethods.add("whenSetSeven_with_7_values_thenGetSeven_hasEqualValues");
        randomMethods.add(("whenSetIndex_thenGetIndex_hasEqualValue"));
        SimpleGeneratorMock simpleGeneratorMock = new SimpleGeneratorMock(randomMethods);
        boolean success = simpleGeneratorMock.generateTests(KlasseMitInnererKlasseTG.class, simpleModelCreator.createGeneratorModel(KlasseMitInnererKlasseTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(KlasseMitInnererKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("KlasseMitInnererKlasseTests", methods);
    }

    @Test
    public void generateDritteKlasseTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("whenSetKomplex_thenGetKomplex_hasEqualValue");
        randomMethods.add("whenSetKomplex2_thenGetKomplex2_hasEqualValue");
        randomMethods.add("whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue");
        randomMethods.add("list");
        randomMethods.add("list2");
        randomMethods.add("whenDoComplexWithPerson_thenReturnNull_komplex");
        randomMethods.add("givenParamInit_whenDoComplexWithPerson_thenReturnNull");
        randomMethods.add("whenDoComplexWithPerson_thenReturnNull_komplex_2");
        randomMethods.add("whenDoComplexWithPerson_thenReturnNull_2");
        randomMethods.add("whenCanBuyBox_thenReturnFalse_6");
        SimpleGenerator simpleGeneratorMock = new SimpleGeneratorMock(randomMethods);
        boolean success = simpleGeneratorMock.generateTests(DritteKlasseTG.class, simpleModelCreator.createGeneratorModel(DritteKlasseTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(DritteKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("DritteKlasseTests", methods);
    }

    @Test
    public void generateInhaltTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("whenSetContent_with_2_values_thenGetContent_hasEqualValues");

        SimpleGenerator simpleGeneratorMock = new SimpleGeneratorMock(randomMethods);
        boolean success = simpleGeneratorMock.generateTests(InhaltTG.class, simpleModelCreator.createGeneratorModel(InhaltTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(InhaltTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("InhaltTests", methods);
    }

    @Test
    public void generateHauptscheinTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        SimpleGenerator simpleGenerator = new SimpleGenerator();
        boolean success = simpleGenerator.generateTests(HauptscheinTG.class, simpleModelCreator.createGeneratorModel(HauptscheinTG.class));
        assert success;

        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(HauptscheinTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }
        checkTestFile("HauptscheinTests", methods);
    }

    @Test
    public void generateNoNameTests() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri,
                "de.tudo.naantg.testproject/test",
                testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());
        simpleModelCreator.setGivenCut(ScheinJob.class);

        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("generatedSetGetTestOfJob");
        randomMethods.add("generatedSetGetTestOfJobId");
        randomMethods.add("generatedConstructorOfScheinJob1");
        randomMethods.add("generatedSetGetTestOfCompanyName");
        randomMethods.add("generatedSetGetTestOfPersonCount");
        randomMethods.add("generatedSetGetTestOfTitle");
        SimpleGenerator simpleGeneratorMock = new SimpleGeneratorMock(randomMethods);
        boolean success = simpleGeneratorMock.generateTests(NoNameTG.class, simpleModelCreator.createGeneratorModel(NoNameTG.class));
        assert success;

        Set<String> testCaseModels = simpleModelCreator.getModel().getTestClassModel().getTestCaseModels().keySet();
        int i = 0;
        int n = testCaseModels.size();
        String[] methods = new String[n];
        for (String testName : testCaseModels) {
            if (testName != null && !testName.isEmpty()) {
                methods[i] = testName;
                i++;
            }
        }

        checkTestFile("NoNameTests", methods);
    }

    private void checkTestFile(String filename, String[] methods) {
        checkTestFile(filename, methods, null);
    }

    private void checkTestFile(String filename, String[] methods, List<String> excepts) {
        TestFileHandler handler = new TestFileHandler();
        handler.setTestPath("src/test/java/de/tudo/naantg/resources/testGen");
        StringBuilder controllClass = handler.readTestFile(filename);
        handler.setTestPath("src/" + testUri + "/testGen");
        StringBuilder actual = handler.readTestFile(filename);
        controllClass.delete(0, "package de.tudo.naantg.resources.testGen;".length());
        actual.delete(0, "package de.tudo.naantg.testproject.test.gen.test.testGen;".length());
        String[] expectedMethods = controllClass.toString().replaceAll("\\s", "").split("@Test");
        String[] actualMethods = actual.toString().replaceAll("\\s", "").split("@Test");
        assertEquals(expectedMethods.length, actualMethods.length);
        String[] actualImports = actualMethods[0].split("import");
        String[] expectedImports = expectedMethods[0].split("import");
        assertTrue(Arrays.asList(actualImports).containsAll(Arrays.asList(actualImports)));
        assertTrue(Arrays.asList(expectedImports).containsAll(Arrays.asList(expectedImports)));

        if (methods.length == 0) {
            handler.deleteTestFile(filename);
            return;
        }

        assertEquals(methods.length, actualMethods.length-1);
        int n = methods.length;
        expectedMethods[n] = expectedMethods[n].substring(0, expectedMethods[n].length()-1);
        actualMethods[n] = actualMethods[n].substring(0, actualMethods[n].length()-1);
        int count = methods.length;

        for (int i = 0; i < count; i++) {
            methods[i] = methods[i] + "()";
        }

        Arrays.sort(expectedMethods);
        Arrays.sort(actualMethods);
        Arrays.sort(methods);

        List<String> privateExpMethods = new ArrayList<>();
        List<String> privateActMethods = new ArrayList<>();

        for (int i = 1; i < expectedMethods.length; i++) {
            String[] expSplitted = expectedMethods[i].split("private");
            String exp = expSplitted[0];
            if (expSplitted.length > 1) {
                for (int s = 1; s < expSplitted.length; s++) {
                    privateExpMethods.add("private" + expSplitted[s]);
                }
            }
            String[] actSplitted = actualMethods[i].split("private");
            String actualTestcase = actSplitted[0];
            if (actSplitted.length > 1) {
                for (int s = 1; s < actSplitted.length; s++) {
                    if (!privateActMethods.contains("private" + actSplitted[s])) {
                        privateActMethods.add("private" + actSplitted[s]);
                    }
                }
            }
            assertEquals(exp, actualTestcase);
        }
        assertEquals(privateExpMethods.size(), privateActMethods.size());

        handler.deleteTestFile(filename);
    }

    @Test
    public void generateTestMethod() {
        GeneratorModel model = new GeneratorModel();
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(model);
        ObjectModel objectModel = new ObjectModel();
        objectModel.setDataType("ErsteKlasse");
        objectModel.setIdentifier("ersteKlasse");
        objectModel.setNewInstance(true);
        String testName = "whenAct_thenNoError";
        List<ObjectModel> objectModels = new ArrayList<>();
        objectModels.add(objectModel);
        model.setObjectModels(objectModels, testName);
        MethodModel methodModel = new MethodModel();
        methodModel.setName("act");
        methodModel.setObject("ersteKlasse");
        methodModel.setReturnType("void");
        model.setMethodOfCUT(methodModel, testName);
        model.setCutObject(objectModel, testName);

        String method = "\t@Test\n"+
                "\tpublic void whenAct_thenNoError() {\n" +
                "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n\n" +
                "\t\tersteKlasse.act();\n\n" +
                "\t}\n";

        String actual = simpleGenerator.createTestMethod("whenAct_thenNoError");
        assertEquals(method, actual);
    }

    @Test
    public void generateTest() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(projectUri, testUri);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());
        GeneratorModel model = simpleModelCreator.createGeneratorModel(ErsteKlasseTG.class);
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(model);

        String method = "\t@Test\n"+
                "\tpublic void whenAct_thenNoError() {\n" +
                "\t\tErsteKlasse ersteKlasse = new ErsteKlasse();\n\n" +
                "\t\tersteKlasse.act();\n\n" +
                "\t}\n";

        String actual = simpleGenerator.createTestMethod("whenAct_thenNoError");
        assertEquals(method, actual);
    }

    @Test
    public void generateObjectStatementTest() {
        SimpleGenerator simpleGenerator = initCreatorForObjectListTests();
        GeneratorModel model = simpleGenerator.getModel();

        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue";
        List<ObjectModel> parameters = model.getParameters(testName);
        ObjectModel parent = parameters.get(0);
        assertEquals("Komplex3", parent.getDataType());
        ObjectModel personList = parent.getInstanceParameters().get(0);
        ObjectModel boxList = parent.getInstanceParameters().get(1);

        String result = ObjectGenerator.generateObjectStatement(personList, model);
        String expected = "\t\tList<Person> list = createNewPersonList();\n";
        assertEquals(expected, result);

        result = ObjectGenerator.generateObjectStatement(boxList, model);
        expected = "\t\tList<Box> list2 = createNewBoxList();\n";
        assertEquals(expected, result);
        List<TestCaseModel> helperMethods = model.getTestClassModel().getPrivateHelperMethods();
        assertEquals(2, helperMethods.size());
        assertEquals("createNewPersonList", helperMethods.get(0).getTestMethodName());
        assertEquals("createNewBoxList", helperMethods.get(1).getTestMethodName());
        assertEquals("list", helperMethods.get(0).getObjectModels().get(0).getIdentifier());
        assertEquals("list2", helperMethods.get(1).getObjectModels().get(0).getIdentifier());
    }

    @Test
    public void generateObjectStatementListTest() throws NoSuchMethodException {
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        SimpleModelCreator simpleModelCreator = initCreator();
        String testName = "whenGetFirstValue_thenReturn0";
        Method method = KlasseMitInnererKlasseTG.class.getMethod(testName);
        simpleModelCreator.getModel().setCut(KlasseMitInnererKlasse.class);
        simpleModelCreator.calculateMethodModel(testName);
        simpleModelCreator.calculateObjects(method);
        simpleGenerator.setModel(simpleModelCreator.getModel());
        ObjectModel list = simpleModelCreator.getModel().getParameters(testName).get(0);

        String result = ObjectGenerator.generateObjectStatement(list, simpleModelCreator.getModel());
        assertEquals("\t\tList<Integer> param1 = new ArrayList<>();\n", result);
    }

    @Test
    public void generateHelperMethodTest() {
        SimpleGenerator simpleGenerator = initCreatorForObjectListTests();
        GeneratorModel model = simpleGenerator.getModel();

        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue";
        List<ObjectModel> parameters = model.getParameters(testName);
        ObjectModel parent = parameters.get(0);
        assertEquals("Komplex3", parent.getDataType());
        ObjectModel personList = parent.getInstanceParameters().get(0);
        ObjectModel boxList = parent.getInstanceParameters().get(1);

        String result = simpleGenerator.generateHelperMethod(personList);
        result = result.replaceAll("\\{\".*\", \".*\"}", "{\"alex\", \"alexa\"}");

        String expected = "\tprivate List<Person> createNewPersonList() {\n" +
            "\t\tList<Person> list = new ArrayList<>();\n" +
            "\t\tString[] personStrs = new String[] {\"alex\", \"alexa\"};\n" +
            "\t\tfor (String elem : personStrs) {\n" +
                "\t\t\tlist.add(new Person(elem));\n" +
            "\t\t}\n" +
            "\t\treturn list;\n" +
            "\t}\n";

        assertEquals(expected, result);

        result = PrivateMethodGenerator.generateHelperMethod(model, boxList);
        result = result.replaceAll("\\{.+, .+}", "{1, 2}");

        expected = "\tprivate List<Box> createNewBoxList() {\n" +
                "\t\tList<Box> list2 = new ArrayList<>();\n" +
                "\t\tint[] boxIVars = new int[] {1, 2};\n" +
                "\t\tfor (int elem : boxIVars) {\n" +
                "\t\t\tlist2.add(new Box(elem));\n" +
                "\t\t}\n" +
                "\t\treturn list2;\n" +
                "\t}\n";

        assertEquals(expected, result);
    }

    @Test
    public void generateObjectValuesForEnumTest() {
        SimpleModelCreator simpleModelCreator = initCreator();
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(simpleModelCreator.getModel());
        String testName = "whenSetContent_with_2_values_thenGetContent_hasEqualValues";
        ObjectModel objectModel = new ObjectModel(List.class, "param1", true);
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Dinge.class);
        ValueCreator valueCreator = new ValueCreator(simpleModelCreator.getModel(), testName, InitType.NONE);

        valueCreator.calculateCollectionParameters(objectModel);

        GeneratorModel model = simpleGenerator.getModel();

        String result = ObjectGenerator.generateObjectStatement(objectModel);
        result = result.replaceAll("\\(Dinge.+, Dinge.+\\)", "(Dinge.BOX, Dinge.FALSCHE)");

        String expected = "\t\tList<Dinge> param1 = Arrays.asList(Dinge.BOX, Dinge.FALSCHE);\n";
        assertEquals(expected, result);
        assertTrue(model.getTestClassModel().getImports().contains("java.util.Arrays"));
    }

    @Test
    public void generateValueArrayObjectsTest() {
        SimpleGenerator simpleGenerator = initCreatorForObjectListTests();
        GeneratorModel model = simpleGenerator.getModel();

        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue";
        List<ObjectModel> parameters = model.getParameters(testName);
        ObjectModel parent = parameters.get(0);
        assertEquals("Komplex3", parent.getDataType());
        ObjectModel personList = parent.getInstanceParameters().get(0);

        List<ObjectModel> results = PrivateMethodGenerator.generateValueArrayObjects(personList);

        assertEquals(1, results.size());
        String resultString = results.get(0).generateObjectStatement();
        assertTrue(resultString.startsWith("\t\tString[] personStrs = new String[] "));
    }

    @Test
    public void generateValueArrayTest() {
        SimpleGenerator simpleGenerator = initCreatorForObjectListTests();
        GeneratorModel model = simpleGenerator.getModel();

        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue";
        List<ObjectModel> parameters = model.getParameters(testName);
        ObjectModel parent = parameters.get(0);
        assertEquals("Komplex3", parent.getDataType());
        ObjectModel personList = parent.getInstanceParameters().get(0);
        ObjectModel person = personList.getInstanceParameters().get(0);

        String result = PrivateMethodGenerator.generateValueArray(personList, 0);

        assertTrue(result.matches("\\{\".*\",\".*\"}"));
    }

    private SimpleGenerator initCreatorForObjectListTests() {
        SimpleModelCreator simpleModelCreator = initCreator();
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(simpleModelCreator.getModel());
        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue";
        try {
            Method method = DritteKlasseTG.class.getMethod(testName);
            simpleModelCreator.setRandomConfigs(DritteKlasseTG.class, method);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObjectModel objectModel = new ObjectModel(Komplex3.class, "param1", true);
        simpleModelCreator.getModel().getParameters(testName).add(objectModel);
        ValueCreator valueCreator = new ValueCreator(simpleModelCreator.getModel(), testName, InitType.NONE);
        valueCreator.calculateObjectParameters(objectModel);
        return simpleGenerator;
    }

    private SimpleModelCreator initCreator() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator("", "");
        GeneratorModel model = new GeneratorModel();
        simpleModelCreator.setModel(model);
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        return simpleModelCreator;
    }

    @Test
    public void generateFieldsTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(simpleModelCreator.getModel());
        Method method = DritteKlasseTG.class.getMethod("whenDoComplex_thenReturnNull");
        simpleModelCreator.calculateMethodModel(method.getName());
        simpleModelCreator.calculateObjects(method);

        List<ObjectModel> objectModels = simpleModelCreator.getModel().getObjectModels(method.getName());
        assertFalse(objectModels.isEmpty());
        objectModels.forEach(System.out::println);

        String actual =  simpleGenerator.generateInputs(method.getName());
        String expected = "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
                "\t\tinitDritteKlasseWithDefaultValues(dritteKlasse);\n";
        assertEquals(expected, actual);

        List<TestCaseModel> testCaseModel = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(3, testCaseModel.size());
        TestCaseModel helperMethod = testCaseModel.get(0);
        assertEquals("initDritteKlasseWithDefaultValues", helperMethod.getTestMethodName());

        expected = "\tprivate void initDritteKlasseWithDefaultValues(DritteKlasse dritteKlasse) {\n" +
                "\t\tint points = 0;\n" +
                "\t\tlong aMillion = 0L;\n" +
                "\t\tint iVar = 0;\n" +
                "\t\tint iVar2 = 0;\n" +
                "\t\tKomplex komplex = new Komplex(iVar, iVar2);\n" +
                "\t\tString str = \"\";\n" +
                "\t\tPerson person = new Person(str);\n" +
                "\t\tinitPersonWithDefaultValues(person);\n" +
                "\t\tint iVar3 = 0;\n" +
                "\t\tBox box = new Box(iVar3);\n" +
                "\t\tinitBoxWithDefaultValues(box);\n" +
                "\t\tKomplex2 komplex2 = new Komplex2(person, box);\n" +
                "\t\tList<Person> list = new ArrayList<>();\n" +
                "\t\tList<Box> list2 = new ArrayList<>();\n" +
                "\t\tKomplex3 komplex3 = new Komplex3(list, list2);\n" +
                "\t\tList<Person> list3 = new ArrayList<>();\n" +
                "\t\tList<Box> list4 = new ArrayList<>();\n" +
                "\t\tString[] array = new String[0];\n" +
                "\t\tKomplex4 komplex4 = new Komplex4(list3, list4, array);\n" +
                "\t\tList<Komplex> komplexes = new ArrayList<>();\n" +
                "\t\tdritteKlasse.setPoints(points);\n" +
                "\t\tdritteKlasse.setAMillion(aMillion);\n" +
                "\t\tdritteKlasse.setKomplex(komplex);\n" +
                "\t\tdritteKlasse.setKomplex2(komplex2);\n" +
                "\t\tdritteKlasse.setKomplex3(komplex3);\n" +
                "\t\tdritteKlasse.setKomplex4(komplex4);\n" +
                "\t\tdritteKlasse.setKomplexes(komplexes);\n" +
                "\t}\n";

        String initMethod = helperMethod.getHelperMethodContent();
        assertEquals(expected, initMethod);

        helperMethod = testCaseModel.get(1);
        assertEquals("initPersonWithDefaultValues", helperMethod.getTestMethodName());

        expected = "\tprivate void initPersonWithDefaultValues(Person person) {\n" +
                "\t\tList<Box> boxes = new ArrayList<>();\n" +
                "\t\tKey key = new Key();\n" +
                "\t\tString name = \"\";\n" +
                "\t\tperson.setBoxes(boxes);\n" +
                "\t\tperson.setKey(key);\n" +
                "\t\tint keyId = 0;\n" +
                "\t\tkey.setKeyId(keyId);\n" +
                "\t\tperson.setName(name);\n" +
                "\t}\n";

        initMethod = helperMethod.getHelperMethodContent();
        assertEquals(expected, initMethod);

        helperMethod = testCaseModel.get(2);
        assertEquals("initBoxWithDefaultValues", helperMethod.getTestMethodName());

        expected = "\tprivate void initBoxWithDefaultValues(Box box) {\n" +
                "\t\tint boxId = 0;\n" +
                "\t\tInhalt content = new Inhalt();\n" +
                "\t\tboolean hasGames = false;\n" +
                "\t\tbox.setBoxId(boxId);\n" +
                "\t\tbox.setContent(content);\n" +
                "\t\tList<Dinge> content2 = new ArrayList<>();\n" +
                "\t\tcontent.setContent(content2);\n" +
                "\t\tbox.setHasGames(hasGames);\n" +
                "\t}\n";

        initMethod = helperMethod.getHelperMethodContent();
        assertEquals(expected, initMethod);
    }

    @Test
    public void generateFieldTestWithRandomValues() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(simpleModelCreator.getModel());
        Method method = DritteKlasseTG.class.getMethod("whenDoComplex_thenReturnValue");
        simpleModelCreator.setRandomConfigs(DritteKlasseTG.class, method);
        simpleModelCreator.calculateMethodModel(method.getName());
        simpleModelCreator.calculateObjects(method);

        List<ObjectModel> objectModels = simpleModelCreator.getModel().getObjectModels(method.getName());
        assertFalse(objectModels.isEmpty());
        //objectModels.forEach(System.out::println);

        String actual =  simpleGenerator.generateInputs(method.getName());

        String expected = "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
                "\t\tinitDritteKlasseWithRandomValues(dritteKlasse);\n";
        assertEquals(expected, actual);

        List<TestCaseModel> testCaseModel = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(9, testCaseModel.size());
        TestCaseModel helperMethod = testCaseModel.get(0);
        assertEquals("initDritteKlasseWithRandomValues", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(1);
        assertEquals("initPersonWithRandomValues", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(2);
        assertEquals("createNewBoxList", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(3);
        assertEquals("initBoxWithRandomValues", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(4);
        assertEquals("createNewPersonList", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(5);
        assertEquals("createNewBoxList", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(6);
        assertEquals("createNewPersonList", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(7);
        assertEquals("createNewBoxList", helperMethod.getTestMethodName());
        helperMethod = testCaseModel.get(8);
        assertEquals("createNewKomplexList", helperMethod.getTestMethodName());

        expected = "\tprivate void initDritteKlasseWithRandomValues(DritteKlasse dritteKlasse) {\n" +
                "\t\tint points = 1;\n" +
                "\t\tlong aMillion = 1L;\n" +
                "\t\tint iVar = 1;\n" +
                "\t\tint iVar2 = 1;\n" +
                "\t\tKomplex komplex = new Komplex(iVar, iVar2);\n" +
                "\t\tString str = \"test\";\n" +
                "\t\tPerson person = new Person(str);\n" +
                "\t\tinitPersonWithRandomValues(person);\n" +
                "\t\tint iVar5 = 1;\n" +
                "\t\tBox box3 = new Box(iVar5);\n" +
                "\t\tinitBoxWithRandomValues(box3);\n" +
                "\t\tKomplex2 komplex2 = new Komplex2(person, box3);\n" +
                "\t\tList<Person> list = createNewPersonList();\n" +
                "\t\tList<Box> list2 = createNewBoxList();\n" +
                "\t\tKomplex3 komplex3 = new Komplex3(list, list2);\n" +
                "\t\tList<Person> list3 = createNewPersonList();\n" +
                "\t\tList<Box> list4 = createNewBoxList();\n" +
                "\t\tString[] array = new String[] {\"a\", \"b\"};\n" +
                "\t\tKomplex4 komplex4 = new Komplex4(list3, list4, array);\n" +
                "\t\tList<Komplex> komplexes = createNewKomplexList();\n" +
                "\t\tdritteKlasse.setPoints(points);\n" +
                "\t\tdritteKlasse.setAMillion(aMillion);\n" +
                "\t\tdritteKlasse.setKomplex(komplex);\n" +
                "\t\tdritteKlasse.setKomplex2(komplex2);\n" +
                "\t\tdritteKlasse.setKomplex3(komplex3);\n" +
                "\t\tdritteKlasse.setKomplex4(komplex4);\n" +
                "\t\tdritteKlasse.setKomplexes(komplexes);\n" +
                "\t}\n";

        String initMethod = testCaseModel.get(0).getHelperMethodContent();
        initMethod = initMethod.replaceAll(" = -?\\d+", " = 1");
        initMethod = initMethod.replaceAll(" = \".*\"", " = \"test\"");
        initMethod = initMethod.replaceAll(" = new String\\[] \\{.+}", " = new String\\[] \\{\"a\", \"b\"}");
        //System.out.println(initMethod);
        assertEquals(expected, initMethod);

        expected = "\tprivate void initPersonWithRandomValues(Person person) {\n" +
                "\t\tList<Box> boxes = createNewBoxList();\n" +
                "\t\tKey key = new Key();\n" +
                "\t\tString name = \"test\";\n" +
                "\t\tperson.setBoxes(boxes);\n" +
                "\t\tperson.setKey(key);\n" +
                "\t\tint keyId = 1;\n" +
                "\t\tkey.setKeyId(keyId);\n" +
                "\t\tperson.setName(name);\n" +
                "\t}\n";
        initMethod = testCaseModel.get(1).getHelperMethodContent();
        initMethod = initMethod.replaceAll(" = -?\\d+", " = 1");
        initMethod = initMethod.replaceAll(" = \".*\"", " = \"test\"");
        assertEquals(expected, initMethod);

        expected = "\tprivate void initBoxWithRandomValues(Box box3) {\n" +
                "\t\tint boxId = 1;\n" +
                "\t\tInhalt content = new Inhalt();\n" +
                "\t\tboolean hasGames = true;\n" +
                "\t\tbox3.setBoxId(boxId);\n" +
                "\t\tbox3.setContent(content);\n" +
                "\t\tList<Dinge> content2 = Arrays.asList(Dinge.FLASCHE, Dinge.GABEL);\n" +
                "\t\tcontent.setContent(content2);\n" +
                "\t\tbox3.setHasGames(hasGames);\n" +
                "\t}\n";
        initMethod = testCaseModel.get(3).getHelperMethodContent();
        initMethod = initMethod.replaceAll(" = -?\\d+", " = 1");
        initMethod = initMethod.replaceAll(" = false", " = true");
        initMethod = initMethod.replaceAll(" = \".*\"", " = \"test\"");
        initMethod = initMethod.replaceAll(" = Arrays.asList\\(Dinge.+, Dinge.+\\)", " = Arrays.asList(Dinge.FLASCHE, Dinge.GABEL)");
        assertEquals(expected, initMethod);

        expected = "\tprivate List<Box> createNewBoxList() {\n" +
                "\t\tList<Box> boxes = new ArrayList<>();\n" +
                "\t\tint[] boxIVar3s = new int[] {1, 1};\n" +
                "\t\tfor (int elem : boxIVar3s) {\n" +
                "\t\t\tboxes.add(new Box(elem));\n" +
                "\t\t}\n" +
                "\t\treturn boxes;\n" +
                "\t}\n";
        initMethod = simpleGenerator.generateHelperMethod(testCaseModel.get(2).getObjectModels().get(0));
        initMethod = initMethod.replaceAll(" = new int\\[] \\{.+, .+}", " = new int[] {1, 1}");
        assertEquals(expected, initMethod);

        expected = "\tprivate List<Person> createNewPersonList() {\n" +
                "\t\tList<Person> list = new ArrayList<>();\n" +
                "\t\tString[] personStr2s = new String[] {\"1\", \"1\"};\n" +
                "\t\tfor (String elem : personStr2s) {\n" +
                "\t\t\tlist.add(new Person(elem));\n" +
                "\t\t}\n" +
                "\t\treturn list;\n" +
                "\t}\n";
        initMethod = simpleGenerator.generateHelperMethod(testCaseModel.get(4).getObjectModels().get(0));
        initMethod = initMethod.replaceAll(" = new String\\[] \\{.+, .+}", " = new String[] {\"1\", \"1\"}");
        assertEquals(expected, initMethod);

        expected = "\tprivate List<Komplex> createNewKomplexList() {\n" +
                "\t\tList<Komplex> komplexes = new ArrayList<>();\n" +
                "\t\tint[] komplexIVar10s = new int[] {1, 1};\n" +
                "\t\tint[] komplexIVar11s = new int[] {1, 1};\n" +
                "\t\tfor (int i = 0; i < komplexIVar10s.length; i++) {\n" +
                "\t\t\tkomplexes.add(new Komplex(komplexIVar10s[i], komplexIVar11s[i]));\n" +
                "\t\t}\n" +
                "\t\treturn komplexes;\n" +
                "\t}\n";
        initMethod = simpleGenerator.generateHelperMethod(testCaseModel.get(8).getObjectModels().get(0));
        initMethod = initMethod.replaceAll(" = new int\\[] \\{.+, .+}", " = new int[] {1, 1}");
        assertEquals(expected, initMethod);
    }

    @Test
    public void generateInputsWithMethodCallTest() throws NoSuchMethodException {
        SimpleGenerator simpleGenerator = new SimpleGenerator();
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        simpleGenerator.setModel(simpleModelCreator.getModel());
        String testName = "whenCanBuyBox_thenReturnTrue";
        simpleModelCreator.calculateMethodModel(testName);
        simpleModelCreator.calculateObjects(DritteKlasseTG.class.getMethod(testName));
        List<ObjectModel> objectModels = simpleModelCreator.getModel().getObjectModels(testName);
        assertFalse(objectModels.isEmpty());
        String result = simpleGenerator.generateInputs(testName);
        String expected = "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
                "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, result);

        testName = "whenDoComplexWithPerson_thenReturnValue_simple";
        simpleModelCreator.calculateMethodModel(testName);
        simpleModelCreator.calculateObjects(DritteKlasseTG.class.getMethod(testName));
        objectModels = simpleModelCreator.getModel().getObjectModels(testName);
        assertFalse(objectModels.isEmpty());
        result = simpleGenerator.generateInputs(testName);
        expected = "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
                "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        assertEquals(expected, result);

        testName = "whenDoComplexWithPerson_thenReturnNull_komplex";
        Method method = DritteKlasseTG.class.getMethod("whenDoComplexWithPerson_thenReturnNull_komplex");
        simpleModelCreator.setRandomConfigs(DritteKlasseTG.class, method);
        simpleModelCreator.calculateMethodModel(testName);
        simpleModelCreator.calculateObjects(DritteKlasseTG.class.getMethod(testName));
        objectModels = simpleModelCreator.getModel().getObjectModels(testName);
        assertFalse(objectModels.isEmpty());
        result = simpleGenerator.generateInputs(testName);
        expected = "\t\tDritteKlasse dritteKlasse = new DritteKlasse();\n" +
                "\t\tString str = \"name\";\n" +
                "\t\tPerson param1 = new Person(str);\n" +
                "\t\tparam1.init();\n";
        assertEquals(expected, result.replaceAll("\".*\"", "\"name\""));
    }

    @Test
    public void generateAnnotationsTest() {
        SimpleGenerator generator = new SimpleGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "whenAnnotationTest";
        model.setTestMethodModel(new TestCaseModel(), testName);
        model.getTestMethodModel(testName).getAnnotations().addAll(Arrays.asList(Params.class, RandomConfigs.class));

        String actual = generator.generateAnnotations(testName);

        assertEquals("\t@Params\n\t@RandomConfigs\n", actual);

        testName = "whenAnnotationTest_2";
        model.setTestMethodModel(new TestCaseModel(), testName);

        actual = generator.generateAnnotations(testName);

        assertEquals("", actual);
    }

}