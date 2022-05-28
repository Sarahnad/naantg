package de.tudo.naantg.creators;

import de.tudo.naantg.generator.ObjectGenerator;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.MethodModel;
import de.tudo.naantg.model.ObjectModel;
import de.tudo.naantg.model.TestClassModel;
import de.tudo.naantg.testproject.scheinboot.*;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.TestControllerTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockCreatorTest {

    @Test
    public void calculateMockingTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        ControllerModelCreator modelCreator = new ControllerModelCreator(pkg, "");
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenPostRegister_thenDisplayError";
        Method method = ScheinControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinService.class, "scheinService", false));
        MockCreator mockCreator = new MockCreator(model, ScheinControllerTG.class);
        mockCreator.setTestName(testName);

        mockCreator.calculateMocking(method, ScheinControllerTG.class);

        List<MethodModel> mockCalls = model.getTestMethodModel(testName).getMockCalls();
        assertEquals(1, mockCalls.size());
        MethodModel mockCall = mockCalls.get(0);
        assertEquals("scheinService", mockCall.getObject());
        assertEquals("createScheinEntity", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("notNull()", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("ScheinEntityException", mockCall.getReturnType());

        model.setCut(TestController.class);
        testName = "whenCreateThings_3";
        method = TestControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        model.getTestClassModel().getMocks().add(new ObjectModel(SonnenscheinRepository.class, "sonnenscheinRepository", false));
        mockCreator = new MockCreator(model, TestControllerTG.class);
        mockCreator.setTestName(testName);

        mockCreator.calculateMocking(method, ScheinControllerTG.class);

        mockCalls = model.getTestMethodModel(testName).getMockCalls();
        assertEquals(3, mockCalls.size());
        mockCall = mockCalls.get(0);
        assertEquals("sonnenscheinRepository", mockCall.getObject());
        assertEquals("findById", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("OWNER_ID", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("Optional", mockCall.getReturnType());
        mockCall = mockCalls.get(1);
        assertEquals("scheinRepository", mockCall.getObject());
        assertEquals("findById", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("SCHEIN_ID", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("ScheinEntity", mockCall.getReturnType());
        assertEquals("new ScheinEntity()",
                ObjectGenerator.generateObjectStatement(mockCall.getReturnObject(), true, false));
        mockCall = mockCalls.get(2);
        assertEquals("sonnenscheinRepository", mockCall.getObject());
        assertEquals("findSonnenscheinTypes", mockCall.getName());
        assertEquals(0, mockCall.getParameterObjects().size());
        assertEquals("List", mockCall.getReturnType());
        assertEquals("(firstJob)", mockCall.getReturnObject().getValue());
        assertEquals("Arrays.asList(firstJob)",
                ObjectGenerator.generateObjectStatement(mockCall.getReturnObject(), true, false));

        testName = "whenCreateThings_4";
        method = TestControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        model.getTestClassModel().getMocks().add(new ObjectModel(SonnenscheinRepository.class, "sonnenscheinRepository", false));
        mockCreator = new MockCreator(model, TestControllerTG.class);
        mockCreator.setTestName(testName);
        model.setProjectPackageUriWithDots(pkg);

        mockCreator.calculateMocking(method, ScheinControllerTG.class);

        mockCalls = model.getTestMethodModel(testName).getMockCalls();
        assertEquals(3, mockCalls.size());
        mockCall = mockCalls.get(0);
        assertEquals("sonnenscheinRepository", mockCall.getObject());
        assertEquals("findById", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("OWNER_ID", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("Optional", mockCall.getReturnType());
        mockCall = mockCalls.get(1);
        assertEquals("scheinRepository", mockCall.getObject());
        assertEquals("findById", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("SCHEIN_ID", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("ScheinEntity", mockCall.getReturnType());
        mockCall = mockCalls.get(2);
        assertEquals("sonnenscheinRepository", mockCall.getObject());
        assertEquals("getFirstJob", mockCall.getName());
        assertEquals(0, mockCall.getParameterObjects().size());
        assertEquals("firstJob", mockCall.getReturnType());
        List<ObjectModel> params = model.getParameters(testName);
        assertEquals(1, params.size());
        assertEquals("new ScheinJob()",
                ObjectGenerator.generateObjectStatement(params.get(0), true, false));
    }

    @Test
    public void calculateInitObjectsTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        GeneratorModel model = new GeneratorModel();
        model.setProjectPackageUriWithDots(pkg);
        MockCreator mockCreator = new MockCreator(model, TestControllerTG.class);

        List<ObjectModel> inits = mockCreator.calculateInitObjects(
                TestControllerTG.class.getMethod("whenCreateThings_5"));

        assertEquals(1, inits.size());
        assertEquals("\t\tOptional<ScheinJob> firstJob = Optional.empty();\n",
                ObjectGenerator.generateObjectStatement(inits.get(0)));
    }

    @Test
    public void detectMockTest() throws NoSuchFieldException {
        MockCreator mockCreator = new MockCreator(new GeneratorModel(), ScheinControllerTG.class);

        Field field = ScheinController.class.getDeclaredField("scheinService");
        boolean actual = mockCreator.detectMock(field);
        assertTrue(actual);

        field = ScheinController.class.getDeclaredField("hauptschein");
        actual = mockCreator.detectMock(field);
        assertTrue(actual);

        field = ScheinController.class.getDeclaredField("scheinJob");
        actual = mockCreator.detectMock(field);
        assertFalse(actual);

        field = ScheinController.class.getDeclaredField("testController");
        actual = mockCreator.detectMock(field);
        assertTrue(actual);

        field = ScheinController.class.getDeclaredField("testRepository");
        actual = mockCreator.detectMock(field);
        assertTrue(actual);
    }

}