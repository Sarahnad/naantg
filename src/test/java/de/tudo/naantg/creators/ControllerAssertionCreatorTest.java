package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.CheckType;
import de.tudo.naantg.model.AssertType;
import de.tudo.naantg.model.Assertion;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.ObjectModel;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.test.ScheinControllerTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerAssertionCreatorTest {

    @Test
    public void calculateAssertionTest() throws NoSuchMethodException {
        ControllerAssertionCreator assertionCreator = new ControllerAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        List<Assertion> assertions = new ArrayList<>();
        String testName = "whenGetRegister_thenGetViewRegister";
        model.setAssertions(assertions, testName);
        Method method = ScheinControllerTG.class.getMethod(testName);

        assertionCreator.calculateAssertion(method, CheckType.PRINT);

        assertEquals(CheckType.PRINT.toString(), assertions.get(0).getExpected());
        List<String> imports = model.getTestClassModel().getImports();
        assertFalse(imports.isEmpty());
        assertEquals(CheckType.PRINT.getImportPaths(), imports.get(0));

        testName = "whenGetRegister_thenSuccess";
        method = ScheinControllerTG.class.getMethod(testName);
        assertions = model.getAssertions(testName);
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.STATUS);

        assertFalse(assertions.isEmpty());
        Assertion assertion = assertions.get(0);
        assertEquals(CheckType.STATUS.toString(), assertion.getExpected());
        assertEquals("isOk", assertion.getActual());
        assertFalse(imports.isEmpty());
        assertEquals(CheckType.STATUS.getImportPaths(), imports.get(0));

        assertions.clear();
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.VIEW);

        assertFalse(assertions.isEmpty());
        assertion = assertions.get(0);
        assertEquals(CheckType.VIEW.toString(), assertion.getExpected());
        assertEquals("register", assertion.getActual());
        assertFalse(imports.isEmpty());
        assertEquals(CheckType.VIEW.getImportPaths(), imports.get(0));

        testName = "whenGetRegister_thenSuccess_2";
        method = ScheinControllerTG.class.getMethod(testName);
        assertions = model.getAssertions(testName);
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.REDIRECT);

        assertFalse(assertions.isEmpty());
        assertion = assertions.get(0);
        assertEquals(CheckType.REDIRECT.toString(), assertion.getExpected());
        assertEquals("login", assertion.getActual());
        assertFalse(imports.isEmpty());
        assertEquals(CheckType.REDIRECT.getImportPaths(), imports.get(0));

        testName = "whenGetRegister_thenSuccess_3";
        method = ScheinControllerTG.class.getMethod(testName);
        assertions = model.getAssertions(testName);
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.CONTENT_CONTAINS);

        assertFalse(assertions.isEmpty());
        assertion = assertions.get(0);
        assertEquals(CheckType.CONTENT_CONTAINS.toString(), assertion.getExpected());
        assertEquals("duplicated username", assertion.getActual());
        assertFalse(imports.isEmpty());
        assertEquals("static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*", imports.get(0));
        assertEquals("static org.hamcrest.Matchers.containsString", imports.get(1));

        testName = "whenGetRegister_thenGetViewRegister";
        method = ScheinControllerTG.class.getMethod(testName);
        assertions = model.getAssertions(testName);
        assertions.clear();
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.ALL);

        assertFalse(assertions.isEmpty());
        assertEquals(3, assertions.size());
        assertion = assertions.get(0);
        assertEquals(CheckType.PRINT.toString(), assertion.getExpected());
        assertion = assertions.get(1);
        assertEquals(CheckType.STATUS.toString(), assertion.getExpected());
        assertEquals("isOk", assertion.getActual());
        assertion = assertions.get(2);
        assertEquals(CheckType.VIEW.toString(), assertion.getExpected());
        assertEquals("register", assertion.getActual());
        assertEquals(2, imports.size());
        assertEquals(CheckType.PRINT.getImportPaths(), imports.get(0));
        assertEquals(CheckType.STATUS.getImportPaths(), imports.get(1));

        testName = "whenPostRegister_thenRedirectToLogin";
        method = ScheinControllerTG.class.getMethod(testName);
        assertions = model.getAssertions(testName);
        assertions.clear();
        imports.clear();

        assertionCreator.calculateAssertion(method, CheckType.ALL);

        assertFalse(assertions.isEmpty());
        assertEquals(3, assertions.size());
        assertion = assertions.get(0);
        assertEquals(CheckType.PRINT.toString(), assertion.getExpected());
        assertion = assertions.get(1);
        assertEquals(CheckType.STATUS.toString(), assertion.getExpected());
        assertEquals("isOk", assertion.getActual());
        assertion = assertions.get(2);
        assertEquals(CheckType.REDIRECT.toString(), assertion.getExpected());
        assertEquals("login", assertion.getActual());
        assertEquals(2, imports.size());
        assertEquals(CheckType.PRINT.getImportPaths(), imports.get(0));
        assertEquals(CheckType.STATUS.getImportPaths(), imports.get(1));
    }

    @Test
    public void checkFieldOfAttributeTest() {
        ControllerAssertionCreator assertionCreator = new ControllerAssertionCreator();
        String comment = assertionCreator.checkFieldOfAttribute(ScheinEntity.class, "scheinEntity", "job");
        assertEquals("", comment);

        comment = assertionCreator.checkFieldOfAttribute(ScheinEntity.class,"scheinEntity", "pet");
        assertEquals("The attribute scheinEntity does not contain a field with name pet.", comment);
    }

    @Test
    public void calculateModelExistCheckTest() {
        ControllerAssertionCreator assertionCreator = new ControllerAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        String packPath = "de.tudo.naantg.testproject";
        model.setProjectPackageUriWithDots(packPath);
        List<Assertion> assertions = new ArrayList<>();
        String testName = "whenGetRegister_thenSuccessWithParams";
        model.setAssertions(assertions, testName);
        String value = "scheinEntity; !name; !scheinId;";

        assertionCreator.calculateModelExistCheck(value, testName);

        assertEquals(3, assertions.size());
        for (Assertion assertion : assertions) {
            System.out.println(assertion.generateAssertion());
            System.out.println(assertion.getExpected());
            assertEquals(AssertType.TRUE, assertion.getAssertType());
        }
        assertEquals("EXIST", assertions.get(0).getExpected());
        assertEquals("scheinEntity", assertions.get(0).getActual());
        assertEquals("NOT_EXIST", assertions.get(1).getExpected());
        assertEquals("name", assertions.get(1).getActual());
        assertEquals("NOT_EXIST", assertions.get(2).getExpected());
        assertEquals("scheinId", assertions.get(2).getActual());
    }

    @Test
    public void calculateModelAttrCheckTest() {
        ControllerAssertionCreator assertionCreator = new ControllerAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        String packPath = "de.tudo.naantg.testproject";
        model.setProjectPackageUriWithDots(packPath);
        List<Assertion> assertions = new ArrayList<>();
        String testName = "whenGetRegister_thenSuccessWithParams";
        model.setAssertions(assertions, testName);
        String value = "scheinEntity; name = Alex*a; scheinId = 5; scheinEntity.password = *";
        ObjectModel scheini = new ObjectModel(ScheinEntity.class, "scheinEntity", true);
        model.getParameters(testName).add(scheini);
        ObjectModel nameObject = new ObjectModel(String.class, "name", true);
        nameObject.setValue("Alex*a");
        model.getParameters(testName).add(nameObject);
        scheini.getInstanceFields().add(nameObject);
        ObjectModel idObject = new ObjectModel(Long.class, "scheinId", true);
        idObject.setValue("5");
        model.getParameters(testName).add(idObject);
        scheini.getInstanceFields().add(idObject);
        ObjectModel passwordObject = new ObjectModel(String.class, "password", true);
        passwordObject.setValue("zzzz");
        model.getParameters(testName).add(passwordObject);
        scheini.getInstanceFields().add(passwordObject);

        assertionCreator.calculateModelAttrCheck(value, testName, false);

        assertEquals(4, assertions.size());
        for (Assertion assertion : assertions) {
            System.out.println(assertion.generateAssertion());
            System.out.println(assertion.getExpected());
            assertEquals(AssertType.TRUE, assertion.getAssertType());
        }
        assertEquals("ATTRIBUTE", assertions.get(0).getExpected());
        assertEquals("scheinEntity", assertions.get(0).getActual());
        assertEquals("ATTRIBUTE", assertions.get(1).getExpected());
        assertEquals("name,Alex*a", assertions.get(1).getActual());
        assertEquals("ATTRIBUTE", assertions.get(2).getExpected());
        assertEquals("scheinId,5", assertions.get(2).getActual());
        assertEquals("ATTRIBUTE", assertions.get(3).getExpected());
        assertEquals("scheinEntity,password,zzzz", assertions.get(3).getActual());

        value = "attr3 = *; scheinEntity: all = *";
        assertions.clear();

        assertionCreator.calculateModelAttrCheck(value, testName, false);

        assertEquals(4, assertions.size());
        for (Assertion assertion : assertions) {
            System.out.println(assertion.generateAssertion());
            System.out.println(assertion.getExpected());
        }
        assertEquals("ATTRIBUTE", assertions.get(0).getExpected());
        assertEquals("attr3,*", assertions.get(0).getActual());
        assertEquals("The attribute class with the name attr3 can not be found in the project package.", assertions.get(0).getComment());
        assertEquals("ATTRIBUTE", assertions.get(1).getExpected());
        assertEquals("name,Alex*a", assertions.get(1).getActual());
        assertEquals("ATTRIBUTE", assertions.get(2).getExpected());
        assertEquals("scheinId,5", assertions.get(2).getActual());
        assertEquals("ATTRIBUTE", assertions.get(3).getExpected());
        assertEquals("password,zzzz", assertions.get(3).getActual());
    }

    @Test
    public void calculateModelAttrErrorCheckTest() {
        ControllerAssertionCreator assertionCreator = new ControllerAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        String packPath = "de.tudo.naantg.testproject";
        model.setProjectPackageUriWithDots(packPath);
        List<Assertion> assertions = new ArrayList<>();
        String testName = "whenGetRegister_thenSuccessWithParams";
        model.setAssertions(assertions, testName);
        String value = "scheinEntity; name = Alex*a; scheinId = 5; scheinEntity.password = *";
        ObjectModel scheini = new ObjectModel(ScheinEntity.class, "scheinEntity", true);
        model.getParameters(testName).add(scheini);
        ObjectModel nameObject = new ObjectModel(String.class, "name", true);
        nameObject.setValue("Alex*a");
        model.getParameters(testName).add(nameObject);
        scheini.getInstanceFields().add(nameObject);
        ObjectModel idObject = new ObjectModel(Long.class, "scheinId", true);
        idObject.setValue("5");
        model.getParameters(testName).add(idObject);
        scheini.getInstanceFields().add(idObject);
        ObjectModel passwordObject = new ObjectModel(String.class, "password", true);
        passwordObject.setValue("zzzz");
        model.getParameters(testName).add(passwordObject);
        scheini.getInstanceFields().add(passwordObject);

        assertionCreator.calculateModelAttrCheck(value, testName, true);

        assertEquals(4, assertions.size());
        for (Assertion assertion : assertions) {
            System.out.println(assertion.generateAssertion());
            System.out.println(assertion.getExpected());
            assertEquals(AssertType.TRUE, assertion.getAssertType());
        }
        assertEquals("ERROR", assertions.get(0).getExpected());
        assertEquals("scheinEntity", assertions.get(0).getActual());
        assertEquals("ERROR", assertions.get(1).getExpected());
        assertEquals("name,Alex*a", assertions.get(1).getActual());
        assertEquals("ERROR", assertions.get(2).getExpected());
        assertEquals("scheinId,5", assertions.get(2).getActual());
        assertEquals("ERROR", assertions.get(3).getExpected());
        assertEquals("scheinEntity,password,zzzz", assertions.get(3).getActual());

        value = "attr3 = *; scheinEntity: all = *";
        assertions.clear();

        assertionCreator.calculateModelAttrCheck(value, testName, true);

        assertEquals(4, assertions.size());
        for (Assertion assertion : assertions) {
            System.out.println(assertion.generateAssertion());
            System.out.println(assertion.getExpected());
        }
        assertEquals("ERROR", assertions.get(0).getExpected());
        assertEquals("attr3,*", assertions.get(0).getActual());
        assertEquals("The attribute class with the name attr3 can not be found in the project package.", assertions.get(0).getComment());
        assertEquals("ERROR", assertions.get(1).getExpected());
        assertEquals("name,Alex*a", assertions.get(1).getActual());
        assertEquals("ERROR", assertions.get(2).getExpected());
        assertEquals("scheinId,5", assertions.get(2).getActual());
        assertEquals("ERROR", assertions.get(3).getExpected());
        assertEquals("password,zzzz", assertions.get(3).getActual());
    }

}