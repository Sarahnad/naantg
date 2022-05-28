package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.generator.ObjectGenerator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.ErsteKlasse;
import de.tudo.naantg.testproject.weiter.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ValueCreatorTest {

    @Test
    void calculateValue() {
    }

    @Test
    void calculateFields() {
    }

    @Test
    public void calculateFieldsTest() throws NoSuchMethodException {
        String testName = "whenDoComplex_thenReturnNull";
        Method methodToTest = DritteKlasse.class.getMethod("doComplex");
        ValueCreator valueCreator = initCreator(DritteKlasse.class, testName, methodToTest, InitType.ALL_DEFAULT);
        List<ObjectModel> parameters = valueCreator.getModel().getParameters(testName);
        ObjectModel parent = new ObjectModel(DritteKlasse.class, "dritteKlasse", true);
        valueCreator.getModel().setCutObject(parent, testName);
        parameters.add(parent);

        valueCreator.calculateFields(parent);

        assertFalse(parameters.isEmpty());
        for (ObjectModel fieldModel : parameters) {
            System.out.println("data type: " +
                    fieldModel.getDataType() + ", name: " +
                    fieldModel.getObjectClass().getName() + ", identifier: " +
                    fieldModel.getIdentifier() + ", parentIdentifier: " +
                    fieldModel.getParentIdentifier() + ", value: " +
                    fieldModel.getValue() + ", instancesCount: " +
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
    }

    @Test
    void calculateField() {
    }

    @Test
    public void calculatePrimitiveParametersTest() {
        String testName = "whenSum_thenReturnValue";
        calculateParameterTest(testName, "int", null);

        testName = "whenGetOneOrTwo_withTrue_thenReturn1";
        calculateParameterTest(testName, "boolean", "true");

        testName = "whenIsOne_thenReturnFalse";
        calculateParameterTest(testName, "String", null);

        testName = "whenIsOne_withOne_thenReturnTrue";
        calculateParameterTest(testName, "String", "\"One\"");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "char", null);

        testName = "whenHaveFun_withH_thenReturnValue";
        calculateParameterTest(testName, "char", "'H'");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "short", null);

        testName = "whenHaveFun_with12345_thenReturnValue";
        calculateParameterTest(testName, "short", "12345");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "long", null);

        testName = "whenHaveFun_withNeg12345_thenReturnValue";
        calculateParameterTest(testName, "long", "-12345L");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "byte", null);

        testName = "whenHaveFun_with16_thenReturnValue";
        calculateParameterTest(testName, "byte", "16");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "float", null);

        testName = "whenHaveFun_withNeg123p456_thenReturnValue";
        calculateParameterTest(testName, "float", "-123.456f");

        testName = "whenHaveFun_thenReturnValue";
        calculateParameterTest(testName, "double", null);

        testName = "whenHaveFun_with123p456_thenReturnValue";
        calculateParameterTest(testName, "double", "123.456d");

        testName = "whenSetIndex_with55_thenGetIndex_hasValue55";
        calculateParameterTest(testName, "int", "55");
    }

    private void calculateParameterTest(String testName, String dataType, String expectedValue) {
        ObjectModel objectModel = new ObjectModel(dataType, "param1", true);
        ValueCreator valueCreator = initCreator(testName);

        valueCreator.calculatePrimitiveParameters(objectModel);

        String actualValue = objectModel.getValue();
        assertNotNull(actualValue);

        String expected;
        if (Utils.isArray(dataType)) {
            String type2 = dataType + " ";
            if (expectedValue == null) type2 = dataType.replace("[]", "[0]");
            expected = "\t\t" + dataType + " param1 = new " + type2 + actualValue + ";\n";
        }
        else {
            String value = expectedValue != null ? expectedValue : actualValue;
            expected = "\t\t" + dataType + " param1 = " + value + ";\n";
        }

        assertEquals(expected, ObjectGenerator.generateObjectStatement(objectModel));
    }

    private ValueCreator initCreator(String testName) {
        GeneratorModel model = new GeneratorModel();
        model.setTestClassModel(new TestClassModel());
        initDefaultConfigs(model, testName);
        return new ValueCreator(model, testName, InitType.NONE);
    }

    private ValueCreator initCreator(Class<?> cut, String testName, Method methodToTest) {
        GeneratorModel model = new GeneratorModel();
        model.setTestClassModel(new TestClassModel());
        model.setCut(cut);
        model.setMethodOfCUT(new MethodModel(), testName);
        model.getMethodOfCUT(testName).setMethodToTest(methodToTest);
        initDefaultConfigs(model, testName);
        return new ValueCreator(model, testName, InitType.NONE);
    }

    private ValueCreator initCreator(Class<?> cut, String testName, Method methodToTest, InitType initType) {
        GeneratorModel model = new GeneratorModel();
        model.setTestClassModel(new TestClassModel());
        model.setCut(cut);
        model.setMethodOfCUT(new MethodModel(), testName);
        model.getMethodOfCUT(testName).setMethodToTest(methodToTest);
        initDefaultConfigs(model, testName);
        return new ValueCreator(model, testName, initType);
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
    public void calculateEnumParameterTest() {
        String testName = "whenSetDinge";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel objectModel = new ObjectModel(Dinge.class, "param1", true);

        valueCreator.calculateEnumParameter(objectModel);

        System.out.println(objectModel.getValue());
        assertNotNull(objectModel.getValue());
        assertNotEquals("", objectModel.getValue());

        testName = "whenSetDinge_withGlas";
        valueCreator = initCreator(testName);
        objectModel = new ObjectModel(Dinge.class, "param1", false);
        objectModel.setValue(Dinge.GLAS.toString());

        valueCreator.calculateEnumParameter(objectModel);

        assertEquals("\t\tDinge param1 = Dinge.GLAS;\n", ObjectGenerator.generateObjectStatement(objectModel));

        testName = "whenSetDinge_withNull";
        valueCreator = initCreator(testName);

        objectModel = new ObjectModel(Dinge.class, "param1", false);

        valueCreator.calculateEnumParameter(objectModel);

        assertEquals("\t\tDinge param1 = null;\n", ObjectGenerator.generateObjectStatement(objectModel));
    }

    @Test
    public void calculateCollectionParametersTest() throws NoSuchMethodException {
        String testName = "whenIsEvenSize_thenReturnTrue";
        Method methodToTest = KlasseMitInnererKlasse.class.getMethod("isEvenSize", Collection.class);
        ValueCreator valueCreator = initCreator(KlasseMitInnererKlasse.class, testName, methodToTest);
        ObjectModel objectModel = new ObjectModel(Collection.class, "param1", true);
        objectModel.setValue("{1;2;3;4}");

        valueCreator.calculateCollectionParameters(objectModel);

        String value = objectModel.getValue();
        assertNotNull(value);
        String expected = "\t\tCollection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4});\n";
        assertEquals(expected, ObjectGenerator.generateObjectStatement(objectModel));
    }

    @Test
    public void calculateCollectionParametersWithEnumsTest() {
        String testName = "whenSetContent_with_2_values_thenGetContent_hasEqualValues";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel parentList = new ObjectModel("List", "param1", true);
        parentList.getGenericClasses().add(Dinge.class);

        valueCreator.calculateCollectionParameters(parentList);

        assertNotNull(parentList.getValue());
        assertNotEquals("", parentList.getValue());
        System.out.println(parentList.getValue());
    }

    @Test
    void calculateListObjectInstance() {
    }

    @Test
    public void calculateObjectInitForCollectionTest() {
        String testName = "whenSetKomplex_thenGetKomplex3";
        ValueCreator valueCreator = initCreator(DritteKlasse.class, testName, null);
        ObjectModel objectModel = new ObjectModel(List.class, "list", true);
        objectModel.getGenericClasses().add(Person.class);

        valueCreator.calculateObjectInitForCollection(objectModel, 5);

        List<ObjectModel> parameters = valueCreator.getModel().getParameters(testName);
        parameters.add(objectModel);
        assertEquals(11, parameters.size());
        List<String> paramsStatements = new ArrayList<>();
        parameters.forEach(param -> paramsStatements.add(param.getDataType()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("List", "String", "Person")));
        paramsStatements.clear();
        parameters.forEach(param -> paramsStatements.add(param.getIdentifier()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("list", "str", "person", "str5", "person4")));
    }

    @Test
    public void calculateObjectInitForCollectionWithEnums() {
        String testName = "whenSetContent_with_2_values_thenGetContent_hasEqualValues";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel objectModel = new ObjectModel(List.class, "param1", true);
        objectModel.getGenericClasses().add(Dinge.class);

        valueCreator.calculateObjectInitForCollection(objectModel, 2);

        assertEquals(2, objectModel.getInstanceParameters().size());
        assertEquals("\t\tList<Dinge> param1 = new ArrayList<>();\n",
                ObjectGenerator.generateObjectStatement(objectModel, false, true));

    }

    @Test
    void calculateArrayObject() {
    }

    @Test
    public void calculateArrayObjectTest() {
        String testName = "whenIsPositive_thenReturnTrue";
        calculateParameterTest(testName, "int[]", null);
    }

    @Test
    void calculateImports() {
    }

    @Test
    public void withValuesTest() {
        String testName = "whenSetSeven_with_7_values_thenGetSeven_hasValues";
        ValueCreator valueCreator = initCreator(testName);
        assertTrue(valueCreator.withValues());

        testName = "whenSetSeven_with_7_values_thenGetSeven";
        valueCreator = initCreator(testName);
        assertTrue(valueCreator.withValues());

        testName = "whenSetSeven_thenGetSeven_hasValues";
        valueCreator = initCreator(testName);
        assertTrue(valueCreator.withValues());

        testName = "whenSetSeven_thenGetSeven";
        valueCreator = initCreator(testName);
        assertFalse(valueCreator.withValues());
    }

    @Test
    public void withValueSizeTest() {
        String testName = "whenSetSeven_with_7_values_thenGetSeven_hasValues";
        ValueCreator valueCreator = initCreator(testName);
        assertEquals(7, valueCreator.withValueSize());

        testName = "whenSetSeven_with_ten_values_thenGetSeven_hasValues";
        valueCreator = initCreator(testName);
        assertEquals(-1, valueCreator.withValueSize());
    }

    @Test
    void calculateObjectParameters() {
    }

    @Test
    public void calculateObjectParametersTestWithDefaultConstructor() {
        String testName = "whenInit_thenReturnTrue";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel objectModel = new ObjectModel(ErsteKlasse.class, "param1", true);

        valueCreator.calculateObjectParameters(objectModel);

        assertEquals(0, objectModel.getInstanceParameters().size());
        assertEquals("\t\tErsteKlasse param1 = new ErsteKlasse();\n",
                ObjectGenerator.generateObjectStatement(objectModel));
        assertFalse(valueCreator.getModel().getTestClassModel().getImports().isEmpty());
        assertEquals("de.tudo.naantg.testproject.ErsteKlasse",
                valueCreator.getModel().getTestClassModel().getImports().get(0));
    }

    @Test
    public void calculateObjectParametersTestWithSimpleValuesConstructor() {
        String testName = "whenSetKomplex_thenGetKomplex";
        ObjectModel objectModel = new ObjectModel(Komplex.class, "param1", true);
        ValueCreator valueCreator = initCreator(testName);

        valueCreator.calculateObjectParameters(objectModel);

        List<ObjectModel> parameters = objectModel.getInstanceParameters();
        assertEquals(2, parameters.size());
        assertTrue(ObjectGenerator.generateObjectStatement(parameters.get(0)).startsWith("\t\tint iVar = "));
        assertTrue(ObjectGenerator.generateObjectStatement(parameters.get(1)).startsWith("\t\tint iVar2 = "));
        assertEquals("\t\tKomplex param1 = new Komplex(iVar, iVar2);\n",
                ObjectGenerator.generateObjectStatement(objectModel));
        assertFalse(valueCreator.getModel().getTestClassModel().getImports().isEmpty());
        assertEquals("de.tudo.naantg.testproject.weiter.Komplex",
                valueCreator.getModel().getTestClassModel().getImports().get(0));
    }

    @Test
    public void calculateObjectParametersTestWithObjectConstructor() {
        String testName = "whenSetKomplex2_thenGetKomplex2";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel objectModel = new ObjectModel(Komplex2.class, "param1", true);

        valueCreator.calculateObjectParameters(objectModel);

        List<ObjectModel> parameters = valueCreator.getModel().getParameters(testName);
        parameters.add(objectModel);
        assertEquals(5, parameters.size());
        List<String> paramsStatements = new ArrayList<>();
        parameters.forEach(param -> paramsStatements.add(param.getDataType()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("int", "String", "Box", "Person", "Komplex2")));
        paramsStatements.clear();
        parameters.forEach(param -> paramsStatements.add(param.getIdentifier()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("iVar", "str", "box", "person", "param1")));

        assertFalse(valueCreator.getModel().getTestClassModel().getImports().isEmpty());
        assertTrue(valueCreator.getModel().getTestClassModel().getImports()
                .containsAll(Arrays.asList("de.tudo.naantg.testproject.weiter.Box",
                        "de.tudo.naantg.testproject.weiter.Komplex2",
                        "de.tudo.naantg.testproject.weiter.Person")));
    }

    @Test
    public void calculateObjectParametersTestWithListConstructor() {
        String testName = "whenSetKomplex3_with_2_values_thenGetKomplex3";
        ValueCreator valueCreator = initCreator(testName);
        ObjectModel objectModel = new ObjectModel(Komplex3.class, "param1", true);

        valueCreator.calculateObjectParameters(objectModel);

        List<ObjectModel> parameters = valueCreator.getModel().getParameters(testName);
        parameters.add(objectModel);
        assertEquals(11, parameters.size());
        List<String> paramsStatements = new ArrayList<>();
        parameters.forEach(param -> paramsStatements.add(param.getDataType()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("int", "int", "String", "String", "List", "List",
                "Person", "Person", "Box", "Box", "Komplex3")));
        paramsStatements.clear();
        parameters.forEach(param -> paramsStatements.add(param.getIdentifier()));
        assertTrue(paramsStatements.containsAll(Arrays.asList("iVar", "iVar2", "str", "str2", "box", "box2",
                "person", "person2", "list", "list2", "param1")));

        ObjectModel parent = parameters.get(parameters.size()-1);
        assertEquals("Komplex3", parent.getDataType());
        testInstances(parent.getInstanceParameters(), 2, "List");
        testInstances(parent.getInstanceParameters().get(0).getInstanceParameters(),
                2, "Person");
        testInstances(parent.getInstanceParameters().get(1).getInstanceParameters(),
                2, "Box");
        testInstances(parent.getInstanceParameters().get(0).getInstanceParameters().get(0).getInstanceParameters(),
                1, "String");
        testInstances(parent.getInstanceParameters().get(0).getInstanceParameters().get(1).getInstanceParameters(),
                1, "String");
        testInstances(parent.getInstanceParameters().get(1).getInstanceParameters().get(0).getInstanceParameters(),
                1, "int");
        testInstances(parent.getInstanceParameters().get(1).getInstanceParameters().get(1).getInstanceParameters(),
                1, "int");
        testInstances(parent.getInstanceParameters().get(0).getInstanceParameters().get(0).getInstanceParameters().get(0).getInstanceParameters(),
                0, "");
        testInstances(parent.getInstanceParameters().get(0).getInstanceParameters().get(1).getInstanceParameters().get(0).getInstanceParameters(),
                0, "");
        testInstances(parent.getInstanceParameters().get(1).getInstanceParameters().get(0).getInstanceParameters().get(0).getInstanceParameters(),
                0, "");
        testInstances(parent.getInstanceParameters().get(1).getInstanceParameters().get(1).getInstanceParameters().get(0).getInstanceParameters(),
                0, "");

        assertFalse(valueCreator.getModel().getTestClassModel().getImports().isEmpty());
        assertTrue(valueCreator.getModel().getTestClassModel().getImports()
                .containsAll(Arrays.asList("de.tudo.naantg.testproject.weiter.Box",
                        "de.tudo.naantg.testproject.weiter.Komplex3",
                        "de.tudo.naantg.testproject.weiter.Person")));
    }

    private void testInstances(List<ObjectModel> instances, int len, String dataType) {
        assertEquals(len, instances.size());
        for (ObjectModel instance : instances) {
            assertNotNull(instance);
            assertEquals(dataType, instance.getDataType());
        }
    }

    @Test
    void getGivenConstructor() {
    }

    @Test
    void setGivenConstructor() {
    }
}