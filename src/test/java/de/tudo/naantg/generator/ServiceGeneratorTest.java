package de.tudo.naantg.generator;

import de.tudo.naantg.creators.ControllerModelCreator;
import de.tudo.naantg.creators.MockCreator;
import de.tudo.naantg.creators.ServiceModelCreator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.scheinboot.ScheinController;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinRepository;
import de.tudo.naantg.testproject.scheinboot.ScheinService;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.ScheinServiceTG;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceGeneratorTest {

    @Test
    public void createTestClassTest() {
        ServiceGenerator generator = new ServiceGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        model.setCut(ScheinService.class);
        TestClassModel testClassModel = new TestClassModel();
        model.setTestClassModel(testClassModel);
        testClassModel.setName("ScheinServiceTests");
        testClassModel.setPackageUri("de.tudo.naantg.testproject.test.testGen");
        testClassModel.getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        testClassModel.getMocks().add(new ObjectModel(BCryptPasswordEncoder.class, "bCryptPasswordEncoder", false));
        model.setTgClass(ScheinServiceTG.class);

        String expected = "package de.tudo.naantg.testproject.test.testGen;\n" +
                "\n" +
                "import de.tudo.naantg.testproject.scheinboot.ScheinService;\n" +
                "\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.junit.jupiter.api.extension.ExtendWith;\n" +
                "import org.mockito.InjectMocks;\n" +
                "import org.mockito.Mock;\n" +
                "import org.mockito.junit.jupiter.MockitoExtension;\n" +
                "import static org.junit.jupiter.api.Assertions.*;\n" +
                "\n\n" +
                "@ExtendWith(MockitoExtension.class)\n" +
                "public class ScheinServiceTests /*implements ScheinServiceTG*/ {\n" +
                "\n" +
                "\t@InjectMocks\n" +
                "\tprivate ScheinService scheinService;\n" +
                "\n" +
                "\t@Mock\n" +
                "\tprivate ScheinRepository scheinRepository;\n" +
                "\n" +
                "\t@Mock\n" +
                "\tprivate BCryptPasswordEncoder bCryptPasswordEncoder;\n" +
                "\n\n\n" +
                "}";

        String actual = generator.createTestClass();

        assertEquals(expected, actual);
    }

    @Test
    public void generateMockCallsTest() throws NoSuchMethodException {
        ServiceGenerator generator = new ServiceGenerator();
        String pkg = "de.tudo.naantg.testproject";
        ServiceModelCreator modelCreator = new ServiceModelCreator(pkg, "");
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        modelCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinService.class);
        String testName = "whenCreateScheinEntity_thenReturnException";
        Method method = ScheinServiceTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        MockCreator mockCreator = new MockCreator(model, ScheinServiceTG.class);
        mockCreator.setTestName(testName);
        mockCreator.calculateMocking(method, ScheinServiceTG.class);
        MockGenerator mockGenerator = new MockGenerator(model, testName);

        String result = mockGenerator.generateMockCalls();

        String expected = "\t\tdoThrow(new DataFormatException())\n" +
                "\t\t\t\t.when(scheinRepository).save(notNull());\n";
        assertEquals(expected, result);
    }

    @Test
    public void generateAssertionsTest() throws NoSuchMethodException {
        ServiceGenerator generator = new ServiceGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        model.setCut(ScheinService.class);
        TestClassModel testClassModel = new TestClassModel();
        model.setTestClassModel(testClassModel);
        String testName = "whenCreateScheinEntity_thenReturnException";
        Assertion assertion = new Assertion("", "ServiceException", AssertType.THROWN);
        model.getAssertions(testName).add(assertion);
        MethodModel methodModel = new MethodModel();
        model.setMethodOfCUT(methodModel, testName);
        methodModel.setMethodToTest(ScheinService.class.getMethod("createScheinEntity", ScheinEntity.class));
        methodModel.setName("createScheinEntity");
        methodModel.setReturnType("void");
        methodModel.setObject("scheinService");
        methodModel.setParameters(new Class<?>[] { ScheinEntity.class });
        methodModel.setArgList(new String[] {"scheinEntity"});

        String expected = "\t\tassertThatThrownBy( () -> " +
                "scheinService.createScheinEntity(scheinEntity) )\n" +
                "\t\t\t\t.isInstanceOf( ServiceException.class );\n";

        String actual = generator.generateAssertions(testName);

        assertEquals(expected, actual);
    }

}