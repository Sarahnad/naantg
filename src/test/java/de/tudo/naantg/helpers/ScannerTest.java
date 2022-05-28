package de.tudo.naantg.helpers;

import de.tudo.naantg.annotations.AnnoType;
import de.tudo.naantg.model.CollectionDataType;
import de.tudo.naantg.model.SpringBootKey;
import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.ErsteKlasse;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinJob;
import de.tudo.naantg.testproject.scheinboot.ScheinRepository;
import de.tudo.naantg.testproject.test.DritteKlasseTG;
import de.tudo.naantg.testproject.test.ErsteKlasseTG;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.ScheinRepositoryTG;
import de.tudo.naantg.testproject.weiter.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    private final String packPath = "de.tudo.naantg.testproject";

    @Test
    public void testPackageScanner() throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = Scanner.getClasses(packPath);
        ArrayList<String> classNames = new ArrayList<>();
        for (Class<?> c : classes) {
            classNames.add(c.getSimpleName());
        }
        assert classNames.size() >= 8;
        String[] expected = {"EineAnnotation", "ErsteKlasse", "ErstesInterface", "ZweiteKlasse",
                "KlasseMitInnererKlasse", "InnereKlasse", "NochEineKlasse", "ErsteKlasseTG"};
        assert classNames.containsAll(Arrays.asList(expected));

        classes = Scanner.getClasses(packPath + ".test");
        assert classes.size() >= 1;
        assertTrue(classes.contains(ErsteKlasseTG.class));
    }

    @Test
    public void testFindMethodsWithTG() throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = Scanner.getClasses(packPath);
        ArrayList<Method> methods = new ArrayList<>();
        for (Class<?> c : classes) {
            methods.addAll(Scanner.findMethodsWithTG(c));
        }
        assertTrue(methods.size() >= 1);
    }

    @Test
    public  void testFindMethodOfClass() {
        Class<?> clas = ErsteKlasse.class;
        String methodName = "act";
        Optional<Method> optMethod = Scanner.findMethodOfClass(clas, methodName);
        assert optMethod.isPresent();

        String falseMethodName = "asdgh";
        optMethod = Scanner.findMethodOfClass(clas, falseMethodName);
        assert !optMethod.isPresent();
    }

    @Test
    public void findCorrespondingClass() {
        Class<?> clas = ErsteKlasseTG.class;
        Optional<Class<?>> correspondigClass = Scanner.findCorrespondingClass(clas.getSimpleName().replace("TG", ""),
                packPath);
        assert correspondigClass.isPresent();
        assertEquals("ErsteKlasse", correspondigClass.get().getSimpleName());
    }

    @Test
    public void findCorrespondingMethodTest() {
        Class<?> clas = ErsteKlasse.class;
        Optional<Method> optionalMethod = Scanner.findCorrespondingMethod(clas, "act");
        assert optionalMethod.isPresent();
        assertEquals("act", optionalMethod.get().getName());
    }

    @Test
    public void hasAssertStateAnnotationTest() throws IOException, ClassNotFoundException  {
        ArrayList<Class<?>> classes = Scanner.getClasses(packPath);
        ArrayList<Method> methods = new ArrayList<>();
        for (Class<?> c : classes) {
            methods.addAll(Scanner.findMethodsWithTG(c));
        }
        ArrayList<Method> methodsWithAssertState = new ArrayList<>();
        for (Method method : methods) {
            if (Scanner.hasAssertStateAnnotation(method)) {
                methodsWithAssertState.add(method);
            }
        }
        assertTrue(methodsWithAssertState.size() > 0);
    }

    @Test
    public void hasInitStateAnnotationTest() throws NoSuchMethodException {
        Method method = DritteKlasseTG.class.getMethod("whenDoComplex_thenReturnNull");
        assertTrue(Scanner.hasInitStateAnnotation(method));
        method = DritteKlasseTG.class.getMethod("whenInit_thenReturnTrue");
        assertFalse(Scanner.hasInitStateAnnotation(method));
    }

    @Test
    public void findGenericOfMethodParametersTest() throws NoSuchMethodException {
        Method method = KlasseMitInnererKlasse.class.getMethod("getFirstValue", List.class);
        Map<String, List<Class<?>>> genericParams = Scanner.findGenericOfMethodParameters(method);
        assert genericParams.get(CollectionDataType.LIST.toString()) != null;
        assertEquals("Integer", genericParams.get(CollectionDataType.LIST.toString()).get(0).getSimpleName());
    }

    @Test
    public void findGenericOfMethodReturnTypeTest() throws NoSuchMethodException {
        Method method = KlasseMitInnererKlasse.class.getMethod("getNames");
        List<Class<?>> generics = Scanner.findGenericOfMethodReturnType(method);
        assertEquals(1, generics.size());
        assertEquals("String", generics.get(0).getSimpleName());
    }

    @Test
    public void findGenericOfFieldTest() throws NoSuchFieldException {
        Field field = DritteKlasse.class.getDeclaredField("komplexes");
        System.out.println(field.getName());
        List<Class<?>> generics = Scanner.findGenericOfField(field);
        assertEquals(1, generics.size());
        assertEquals("Komplex", generics.get(0).getSimpleName());
    }

    @Test
    public void getDefaultConstructorTest() {
        Optional<Constructor<?>> optDefConstr = Scanner.getDefaultConstructor(ErsteKlasse.class);
        assertTrue(optDefConstr.isPresent());
        Constructor<?> constructor = optDefConstr.get();
        String[] splitted = constructor.getName().split("\\.");
        String simpleName = splitted[splitted.length-1];
        assertEquals("ErsteKlasse", simpleName);
    }

    @Test
    public void getSimplestConstructorTest() {
        Optional<Constructor<?>> simplestConstr = Scanner.getSimplestConstructor(Komplex.class);
        assertTrue(simplestConstr.isPresent());
        Constructor<?> constructor = simplestConstr.get();
        assertEquals(2, constructor.getParameterCount());
        assertEquals("int", constructor.getParameterTypes()[0].getSimpleName());
        assertEquals("int", constructor.getParameterTypes()[1].getSimpleName());

        simplestConstr = Scanner.getSimplestConstructor(Komplex2.class);
        assertTrue(simplestConstr.isPresent());
        constructor = simplestConstr.get();
        assertEquals(2, constructor.getParameterCount());
        assertEquals("Person", constructor.getParameterTypes()[0].getSimpleName());
        assertEquals("Box", constructor.getParameterTypes()[1].getSimpleName());

        simplestConstr = Scanner.getSimplestConstructor(Komplex3.class);
        assertTrue(simplestConstr.isPresent());
        constructor = simplestConstr.get();
        assertEquals(2, constructor.getParameterCount());
        assertEquals("List", constructor.getParameterTypes()[0].getSimpleName());
        assertEquals("List", constructor.getParameterTypes()[1].getSimpleName());
    }

    @Test
    public void getPrimitiveParamCountTest() {
        assertEquals(1, Scanner.getPrimitiveParamCount(new Class<?>[] {int.class}));
        assertEquals(1, Scanner.getPrimitiveParamCount(new Class<?>[] {int.class, ErsteKlasse.class}));
        assertEquals(2, Scanner.getPrimitiveParamCount(new Class<?>[] {int.class, String.class}));
        assertEquals(1, Scanner.getPrimitiveParamCount(new Class<?>[] {Komplex.class, int.class, ErsteKlasse.class}));
    }

    @Test
    public void findGenericOfConstructorParameters() throws NoSuchMethodException {
        Constructor<?> constructor = Komplex.class.getConstructor(List.class, List.class);
        Map<String, List<Class<?>>> genericParams = Scanner.findGenericOfConstructorParameters(constructor);
        assert genericParams.get(CollectionDataType.LIST.toString()) != null;
        assertEquals("Person", genericParams.get(CollectionDataType.LIST.toString()).get(0).getSimpleName());
        assertEquals("Box", genericParams.get(CollectionDataType.LIST.toString()).get(1).getSimpleName());
    }

    @Test
    public void hasSetterMethodTest() throws NoSuchFieldException {
        Class<?> parentClass = DritteKlasse.class;
        Field field = parentClass.getDeclaredField("aMillion");
        assertTrue(Scanner.hasSetterMethod(parentClass, field));

        assertTrue(Scanner.hasSetterMethod(DritteKlasse.class, "aMillion"));

        parentClass = Komplex2.class;
        field = parentClass.getDeclaredField("freeBoxes");
        assertFalse(Scanner.hasSetterMethod(parentClass, field));

        assertFalse(Scanner.hasSetterMethod(Komplex2.class, "freeBoxes"));

        assertTrue(Scanner.hasSetterMethod(ScheinJob.class, Company.class.getDeclaredField("companyName")));
        assertTrue(Scanner.hasSetterMethod(ScheinJob.class, "companyName"));
    }

    @Test
    public void hasGetterMethodTest() throws NoSuchFieldException {
        Class<?> parentClass = DritteKlasse.class;
        Field field = parentClass.getDeclaredField("aMillion");
        assertTrue(Scanner.hasGetterMethod(parentClass, field));

        assertTrue(Scanner.hasGetterMethod(DritteKlasse.class, "aMillion"));

        parentClass = Komplex2.class;
        field = parentClass.getDeclaredField("persons");
        assertFalse(Scanner.hasGetterMethod(parentClass, field));

        assertFalse(Scanner.hasGetterMethod(Komplex2.class, "persons"));

        assertTrue(Scanner.hasGetterMethod(ScheinJob.class, Company.class.getDeclaredField("companyName")));
        assertTrue(Scanner.hasGetterMethod(ScheinJob.class, "companyName"));
    }

    @Test
    public void hasClassAnnotationTest() {
        assertFalse(Scanner.hasClassAnnotation(DritteKlasseTG.class, "RepositoryTG"));
        assertTrue(Scanner.hasClassAnnotation(ScheinRepositoryTG.class, "RepositoryTG"));
    }

    @Test
    public void hasFieldAnnotationTest() throws NoSuchFieldException {
        Class<?> testClass = ScheinEntity.class;
        Field field = testClass.getDeclaredField("scheinId");
        assertTrue(Scanner.hasFieldAnnotation(field, SpringBootKey.GENERATED.getKeyword()));

        field = testClass.getDeclaredField("password");
        assertFalse(Scanner.hasFieldAnnotation(field, SpringBootKey.GENERATED.getKeyword()));
    }

    @Test
    public void findAnnotationClassTest() throws NoSuchMethodException {
        Method method = ScheinControllerTG.class.getMethod("whenGetRegister_thenSuccess");

        Optional<Annotation> optionalAnnotation = Scanner.findAnnotationClass(method, AnnoType.EXPECT.getType());

        assertTrue(optionalAnnotation.isPresent());
    }

    @Test
    public void findConstructorTest() {
        Optional<Constructor<?>> optionalConstructor = Scanner.findConstructor(Arrays.asList("int", "int"), Komplex.class);
        assertTrue(optionalConstructor.isPresent());

        optionalConstructor = Scanner.findConstructor(Arrays.asList("int", "String"), Komplex.class);
        assertFalse(optionalConstructor.isPresent());

        optionalConstructor = Scanner.findConstructor(Collections.singletonList("int"), Komplex.class);
        assertFalse(optionalConstructor.isPresent());

        optionalConstructor = Scanner.findConstructor(Arrays.asList("List", "List"), Komplex.class);
        assertTrue(optionalConstructor.isPresent());
    }

    @Test
    public void findCorrespondingFieldTest() {
        Optional<Class<?>> optField = Scanner.findCorrespondingField("name", packPath);
        assertTrue(optField.isPresent());
        assertEquals(String.class, optField.get());

        optField = Scanner.findCorrespondingField("scheinId", packPath);
        assertTrue(optField.isPresent());
        assertEquals(Long.class, optField.get());

        optField = Scanner.findCorrespondingField("person", packPath);
        assertFalse(optField.isPresent());
    }

    @Test
    public void findEntityClassTest() {
        Optional<Class<?>> optField = Scanner.findEntityClass("ScheinEntity", packPath);
        assertTrue(optField.isPresent());
        assertEquals(ScheinEntity.class, optField.get());

        optField = Scanner.findEntityClass("Hauptschein", packPath);
        assertFalse(optField.isPresent());
    }

    @Test
    public void findAssignableClassTest() {
        Optional<Class<?>> found = Scanner.findAssignableClass(ScheinRepository.class, packPath);
        assertTrue(found.isPresent());
        assertEquals("ScheinRepoImpl", found.get().getSimpleName());
    }

    @Test
    public void findAllSuperFieldsTest() {
        List<Field> allFields = Scanner.findAllFields(ScheinJob.class);
        allFields.forEach(field -> System.out.println(field.getName()));
    }

}
