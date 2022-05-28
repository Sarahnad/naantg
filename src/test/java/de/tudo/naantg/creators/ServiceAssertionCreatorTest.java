package de.tudo.naantg.creators;

import de.tudo.naantg.model.*;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinService;
import de.tudo.naantg.testproject.test.ScheinServiceTG;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceAssertionCreatorTest {

    @Test
    public void calculateReturnAssertionsTest() throws NoSuchMethodException {
        ServiceAssertionCreator assertionCreator = new ServiceAssertionCreator();
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        String testName = "whenCreateScheinEntity_thenReturnException";
        model.setTestClassModel(new TestClassModel());
        MethodModel methodModel = new MethodModel();
        methodModel.setMethodToTest(ScheinService.class.getMethod("createScheinEntity", ScheinEntity.class));
        model.setMethodOfCUT(methodModel, testName);
        List<String> expected = new ArrayList<>();
        expected.add("RETURN");
        expected.add("EXCEPTION");

        assertionCreator.calculateReturnAssertions(ScheinServiceTG.class.getMethod(testName), expected);

        List<Assertion> assertions = model.getAssertions(testName);
        assertEquals(1, assertions.size());
        assertEquals(AssertType.THROWN, assertions.get(0).getAssertType());
        assertEquals("ScheinEntityException", assertions.get(0).getActual());
    }

}