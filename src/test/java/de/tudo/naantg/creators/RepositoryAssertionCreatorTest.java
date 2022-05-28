package de.tudo.naantg.creators;

import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.test.ScheinRepositoryTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryAssertionCreatorTest {

    @Test
    public void createAssertionTest() throws NoSuchMethodException {
        RepositoryAssertionCreator assertionCreator = new RepositoryAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        model.setMethodOfCUT(methodModel, testName);
        initParameters(model, testName);
        List<String> expected = Arrays.asList("RETURN", "VALUE");

        assertionCreator.calculateReturnAssertions(ScheinRepositoryTG.class.getMethod(testName), expected);

        List<Assertion> assertions = model.getAssertions(testName);
        assertEquals(4, assertions.size());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());
        String expectedAssertion = "actual.get().getName().equals(scheinEntity.getName())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(1).generateAssertion());
        expectedAssertion = "actual.get().getScheinId().equals(scheinEntity.getScheinId())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(2).generateAssertion());
        expectedAssertion = "actual.get().getPassword().equals(scheinEntity.getPassword())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(3).generateAssertion());
    }

    @Test
    public void calculateReturnAssertionsTestWithOptional() throws NoSuchMethodException {
        RepositoryAssertionCreator assertionCreator = new RepositoryAssertionCreator();
        assertionCreator.setModel(new GeneratorModel());
        assertionCreator.getModel().setTestClassModel(new TestClassModel());
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        assertionCreator.getModel().setMethodOfCUT(methodModel, testName);
        List<String> expected = Arrays.asList("RETURN", "VALUE");

        assertionCreator.calculateReturnAssertions(ScheinRepositoryTG.class.getMethod(testName), expected);

        List<Assertion> assertions = assertionCreator.getModel().getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());
    }

    @Test
    public void calculateOptionalReturnAssertionsTest() throws NoSuchMethodException {
        RepositoryAssertionCreator assertionCreator = new RepositoryAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        model.setTestClassModel(new TestClassModel());
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        Method method = ScheinRepositoryTG.class.getMethod(testName);
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        model.setMethodOfCUT(methodModel, testName);
        initParameters(model, testName);
        List<String> expected = Arrays.asList("RETURN", "VALUE");
        Assertion assertion = new Assertion();
        String actual = "actual";
        List<Assertion> assertions = model.getAssertions(testName);
        assertions.add(assertion);
        assertion.setActual(actual);

        assertionCreator.calculateOptionalReturnAssertions(expected, assertions, method);

        assertions = model.getAssertions(testName);
        assertEquals(4, assertions.size());
        assertEquals("\t\tassertTrue(actual.isPresent());\n", assertions.get(0).generateAssertion());
        String expectedAssertion = "actual.get().getName().equals(scheinEntity.getName())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(1).generateAssertion());
        expectedAssertion = "actual.get().getScheinId().equals(scheinEntity.getScheinId())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(2).generateAssertion());
        expectedAssertion = "actual.get().getPassword().equals(scheinEntity.getPassword())";
        assertEquals("\t\tassertThat(" + expectedAssertion + ");\n", assertions.get(3).generateAssertion());
    }

    @Test
    public void calculateFieldAssertionTest() {
        RepositoryAssertionCreator assertionCreator = new RepositoryAssertionCreator();
        List<Assertion> assertions = new ArrayList<>();
        ObjectModel parent = new ObjectModel("Parent", "parent", true);
        ObjectModel parentName = new ObjectModel("String", "name", true);
        parentName.setGetterName("getName");
        ObjectModel parentId = new ObjectModel("Long", "id", true);
        parentId.setGetterName("getId");
        ObjectModel parentHasChildren = new ObjectModel("boolean", "hasChildren", true);
        parentHasChildren.setGetterName("getHasChildren");
        parent.getInstanceFields().add(parentName);
        parent.getInstanceFields().add(parentId);
        parent.getInstanceFields().add(parentHasChildren);
        ObjectModel child = new ObjectModel("Child", "firstChild", true);
        child.setGetterName("getChild");
        parent.getInstanceFields().add(child);
        ObjectModel childName = new ObjectModel("String", "childName", true);
        childName.setGetterName("getChildName");
        child.getInstanceFields().add(childName);

        for (ObjectModel field : parent.getInstanceFields()) {
            assertionCreator.calculateFieldAssertion(field, parent, null, null, assertions);
        }
        assertEquals(5, assertions.size());

        String expected = "actual.get().getName().equals(parent.getName())";
        assertEquals("\t\tassertThat(" + expected + ");\n", assertions.get(0).generateAssertion());
        expected = "actual.get().getId().equals(parent.getId())";
        assertEquals("\t\tassertThat(" + expected + ");\n", assertions.get(1).generateAssertion());
        expected = "actual.get().getHasChildren() == parent.getHasChildren()";
        assertEquals("\t\tassertThat(" + expected + ");\n", assertions.get(2).generateAssertion());
        expected = "actual.get().getChild().equals(parent.getChild())";
        assertEquals("\t\tassertThat(" + expected + ");\n", assertions.get(3).generateAssertion());
        expected = "actual.get().getChild().getChildName().equals(parent.getChild().getChildName())";
        assertEquals("\t\tassertThat(" + expected + ");\n", assertions.get(4).generateAssertion());

    }

    private void initParameters(GeneratorModel model, String testName) {
        ObjectModel parent = new ObjectModel("ScheinEntity", "scheinEntity", true);
        ObjectModel parentName = new ObjectModel("String", "name", true);
        ObjectModel parentId = new ObjectModel("Long", "scheinId", true);
        ObjectModel parentPassword = new ObjectModel("String", "password", true);

        parentName.setGetterName("getName");
        parentId.setGetterName("getScheinId");
        parentPassword.setGetterName("getPassword");

        parent.getInstanceFields().add(parentName);
        parent.getInstanceFields().add(parentId);
        parent.getInstanceFields().add(parentPassword);

        model.getParameters(testName).add(parent);
        model.getParameters(testName).add(parentName);
        model.getParameters(testName).add(parentId);
        model.getParameters(testName).add(parentPassword);
    }

}