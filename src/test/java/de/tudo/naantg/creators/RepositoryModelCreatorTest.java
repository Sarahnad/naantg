package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.ObjectModel;
import de.tudo.naantg.model.RandomConfigs;
import de.tudo.naantg.model.TestClassModel;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import de.tudo.naantg.testproject.scheinboot.ScheinRepository;
import de.tudo.naantg.testproject.test.ScheinRepositoryTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryModelCreatorTest {

    private String cutUri = "de.tudo.naantg.testproject";


    @Test
    public void findParamsForMethodToTestTest() {
        RepositoryModelCreator modelCreator = new RepositoryModelCreator(cutUri, "");
        ObjectModel cut = new ObjectModel(ScheinRepository.class, "this", false);
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnNull";
        modelCreator.getModel().setCut(ScheinRepository.class);
        modelCreator.getModel().setCutObject(cut, testName);
        modelCreator.calculateMethodModel(testName);
        ObjectModel objectModel = new ObjectModel(ScheinEntity.class, "scheinEntity", true);
        ValueCreator valueCreator = new ValueCreator(modelCreator.getModel(), testName, InitType.NONE);

        valueCreator.calculateObjectParameters(objectModel);
        modelCreator.calculateFields(objectModel, testName, InitType.ALL_DEFAULT);

        List<ObjectModel> params = modelCreator.getModel().getParameters(testName);
        assertEquals(8, params.size());

        modelCreator.findParamsForMethodToTest(objectModel, testName);

        List<ObjectModel> givenInits = modelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, givenInits.size());
        assertEquals("Long", givenInits.get(0).getDataType());
        assertEquals("param1", givenInits.get(0).getIdentifier());
        assertEquals("scheinId", params.get(1).getIdentifier());
        assertEquals("GeneratedValue", params.get(1).getAnnotations().get(0));

    }

    @Test
    public void calculateInitValuesTest() throws NoSuchMethodException {
        RepositoryModelCreator modelCreator = new RepositoryModelCreator(cutUri, "");
        ObjectModel cut = new ObjectModel(ScheinRepository.class, "this", false);
        String testName = "givenScheinEntity_whenFindByScheinId_thenReturnNull";
        Method method = ScheinRepositoryTG.class.getMethod(testName);
        modelCreator.setRandomConfigs(ScheinRepositoryTG.class, method);
        modelCreator.getModel().setCut(ScheinRepository.class);
        modelCreator.getModel().setCutObject(cut, testName);
        modelCreator.calculateMethodModel(testName);

        modelCreator.calculateInitMethods("", testName);

        List<ObjectModel> params = modelCreator.getModel().getParameters(testName);
        params.forEach(System.out::println);
        assertEquals(9, params.size());
        List<ObjectModel> givenInits = modelCreator.getModel().getGivenInitObjects(testName);
        assertEquals(1, givenInits.size());
        assertEquals("Long", givenInits.get(0).getDataType());
        assertEquals("param1", givenInits.get(0).getIdentifier());
        assertEquals("GeneratedValue", params.get(2).getAnnotations().get(0));

        assertFalse(modelCreator.getModel().getCutObject(testName).isNewInstance());
    }

    @Test
    public void calculateObjectsTest() throws NoSuchMethodException {
        RepositoryModelCreator modelCreator = new RepositoryModelCreator(cutUri, "");
        String testName = "givenScheinEntity_whenFindByScheinId_thenReturnNull";
        Method method = ScheinRepositoryTG.class.getMethod("whenFindByScheinId_withScheinEntity_thenReturnNull");
        modelCreator.setRandomConfigs(ScheinRepositoryTG.class, method);
        modelCreator.getModel().setCut(ScheinRepository.class);
        ObjectModel cut = new ObjectModel(ScheinRepository.class, "scheinEntity", false);
        modelCreator.getModel().setCutObject(cut, testName);
        modelCreator.calculateMethodModel(testName);

        modelCreator.calculateObjects(ScheinRepositoryTG.class.getMethod(testName));

        List<ObjectModel> objectModels = modelCreator.getModel().getObjectModels(testName);
        objectModels.forEach(System.out::println);
        assertEquals(10, objectModels.size());

        String methodStatement = modelCreator.getModel().getMethodOfCUT(testName).generateMethodStatement();
        String expected = "\t\tOptional<ScheinEntity> actual = scheinRepository.findByScheinId(scheinEntity.getScheinId());\n";
        assertEquals(expected, methodStatement);
    }

    @Test
    public void setRandomConfigsTest() throws NoSuchMethodException {
        SimpleModelCreator simpleModelCreator = initCreator();
        Class<?> tgClass = ScheinRepositoryTG.class;
        String testName = "whenFindByScheinId_withScheinEntity_thenReturnValue";
        Method method = tgClass.getMethod(testName);

        simpleModelCreator.setRandomConfigs(tgClass, method);

        RandomConfigs randomConfigs = simpleModelCreator.getModel().getTestMethodModel(testName).getRandomConfigs();
        assertNotNull(randomConfigs);
        assertEquals("", randomConfigs.getMinValue());
        assertEquals("", randomConfigs.getMaxValue());
        assertEquals(0, randomConfigs.getMinListSize());
        assertEquals(10, randomConfigs.getMaxListSize());
        assertEquals(5, randomConfigs.getMinStringLength());
        assertEquals(7, randomConfigs.getMaxStringLength());
        assertArrayEquals(new Alphabet[] {Alphabet.BIG_LETTERS, Alphabet.LITTLE_LETTERS}, randomConfigs.getAlphabet());
    }

    //@Test
    public void findClassGenericsTest() throws NoSuchMethodException {
        /*RepositoryModelCreator modelCreator = initCreator();
        Class<?> tgClass = SonnenscheinRepositoryTG.class;
        String testName = "whenFindById_thenReturned_isEmpty";
        Method method = tgClass.getMethod(testName);
        modelCreator.setRandomConfigs(tgClass, method);*/




    }

    private RepositoryModelCreator initCreator() {
        RepositoryModelCreator modelCreator = new RepositoryModelCreator("", "");
        modelCreator.setAssertionCreator(new SimpleAssertionCreator());
        GeneratorModel model = new GeneratorModel();
        modelCreator.setModel(model);
        modelCreator.getModel().setTestClassModel(new TestClassModel());
        return modelCreator;
    }

}