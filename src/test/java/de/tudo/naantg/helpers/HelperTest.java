package de.tudo.naantg.helpers;

import de.tudo.naantg.creators.RepositoryAssertionCreator;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.MethodModel;
import de.tudo.naantg.model.ObjectModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelperTest {

    @Test
    public void findGenericObjectTest() {
        RepositoryAssertionCreator assertionCreator = new RepositoryAssertionCreator();
        String testName = "whenFindById_withScheinEntity_thenReturnValue";
        GeneratorModel model = new GeneratorModel();
        assertionCreator.setModel(model);
        MethodModel methodModel = new MethodModel();
        methodModel.setReturnType("Optional");
        methodModel.getGenerics().add("ScheinEntity");
        model.setMethodOfCUT(methodModel, testName);

        initParameters(model, testName);

        ObjectModel found = Helper.findOptionalObject(model, testName);

        assertEquals(model.getParameters(testName).get(0), found);
    }

    private void initParameters(GeneratorModel model, String testName) {
        ObjectModel parent = new ObjectModel("ScheinEntity", "scheinEntity", true);
        ObjectModel parentName = new ObjectModel("String", "name", true);
        ObjectModel parentId = new ObjectModel("long", "scheinId", true);
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