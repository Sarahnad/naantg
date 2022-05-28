package de.tudo.naantg.generator;

import de.tudo.naantg.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryGeneratorTest {

    @Test
    public void generateSpecialInputsTest() {
        RepositoryGenerator generator = new RepositoryGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "givenScheinEntity_whenFindByScheinId_thenReturnValue";
        initMethodModel(model, testName);
        initParameters(model, testName);
        initAssertions(model, testName);
        model.getObjectModels(testName).addAll(model.getParameters(testName));

        String result = generator.generateInputs(testName);

        String expected = "\t\tScheinEntity scheinEntity = new ScheinEntity();\n" +
                "\t\tinitScheinEntityWithDefaultValues(scheinEntity);\n" +
                "\t\tString name = Scheini;\n" +
                "\t\tString password = gwzeUUG7;\n" +
                "\n" +
                "\t\tentityManager.persist(scheinEntity);\n" +
                "\t\tentityManager.flush();\n";
        assertEquals(expected, result);
    }

    @Test
    public void removeSpecialInputsTest() {
        RepositoryGenerator generator = new RepositoryGenerator();
        GeneratorModel model = new GeneratorModel();
        generator.setModel(model);
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        initParameters(model, testName);
        model.getObjectModels(testName).addAll(model.getParameters(testName));

        generator.removeSpecialInputs(testName);

        List<ObjectModel> parameters = model.getObjectModels(testName);
        assertEquals(3, parameters.size());
        assertEquals("ScheinEntity", parameters.get(0).getDataType());
        assertEquals("String", parameters.get(1).getDataType());
        assertEquals("String", parameters.get(2).getDataType());
    }

    private void initParameters(GeneratorModel model, String testName) {
        ObjectModel parent = new ObjectModel("ScheinEntity", "scheinEntity", true);
        ObjectModel parentName = new ObjectModel("String", "name", true);
        ObjectModel parentId = new ObjectModel("Long", "scheinId", true);
        ObjectModel parentPassword = new ObjectModel("String", "password", true);

        parentName.setValue("Scheini");
        parentId.setValue("33");
        parentPassword.setValue("gwzeUUG7");

        parent.getInstanceFields().add(parentName);
        parent.getInstanceFields().add(parentId);
        parent.getInstanceFields().add(parentPassword);

        parentId.getAnnotations().add(SpringBootKey.GENERATED.getKeyword());

        model.getParameters(testName).add(parent);
        model.getParameters(testName).add(parentName);
        model.getParameters(testName).add(parentId);
        model.getParameters(testName).add(parentPassword);
    }

    private void initMethodModel(GeneratorModel model, String testName) {
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        model.setMethodOfCUT(methodModel, testName);
    }

    private void initAssertions(GeneratorModel model, String testName) {
        List<Assertion> assertions = new ArrayList<>();
        assertions.add(new Assertion("", "actual.isPresent()", AssertType.THAT));
        assertions.add(new Assertion("", "actual.get().getName().equals(scheinEntity.getName())", AssertType.THAT));
        assertions.add(new Assertion("", "actual.get().getScheinId() == scheinEntity.getScheinId()", AssertType.THAT));
        assertions.add(new Assertion("", "actual.get().getPassword().equals(scheinEntity.getPassword()", AssertType.THAT));
        model.getAssertions(testName).addAll(assertions);
    }

}