/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.creators;

import de.tudo.naantg.annotations.*;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.helpers.TestFileHandler;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.parser.ParseKey;
//import org.springframework.data.repository.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates all information for mocking.
 */
public class MockCreator {

    /**
     * the generator model
     */
    private final GeneratorModel model;

    /**
     * the test generation interface
     */
    private final Class<?> tgClass;

    /**
     * the name of the test case
     */
    private String testName;

    /**
     * the defined init type
     */
    private InitType initType;


    /**
     * Creates the mock creator.
     * @param model the generator model
     * @param tgClass the test generation interface
     */
    public MockCreator(GeneratorModel model, Class<?> tgClass) {
        this.model = model;
        this.tgClass = tgClass;
        this.initType = InitType.NONE;
    }

    /**
     * Add additional mocks and calculates the mocking.
     */
    public void addAndCalculateMocking() {
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.ADD_MOCKS.getType())) {
            Class<?>[] mockClasses = tgClass.getDeclaredAnnotation(AddMocks.class).value();
            for (Class<?> mockClass : mockClasses) {
                String identifier = model.getTestClassModel().getUniqueIdentifier(mockClass.getSimpleName());
                ObjectModel mockObject = new ObjectModel(mockClass, identifier, false);
                model.getTestClassModel().getMocks().add(mockObject);
                model.getTestClassModel().addImport(mockObject.getObjectClass().getName());
            }
        }

        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath(model.getTestGenPackageUri() + "/testGen");
        List<Method> methods = Scanner.findMethodsWithTG(tgClass);
        for (Method method : methods) {
            this.testName = method.getName();
            if (testFileHandler.containsTestCase(model.getTestClassName(), method.getName())) {
                continue;
            }
            calculateMocking(method, tgClass);
        }
    }

    /**
     * Detects classes that should be mocked.
     * @param parent the cut object
     */
    public void detectAndAddMocks(ObjectModel parent) {
        //Field[] fields = parent.getObjectClass().getDeclaredFields();
        List<Field> fields = Scanner.findAllFields(parent.getObjectClass());
        for (Field field : fields) {
            if (detectMock(field)) {
                String identifier = model.getTestClassModel().getUniqueIdentifier(field.getType().getSimpleName());
                ObjectModel mockObject = new ObjectModel(field.getType(), identifier, false);
                List<ObjectModel> mocks = model.getTestClassModel().getMocks();
                boolean isFound = false;
                for (ObjectModel mock : mocks) {
                    if (mock.getDataType().equals(mockObject.getDataType())) {
                        isFound = true;
                    }
                }
                if (!isFound) {
                    mocks.add(mockObject);
                    model.getTestClassModel().addImport(field.getType().getName());
                }
            }
        }
    }

    /**
     * Checks, whether the given field should be mocked.
     * @param field the field.
     * @return true, if the field should be mocked.
     */
    public boolean detectMock(Field field) {
        Class<?> fieldClass = field.getType();
        Class<?>[] extInterfaces = field.getType().getInterfaces();
        boolean isRepository = fieldClass.isInterface() && fieldClass.getSimpleName().contains("Repository"); //Arrays.asList(extInterfaces).contains(Repository.class);
        boolean isController = Scanner.hasClassAnnotation(fieldClass, SpringBootKey.CONTROLLER.getKeyword());
        boolean isService = Scanner.hasClassAnnotation(fieldClass, SpringBootKey.SERVICE.getKeyword());
        boolean isAutowired = Scanner.hasFieldAnnotation(field, SpringBootKey.AUTOWIRED.getKeyword());
        return (isAutowired ||
                (fieldClass.isInterface() && (isRepository || isController || isService)) ||
                isController || isService ||
                fieldClass.isInterface() && !Utils.isCollectionType(fieldClass.getSimpleName()));
    }

    /**
     * Calculates the mocking definition given in the Mocking annotation.
     * @param method the test generation method
     */
    public void calculateMocking(Method method, Class<?> tgClass) {
        String definition = "";
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.MOCKING.getType())) {
            definition = tgClass.getDeclaredAnnotation(Mocking.class).value();
        }
        if (Scanner.hasAnnotation(method, AnnoType.MOCKING.getType())) {
            String methodDefinition = method.getDeclaredAnnotation(Mocking.class).value();
            definition = definition.isEmpty()? methodDefinition : definition + ";" + methodDefinition;
        }
        if (definition.isEmpty()) return;

        if (Scanner.hasClassAnnotation(tgClass, AnnoType.INIT_STATE.getType())) {
            initType = tgClass.getDeclaredAnnotation(InitState.class).type();
        }
        if (Scanner.hasAnnotation(method, AnnoType.INIT_STATE.getType())) {
            initType = method.getDeclaredAnnotation(InitState.class).type();
        }

        model.getGivenInitObjects(testName).addAll(calculateInitObjects(method));

        List<String> parsed = AnnotationParser.parseMockingValue(definition);

        String testName = method.getName();
        MethodModel methodModel = new MethodModel();
        List<String> params = new ArrayList<>();
        boolean isClass = false;
        boolean isMethod = false;
        boolean isParam = false;
        boolean isDefaultClass = false;
        boolean isValue = false;

        for (String part : parsed) {
            if (part.equals(StateKey.MOCK_CLASS.toString())) {
                isClass = true;
            }
            else if (part.equals(StateKey.MOCK_METHOD.toString())) {
                isMethod = true;
            }
            else if (part.equals(StateKey.PARAM.toString())) {
                isParam = true;
            }
            else if (part.equals(StateKey.VALUE.toString())) {
                isValue = true;
            }
            else if (isClass && part.equals(StateKey.DEFAULT.toString())) {
                isDefaultClass = true;
                isClass = false;
            }
            else if (isClass) {
                findMockClass(part, methodModel);
                isClass = false;
            }
            else if (isMethod) {
                methodModel.setName(part);
                isMethod = false;
            }
            else if (isParam) {
                if (!part.isEmpty()) {
                    params.add(part);
                }
                isParam = false;
            }
            else if (isValue) {
                createMockMethod(params, methodModel, isDefaultClass, part, method);
                model.getTestMethodModel(testName).getMockCalls().add(methodModel);
                isDefaultClass = false;
                methodModel = new MethodModel();
                isValue = false;
                params.clear();
            }
        }
    }

    /**
     * Finds the corresponding mock class for the given mock class name and
     * adds it to the methodModel.
     * @param mockClass the name of the mock class
     * @param methodModel the mocking statement model
     */
    private void findMockClass(String mockClass, MethodModel methodModel) {
        List<ObjectModel> mocks = model.getTestClassModel().getMocks();
        for (ObjectModel mock : mocks) {
            if (mock.getDataType().equals(mockClass)) {
                methodModel.setObject(mock.getIdentifier());
                methodModel.setObjectClass(mock.getObjectClass());
            }
        }
    }

    /**
     * Finds the corresponding mock class with the given parameter size
     * for the given mock class name and adds it to the methodModel.
     * @param methodName the name of the mock method
     * @param paramSize the parameter count of the mock method
     * @param methodModel the mocking statement model
     */
    private void findMockClass(String methodName, int paramSize, MethodModel methodModel) {
        List<ObjectModel> mocks = model.getTestClassModel().getMocks();
        for (ObjectModel mock : mocks) {
            Optional<Method> optionalMethod = Scanner.findMethodOfClass(mock.getObjectClass(), methodName);
            if (optionalMethod.isPresent()) {
                Method method = optionalMethod.get();
                if (method.getParameters().length == paramSize) {
                    methodModel.setObject(mock.getIdentifier());
                    methodModel.setObjectClass(mock.getObjectClass());
                }
            }
        }
    }

    /**
     * Creates the mocking method model.
     * @param params the parameter of the mock method
     * @param methodModel the mocking statement model
     * @param isDefaultClass whether the mock class is not explicitly given (can be determined)
     * @param value the return value of the mock method
     */
    public void createMockMethod(List<String> params, MethodModel methodModel, boolean isDefaultClass, String value, Method tgMethod) {
        if (isDefaultClass) {
            findMockClass(methodModel.getName(), params.size(), methodModel);
        }
        Optional<Method> optionalMethod = Scanner.findMethodOfClass(methodModel.getObjectClass(), methodModel.getName());
        if (!optionalMethod.isPresent()) return;

        Method method = optionalMethod.get();
        Parameter[] methodParameters = method.getParameters();
        methodModel.setParameters(method.getParameterTypes());
        if (methodParameters.length == params.size()) {
            String[] argList = new String[methodParameters.length];
            methodModel.setArgList(argList);
            for (int i = 0; i < params.size(); i++) {
                ObjectModel paramObject = new ObjectModel();
                String param = params.get(i);
                paramObject.setObjectClass(methodParameters[i].getType());
                if (param.equals(StateKey.NOT_NULL.toString())) {
                    argList[i] = SpringBootKey.NOT_NULL.getKeyword();
                    paramObject.setIdentifier(SpringBootKey.NOT_NULL.getKeyword());
                    model.getTestClassModel().addImport(SpringBootKey.NOT_NULL.getImportPath());
                } else {
                    paramObject.setIdentifier(param);
                    argList[i] = param;
                }
                methodModel.getParameterObjects().add(paramObject);
            }
        }

        if (value.contains(StateKey.EXCEPTION.toString())) {
            calculateExceptionType(value, method, methodModel);
        }
        else {
            calculateReturnValue(value, method, methodModel, tgMethod);
            methodModel.setReturnIdentifier(SpringBootKey.WHEN.getKeyword());
            model.getTestClassModel().addImport(SpringBootKey.WHEN.getImportPath());
        }
    }

    /**
     * Calculates the return value of the mock method.
     * @param value the return value
     * @param method the mock method
     * @param methodModel the mock method model
     */
    private void calculateReturnValue(String value, Method method, MethodModel methodModel, Method tgMethod) {
        if (value.equals(ParseKey.NULL.getKeyword()) || value.equals(StateKey.NULL.toString())) {
            methodModel.setReturnType(value.toLowerCase());
            return;
        }

        Class<?> returnType = method.getReturnType();
        List<Class<?>> genericClasses = Scanner.findGenericOfMethodReturnType(method);

        List<ObjectModel> givenObjects = model.getGivenInitObjects(testName);
        List<ObjectModel> found = givenObjects.stream()
                .filter(objectModel -> (objectModel.getDataType().equals(returnType.getSimpleName()) ||
                        objectModel.getObjectClass() != null && returnType.isAssignableFrom(objectModel.getObjectClass()) ||
                        objectModel.getDataType().equals("Object"))
                        && objectModel.getIdentifier().equals(value)).collect(Collectors.toList());
        if (!found.isEmpty()) {
            methodModel.setReturnType(value);
            return;
        }

        String identifier = model.getUniqueIdentifier(testName, returnType.getSimpleName());
        ObjectModel returnObject = new ObjectModel(returnType, identifier, true);
        //genericClass.ifPresent(returnObject::setGenericClass);
        if (Utils.isObjectType(returnType.getSimpleName())) {
            model.getTestClassModel().addImport(returnType.getName());
        }
        returnObject.setGenericClasses(genericClasses);
        ValueCreator valueCreator = new ValueCreator(model, testName, initType);
        valueCreator.calculateValue(returnObject);
        methodModel.setReturnType(returnType.getSimpleName());
        methodModel.setReturnObject(returnObject);

        if (value.contains("(")) {
            String attrs = value.substring(value.indexOf("("));
            returnObject.setValue(attrs);
        }
    }

    /**
     * Creates the mocking objects.
     * @param tgMethod the tg method.
     * @return the list of all mocked object.
     */
    public List<ObjectModel> calculateInitObjects(Method tgMethod) {
        List<ObjectModel> inits = new ArrayList<>();
        String definition = "";
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.MOCKING.getType())) {
            definition = tgClass.getDeclaredAnnotation(Mocking.class).initState();
        }
        if (Scanner.hasAnnotation(tgMethod, AnnoType.MOCKING.getType())) {
            String methodDefinition = tgMethod.getDeclaredAnnotation(Mocking.class).initState();
            definition = definition.isEmpty()? methodDefinition : definition + ";" + methodDefinition;
        }
        if (definition.isEmpty()) return inits;
        inits = determineGivenObjects(tgMethod, definition);
        return inits;
    }

    /**
     * Looks for the given init type (random or default).
     * @param tgMethod the tg method.
     * @return the given init type.
     */
    private InitType determineInitType(Method tgMethod) {
        boolean initDefault = false;
        boolean initRandom = false;
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.MOCKING.getType())) {
            initDefault = tgClass.getDeclaredAnnotation(Mocking.class).initFieldsWithDefault();
            initRandom = tgClass.getDeclaredAnnotation(Mocking.class).initFieldsWithRandom();
        }
        if (Scanner.hasAnnotation(tgMethod, AnnoType.MOCKING.getType())) {
            if (!initDefault) {
                initDefault = tgMethod.getDeclaredAnnotation(Mocking.class).initFieldsWithDefault();
            }
            if (!initRandom) {
                initRandom = tgMethod.getDeclaredAnnotation(Mocking.class).initFieldsWithRandom();
            }
        }

        if (initDefault) {
            initType = InitType.ALL_DEFAULT;
        }
        if (initRandom) {
            initType = InitType.ALL_RANDOM;
        }
        else {
            return InitType.NONE;
        }
        return initType;
    }

    /**
     * Determines the given replacement of the MappingValue annotation if it exists.
     * @param tgMethod the test generation method
     * @return the replacement
     */
    public List<ObjectModel> determineGivenObjects(Method tgMethod, String definition) {
        List<String> parsed = AnnotationParser.parseMappingValue(definition);
        InitType definedType = determineInitType(tgMethod);

        ValueCreator valueCreator = new ValueCreator(model, tgMethod.getName(), initType);
        if (definedType.equals(InitType.ALL_RANDOM) || definedType.equals(InitType.ALL_DEFAULT)) {
            valueCreator.setRecursiveDeep(1);
        }
        List<ObjectModel> inits = new ArrayList<>();
        boolean isType = false;
        boolean isName = false;
        boolean isValue = false;
        String type = "";
        String name = "";
        String val = "";
        for (String part : parsed) {
            if (part.equals(StateKey.TYPE.toString())) {
                isType = true;
                if (!name.isEmpty()) {
                    addObject(inits, type, name, val, valueCreator, tgMethod.getName());
                    type = "";
                    name = "";
                    val = "";
                }
            }
            else if (part.equals(StateKey.PARAM.toString())) {
                isName = true;
            }
            else if (part.equals(StateKey.VALUE.toString())) {
                isValue = true;
            }
            else if (isType) {
                type = part;
                isType = false;
            }
            else if (isName) {
                name = model.getUniqueIdentifier(tgMethod.getName(), type, part);
                isName = false;
            }
            else if (isValue) {
                val = part;
                isValue = false;
            }
        }
        if (!name.isEmpty()) {
            addObject(inits, type, name, val, valueCreator, tgMethod.getName());
        }
        return inits;
    }

    /**
     * Creates an object with the given values and adds it to the inits.
     * @param inits the list of all init objects.
     * @param type the data type of the object.
     * @param name the name of the object.
     * @param val the value of the object.
     * @param valueCreator the value creator.
     * @param testName the name of the test case.
     */
    private void addObject(List<ObjectModel> inits, String type, String name, String val,
                                      ValueCreator valueCreator, String testName) {
        String generic = "";
        if (type.contains("<")) {
            generic = type.substring(type.indexOf("<"));
            generic = generic.substring(1, generic.length()-1);
            type = type.replaceAll("<.+>", "");
        }
        ObjectModel objectModel = new ObjectModel(type, name, true);
        Optional<Class<?>> correspondingClass;
        if (Utils.isObjectType(type)) {
            correspondingClass = Scanner.findCorrespondingClass(type, model.getProjectPackageUriWithDots());
            correspondingClass.ifPresent(objectModel::setObjectClass);
            correspondingClass.ifPresent(obj -> model.getTestClassModel().addImport(obj.getName()));
        }
        if (!generic.isEmpty()) {
            objectModel.getGenericClasses().clear();
            if (Utils.isSimpleType(generic)) {
                objectModel.getGenericClasses().add(SimpleDataType.getTypeClass(generic));
            }
            else if (Utils.isCollectionType(generic)) {
                objectModel.getGenericClasses().add(CollectionDataType.getTypeClass(generic));
            }
            else {
                correspondingClass = Scanner.findCorrespondingClass(generic, model.getProjectPackageUriWithDots());
                correspondingClass.ifPresent(objectModel.getGenericClasses()::add);
            }
        }
        inits.add(objectModel);

        if (!val.isEmpty()) {
            objectModel.setValue(val);
        }
        valueCreator.setInitObjects(inits);
        valueCreator.calculateValue(objectModel);
        model.getParameters(testName).add(objectModel);
    }

    /**
     * Calculates the exception type of the mock method.
     * @param value the exception
     * @param method the mock method
     * @param methodModel the mock method model
     */
    public void calculateExceptionType(String value, Method method, MethodModel methodModel) {
        Class<?>[] exs = method.getExceptionTypes();
        if (exs.length == 1) {
            Class<?> exception = exs[0];
            ObjectModel returnObject = new ObjectModel(exception, exception.getSimpleName(), true);
            ValueCreator valueCreator = new ValueCreator(model, testName, InitType.NONE);
            valueCreator.calculateValue(returnObject);
            methodModel.setReturnType(exception.getSimpleName());
            methodModel.setReturnObject(returnObject);
            model.getTestClassModel().addImport(exception.getName());
        }
        else if (exs.length > 1) {
            // todo
        }
        else {
            if (method.getName().equals(SpringBootKey.SAVE_AND_FLUSH.getKeyword())) {
                String newException = SpringBootKey.DATA_EXCEPTION.getKeyword();
                ObjectModel returnObject = new ObjectModel(newException,
                        model.getUniqueIdentifier(method.getName(), newException), true);
                ObjectModel exParam = new ObjectModel(String.class,
                        model.getTestMethodModel(testName).getUniqueIdentifier("String"),
                        true);
                exParam.setValue("\"\"");
                model.getObjectModels(testName).add(exParam);
                returnObject.getInstanceParameters().add(exParam);
                methodModel.setReturnObject(returnObject);
                model.getTestClassModel().addImport(SpringBootKey.DATA_EXCEPTION.getImportPath());
            }
            else {
                methodModel.setReturnType("");
            }
        }
        methodModel.setReturnIdentifier(SpringBootKey.THROW.getKeyword());
        model.getTestClassModel().addImport(SpringBootKey.THROW.getImportPath());
    }

    /**
     * Sets the name of the test case.
     * @param testName the name of the test case
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }


}
