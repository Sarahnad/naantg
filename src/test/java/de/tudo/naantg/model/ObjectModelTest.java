package de.tudo.naantg.model;

import de.tudo.naantg.generator.ObjectGenerator;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectModelTest {

    @Test
    public void createInputObjectTest() {
        String objectInstance = "\t\tErsteKlasse var = new ErsteKlasse();\n";
        ObjectModel objectModel = new ObjectModel();
        objectModel.setDataType("ErsteKlasse");
        objectModel.setNewInstance(true);
        objectModel.setIdentifier("var");

        String actual = objectModel.generateObjectStatement();
        assertEquals(objectInstance, actual);

        String objectInstanceWithParams = "\t\tErsteKlasse var = new ErsteKlasse(param1, param2, param3);\n";
        ObjectModel[] params = {new ObjectModel("String", "param1", true),
                new ObjectModel("String", "param2", true),
                new ObjectModel("String", "param3", true)};
        objectModel.getInstanceParameters().addAll(Arrays.asList(params));

        actual = objectModel.generateObjectStatement();
        assertEquals(objectInstanceWithParams, actual);

        String objectInstanceSimple = "\t\tint var = 5;\n";
        objectModel.setValue("5");
        objectModel.setDataType("int");

        actual = objectModel.generateObjectStatement();
        assertEquals(objectInstanceSimple, actual);
    }

    @Test
    public void createListInputObjectTest() {
        String objectInstance = "\t\tList<String> param1 = new ArrayList<>();\n";
        ObjectModel objectModel = new ObjectModel();
        objectModel.setDataType("List");
        objectModel.getGenericClasses().add(String.class);
        objectModel.setNewInstance(true);
        objectModel.setIdentifier("param1");

        String actual = objectModel.generateObjectStatement();
        assertEquals(objectInstance, actual);

        objectInstance = "\t\tArrayList<String> param1 = new ArrayList<>();\n";
        objectModel.setDataType("ArrayList");

        actual = objectModel.generateObjectStatement();
        assertEquals(objectInstance, actual);

        objectInstance = "\t\tint[] param1 = new int[] {1, 2, 3};\n";
        objectModel.setDataType("int[]");
        objectModel.setValue("{1,2,3}");

        actual = objectModel.generateObjectStatement();
        assertEquals(objectInstance, actual);

        objectInstance = "\t\tArrayList<String> param1 = new ArrayList<>(Arrays.asList(1, 2, 3));\n";
        objectModel.setDataType("ArrayList");
        objectModel.setValue("{1,2,3}");

        actual = objectModel.generateObjectStatement();
        assertEquals(objectInstance, actual);

        objectInstance = "\t\tCollection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4});\n";
        objectModel.setDataType("Collection");
        objectModel.setGenericClasses(Collections.singletonList(Byte.class));
        objectModel.setValue("{1,2,3,4}");

        actual = ObjectGenerator.generateObjectStatement(objectModel);
        assertEquals(objectInstance, actual);
    }

    @Test
    public void createSetStatement() {
        ObjectModel objectModel = new ObjectModel();
        objectModel.setIdentifier("actual");
        objectModel.setDataType("int[]");
        objectModel.setValue("testClass.getArray()");
        assertEquals("\t\tint[] actual = testClass.getArray();\n", objectModel.generateObjectStatement());
    }


    @Test
    public void addCollectionNewInstanceTest() {
        String expected = "\t\tCollection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4});\n";
        ObjectModel objectModel = new ObjectModel("Collection", "param1", true);
        objectModel.setValue("{1, 2, 3, 4}");
        objectModel.getGenericClasses().add(Byte.class);
        assertEquals(expected, objectModel.generateObjectStatement());

        expected = "Collection<Integer> param1 = Arrays.asList(1, 2, 3, 4)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Integer.class);
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Short> param1 = Arrays.asList(new Short[] {1, 2, 3, 4})";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Short.class);
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Long> param1 = Arrays.asList(1L, 2L, 3L, 4L)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Long.class);
        objectModel.setValue("{1L,2L,3L,4L}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Float> param1 = Arrays.asList(1f, 2f, 3f, 4f)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Float.class);
        objectModel.setValue("{1f,2f,3f,4f}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Double> param1 = Arrays.asList(1d, 2d, 3d, 4d)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Double.class);
        objectModel.setValue("{1d, 2d, 3d, 4d}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<String> param1 = Arrays.asList(\"a\", \"b\")";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(String.class);
        objectModel.setValue("{\"a\", \"b\"}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Character> param1 = Arrays.asList('a', 's')";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Character.class);
        objectModel.setValue("{'a', 's'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Collection<Boolean> param1 = Arrays.asList(true, false)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Boolean.class);
        objectModel.setValue("{true, false}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Byte> param1 = new ArrayList<>(Arrays.asList(new Byte[] {1, 2, 3, 4}))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Byte.class);
        objectModel.setValue("{1, 2, 3, 4}");
        objectModel.setDataType("ArrayList");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Integer> param1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Integer.class);
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Short> param1 = new ArrayList<>(Arrays.asList(new Short[] {1, 2, 3, 4}))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Short.class);
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Long> param1 = new ArrayList<>(Arrays.asList(1L, 2L))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Long.class);
        objectModel.setValue("{1L, 2L}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Float> param1 = new ArrayList<>(Arrays.asList(1f, 2f))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Float.class);
        objectModel.setValue("{1f, 2f}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Double> param1 = new ArrayList<>(Arrays.asList(1d, 2d))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Double.class);
        objectModel.setValue("{1d, 2d}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<String> param1 = new ArrayList<>(Arrays.asList(\"1\", \"2\"))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(String.class);
        objectModel.setValue("{\"1\", \"2\"}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Character> param1 = new ArrayList<>(Arrays.asList('1', '2'))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Character.class);
        objectModel.setValue("{'1', '2'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "ArrayList<Boolean> param1 = new ArrayList<>(Arrays.asList(true, true, false, false))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Boolean.class);
        objectModel.setValue("{true, true, false, false}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Short> param1 = new LinkedList<>(Arrays.asList(new Short[] {1, 2, 3, 4}))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Short.class);
        objectModel.setDataType("LinkedList");
        objectModel.setValue("{1, 2, 3, 4}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Long> param1 = new LinkedList<>(Arrays.asList(1L, 2L))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Long.class);
        objectModel.setValue("{1L, 2L}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Float> param1 = new LinkedList<>(Arrays.asList(1f, 2f))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Float.class);
        objectModel.setValue("{1f, 2f}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Double> param1 = new LinkedList<>(Arrays.asList(1d, 2d))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Double.class);
        objectModel.setValue("{1d, 2d}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<String> param1 = new LinkedList<>(Arrays.asList(\"1\", \"2\"))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(String.class);
        objectModel.setValue("{\"1\", \"2\"}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Character> param1 = new LinkedList<>(Arrays.asList('1', '2'))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Character.class);
        objectModel.setValue("{'1', '2'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "LinkedList<Boolean> param1 = new LinkedList<>(Arrays.asList(true, false))";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Boolean.class);
        objectModel.setValue("{true, false}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Short> param1 = Arrays.asList(new Short[] {1, 2, 3, 4})";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Short.class);
        objectModel.setDataType("List");
        objectModel.setValue("{1, 2, 3, 4}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Long> param1 = Arrays.asList(1L, 2L)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Long.class);
        objectModel.setValue("{1L, 2L}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Float> param1 = Arrays.asList(1f, 2f)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Float.class);
        objectModel.setValue("{1f, 2f}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Double> param1 = Arrays.asList(1d, 2d)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Double.class);
        objectModel.setValue("{1d, 2d}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<String> param1 = Arrays.asList(\"1\", \"2\")";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(String.class);
        objectModel.setValue("{\"1\", \"2\"}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Character> param1 = Arrays.asList('1', '2')";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Character.class);
        objectModel.setValue("{'1', '2'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "List<Boolean> param1 = Arrays.asList(true, false)";
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(Boolean.class);
        objectModel.setValue("{true, false}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "byte[] param1 = new byte[] {1, 2, 3}";
        objectModel.setDataType("byte[]");
        objectModel.setValue("{1, 2, 3}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "int[] param1 = new int[] {1, 2, 3}";
        objectModel.setDataType("int[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "short[] param1 = new short[] {1, 2, 3}";
        objectModel.setDataType("short[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "long[] param1 = new long[] {1, 2, 3}";
        objectModel.setDataType("long[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "float[] param1 = new float[] {1, 2, 3}";
        objectModel.setDataType("float[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "double[] param1 = new double[] {1, 2, 3}";
        objectModel.setDataType("double[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "String[] param1 = new String[] {\"1\", \"2\", \"3\"}";
        objectModel.setDataType("String[]");
        objectModel.setValue("{\"1\", \"2\", \"3\"}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "char[] param1 = new char[] {'1', '2', '3'}";
        objectModel.setDataType("char[]");
        objectModel.setValue("{'1', '2', '3'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "boolean[] param1 = new boolean[] {false, true}";
        objectModel.setDataType("boolean[]");
        objectModel.setValue("{false, true}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Byte[] param1 = new Byte[] {1, 2, 3}";
        objectModel.setDataType("Byte[]");
        objectModel.setValue("{1, 2, 3}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Integer[] param1 = new Integer[] {1, 2, 3}";
        objectModel.setDataType("Integer[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Short[] param1 = new Short[] {1, 2, 3}";
        objectModel.setDataType("Short[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Long[] param1 = new Long[] {1, 2, 3}";
        objectModel.setDataType("Long[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Float[] param1 = new Float[] {1, 2, 3}";
        objectModel.setDataType("Float[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Double[] param1 = new Double[] {1, 2, 3}";
        objectModel.setDataType("Double[]");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Character[] param1 = new Character[] {'1', '2', '3'}";
        objectModel.setDataType("Character[]");
        objectModel.setValue("{'1', '2', '3'}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

        expected = "Boolean[] param1 = new Boolean[] {false, true}";
        objectModel.setDataType("Boolean[]");
        objectModel.setValue("{false, true}");
        assertEquals("\t\t" + expected + ";\n", objectModel.generateObjectStatement());

    }

    @Test
    public void createOptionalStatementTest() {
        String expected = "Optional<ScheinEntity> optScheinEntity = Optional.empty()";
        ObjectModel objectModel = new ObjectModel(Optional.class, "optScheinEntity", true);
        objectModel.getGenericClasses().add(ScheinEntity.class);
        assertEquals("\t\t" + expected + ";\n", ObjectGenerator.generateObjectStatement(objectModel));

        expected = "Optional<ScheinEntity> optScheinEntity = Optional.of(schein)";
        objectModel = new ObjectModel(Optional.class, "optScheinEntity", true);
        objectModel.getGenericClasses().clear();
        objectModel.getGenericClasses().add(ScheinEntity.class);
        objectModel.getInstanceParameters().add(new ObjectModel(ScheinEntity.class, "schein", true));
        assertEquals("\t\t" + expected + ";\n", ObjectGenerator.generateObjectStatement(objectModel));

    }

}