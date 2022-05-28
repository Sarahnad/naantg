package de.tudo.naantg.creators;


import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.annotations.Params;
import de.tudo.naantg.generator.ObjectGenerator;
import de.tudo.naantg.generator.SimpleGenerator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.Statistik;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinJob;
import de.tudo.naantg.testproject.test.*;

import de.tudo.naantg.testproject.weiter.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleModelCreatorTest {

    @Test
    public void createGeneratorModel() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String testPackage = "de.tudo.naantg.testproject.test.gen.test_2.testGen";
        String className = "ErsteKlasseTests";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());
        List<String> imports = new ArrayList<>();
        imports.add("de.tudo.naantg.testproject.ErsteKlasse");
        imports.add("de.tudo.naantg.testproject.test.ErsteKlasseTG");

        GeneratorModel generatorModel = simpleModelCreator.createGeneratorModel(ErsteKlasseTG.class);

        assert generatorModel != null;
        assert generatorModel.getTestClassModel() != null;
        assertEquals(testPackage, generatorModel.getTestClassModel().getPackageUri());
        assertEquals(className, generatorModel.getTestClassModel().getName());
        assertEquals(imports, generatorModel.getTestClassModel().getImports());

        SimpleGenerator simpleGenerator = new SimpleGenerator();
        simpleGenerator.setModel(generatorModel);
        String expected = "package " + testPackage + ";" +
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
    public void testGetAssertionForKlasseMitInnererKlasse() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());
        GeneratorModel generatorModel = simpleModelCreator.createGeneratorModel(KlasseMitInnererKlasseTG.class);

        String testName = "whenSetIndex_thenGetIndex_hasEqualValue";
        String expected = "\t\tassertEquals(param1, actual);\n";
        Assertion assertion = generatorModel.getTestMethodModel(testName).getAssertions().get(0);
        assertEquals(expected, assertion.generateAssertion());

        testName = "whenSetIndex_with55_thenGetIndex_hasValue55";
        expected = "\t\tassertEquals(55, actual);\n";
        assertion = generatorModel.getTestMethodModel(testName).getAssertions().get(0);
        assertEquals(expected, assertion.generateAssertion());

        testName = "whenGetCollection_with5_thenReturned_size_is5";
        expected = "\t\tassertEquals(5, actual.size());\n";
        assertion = generatorModel.getTestMethodModel(testName).getAssertions().get(1);
        assertEquals(expected, assertion.generateAssertion());
    }

    private SimpleModelCreator initCreator() {
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator("", "");
        simpleModelCreator.setAssertionCreator(new SimpleAssertionCreator());
        GeneratorModel model = new GeneratorModel();
        simpleModelCreator.setModel(model);
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        return simpleModelCreator;
    }

    @Test
    public void calculateCollectionReturnTypeTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        List<String> imports = simpleModelCreator.getModel().getTestClassModel().getImports();
        Method method = KlasseMitInnererKlasse.class.getMethod("getNames");
        MethodModel methodModel = new MethodModel();
        methodModel.setMethodToTest(method);
        simpleModelCreator.calculateCollectionReturnType(method.getReturnType(), methodModel);
        assertEquals("String", methodModel.getGenerics().get(0));
        assertEquals(1, imports.size());
        assertEquals(CollectionDataType.LIST.toString(), imports.get(0));

        imports = new ArrayList<>();
        simpleModelCreator.getModel().getTestClassModel().setImports(imports);
        method = KlasseMitInnererKlasse.class.getMethod("getIntArray");
        methodModel = new MethodModel();
        methodModel.setMethodToTest(method);
        simpleModelCreator.calculateCollectionReturnType(method.getReturnType(), methodModel);
        assertTrue(methodModel.getGenerics().isEmpty());
        assertEquals(1, imports.size());
        assertEquals(CollectionDataType.ARRAYS.toString(), imports.get(0));

        imports = new ArrayList<>();
        simpleModelCreator.getModel().getTestClassModel().setImports(imports);
        method = KlasseMitInnererKlasse.class.getMethod("getCollection", int.class);
        methodModel = new MethodModel();
        methodModel.setMethodToTest(method);
        simpleModelCreator.calculateCollectionReturnType(method.getReturnType(), methodModel);
        assertEquals("Double", methodModel.getGenerics().get(0));
        assertEquals(1, imports.size());
        assertEquals(CollectionDataType.COLLECTION.toString(), imports.get(0));

        imports = new ArrayList<>();
        simpleModelCreator.getModel().getTestClassModel().setImports(imports);
        method = DritteKlasse.class.getMethod("getKomplexes");
        methodModel = new MethodModel();
        methodModel.setMethodToTest(method);
        simpleModelCreator.calculateCollectionReturnType(method.getReturnType(), methodModel);
        assertEquals("Komplex", methodModel.getGenerics().get(0));
        assertEquals(2, imports.size());
        assertEquals("de.tudo.naantg.testproject.weiter.Komplex", imports.get(0));
        assertEquals(CollectionDataType.LIST.toString(), imports.get(1));
    }

    @Test
    public void calculatesParametersTest() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(NochEineKlasseTG.class);
        simpleModelCreator.getModel().setCut(NochEineKlasse.class);
        for (Method method : tgMethods) {
            simpleModelCreator.setRandomConfigs(NochEineKlasseTG.class, method);
            simpleModelCreator.calculateMethodModel(method.getName());
            simpleModelCreator.calculateObjects(method);
        }

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters("whenSum_thenReturn10");
        assertEquals(2, parameters.size());
        assertEquals("\t\tint param1 = 4;\n", parameters.get(0).generateObjectStatement());
        assertEquals("\t\tint param2 = 6;\n", parameters.get(1).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenGetOneOrTwo_thenReturn2");
        assertEquals(1, parameters.size());
        assertEquals("\t\tboolean param1 = false;\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenIsOne_thenReturnTrue");
        assertEquals(1, parameters.size());
        assertEquals("\t\tString param1 = \"one\";\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenHaveFun_thenReturnValue_1");
        assertEquals(6, parameters.size());
        assertEquals("\t\tbyte param2 = 8;\n", parameters.get(1).generateObjectStatement());
        assertEquals("\t\tshort param3 = 1;\n", parameters.get(2).generateObjectStatement());
        assertEquals("\t\tdouble param5 = 2.0d;\n", parameters.get(4).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenHaveFun_thenReturnValue_2");
        assertEquals(6, parameters.size());

        parameters = simpleModelCreator.getModel().getParameters("whenHaveFun_thenReturnValue_3");
        assertEquals(6, parameters.size());
        assertEquals("\t\tchar param1 = '8';\n", parameters.get(0).generateObjectStatement());
        assertEquals("\t\tlong param4 = 1L;\n", parameters.get(3).generateObjectStatement());
        assertEquals("\t\tfloat param6 = 2.0f;\n", parameters.get(5).generateObjectStatement());
    }

    @Test
    public void calculatesParametersWithListsTest() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(KlasseMitInnererKlasseTG.class);
        simpleModelCreator.getModel().setCut(KlasseMitInnererKlasse.class);
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        for (Method method : tgMethods) {
            simpleModelCreator.calculateMethodModel(method.getName());
            simpleModelCreator.calculateObjects(method);
        }

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters("whenGetFirstValue_thenReturn0");
        assertEquals(1, parameters.size());
        assertEquals("\t\tList<Integer> param1 = new ArrayList<>();\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenGetFirstValue_thenReturn0_1");
        assertEquals(1, parameters.size());
        assertEquals("\t\tLinkedList<Integer> param1 = new LinkedList<>();\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenGetCollection_with5_thenReturned_size_is5");
        MethodModel methodModel = simpleModelCreator.getModel().getMethodOfCUT("whenGetCollection_with5_thenReturned_size_is5");
        assertNotNull(methodModel);
        assertEquals("\t\tCollection<Double> actual = klasseMitInnererKlasse.getCollection(param1);\n",
                methodModel.generateMethodStatement());
        assertEquals(1, parameters.size());
        assertEquals("\t\tint param1 = 5;\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenIsEvenSize_thenReturnTrue");
        methodModel = simpleModelCreator.getModel().getMethodOfCUT("whenIsEvenSize_thenReturnTrue");
        assertNotNull(methodModel);
        assertEquals("\t\tboolean actual = klasseMitInnererKlasse.isEvenSize(param1);\n",
                methodModel.generateMethodStatement());
        assertEquals(1, parameters.size());
        assertEquals("\t\tCollection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4});\n",
                parameters.get(0).generateObjectStatement());

        List<String> imports = simpleModelCreator.getModel().getTestClassModel().getImports();
        assertEquals(5, imports.size());
        List<String> expectedImports = new ArrayList<>();
        expectedImports.add("java.util.List");
        expectedImports.add("java.util.ArrayList");
        expectedImports.add("java.util.LinkedList");
        expectedImports.add("java.util.Arrays");
        expectedImports.add("java.util.Collection");
        assertTrue(imports.containsAll(expectedImports));
    }



    @Test
    public void collectionParameterTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(KlasseMitInnererKlasse.class);

        String testName = "whenSetSeven_thenGetSeven_hasValues";
        Method method = KlasseMitInnererKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(method.getName());
        String identifier = simpleModelCreator.getModel().getMethodOfCUT(testName).getObject();
        ObjectModel objectModel = new ObjectModel(KlasseMitInnererKlasse.class, identifier, true);
        simpleModelCreator.getModel().setCutObject(objectModel, testName);

        simpleModelCreator.calculatesParameters(method);

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0).generateObjectStatement().startsWith("\t\tint[] param1 = new int[] {"));
        assertTrue(parameters.get(0).generateObjectStatement().endsWith("};\n"));

        testName = "whenIsPositive_thenReturnTrue";
        method = KlasseMitInnererKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(method.getName());
        simpleModelCreator.getModel().setCutObject(objectModel, testName);

        simpleModelCreator.calculatesParameters(method);

        parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(1, parameters.size());
        assertEquals("\t\tint[] param1 = new int[] {1, 4, 6, 3};\n", parameters.get(0).generateObjectStatement());
    }

    @Test
    public void calculatesParametersWithArrayTest() {
        String pkg = "de.tudo.naantg.testproject/test/gen/test_2";
        String cutUri = "de.tudo.naantg.testproject";
        SimpleModelCreator simpleModelCreator = new SimpleModelCreator(cutUri, pkg);
        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(KlasseMitInnererKlasseTG.class);
        simpleModelCreator.getModel().setCut(KlasseMitInnererKlasse.class);
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        for (Method method : tgMethods) {
            simpleModelCreator.calculateMethodModel(method.getName());
            simpleModelCreator.calculateObjects(method);
        }

        List<String> imports = simpleModelCreator.getModel().getTestClassModel().getImports();
        assertEquals(5, imports.size());
        List<String> expectedImports = new ArrayList<>();
        expectedImports.add("java.util.List");
        expectedImports.add("java.util.ArrayList");
        expectedImports.add("java.util.LinkedList");
        expectedImports.add("java.util.Arrays");
        expectedImports.add("java.util.Collection");
        assertTrue(imports.containsAll(expectedImports));

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters("whenIsPositive_thenReturnTrue");
        assertEquals(1, parameters.size());
        assertEquals("\t\tint[] param1 = new int[] {1, 4, 6, 3};\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenGetFirstValue_thenReturn0_1");
        assertEquals(1, parameters.size());
        assertEquals("\t\tLinkedList<Integer> param1 = new LinkedList<>();\n", parameters.get(0).generateObjectStatement());

        parameters = simpleModelCreator.getModel().getParameters("whenSetSeven_thenGetSeven_hasValues");
        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0).generateObjectStatement().startsWith("\t\tint[] param1 = new int[] {"));
        assertTrue(parameters.get(0).generateObjectStatement().endsWith("};\n"));

    }

    //@Test
    public void setAllInstanceParametersToPrivateTest() {
        SimpleModelCreator simpleModelCreator = initCreator();

        String testName = "whenSetKomplex_with_2_values_thenGetKomplex3";
        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(11, parameters.size());

        int index = parameters.size()-1;
        ObjectModel parent = parameters.get(index);
        assertEquals("Komplex3", parent.getDataType());
        assertFalse(parent.isPrivate());
        assertFalse(parent.getInstanceParameters().get(0).isPrivate());
        assertFalse(parent.getInstanceParameters().get(1).isPrivate());

        parent = parent.getInstanceParameters().get(0);
        assertEquals("List", parent.getDataType());
        assertTrue(parent.getInstanceParameters().get(0).isPrivate());
        assertTrue(parent.getInstanceParameters().get(1).isPrivate());

        parent = parent.getInstanceParameters().get(0);
        assertEquals("Person", parent.getDataType());
        assertTrue(parent.getInstanceParameters().get(0).isPrivate());
    }

    @Test
    public void calculateMethodModelTest() {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        String testName = "whenGetKomplex_thenReturnNull";
        simpleModelCreator.calculateMethodModel(testName);
        List<String> imports = simpleModelCreator.getModel().getTestClassModel().getImports();
        assertFalse(imports.isEmpty());
        assertEquals("de.tudo.naantg.testproject.weiter.Komplex", imports.get(0));
        assertNotNull(simpleModelCreator.getModel().getMethodOfCUT(testName));
        String expected = "\t\tKomplex actual = dritteKlasse.getKomplex();\n";
        assertEquals(expected, simpleModelCreator.getModel().getMethodOfCUT(testName).generateMethodStatement());
    }

    @Test
    public void calculateMethodTestWithEnums() {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(Inhalt.class);
        String testName = "whenGetContent_thenReturnNull";

        simpleModelCreator.calculateMethodModel(testName);

        assertNotNull(simpleModelCreator.getModel().getMethodOfCUT(testName));
        String expected = "\t\tList<Dinge> actual = inhalt.getContent();\n";
        assertEquals(expected, simpleModelCreator.getModel().getMethodOfCUT(testName).generateMethodStatement());

        List<String> imports = simpleModelCreator.getModel().getTestClassModel().getImports();
        assertEquals(2, imports.size());
        assertEquals("de.tudo.naantg.testproject.weiter.Dinge", imports.get(0));
        assertEquals("java.util.List", imports.get(1));
    }

    @Test
    public void calculateObjectsWithEnumTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setCut(Inhalt.class);
        String testName = "whenSetContent_thenNoError";
        Method method = InhaltTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(testName);

        simpleModelCreator.calculateObjects(method);
        assertEquals(1, simpleModelCreator.getModel().getParameters(testName).size());
        assertEquals(4, simpleModelCreator.getModel().getTestClassModel().getImports().size());
    }

    @Test
    public void calculateObjectsWithFields() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        String testName = "whenDoComplex_thenReturnNull";
        Method method = DritteKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(testName);

        simpleModelCreator.calculateObjects(method);

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertFalse(parameters.isEmpty());
        for (ObjectModel fieldModel : parameters) {
            System.out.println("data type: " +
                    fieldModel.getDataType() + ", identifier: " +
                    fieldModel.getIdentifier() + ", parentIdentifier: " +
                    fieldModel.getParentIdentifier() + ", value: " +
                    fieldModel.getValue() + ", isPrivate: " +
                    fieldModel.isPrivate() + ", instancesCount: " +
                    fieldModel.getInstanceParameters().size() + ", fieldInstancesCount: " +
                    fieldModel.getInstanceFields().size());
        }

        List<String> fieldStatements = new ArrayList<>();
        parameters.forEach(param -> fieldStatements.add(param.getDataType()));
        assertTrue(fieldStatements.containsAll(Arrays.asList("int", "long", "String", "String[]", "Komplex",
                "Komplex2", "Komplex3", "Komplex4", "List", "Person", "Box")));

        fieldStatements.clear();
        parameters.forEach(param -> fieldStatements.add(param.getIdentifier()));
        assertTrue(fieldStatements.containsAll(Arrays.asList("points", "aMillion", "iVar", "iVar2", "komplex",
                "str", "person", "iVar3", "box", "komplex2", "list", "list2", "komplex3", "list3", "list4",
                "array", "komplex4", "komplexes")));

        List<ObjectModel> komplexes = parameters.stream().filter(model -> model.getDataType().contains("Komplex"))
                .collect(Collectors.toList());
        assertEquals(4, komplexes.size());
        for (ObjectModel model : komplexes) {
            assertEquals(0, model.getInstanceFields().size());
        }
        assertEquals(7, simpleModelCreator.getModel().getObjectModels(testName).get(0).getInstanceFields().size());
        /*for (ObjectModel model : modelCreator.getModel().getObjectModels(testName).get(0).getInstanceFields()) {
            assertTrue(model.isPrivate());
        }*/
    }

    @Test
    public void calculateObjectsWithNoFields() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        String testName = "whenSetKomplex_thenGetKomplex_hasEqualValue";
        Method method = DritteKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(testName);

        simpleModelCreator.calculateObjects(method);

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertFalse(parameters.isEmpty());
        for (ObjectModel fieldModel : parameters) {
            System.out.println("data type: " +
                    fieldModel.getDataType() + ", identifier: " +
                    fieldModel.getIdentifier() + ", parentIdentifier: " +
                    fieldModel.getParentIdentifier() + ", value: " +
                    fieldModel.getValue() + ", instancesCount: " +
                    fieldModel.getInstanceParameters().size() + ", fieldInstancesCount: " +
                    fieldModel.getInstanceFields().size());
        }

        assertEquals(3, parameters.size());


    }

    @Test
    public void calculateObjectsWithParameterAndFields() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        simpleModelCreator.getModel().setTestClassModel(new TestClassModel());
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        simpleModelCreator.getModel().getTestClassModel().setImports(new ArrayList<>());
        String testName = "whenDoComplexWithPerson_thenReturnNull";
        Method method = DritteKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculateMethodModel(testName);

        simpleModelCreator.calculateObjects(method);

        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertFalse(parameters.isEmpty());
        for (ObjectModel fieldModel : parameters) {
            boolean isParamParent = fieldModel.getParameterOfParent() != null;
            boolean isFieldParent = fieldModel.getFieldOfParent() != null;
            System.out.println("data type: " +
                    fieldModel.getDataType() + ", identifier: " +
                    fieldModel.getIdentifier() + ", value: " +
                    fieldModel.getValue() + ", instancesCount: " +
                    fieldModel.getInstanceParameters().size() + ", fieldInstancesCount: " +
                    fieldModel.getInstanceFields().size());
            if (isParamParent)
                System.out.println("param parent: " + fieldModel.getParameterOfParent().getIdentifier());
            if (isFieldParent)
                System.out.println("field parent: " + fieldModel.getFieldOfParent().getIdentifier());

        }



    }

    @Test
    public void createTestCasesTestWithInitMethods() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        String testName = "whenCanBuyBox_thenReturnTrue";
        simpleModelCreator.getModel().setCut(DritteKlasse.class);
        simpleModelCreator.getModel().setTgClass(DritteKlasseTG.class);

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        List<ObjectModel> preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        assertEquals(1, simpleModelCreator.getModel().getCutObject(testName).getMethodCalls().size());
        MethodModel methodModel = simpleModelCreator.getModel().getCutObject(testName).getMethodCalls().get(0);
        String expected = "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, methodModel.generateMethodStatement());
        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(0, parameters.size());

        testName = "whenCanBuyBox_thenReturnFalse_2";

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        TestCaseModel model = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(model);
        assertEquals("initBox", model.getTestMethodName());
        ObjectModel cut = simpleModelCreator.getModel().getCutObject(testName);
        assertEquals(cut, model.getParameters().get(0));
        assertNull(model.getReturnObject());

        testName = "whenCanBuyBox_thenReturnFalse_3";
        String pkg = "de.tudo.naantg.testproject";
        simpleModelCreator.getModel().setProjectPackageUriWithDots(pkg);

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        testName = "whenDoComplexWithPerson_thenReturnValue_simple";

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(1, parameters.size());
        assertEquals("param1", parameters.get(0).getIdentifier());
        assertEquals("dritteKlasse.createPerson()", parameters.get(0).getValue());
        //cut = simpleModelCreator.getModel().getCutObject(testName);
        //methodModel = cut.getMethodCalls().get(0);
        //expected = "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        //assertEquals(expected, methodModel.generateMethodStatement());


        testName = "whenCanBuyBox_thenReturnFalse_5";

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        assertEquals(2, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
        cut = simpleModelCreator.getModel().getCutObject(testName);
        assertEquals("initialBoxes()", cut.getValue());

        testName = "whenDoComplexWithPerson_thenReturnNull_komplex";

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(0, preStates.size());
        assertEquals(2, parameters.size());
        assertEquals("param1", parameters.get(1).getIdentifier());
        methodModel = parameters.get(1).getMethodCalls().get(0);
        expected = "\t\tparam1.init();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2";
        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        preStates.add(new ObjectModel(int.class, "a", true));
        preStates.add(new ObjectModel(int.class, "b", true));

        simpleModelCreator.createTestCases(DritteKlasseTG.class.getMethod(testName));

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(2, preStates.size());
        assertEquals(2, parameters.size());
        assertEquals("param1", parameters.get(1).getIdentifier());
        methodModel = parameters.get(1).getMethodCalls().get(0);
        expected = "\t\tparam1.init(a, b);\n";
        assertEquals(expected, methodModel.generateMethodStatement());
        parameters.forEach(System.out::println);
    }

    @Test
    public void calculateInitMethods() {
        SimpleModelCreator simpleModelCreator = initCreator();
        ObjectModel cut = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);

        String testName = "whenCanBuyBox_thenReturnTrue";
        simpleModelCreator.getModel().setCutObject(cut, testName);
        String initValue = "this.initBuy()";

        simpleModelCreator.calculateInitMethods(initValue, testName);

        List<ObjectModel> preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        assertEquals(1, cut.getMethodCalls().size());
        MethodModel methodModel = cut.getMethodCalls().get(0);
        String expected = "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        testName = "givenThisInitBuy_whenCanBuyBox_thenReturnTrue";
        cut = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);
        simpleModelCreator.getModel().setCutObject(cut, testName);
        initValue = "";

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        assertEquals(1, cut.getMethodCalls().size());
        methodModel = cut.getMethodCalls().get(0);
        expected = "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "initialBoxes(this)";
        testName = "whenCanBuyBox_thenReturnFalse_2";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        TestCaseModel model = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(model);
        assertEquals("initialBoxes", model.getTestMethodName());
        assertEquals(cut, model.getParameters().get(0));
        assertNull(model.getReturnObject());

        initValue = "";
        testName = "givenTestInitBox_with_this_whenCanBuyBox_thenReturnFalse";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        assertEquals(0, preStates.size());
        model = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(model);
        assertEquals("initialBoxes", model.getTestMethodName());
        assertEquals(cut, model.getParameters().get(0));
        assertNull(model.getReturnObject());

        initValue = "Statistik.work()";
        testName = "whenCanBuyBox_thenReturnFalse_3";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        String pkg = "de.tudo.naantg.testproject";
        simpleModelCreator.getModel().setProjectPackageUriWithDots(pkg);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "";
        testName = "givenStaticStatistik_work_whenCanBuyBox_thenReturnFalse";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "initialBoxes(this); Statistik.work()";
        testName = "whenCanBuyBox_thenReturnFalse_4";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());
        model = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(model);
        assertEquals("initialBoxes", model.getTestMethodName());
        assertEquals(cut, model.getParameters().get(0));
        assertNull(model.getReturnObject());

        initValue = "p1 = this.createPerson()";
        testName = "whenDoComplexWithPerson_thenReturnValue_simple";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        MethodModel methodOfCut = new MethodModel();
        methodOfCut.setParameters(new Class<?>[]{Person.class});
        methodOfCut.setReturnObject(new ObjectModel(Person.class, "person", true));
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        assertEquals("dritteKlasse.createPerson()", preStates.get(0).getValue());
        //methodModel = cut.getMethodCalls().get(1);
        //expected = "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        //assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "";
        testName = "given_param_ofThisCreatePerson_whenDoComplexWithPerson_thenReturnValue_simple";
        cut = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        assertEquals("dritteKlasse.createPerson()", preStates.get(0).getValue());
        //methodModel = cut.getMethodCalls().get(0);
        //expected = "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        //assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "this = initialBoxes()";
        testName = "whenCanBuyBox_thenReturnFalse_5";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().clear();

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        assertEquals(1, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
        assertEquals("initialBoxes()", cut.getValue());

        initValue = "";
        testName = "given_this_ofTestInitialBoxes_whenCanBuyBox_thenReturnFalse";
        cut = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().clear();

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        assertEquals(1, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
        assertEquals("initialBoxes()", cut.getValue());

        initValue = "p1.init()";
        testName = "whenDoComplexWithPerson_thenReturnNull_komplex";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tparam1.init();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "";
        testName = "givenParamInit_whenDoComplexWithPerson_thenReturnNull";
        cut = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tparam1.init();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        initValue = "p1.init(a, b)";
        testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2";
        simpleModelCreator.getModel().setCut(cut.getObjectClass());
        simpleModelCreator.getModel().setCutObject(cut, testName);
        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        preStates.add(new ObjectModel(int.class, "a", true));
        preStates.add(new ObjectModel(int.class, "b", true));
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.calculateInitMethods(initValue, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(3, preStates.size());
        assertEquals("param1", preStates.get(2).getIdentifier());
        methodModel = preStates.get(2).getMethodCalls().get(0);
        expected = "\t\tparam1.init(a, b);\n";
        assertEquals(expected, methodModel.generateMethodStatement());
    }

    @Test
    public void createMethodCallTest() {
        SimpleModelCreator simpleModelCreator = initCreator();
        ObjectModel cutObject = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);

        String testName = "whenCanBuyBox_thenReturnTrue";
        String methodCallName = "initBuy";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        List<ObjectModel> preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);

        // this.initBuy()
        simpleModelCreator.createMethodCall(methodCallName, "THIS", new ArrayList<>(), "", false, testName);

        assertEquals(0, preStates.size());
        assertEquals(1, cutObject.getMethodCalls().size());
        MethodModel methodModel = cutObject.getMethodCalls().get(0);
        String expected = "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // initialBoxes(this)
        methodCallName = "initialBoxes";
        testName = "whenCanBuyBox_thenReturnFalse_2";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);

        simpleModelCreator.createMethodCall(methodCallName, "",  Collections.singletonList("THIS"), "", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        TestCaseModel model = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(model);
        assertEquals(methodCallName, model.getTestMethodName());
        assertEquals(cutObject, model.getParameters().get(0));
        assertNull(model.getReturnObject());

        // Statistik.work()
        methodCallName = "work";
        testName = "whenCanBuyBox_thenReturnFalse_3";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        String pkg = "de.tudo.naantg.testproject";
        simpleModelCreator.getModel().setProjectPackageUriWithDots(pkg);

        simpleModelCreator.createMethodCall(methodCallName, "Statistik",  new ArrayList<>(), "", true, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // p1 = this.createPerson()
        methodCallName = "createPerson";
        testName = "whenDoComplexWithPerson_thenReturnValue_simple";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        MethodModel methodOfCut = new MethodModel();
        methodOfCut.setParameters(new Class<?>[]{Person.class});
        methodOfCut.setReturnObject(new ObjectModel(Person.class, "person", true));
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.createMethodCall(methodCallName, "THIS",  new ArrayList<>(), "param1", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        assertEquals("dritteKlasse.createPerson()", preStates.get(0).getValue());
        //methodModel = cutObject.getMethodCalls().get(1);
        //expected = "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        //assertEquals(expected, methodModel.generateMethodStatement());

        // this = initialBoxes()
        methodCallName = "initialBoxes";
        testName = "whenCanBuyBox_thenReturnFalse_5";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().clear();

        simpleModelCreator.createMethodCall(methodCallName, "",  new ArrayList<>(), "THIS", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(0, preStates.size());
        assertEquals(1, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
        assertEquals("initialBoxes()", cutObject.getValue());

        // p1.init()
        methodCallName = "init";
        testName = "whenDoComplexWithPerson_thenReturnNull_komplex";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().clear();
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.createMethodCall(methodCallName, "param1",  new ArrayList<>(), "", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tparam1.init();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // p1.init(a, b)
        methodCallName = "init";
        testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2";
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        preStates.add(new ObjectModel(int.class, "a", true));
        preStates.add(new ObjectModel(int.class, "b", true));
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.createMethodCall(methodCallName, "param1",  Arrays.asList("a", "b"), "", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(3, preStates.size());
        assertEquals("param1", preStates.get(2).getIdentifier());
        methodModel = preStates.get(2).getMethodCalls().get(0);
        expected = "\t\tparam1.init(a, b);\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2_2";
        simpleModelCreator.getModel().getGivenInitObjects(testName).clear();
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        simpleModelCreator.getModel().setMethodOfCUT(methodOfCut, testName);

        simpleModelCreator.createMethodCall(methodCallName, "param1",  Arrays.asList("a", "b"), "", false, testName);

        preStates = simpleModelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(3, preStates.size());
        assertEquals("param1", preStates.get(0).getIdentifier());
        methodModel = preStates.get(0).getMethodCalls().get(0);
        expected = "\t\tparam1.init(a, b);\n";
        assertEquals(expected, methodModel.generateMethodStatement());
    }

    @Test
    public void validateAndAddInitMethodStatementTest() {
        SimpleModelCreator simpleModelCreator = initCreator();

        // this.initBuy()
        DritteKlasse cut = new DritteKlasse();
        String identifier = "dritteKlasse";
        ObjectModel cutObject = new ObjectModel(cut.getClass(), identifier, true);
        String methodCallName = "initBuy";

        simpleModelCreator.validateAndAddInitMethodStatement(methodCallName, 0, cutObject);

        MethodModel methodModel = cutObject.getMethodCalls().get(0);
        String expected = "\t\tdritteKlasse.initBuy();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // Statistik.work()
        methodCallName = "work";
        identifier = "Statistik";
        cutObject = new ObjectModel(Statistik.class, identifier, false);

        simpleModelCreator.validateAndAddInitMethodStatement(methodCallName, 0, cutObject);

        methodModel = cutObject.getMethodCalls().get(0);
        expected = "\t\tStatistik.work();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // p1 = this.createPerson()
        methodCallName = "createPerson";
        identifier = "dritteKlasse";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);

        simpleModelCreator.validateAndAddInitMethodStatement(methodCallName, 0, cutObject);

        methodModel = cutObject.getMethodCalls().get(0);
        expected = "\t\tPerson actual = dritteKlasse.createPerson();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // p1.init()
        methodCallName = "init";
        identifier = "param1";
        cutObject = new ObjectModel(Person.class, identifier, true);

        simpleModelCreator.validateAndAddInitMethodStatement(methodCallName, 0, cutObject);

        methodModel = cutObject.getMethodCalls().get(0);
        expected = "\t\tparam1.init();\n";
        assertEquals(expected, methodModel.generateMethodStatement());

        // p1.init(a, b)
        methodCallName = "init";
        identifier = "param1";
        cutObject = new ObjectModel(Person.class, identifier, true);

        simpleModelCreator.validateAndAddInitMethodStatement(methodCallName, 2, cutObject);

        methodModel = cutObject.getMethodCalls().get(0);
        expected = "\t\tparam1.init(param1, param2);\n";
        assertEquals(expected, methodModel.generateMethodStatement());
    }

    @Test
    public void createInitHelperMethodTest() {
        SimpleModelCreator simpleModelCreator = initCreator();
        DritteKlasse cut = new DritteKlasse();
        String identifier = "dritteKlasse";
        // initialBoxes(this)
        String methodCallName = "initialBoxes";
        String testName = "whenCanBuyBox_thenReturnFalse_2";
        ObjectModel cutObject = new ObjectModel(cut.getClass(), identifier, true);
        simpleModelCreator.getModel().setCut(cut.getClass());
        simpleModelCreator.getModel().setCutObject(cutObject, testName);

        simpleModelCreator.createInitHelperMethod(methodCallName, Collections.singletonList("THIS"), "", testName);

        TestCaseModel methodModel = simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().get(0);
        assertNotNull(methodModel);
        assertEquals(methodCallName, methodModel.getTestMethodName());
        assertEquals(cutObject, methodModel.getParameters().get(0));
        assertNull(methodModel.getReturnObject());

        // this = initialBoxes()
        methodCallName = "initialBoxes";
        testName = "whenCanBuyBox_thenReturnFalse_5";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        simpleModelCreator.getModel().setCut(cut.getClass());
        simpleModelCreator.getModel().setCutObject(cutObject, testName);
        simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().clear();

        simpleModelCreator.createInitHelperMethod(methodCallName, new ArrayList<>(), "THIS", testName);

        assertEquals(1, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
        assertEquals("initialBoxes()", cutObject.getValue());
    }

    @Test
    public void calculateParametersWithPreState() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        Class<?> cut = DritteKlasse.class;
        simpleModelCreator.getModel().setCut(cut);
        String testName = "whenDoComplexWithPerson_thenReturnValue_simple";
        simpleModelCreator.calculateMethodModel(testName);
        ObjectModel objectModel = new ObjectModel(cut, "dritteKlasse", true);
        simpleModelCreator.getModel().setCutObject(objectModel, testName);

        Method method = DritteKlasseTG.class.getMethod(testName);
        simpleModelCreator.calculatesParameters(method);

        String initValue = "p1 = createPerson()";
        simpleModelCreator.calculateInitMethods(initValue, testName);
        List<ObjectModel> givenParameters = simpleModelCreator.getModel().getTestMethodModel(testName).getGivenInitObjects();
        List<ObjectModel> parameters = simpleModelCreator.getModel().getParameters(testName);
        assertEquals(0, givenParameters.size());
        assertEquals(1, parameters.size());
        assertEquals("param1", parameters.get(0).getIdentifier());
        assertEquals("Person", parameters.get(0).getDataType());
        assertEquals("createPerson()", parameters.get(0).getValue());
        assertEquals(1, simpleModelCreator.getModel().getTestClassModel().getPrivateHelperMethods().size());
    }

    @Test
    public void setRandomConfigsTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        Class<?> tgClass = DritteKlasseTG.class;
        String testName = "whenCanBuyBox_thenReturnTrue";
        Method method = tgClass.getMethod(testName);

        simpleModelCreator.setRandomConfigs(tgClass, method);

        RandomConfigs randomConfigs = simpleModelCreator.getModel().getTestMethodModel(testName).getRandomConfigs();
        assertNotNull(randomConfigs);
        assertEquals("", randomConfigs.getMinValue());
        assertEquals("", randomConfigs.getMaxValue());
        assertEquals(0, randomConfigs.getMinListSize());
        assertEquals(10, randomConfigs.getMaxListSize());
        assertEquals(0, randomConfigs.getMinStringLength());
        assertEquals(20, randomConfigs.getMaxStringLength());
        assertArrayEquals(new Alphabet[] {Alphabet.ALL}, randomConfigs.getAlphabet());
    }

    private void initDefaultConfigs(GeneratorModel model, String testName) {
        RandomConfigs randomConfigs = new RandomConfigs();
        randomConfigs.setMinValue("");
        randomConfigs.setMaxValue("");
        randomConfigs.setMinListSize(0);
        randomConfigs.setMaxListSize(10);
        randomConfigs.setMinStringLength(0);
        randomConfigs.setMaxStringLength(20);
        randomConfigs.setAlphabet(new Alphabet[] {Alphabet.ALL});
        model.getTestMethodModel(testName).setRandomConfigs(randomConfigs);
    }

    @Test
    public void calculateGivenConstructorsTest() {
        SimpleModelCreator modelCreator = initCreator();
        String testName = "whenPostRegister_thenRedirectToLogin_1";
        initDefaultConfigs(modelCreator.getModel(), testName);
        String definition = "p2 = ScheinEntity(String, String)";
        Class<?>[] parameters = new Class<?>[] {Model.class, ScheinEntity.class};
        List<String> parsed = AnnotationParser.parseConstructors(definition, parameters);

        modelCreator.calculateGivenConstructors(parsed, parameters, testName, InitType.NONE);

        List<ObjectModel> givenParams = modelCreator.getModel().getGivenInitObjects(testName);
        List<ObjectModel> params = modelCreator.getModel().getParameters(testName);
        assertEquals(1, givenParams.size());
        assertEquals(3, params.size());
        params.forEach(System.out::println);
        assertEquals("ScheinEntity", params.get(0).getDataType());
        assertEquals("String", params.get(1).getDataType());
        assertEquals("String", params.get(2).getDataType());
    }

    @Test
    public void calculateAnnotationsTest() throws NoSuchMethodException {
        SimpleModelCreator modelCreator = initCreator();
        modelCreator.getModel().setTgClass(HauptscheinTG.class);
        String testName = "whenCalc_thenReturnException";
        Method method = HauptscheinTG.class.getMethod(testName);

        modelCreator.calculateAnnotations(method);

        List<Class<?>> annos = modelCreator.getModel().getTestMethodModel(testName).getAnnotations();
        List<String> imports = modelCreator.getModel().getTestClassModel().getImports();
        assertEquals(1, annos.size());
        assertEquals(Params.class, annos.get(0));
        assertTrue(imports.contains(Params.class.getName()));

        testName = "whenAnnotationTest";
        method = HauptscheinTG.class.getMethod(testName);

        modelCreator.calculateAnnotations(method);

        annos = modelCreator.getModel().getTestMethodModel(testName).getAnnotations();
        assertEquals(3, annos.size());
        assertEquals(Params.class, annos.get(0));
        assertEquals(Param.class, annos.get(1));
        assertEquals(de.tudo.naantg.annotations.RandomConfigs.class, annos.get(2));

        testName = "whenAnnotationTest_2";
        method = HauptscheinTG.class.getMethod(testName);

        modelCreator.calculateAnnotations(method);

        annos = modelCreator.getModel().getTestMethodModel(testName).getAnnotations();
        assertEquals(0, annos.size());
    }

    @Test
    public void addObjectTest() {
        SimpleModelCreator modelCreator = initCreator();
        DritteKlasse cut = new DritteKlasse();
        String identifier = "dritteKlasse";
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);

        String testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2";
        ObjectModel cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);
        ValueCreator valueCreator = new ValueCreator(modelCreator.getModel(), testName, InitType.NONE);
        // int a = 3; int b = 4
        String type = "int";
        String name = "a";
        String val = "3";
        List<ObjectModel> inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(type, name, val, valueCreator, testName);
        assertEquals(1, inits.size());
        assertEquals("\t\tint a = 3;\n", ObjectGenerator.generateObjectStatement(inits.get(0)));

        type = "int";
        name = "b";
        val = "4";

        modelCreator.addObject(type, name, val, valueCreator, testName);
        assertEquals(2, inits.size());
        assertEquals("\t\tint b = 4;\n", ObjectGenerator.generateObjectStatement(inits.get(1)));

        testName = "whenDoComplexWithPerson_thenReturnValue_2";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);
        valueCreator = new ValueCreator(modelCreator.getModel(), testName, InitType.NONE);
        // List<Box> boxes = null
        type = "List<Box>";
        name = "boxes";
        val = "NULL";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(type, name, val, valueCreator, testName);
        assertEquals(1, inits.size());
        assertEquals("\t\tList<Box> boxes = null;\n", ObjectGenerator.generateObjectStatement(inits.get(0)));

        testName = "whenCanBuyBox_thenReturnTrue_2";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);
        // this.points = 50
        String caller = "THIS";
        List<String> fields = Collections.singletonList("points");
        val = "50";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(caller, fields, val, new ArrayList<>(), testName);
        assertEquals(0, inits.size());
        assertEquals(1, cutObject.getFieldCalls().size());
        assertEquals("\t\tdritteKlasse.setPoints(50);\n", ObjectGenerator.generateFieldCalls(cutObject.getFieldCalls().get(0)));

        testName = "whenCanBuyBox_thenReturnFalse_6";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);
        // Person p; List ps = [p]; p.name = Jan*a; this.komplex.persons = ps
        type = "Person";
        name = "p";
        val = "";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(type, name, val, valueCreator, testName);
        assertEquals(1, inits.size());
        assertEquals(1, modelCreator.getModel().getParameters(testName).size());
        assertEquals("\t\tPerson p = new Person(str);\n", ObjectGenerator.generateObjectStatement(inits.get(0)));
        assertEquals(1, inits.get(0).getInstanceParameters().size());

        type = "List";
        name = "ps";
        val = "[p]";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(type, name, val, valueCreator, testName);
        assertEquals(2, inits.size());
        assertEquals(2, modelCreator.getModel().getParameters(testName).size());
        assertEquals("\t\tList<Person> ps = Arrays.asList(p);\n",
                ObjectGenerator.generateObjectStatement(inits.get(1), modelCreator.getModel(),true));

        caller = "p";
        fields = Collections.singletonList("name");
        val = "Jan*a";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(caller, fields, val, new ArrayList<>(), testName);
        assertEquals(2, inits.size());
        assertEquals(1, inits.get(0).getFieldCalls().size());
        assertEquals("\t\tp.setName(\"Jan*a\");\n", ObjectGenerator.generateFieldCalls(inits.get(0).getFieldCalls().get(0)));

        caller = "THIS";
        fields = Arrays.asList("komplex", "persons");
        val = "ps";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.addObject(caller, fields, val, new ArrayList<>(), testName);
        assertEquals(2, inits.size());
        assertEquals(1, cutObject.getFieldCalls().size());
        assertEquals("\t\tdritteKlasse.getKomplex().getPersons().addAll(ps);\n", ObjectGenerator.generateFieldCalls(cutObject.getFieldCalls().get(0)));
    }

    @Test
    public void calculateInitStateTest() {
        SimpleModelCreator modelCreator = initCreator();
        DritteKlasse cut = new DritteKlasse();
        String identifier = "dritteKlasse";
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);

        String testName = "whenDoComplexWithPerson_thenReturnNull_komplex_2";
        ObjectModel cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);

        String initValue = "int a = 3; int b = 4";
        List<ObjectModel> inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.calculateInitState(initValue, InitType.NONE, testName);

        assertEquals(2, inits.size());
        assertEquals("\t\tint a = 3;\n", ObjectGenerator.generateObjectStatement(inits.get(0)));
        assertEquals("\t\tint b = 4;\n", ObjectGenerator.generateObjectStatement(inits.get(1)));

        testName = "whenDoComplexWithPerson_thenReturnValue_2";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);

        initValue = "List<Box> boxes = null";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.calculateInitState(initValue, InitType.NONE, testName);

        assertEquals(1, inits.size());
        assertEquals("\t\tList<Box> boxes = null;\n", ObjectGenerator.generateObjectStatement(inits.get(0)));

        testName = "whenCanBuyBox_thenReturnTrue_2";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);

        initValue = "this.points = 50";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.calculateInitState(initValue, InitType.NONE, testName);

        assertEquals(0, inits.size());
        assertEquals(1, cutObject.getFieldCalls().size());
        assertEquals("\t\tdritteKlasse.setPoints(50);\n", ObjectGenerator.generateFieldCalls(cutObject.getFieldCalls().get(0)));

        testName = "whenCanBuyBox_thenReturnFalse_6";
        cutObject = new ObjectModel(cut.getClass(), identifier, true);
        modelCreator.getModel().setCut(cut.getClass());
        modelCreator.getModel().setCutObject(cutObject, testName);

        initValue = "Person p; List ps = [p]; p.name = Jan*a; this.komplex.persons = ps";
        inits = modelCreator.getModel().getGivenInitObjects(testName);

        modelCreator.calculateInitState(initValue, InitType.NONE, testName);

        assertEquals(2, inits.size());
        assertEquals(1, cutObject.getFieldCalls().size());
        assertEquals("\t\tPerson p = new Person(str);\n", ObjectGenerator.generateObjectStatement(inits.get(0)));
        assertEquals(1, inits.get(0).getInstanceParameters().size());
        assertEquals("\t\tList<Person> ps = Arrays.asList(p);\n",
                ObjectGenerator.generateObjectStatement(inits.get(1), modelCreator.getModel(),true));
        assertEquals("\t\tp.setName(\"Jan*a\");\n", ObjectGenerator.generateFieldCalls(inits.get(0).getFieldCalls().get(0)));
        assertEquals("\t\tdritteKlasse.getKomplex().getPersons().addAll(ps);\n", ObjectGenerator.generateFieldCalls(cutObject.getFieldCalls().get(0)));
    }

    @Test
    public void findCutTest() {
        SimpleModelCreator modelCreator = new SimpleModelCreator("", "", "");
        Class<?> tgClass = NoNameTG.class;
        Class<?> cut = modelCreator.findCut(tgClass);
        assertNull(cut);

        modelCreator = new SimpleModelCreator("", "", "");
        modelCreator.setGivenCut(ScheinJob.class);
        cut = modelCreator.findCut(tgClass);
        assertEquals(ScheinJob.class, cut);

        modelCreator = new SimpleModelCreator("", "", "");
        tgClass = DritteKlasseTG.class;
        cut = modelCreator.findCut(tgClass);
        assertNull(cut);
    }

    @Test
    public void createSetGetMethodTest() throws NoSuchFieldException {
        SimpleModelCreator modelCreator = initCreator();
        SimpleAssertionCreator assertionCreator = new SimpleAssertionCreator();
        assertionCreator.setModel(modelCreator.getModel());
        modelCreator.setAssertionCreator(assertionCreator);
        Class<?> cut = ScheinJob.class;
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);
        modelCreator.getModel().setCut(cut);
        String testName = "generatedSetGetTestOfJob";

        modelCreator.createSetGetMethod(cut.getDeclaredField("job"));

        List<Assertion> assertions = modelCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals("\t\tassertEquals(param1, actual);\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void createSetterGetterTestsTest() {
        SimpleModelCreator modelCreator = initCreator();
        SimpleAssertionCreator assertionCreator = new SimpleAssertionCreator();
        modelCreator.setAssertionCreator(assertionCreator);
        Class<?> cut = ScheinJob.class;
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);
        modelCreator.getModel().setCut(cut);

        modelCreator.createSetterGetterTests(NoNameTG.class);

        String testName = "generatedSetGetTestOfJob";
        List<Assertion> assertions = modelCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals("\t\tassertEquals(param1, actual);\n", assertions.get(0).generateAssertion());

        testName = "generatedSetGetTestOfJobId";
        assertions = modelCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals("\t\tassertEquals(param1, actual);\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void createConstructorTestTest() {
        SimpleModelCreator modelCreator = initCreator();
        SimpleAssertionCreator assertionCreator = new SimpleAssertionCreator();
        modelCreator.setAssertionCreator(assertionCreator);
        assertionCreator.setModel(modelCreator.getModel());
        Class<?> cut = ScheinJob.class;
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);
        modelCreator.getModel().setCut(cut);
        Constructor<?>[] constructors = ScheinJob.class.getDeclaredConstructors();
        assertEquals(2, constructors.length);

        modelCreator.createConstructor(constructors[0], 1);

        String testName = "generatedConstructorOfJobId1";
        assertNull(modelCreator.getModel().getTestClassModel().getTestCaseModels().get(testName));
        List<TestCaseModel> helpMethods = modelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(1, helpMethods.size());
        assertEquals(0, helpMethods.get(0).getObjectModels().size());

        modelCreator.createConstructor(constructors[1], 2);

        testName = "generatedConstructorOfJobId2";
        assertNull(modelCreator.getModel().getTestClassModel().getTestCaseModels().get(testName));
        helpMethods = modelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(2, helpMethods.size());
        assertEquals(1, helpMethods.get(1).getObjectModels().size());
        assertEquals("str", helpMethods.get(1).getObjectModels().get(0).getIdentifier());
    }

    @Test
    public void createConstructorsTest() {
        SimpleModelCreator modelCreator = initCreator();
        SimpleAssertionCreator assertionCreator = new SimpleAssertionCreator();
        modelCreator.setAssertionCreator(assertionCreator);
        Class<?> cut = ScheinJob.class;
        String pkg = "de.tudo.naantg.testproject";
        modelCreator.getModel().setProjectPackageUriWithDots(pkg);
        modelCreator.getModel().setCut(cut);

        modelCreator.createConstructors(NoNameTG.class);

        String testName = "generatedConstructorOfJobId1";
        assertNull(modelCreator.getModel().getTestClassModel().getTestCaseModels().get(testName));
        List<TestCaseModel> helpMethods = modelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(1, helpMethods.size());
        testName = "generatedConstructorOfJobId2";
        assertNull(modelCreator.getModel().getTestClassModel().getTestCaseModels().get(testName));
        helpMethods = modelCreator.getModel().getTestClassModel().getPrivateHelperMethods();
        assertEquals(1, helpMethods.get(0).getObjectModels().size());
        assertEquals("str", helpMethods.get(0).getObjectModels().get(0).getIdentifier());
    }

}