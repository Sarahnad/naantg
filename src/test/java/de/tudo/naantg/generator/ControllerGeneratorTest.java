package de.tudo.naantg.generator;

import de.tudo.naantg.annotations.CheckType;
import de.tudo.naantg.annotations.StatusType;
import de.tudo.naantg.creators.ControllerModelCreator;
import de.tudo.naantg.creators.MockCreator;
import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.scheinboot.*;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import de.tudo.naantg.testproject.test.TestControllerTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerGeneratorTest {

    @Test
    public void generateParamTest() {
        ControllerGenerator generator = new ControllerGenerator();
        ObjectModel objectModel = new ObjectModel(String.class, "name", true);
        objectModel.setValue("alex:a");

        String result = generator.generateParam(objectModel);

        String expected = "\t\t\t\t\t\t.param(\"name\", \"alex:a\")\n";
        assertEquals(expected, result);

        objectModel = new ObjectModel(List.class, "names", true);
        objectModel.setValue("(alex, alexa, alexis)");

        result = generator.generateParam(objectModel);

        expected = "\t\t\t\t\t\t.param(\"names\", \"alex\", \"alexa\", \"alexis\")\n";
        assertEquals(expected, result);
    }

    @Test
    public void generateGetStatementTest() {
        ControllerGenerator generator = new ControllerGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "whenGetRegister_thenGetViewRegister";
        MethodModel methodModel = new MethodModel();
        methodModel.getAnnotationValues().put(SpringBootKey.GET.getKeyword(), Collections.singletonList("\"/register\""));
        model.setTestClassModel(new TestClassModel());
        model.setMethodOfCUT(methodModel, testName);

        String actual = generator.generateGetStatement(testName);

        String expected = "\t\t\t\tget(\"/register\")\n";
        assertEquals(expected, actual);
    }

    @Test
    public void generatePostStatementTest() {
        ControllerGenerator generator = new ControllerGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "whenPostRegister_thenRedirectLogin";
        MethodModel methodModel = new MethodModel();
        methodModel.getAnnotationValues().put(SpringBootKey.POST.getKeyword(), Collections.singletonList("\"/register\""));
        model.setTestClassModel(new TestClassModel());
        model.setMethodOfCUT(methodModel, testName);
        ObjectModel cutObject = new ObjectModel("cut", "cut", false);
        model.setCutObject(cutObject, testName);
        ObjectModel param1 = new ObjectModel(Hauptschein.class, "param1", true);
        ObjectModel objectModel = new ObjectModel(ScheinEntity.class, "param2", true);
        //objectModel.getGivenAnnotations().add(SpringBootKey.MODEL.getKeyword());
        ObjectModel nameObject = new ObjectModel(String.class, "name", true);
        nameObject.setValue("alex:a");
        ObjectModel passwordObject = new ObjectModel(String.class, "password", true);
        passwordObject.setValue("asdfgh");
        objectModel.getInstanceParameters().add(nameObject);
        objectModel.getInstanceParameters().add(passwordObject);
        model.getObjectModels(testName).add(param1);
        model.getObjectModels(testName).add(objectModel);
        cutObject.getInstanceParameters().add(nameObject);
        cutObject.getInstanceParameters().add(passwordObject);

        String actual = generator.generatePostStatement(testName);

        /*String expected = "\t\t\t\tpost(\"/register\")\n" +
                "\t\t\t\t\t\t.param(\"name\", \"alex:a\")\n" +
                "\t\t\t\t\t\t.param(\"password\", \"asdfgh\")\n";*/
        String expected = "\t\t\t\tpost(\"/register\")\n";
        assertEquals(expected, actual);
    }

    @Test
    public void generateInputsTest() {
        ControllerGenerator generator = new ControllerGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "whenPostRegister_thenRedirectLogin";
        MethodModel methodModel = new MethodModel();
        methodModel.getAnnotationValues().put(SpringBootKey.POST.getKeyword(), Collections.singletonList("\"/register\""));
        model.setTestClassModel(new TestClassModel());
        model.setMethodOfCUT(methodModel, testName);
        ObjectModel cutObject = new ObjectModel("cut", "cut", false);
        model.setCutObject(cutObject, testName);
        ObjectModel objectModel = new ObjectModel(ScheinEntity.class, "param1", true);
        //objectModel.getGivenAnnotations().add(SpringBootKey.MODEL.getKeyword());
        ObjectModel nameObject = new ObjectModel(String.class, "name", true);
        nameObject.setValue("alex:a");
        ObjectModel passwordObject = new ObjectModel(String.class, "password", true);
        passwordObject.setValue("asdfgh");
        objectModel.getInstanceParameters().add(nameObject);
        objectModel.getInstanceParameters().add(passwordObject);
        model.getObjectModels(testName).add(objectModel);
        cutObject.getInstanceParameters().add(nameObject);
        cutObject.getInstanceParameters().add(passwordObject);

        String actual = generator.generateInputs(testName);

        String expected = "\n\t\tthis.mockMvc.perform(\n" +
                "\t\t\t\tpost(\"/register\")\n" +
                "\t\t\t\t\t\t.param(\"name\", \"alex:a\")\n" +
                "\t\t\t\t\t\t.param(\"password\", \"asdfgh\")\n" +
                "\t\t)";
        assertEquals(expected, actual);

        testName = "whenGetRegister_thenGetViewRegister";
        methodModel = new MethodModel();
        methodModel.getAnnotationValues().put(SpringBootKey.GET.getKeyword(), Collections.singletonList("\"/register\""));
        model.setTestClassModel(new TestClassModel());
        model.setMethodOfCUT(methodModel, testName);
        objectModel = new ObjectModel(ScheinEntity.class, "param1", true);
        model.getObjectModels(testName).add(objectModel);
        model.setCutObject(new ObjectModel(), testName);

        actual = generator.generateInputs(testName);

        expected = "\n\t\tthis.mockMvc.perform(\n" +
                "\t\t\t\tget(\"/register\")\n" +
                "\t\t)";
        assertEquals(expected, actual);

        testName = "whenCreateThings";
        methodModel = new MethodModel();
        methodModel.getAnnotationValues().put(SpringBootKey.GET.getKeyword(),
                Collections.singletonList("\"first/thing/{scheinId}/test\", SCHEINID"));
        model.setTestClassModel(new TestClassModel());
        model.setMethodOfCUT(methodModel, testName);
        objectModel = new ObjectModel(ScheinEntity.class, "param1", true);
        model.getObjectModels(testName).add(objectModel);
        ObjectModel idModel = new ObjectModel(int.class, "SCHEINID", true);
        idModel.setValue("1");
        model.getParameters(testName).add(idModel);
        model.setCutObject(new ObjectModel(), testName);

        actual = generator.generateInputs(testName);

        expected = "\t\tint SCHEINID = 1;\n" +
                "\n\t\tthis.mockMvc.perform(\n" +
                "\t\t\t\tget(\"first/thing/{scheinId}/test\", SCHEINID)\n" +
                "\t\t)";
        assertEquals(expected, actual);
    }

    @Test
    public void generateExpectedTest() {
        ControllerGenerator generator = new ControllerGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        String testName = "whenGetRegister_thenGetViewRegister";
        List<Assertion> assertions = model.getAssertions(testName);
        assertions.add(new Assertion(CheckType.PRINT.toString(), "", AssertType.TRUE));
        assertions.add(new Assertion(CheckType.STATUS.toString(), StatusType.OK.getType(), AssertType.TRUE));
        assertions.add(new Assertion(CheckType.VIEW.toString(), "register", AssertType.TRUE));

        String result = generator.generateExpected(testName);

        String expected = "\t\t\t\t.andDo(print())\n" +
                "\t\t\t\t.andExpect(status().isOk())\n" +
                "\t\t\t\t.andExpect(view().name(\"register\"));\n";

        assertEquals(expected, result);

        testName = "whenPostRegister_thenRedirectLogin";
        assertions = model.getAssertions(testName);
        assertions.add(new Assertion(CheckType.PRINT.toString(), "", AssertType.TRUE));
        assertions.add(new Assertion(CheckType.STATUS.toString(), StatusType.FOUND.getType(), AssertType.TRUE));
        assertions.add(new Assertion(CheckType.REDIRECT.toString(), "login", AssertType.TRUE));

        result = generator.generateExpected(testName);

        expected = "\t\t\t\t.andDo(print())\n" +
                "\t\t\t\t.andExpect(status().isFound())\n" +
                "\t\t\t\t.andExpect(redirectedUrl(\"/login\"));\n";

        assertEquals(expected, result);

        testName = "whenPostRegister_thenDisplayError";
        assertions = model.getAssertions(testName);
        assertions.add(new Assertion(CheckType.PRINT.toString(), "", AssertType.TRUE));
        assertions.add(new Assertion(CheckType.STATUS.toString(), StatusType.OK.getType(), AssertType.TRUE));
        assertions.add(new Assertion(CheckType.CONTENT_CONTAINS.toString(), "already taken", AssertType.TRUE));

        result = generator.generateExpected(testName);

        expected = "\t\t\t\t.andDo(print())\n" +
                "\t\t\t\t.andExpect(status().isOk())\n" +
                "\t\t\t\t.andExpect(content().string(containsString(\"already taken\")));\n";

        assertEquals(expected, result);

        testName = "whenGetRegister_thenSuccessWithParams";
        assertions = model.getAssertions(testName);
        assertions.add(new Assertion(StateKey.EXIST.toString(), "name", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.NOT_EXIST.toString(), "password", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), "scheinEntity", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), "name,Alex*a", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), "scheinEntity,name,Alex*a", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ERROR.toString(), "scheinEntity", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ERROR.toString(), "scheinEntity,name", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ERROR.toString(), "scheinEntity,name,name is duplicated!", AssertType.TRUE));
        assertions.add(new Assertion(StateKey.ATTRIBUTE.toString(), "pet", AssertType.TRUE));
        assertions.get(assertions.size()-1).setComment("The attribute pet does not exist!");

        result = generator.generateExpected(testName);

        expected = "\t\t\t\t.andExpect(model().attributeExists(\"name\"))\n" +
                "\t\t\t\t.andExpect(model().attributeDoesNotExist(\"password\"))\n" +
                "\t\t\t\t.andExpect(model().attributeHasNoErrors(\"scheinEntity\"))\n" +
                "\t\t\t\t.andExpect(model().attribute(\"name\", \"Alex*a\"))\n" +
                "\t\t\t\t.andExpect(model().attribute(\"scheinEntity\", hasProperty(\"name\", is(\"Alex*a\"))))\n" +
                "\t\t\t\t.andExpect(model().attributeHasErrors(\"scheinEntity\"))\n" +
                "\t\t\t\t.andExpect(model().attributeHasFieldErrors(\"scheinEntity\", \"name\"))\n" +
                "\t\t\t\t.andExpect(model().attributeHasFieldErrorCode(\"scheinEntity\", \"name\", \"name is duplicated!\"))\n" +
                "\t\t\t\t// The attribute pet does not exist!\n" +
                "\t\t\t\t.andExpect(model().attributeHasNoErrors(\"pet\"));\n";


        assertEquals(expected, result);
    }

    @Test
    public void generateIntegrationTest() throws NoSuchMethodException {
        ControllerGenerator generator = new ControllerGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        ControllerModelCreator modelCreator = new ControllerModelCreator("", "");
        modelCreator.setModel(model);
        modelCreator.setMockCreator(new MockCreator(model, ScheinControllerTG.class));
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenGetRegister_thenGetViewRegister";

        modelCreator.calculateMethodModel(testName);
        Method method = ScheinControllerTG.class.getMethod(testName);
        modelCreator.calculateObjects(method);
        String actual = generator.generateInputs(testName);

        String expected = "\n\t\tthis.mockMvc.perform(\n" +
                "\t\t\t\tget(\"/register\")\n" +
                "\t\t)";
        assertEquals(expected, actual);

        testName = "whenPostRegister_thenRedirectToLogin";
        modelCreator.calculateMethodModel(testName);
        method = ScheinControllerTG.class.getMethod(testName);
        modelCreator.calculateObjects(method);
        actual = generator.generateInputs(testName);

        expected = "\n\t\tthis.mockMvc.perform(\n" +
                "\t\t\t\tpost(\"/register\")\n" +
                "\t\t)";
        assertEquals(expected, actual);

        // "\t\t\t\t\t\t.param(\"name\", \"alex:a\")\n" +
        // "\t\t\t\t\t\t.param(\"password\", \"asdfgh\")\n" +

    }

    @Test
    public void generateMockCallsTest() throws NoSuchMethodException {
        ControllerGenerator generator = new ControllerGenerator();
        String pkg = "de.tudo.naantg.testproject";
        ControllerModelCreator modelCreator = new ControllerModelCreator(pkg, "");
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        modelCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        model.setCut(ScheinController.class);
        String testName = "whenPostRegister_thenDisplayError";
        Method method = ScheinControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinService.class, "scheinService", false));
        MockCreator mockCreator = new MockCreator(model, ScheinControllerTG.class);
        mockCreator.setTestName(testName);
        mockCreator.calculateMocking(method, ScheinControllerTG.class);
        MockGenerator mockGenerator = new MockGenerator(model, testName);

        String result = mockGenerator.generateMockCalls();

        String expected = "\t\tdoThrow(new ScheinEntityException())\n" +
                "\t\t\t\t.when(scheinService).createScheinEntity(notNull());\n";
        assertEquals(expected, result);

        testName = "whenLobby_thenShowShines";
        method = ScheinControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinService.class, "scheinService", false));
        mockCreator.setTestName(testName);
        mockCreator.calculateMocking(method, ScheinControllerTG.class);
        mockGenerator = new MockGenerator(model, testName);

        result = mockGenerator.generateMockCalls();

        // or: given(...).willReturn(...);
        expected = "\t\twhen(scheinService.findActiveShines()).thenReturn(new ArrayList<>());\n";
        assertEquals(expected, result);

        testName = "whenCreateThings";
        method = TestControllerTG.class.getMethod(testName);
        model.getTestClassModel().getMocks().add(new ObjectModel(ScheinRepository.class, "scheinRepository", false));
        model.getTestClassModel().getMocks().add(new ObjectModel(SonnenscheinRepository.class, "sonnenscheinRepository", false));
        mockCreator.setTestName(testName);
        mockCreator.calculateMocking(method, TestControllerTG.class);
        mockGenerator = new MockGenerator(model, testName);

        result = mockGenerator.generateMockCalls();

        expected = "\t\twhen(sonnenscheinRepository.findById(OWNER_ID)).thenReturn(Optional.empty());\n";
        assertEquals(expected, result);
    }

}