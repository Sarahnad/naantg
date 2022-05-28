package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.MethodModel;
import de.tudo.naantg.model.ObjectModel;
import de.tudo.naantg.model.TestClassModel;
import de.tudo.naantg.testproject.scheinboot.ScheinController;
import de.tudo.naantg.testproject.scheinboot.ScheinRepository;
import de.tudo.naantg.testproject.scheinboot.ScheinService;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.ScheinServiceTG;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceModelCreatorTest {

    @Test
    public void calculateFieldsTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        ServiceModelCreator modelCreator = new ServiceModelCreator(pkg, "");
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        modelCreator.setMockCreator(new MockCreator(model, ScheinServiceTG.class));
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinService.class);
        String testName = "whenCreateScheinEntity_thenReturnException";
        modelCreator.calculateMethodModel(testName);
        ObjectModel parent = new ObjectModel(ScheinService.class, "scheinService", false);

        modelCreator.calculateFields(parent, testName, InitType.NONE);

        assertEquals(2, model.getTestClassModel().getMocks().size());
        assertEquals("ScheinRepository", model.getTestClassModel().getMocks().get(0).getDataType());
        assertTrue(model.getTestClassModel().getImports().contains(pkg + ".scheinboot.ScheinRepository"));
        assertEquals("PasswordEncoder", model.getTestClassModel().getMocks().get(1).getDataType());
    }

    @Test
    public void calculateMockingTest() throws NoSuchMethodException {
        String pkg = "de.tudo.naantg.testproject";
        GeneratorModel model = new GeneratorModel();
        model.setProjectPackageUriWithDots(pkg);
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinService.class);
        String testName = "whenCreateScheinEntity_thenReturnException";
        Method method = ScheinServiceTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        model.getTestClassModel().getMocks().add(new ObjectModel(BCryptPasswordEncoder.class, "bCryptPasswordEncoder", false));

        MockCreator mockCreator = new MockCreator(model, ScheinServiceTG.class);
        mockCreator.setTestName(testName);
        mockCreator.calculateMocking(method, ScheinServiceTG.class);

        List<MethodModel> mockCalls = model.getTestMethodModel(testName).getMockCalls();
        assertEquals(1, mockCalls.size());
        MethodModel mockCall = mockCalls.get(0);
        assertEquals("scheinRepository", mockCall.getObject());
        assertEquals("save", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("notNull()", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("DataFormatException", mockCall.getReturnType());
        assertEquals("DataFormatException", mockCall.getReturnObject().getDataType());

        testName = "whenActWithShineTyps_thenReturnTrue";
        method = ScheinServiceTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));

        mockCreator = new MockCreator(model, ScheinServiceTG.class);
        mockCreator.setTestName(testName);

        mockCreator.calculateMocking(method, ScheinServiceTG.class);

        mockCalls = model.getTestMethodModel(testName).getMockCalls();
        assertEquals(1, mockCalls.size());
        mockCall = mockCalls.get(0);
        assertEquals("scheinRepository", mockCall.getObject());
        assertEquals("getShineType", mockCall.getName());
        assertEquals(1, mockCall.getParameterObjects().size());
        assertEquals("true", mockCall.getParameterObjects().get(0).getIdentifier());
        assertEquals("shineType", mockCall.getReturnType());
        assertEquals(1, model.getParameters(testName).size());
        assertEquals("shineType", model.getParameters(testName).get(0).getIdentifier());
    }

    @Test
    public void generateModelTest() {
        String pkg = "de.tudo.naantg.testproject";
        ServiceModelCreator modelCreator = new ServiceModelCreator(pkg, "");
        ServiceAssertionCreator assertionCreator = new ServiceAssertionCreator();
        modelCreator.setAssertionCreator(assertionCreator);

        modelCreator.createGeneratorModel(ScheinServiceTG.class);

        String testName = "whenActWithShineTyps_thenReturnTrue";
        assertEquals(1, modelCreator.getModel().getParameters(testName).size());
        assertEquals("shineType", modelCreator.getModel().getParameters(testName).get(0).getIdentifier());
    }

}