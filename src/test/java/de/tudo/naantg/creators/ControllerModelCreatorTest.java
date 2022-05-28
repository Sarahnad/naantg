package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.ControllerTG;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.generator.ObjectGenerator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.scheinboot.ScheinController;
import de.tudo.naantg.testproject.scheinboot.ScheinService;
import de.tudo.naantg.testproject.scheinboot.TestController;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.TestControllerTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerModelCreatorTest {

    @Test
    public void calculateMethodModelTest() {
        ControllerModelCreator modelCreator = new ControllerModelCreator("", "");
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenGetRegister_thenGetViewRegister";

        modelCreator.calculateMethodModel(testName);

        MethodModel methodModel = model.getMethodOfCUT(testName);
        List<String> values = methodModel.getAnnotationValues().get(SpringBootKey.GET.getKeyword());
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("\"/register\"", values.get(0));
        assertEquals(1, model.getTestClassModel().getImports().size());
        assertEquals(SpringBootKey.GET.getImportPath(), model.getTestClassModel().getImports().get(0));

        testName = "whenPostRegister_thenRedirectToLogin";
        model.getTestClassModel().getImports().clear();

        modelCreator.calculateMethodModel(testName);

        methodModel = model.getMethodOfCUT(testName);
        values = methodModel.getAnnotationValues().get(SpringBootKey.POST.getKeyword());
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("\"/register\"", values.get(0));
        assertEquals(1, model.getTestClassModel().getImports().size());
        assertEquals(SpringBootKey.POST.getImportPath(), model.getTestClassModel().getImports().get(0));

        assertEquals(2, methodModel.getParameters().length);

        testName = "whenCreateThings";
        model.setTestClassModel(new TestClassModel());
        model.setCut(TestController.class);
        model.setTgClass(TestControllerTG.class);
        model.getTestClassModel().getImports().clear();

        modelCreator.calculateMethodModel(testName);

        methodModel = model.getMethodOfCUT(testName);
        values = methodModel.getAnnotationValues().get(SpringBootKey.GET.getKeyword());
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("\"/other/path/with/{id}\", OWNER_ID", values.get(0));
        assertEquals(1, model.getTestClassModel().getImports().size());
        assertEquals(SpringBootKey.GET.getImportPath(), model.getTestClassModel().getImports().get(0));
        List<ObjectModel> params = model.getParameters(testName);
        assertEquals(1, params.size());
        assertEquals("\t\tint OWNER_ID = 1;\n", ObjectGenerator.generateObjectStatement(params.get(0)));

        testName = "whenCreateThings_2";
        model.setTestClassModel(new TestClassModel());
        model.setCut(TestController.class);
        model.setTgClass(TestControllerTG.class);
        model.getTestClassModel().getImports().clear();

        modelCreator.calculateMethodModel(testName);

        methodModel = model.getMethodOfCUT(testName);
        values = methodModel.getAnnotationValues().get(SpringBootKey.GET.getKeyword());
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("\"first/thing/{scheinId}/test\", OWNER_ID", values.get(0));
        assertEquals(1, model.getTestClassModel().getImports().size());
        assertEquals(SpringBootKey.GET.getImportPath(), model.getTestClassModel().getImports().get(0));
        params = model.getParameters(testName);
        assertEquals(1, params.size());
        assertTrue(ObjectGenerator.generateObjectStatement(params.get(0)).startsWith("\t\tint OWNER_ID = "));
    }

    @Test
    public void calculateParameterTest() throws NoSuchMethodException {
        ControllerModelCreator modelCreator = new ControllerModelCreator("", "");
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        modelCreator.setMockCreator(new MockCreator(model, ScheinControllerTG.class));
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenGetRegister_thenGetViewRegister";
        modelCreator.calculateMethodModel(testName);
        Method method = ScheinControllerTG.class.getMethod(testName);

        modelCreator.calculateObjects(method);

        assertEquals(1, model.getParameters(testName).size());
        assertEquals(1, model.getParameters(testName).get(0).getGivenAnnotations().size());

        testName = "whenPostRegister_thenRedirectToLogin";
        modelCreator.calculateMethodModel(testName);
        method = ScheinControllerTG.class.getMethod(testName);

        modelCreator.calculateObjects(method);

        assertEquals(2, model.getParameters(testName).size());
        assertEquals(0, model.getParameters(testName).get(0).getGivenAnnotations().size());
        assertEquals(1, model.getParameters(testName).get(1).getGivenAnnotations().size());

    }

    @Test
    public void createGeneratorModelTest() {
        String pkg = "de.tudo.naantg.testproject";
        String testPackage = "de.tudo.naantg.testproject.test";
        ControllerModelCreator modelCreator = new ControllerModelCreator(pkg, testPackage);

        GeneratorModel model = modelCreator.createGeneratorModel(ScheinControllerTG.class);

        assertEquals(1, model.getTestClassModel().getMocks().size());
        assertEquals("ScheinDetailService", model.getTestClassModel().getMocks().get(0).getDataType());
        assertTrue(model.getTestClassModel().getImports().contains(pkg + ".scheinboot.ScheinDetailService"));
    }

    @Test
    public void calculateFieldsTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        ControllerModelCreator modelCreator = new ControllerModelCreator(pkg, "");
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        modelCreator.setMockCreator(new MockCreator(model, ScheinControllerTG.class));
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenGetRegister_thenGetViewRegister";
        modelCreator.calculateMethodModel(testName);
        Method method = ScheinControllerTG.class.getMethod(testName);
        ObjectModel parent = new ObjectModel(ScheinController.class, "scheinController", false);

        modelCreator.calculateFields(parent, testName, InitType.NONE);
        assertEquals(4, model.getTestClassModel().getMocks().size());
        assertEquals("ScheinService", model.getTestClassModel().getMocks().get(0).getDataType());
        assertTrue(model.getTestClassModel().getImports().contains(pkg + ".scheinboot.ScheinService"));
    }

    @Test
    public void calculateControllerParameterTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        ControllerModelCreator modelCreator = new ControllerModelCreator(pkg, "");
        String testName = "whenGetRegister_thenSuccessWithParams";
        Method method = ScheinControllerTG.class.getMethod(testName);
        ObjectModel objectModel = new ObjectModel("cut", "cut", false);
        modelCreator.getModel().setCutObject(objectModel, testName);

        modelCreator.calculateControllerParameter(method);

        List<ObjectModel> params = modelCreator.getModel().getCutObject(testName).getInstanceParameters();
        assertEquals(3, params.size());
        params.forEach(System.out::println);
        assertEquals("Alex:a", params.get(0).getValue());
        assertEquals("7", params.get(2).getValue());
    }

    @Test
    public void determineGivenReplacementTest() throws NoSuchMethodException {
        String testName = "whenCreateThings";
        Method method = TestControllerTG.class.getMethod(testName);
        ControllerModelCreator modelCreator = new ControllerModelCreator("", "");

        String replacement = modelCreator.determineGivenReplacement(method, InitType.NONE);

        List<ObjectModel> params = modelCreator.getModel().getParameters(testName);
        assertEquals("OWNER_ID", replacement);
        assertEquals(1, params.size());
        assertEquals("\t\tint OWNER_ID = 1;\n",
                ObjectGenerator.generateObjectStatement(params.get(0)));
    }

    @Test
    public void calculateReplacementsTest() {
        ControllerModelCreator modelCreator = new ControllerModelCreator("", "");
        String testName = "whenCreateThings";
        String value = "/owners/{ownerId}/pets/petId/edit";
        String result = modelCreator.calculateReplacements(value, testName);
        String expected = "\"/owners/{ownerId}/pets/petId/edit\", OWNERID";
        assertEquals(expected, result);

        value = "/owners/{ownerId}/pets/{petId}/edit";
        result = modelCreator.calculateReplacements(value, testName);
        expected = "\"/owners/{ownerId}/pets/{petId}/edit\", OWNERID, PETID";
        assertEquals(expected, result);
    }

}