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
import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.TestFileHandler;
import de.tudo.naantg.model.*;
import de.tudo.naantg.model.RandomConfigs;
import de.tudo.naantg.parser.AnnotationParser;
import de.tudo.naantg.parser.MethodNameParser;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.parser.ParseKey;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Selects all necessary information for the test de.tudo.naantg.generator from the test generation method name
 * and the method de.tudo.naantg.annotations and adds them to a GeneratorModel.
 */
public class SimpleModelCreator implements ModelCreator {

    /**
     * the container fot the generator information
     */
    private GeneratorModel model;

    /**
     * the tgClass annotation
     */
    private Class<?> givenCut;

    /**
     * the assertion creator;
     */
    private AssertionCreator assertionCreator;

    /**
     * the context of the logger information
     */
    private final String LOG_INFO = "[ModelCreator] ";

    /**
     * Tests can be created for all test de.tudo.naantg.generator interfaces and
     * methods in the test package with the testPackageUri.
     * All classes under test must exist in the test package with the testPackageUri.
     * The generated tests can be found in the testPackageUri/testGen folder.
     * @param projectPackageUri the project package uri
     * @param testPackageUri the test package uri
     */
    public SimpleModelCreator(String projectPackageUri, String testPackageUri) {
        this.model = new GeneratorModel();
        convertPackageUris(projectPackageUri, false, false);
        convertPackageUris(testPackageUri, true, false);
    }

    /**
     * Tests can be created for all test de.tudo.naantg.generator interfaces and
     * methods in the test package with the testPackageUri.
     * All classes under test must exist in the test package with the testPackageUri.
     * The generated tests can be found in the testGenPath/testGen folder.
     * @param projectPackageUri the project package uri
     * @param testPackageUri the test package uri
     * @param testGenPath the path for testGen folder
     */
    public SimpleModelCreator(String projectPackageUri, String testPackageUri, String testGenPath) {
        this(projectPackageUri, testPackageUri);
        if (testGenPath != null && !testGenPath.equals("")) {
            convertPackageUris(testGenPath, false, true);
        }
    }

    /**
     * Creates the GeneratorModel for the given test generation interface and its methods.
     * @param tgClass the test generation interface
     * @return the GeneratorModel
     */
    public GeneratorModel createGeneratorModel(Class<?> tgClass) {
        createTestClass(tgClass);

        createSetterGetterTests(tgClass);
        createConstructors(tgClass);

        TestFileHandler testFileHandler = new TestFileHandler();
        testFileHandler.setTestPath(model.getTestGenPackageUri() + "/testGen");

        List<Method> methods = Scanner.findMethodsWithTG(tgClass);
        for (Method method : methods) {
            if (testFileHandler.containsTestCase(model.getTestClassName(), method.getName())) {
                continue;
            }
            createTestCases(method);
        }

        return model;
    }

    /**
     * Creates the test class information.
     * @param tgClass the test generation class
     */
    public void createTestClass(Class<?> tgClass) {
        String packageUri = model.getTestPackageUriWithDots();
        model.setTgClass(tgClass);

        TestClassModel testClassModel = new TestClassModel();
        testClassModel.setPackageUri(packageUri);
        String testClassName = getTestclassName(tgClass.getSimpleName());
        testClassModel.setName(testClassName);
        model.setTestClassName(testClassName);
        testClassModel.setClassType(TestClassType.SIMPLE);
        model.setTestClassModel(testClassModel);

        Class<?> cut = findCut(tgClass);
        model.setCut(cut);

        testClassModel.addImport(cut.getName().replace("$", "."));
        testClassModel.addImport(tgClass.getName());

    }

    /**
     * Defines the class under test for the given tg class.
     * @param tgClass the tg class.
     * @return the class under test.
     */
    public Class<?> findCut(Class<?> tgClass) {
        Class<?> cut;
        if (givenCut != null && !Utils.isString(givenCut.getSimpleName())) {
            cut = givenCut;
        }
        else {
            Optional<Class<?>> optCut = Scanner.findCorrespondingClass(tgClass.getSimpleName().replace("TG", ""),
                    model.getProjectPackageUriWithDots());
            if (!optCut.isPresent()) {
                Logger.logWarning(LOG_INFO + "There is no corresponding class for " + tgClass.getSimpleName() + "!");
                return null;
            }
            cut = optCut.get();
        }
        return cut;
    }

    /**
     * Creates the test case for the test generation method named with testName.
     * @param method the test generation method
     */
    protected void createTestCases(Method method) {
        Class<?> cut = model.getCut();
        if (cut == null) return;
        String testName = method.getName();
        assertionCreator.setModel(model);

        setRandomConfigs(model.getTgClass(), method);
        model.getTestMethodModel(testName).setTgMethod(method);
        calculateAnnotations(method);
        calculateMethodModel(testName);
        calculateObjects(method);
        assertionCreator.calculateAssertions(method);
    }

    /**
     * Calculates the given annotations of the init state.
     * @param method the test case
     */
    public void calculateAnnotations(Method method) {
        Class<?> tgClass = model.getTgClass();
        List<Class<?>> annos = new ArrayList<>();
        List<Class<?>> withoutAnnos = new ArrayList<>();
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.INIT_STATE.getType())) {
            annos.addAll(Arrays.asList(tgClass.getDeclaredAnnotation(InitState.class).withAnnotations()));
            withoutAnnos.addAll(Arrays.asList(tgClass.getDeclaredAnnotation(InitState.class).withoutAnnotations()));
        }
        if (Scanner.hasAnnotation(method, AnnoType.INIT_STATE.getType())) {
            annos.addAll(Arrays.asList(method.getDeclaredAnnotation(InitState.class).withAnnotations()));
            withoutAnnos.addAll(Arrays.asList(method.getDeclaredAnnotation(InitState.class).withoutAnnotations()));
        }
        addAnnotations(annos, withoutAnnos, method.getName());
    }

    /**
     * Adds the annotation classes that are not contained in withoutAnnos and not equal "TG".
     */
    private void addAnnotations(List<Class<?>> annos, List<Class<?>> withoutAnnos, String testName) {
        for (Class<?> anno : annos) {
            if (anno.getSimpleName().equals(AnnoType.TG.getType())) {
                continue;
            }
            if (withoutAnnos.contains(anno)) {
                continue;
            }

            model.getTestMethodModel(testName).getAnnotations().add(anno);
            model.getTestClassModel().addImport(anno.getName());
        }
    }

    /**
     * Sets default random configs.
     * @param tgClass the test generation class
     * @param method the test case
     */
    public void setRandomConfigs(Class<?> tgClass, Method method) {
        RandomConfigs randomConfigs = new RandomConfigs();
        String value;
        int parameter;
        Alphabet[] alphabets;

        if (Scanner.hasClassAnnotation(tgClass, "RandomConfigs")) {
            value = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minValue();
            randomConfigs.setMinValue(value);
            value = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxValue();
            randomConfigs.setMaxValue(value);
            parameter = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minListSize();
            randomConfigs.setMinListSize(parameter);
            parameter = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxListSize();
            randomConfigs.setMaxListSize(parameter);
            parameter = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minStringLength();
            randomConfigs.setMinStringLength(parameter);
            parameter = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxStringLength();
            randomConfigs.setMaxStringLength(parameter);
            alphabets = tgClass.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).alphabet();
            randomConfigs.setAlphabet(alphabets);
        }
        else if (Scanner.hasAnnotation(method, "RandomConfigs")) {
            value = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minValue();
            randomConfigs.setMinValue(value);
            value = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxValue();
            randomConfigs.setMaxValue(value);
            parameter = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minListSize();
            randomConfigs.setMinListSize(parameter);
            parameter = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxListSize();
            randomConfigs.setMaxListSize(parameter);
            parameter = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).minStringLength();
            randomConfigs.setMinStringLength(parameter);
            parameter = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).maxStringLength();
            randomConfigs.setMaxStringLength(parameter);
            alphabets = method.getDeclaredAnnotation(de.tudo.naantg.annotations.RandomConfigs.class).alphabet();
            randomConfigs.setAlphabet(alphabets);
        }
        else {
            randomConfigs.setMinValue("");
            randomConfigs.setMaxValue("");
            randomConfigs.setMinListSize(0);
            randomConfigs.setMaxListSize(10);
            randomConfigs.setMinStringLength(0);
            randomConfigs.setMaxStringLength(20);
            randomConfigs.setAlphabet(new Alphabet[] {Alphabet.ALL});
        }
        model.getTestMethodModel(method.getName()).setRandomConfigs(randomConfigs);
    }

    /**
     * Selects the information about the method to test.
     * @param testName the name of the test case
     */
    public void calculateMethodModel(String testName) {
        calculateMethodModel(testName, "");
    }

    /**
     * Selects the information about the method to test.
     * @param testName the name of the test case
     * @param methodToTest the name of the method to test
     */
    public void calculateMethodModel(String testName, String methodToTest) {
        Class<?> cut = model.getCut();
        String methodName = methodToTest.isEmpty()? MethodNameParser.getMethodName(testName) : methodToTest;
        Optional<Method> optionalMethod = Scanner.findCorrespondingMethod(cut, methodName);
        if (!optionalMethod.isPresent()) {
            Logger.logWarning(LOG_INFO + "There exists no method with name " + methodName + " in " + cut.getSimpleName() + "!");
            return;
        }
        MethodModel methodModel = new MethodModel();
        methodModel.setName(methodName);
        methodModel.setMethodToTest(optionalMethod.get());
        Class<?> returnClass = optionalMethod.get().getReturnType();
        String returnType = returnClass.getSimpleName();
        if (Utils.isCollectionType(returnType)) {
            calculateCollectionReturnType(returnClass, methodModel);
        }
        else if (Utils.isObjectType(returnType)) {
            model.getTestClassModel().addImport(returnClass.getName());
        }
        String identifier = model.getTestMethodModel(testName).getUniqueIdentifier(cut.getSimpleName());
        methodModel.setReturnType(returnType);
        if (methodModel.getReturnObject() == null) {
            methodModel.setReturnObject(new ObjectModel(returnClass, "", false));
        }
        methodModel.setObject(identifier);
        if (optionalMethod.get().getParameterCount() > 0) {
            Class<?>[] parameters = optionalMethod.get().getParameterTypes();
            methodModel.setParameters(parameters);
        }
        model.setMethodOfCUT(methodModel, testName);
    }

    /**
     * Calculates the optional generic for the collection return type and adds necessary imports.
     * @param returnType the return type of the method
     * @param methodModel the model of the method to test
     */
    public void calculateCollectionReturnType(Class<?> returnType, MethodModel methodModel) {
        List<Class<?>> generics = Scanner.findGenericOfMethodReturnType(methodModel.getMethodToTest());
        List<String> genericNames = new ArrayList<>();
        if (!generics.isEmpty()) {
            //generics.forEach(generic -> genericNames.add(generic.getSimpleName()));
            for (Class<?> generic : generics) {
                genericNames.add(generic.getSimpleName());
                if (!Utils.isSimpleType(generic.getSimpleName())) {
                    model.getTestClassModel().addImport(generic.getName());
                }
            }
            if (methodModel.getReturnObject() == null) {
                methodModel.setReturnObject(new ObjectModel(returnType, "", false));
            }
            methodModel.getReturnObject().setGenericClasses(generics);

            methodModel.setGenerics(genericNames);
        }
        //!generics.isEmpty(aClass -> methodModel.setGenerics(aClass.getSimpleName()));
        /*if (generics.isPresent() && !Utils.isSimpleType(generics.get().getSimpleName())) {
            generics.ifPresent(aClass -> model.getTestClassModel().addImport(aClass.getName()));
        }*/
        if (Utils.isArray(returnType.getName())) {
            model.getTestClassModel().addImport(CollectionDataType.ARRAYS.toString());
            if (!Utils.isSimpleType(returnType.getComponentType().getSimpleName())) {
                model.getTestClassModel().addImport(returnType.getComponentType().getName());
            }
        }
        else if (!Utils.isSimpleType(returnType.getSimpleName())) {
            model.getTestClassModel().addImport(returnType.getName());
        }
    }

    /**
     * Selects the information about the objects and primitive values of the test case.
     * @param method the test case
     */
    public void calculateObjects(Method method) {
        Class<?> cut = model.getCut();
        if (cut == null || method == null) return;
        String testName = method.getName();
        if (model.getMethodOfCUT(testName) == null) return;

        InitType initType = InitType.NONE;
        String initValue = "";
        String initMethods = "";
        Class<?> tgClass = model.getTgClass();
        if (tgClass!= null && Scanner.hasClassAnnotation(tgClass, AnnoType.INIT_STATE.getType())) {
            initType = tgClass.getDeclaredAnnotation(InitState.class).type();
            initValue = tgClass.getDeclaredAnnotation(InitState.class).value();
            initMethods = tgClass.getDeclaredAnnotation(InitState.class).methods();
        }
        if (Scanner.hasInitStateAnnotation(method)) {
            initType = method.getDeclaredAnnotation(InitState.class).type();
            String methodInitValue = method.getDeclaredAnnotation(InitState.class).value();
            String methodInitMethods = method.getDeclaredAnnotation(InitState.class).methods();
            initValue = initValue.isEmpty()? methodInitValue : initValue + ";" + methodInitValue;
            initMethods = initMethods.isEmpty()? methodInitMethods : initMethods + ";" + methodInitMethods;
        }

        String identifier = model.getMethodOfCUT(testName).getObject();
        ObjectModel objectModel = new ObjectModel(cut, identifier, true);
        objectModel.setInitType(initType);
        model.setCutObject(objectModel, testName);
        model.getObjectModels(testName).add(objectModel);

        if (initType.equals(InitType.NONE)) {
            calculatesParameters(method);
        }
        else {
            calculatesParameters(method, initType);
        }

        calculateInitState(initValue, initType, testName);

        calculateInitMethods(initMethods, testName);

        calculateFields(objectModel, testName, initType);
        model.getObjectModels(testName).addAll(model.getParameters(testName));
    }

    /**
     * Checks if the parameters are given and can be used.
     * @param givenParams the given parameters
     * @param index the indes of the parameter
     * @return true if enough parameters are given, otherwise false
     */
    private boolean isParamGiven(String[] givenParams, int index) {
        return givenParams != null && !givenParams[index].equals("");
    }

    /**
     * Calculates all fields of the parent object.
     * @param parent the parent object
     * @param testName the name of the test case
     * @param initType the init type
     */
    public void calculateFields(ObjectModel parent, String testName, InitType initType) {
        ValueCreator valueCreator = new ValueCreator(model, testName, initType);
        valueCreator.calculateFields(parent);
    }

    /**
     * Calculates all instances for the parameters of the test case.
     * @param method the method of the test case
     */
    public void calculatesParameters(Method method) {
        calculatesParameters(method, InitType.NONE, method.getName());
    }

    /**
     * Calculates all instances for the parameters of the test case.
     * @param method the method of the test case
     * @param initType the init type
     */
    public void calculatesParameters(Method method, InitType initType) {
        calculatesParameters(method, initType, method.getName());
    }

    /**
     * Calculates all instances for the parameters of the test case.
     * @param testName the name of the test case
     */
    public void calculatesParameters(String testName) {
        calculatesParameters(null, InitType.NONE, testName);
    }

    /**
     * Calculates all instances for the parameters of the method to test.
     * @param method the method of the test case
     * @param initType the init type
     * @param methodName the name of the test case
     */
    public void calculatesParameters(Method method, InitType initType, String methodName) {
        String testName = method != null ? method.getName() : methodName;
        Class<?>[] parameters = model.getMethodOfCUT(testName).getParameters();

        if (parameters != null && parameters.length > 0) {
            String[] givenParams = null;
            List<String> givenConstructors;
            String params = "";
            Class<?>[] paramType = null;
            String constructors = "";
            if (method != null && Scanner.hasParamsAnnotation(method)) {
                params = method.getDeclaredAnnotation(Params.class).value();
                paramType = method.getDeclaredAnnotation(Params.class).type();
                constructors = method.getDeclaredAnnotation(Params.class).constructor();
            }
            if (!params.equals("")) {
                givenParams = AnnotationParser.parseParams(params, parameters.length);
            }
            if (!constructors.equals("")) {
                givenConstructors = AnnotationParser.parseConstructors(constructors, parameters);
                calculateGivenConstructors(givenConstructors, parameters, testName, initType);
            }

            int index = 1;

            for (Class<?> parameter : parameters) {
                //if (checkDefinedInPreState(parameter, index, testName)) continue;

                ObjectModel newParameter = new ObjectModel(parameter, "param" + index, true);
                if (parameter.isInterface() && paramType != null) {
                    for (Class<?> type : paramType) {
                        if (!type.getSimpleName().equals(CollectionDataType.ARRAY_LIST.getType())
                                && parameter.isAssignableFrom(type)) {
                            //newParameter.setObjectClass(type);
                            newParameter.setDataType(type.getSimpleName());
                        }
                    }
                }
                String givenValue = isParamGiven(givenParams, index)? givenParams[index] : null;
                newParameter.setValue(givenValue);

                calculateParameter(newParameter, testName, initType);

                index++;
                model.getParameters(testName).add(newParameter);
            }
        }
    }

    /**
     * Calculates the given constructor.
     * @param givenConstructors the list of the given constructors.
     * @param parameters the constructor parameters.
     * @param testName the name of the test case.
     * @param initType the init type.
     */
    public void calculateGivenConstructors(List<String> givenConstructors, Class<?>[] parameters, String testName, InitType initType) {
        ValueCreator valueCreator = new ValueCreator(model, testName, initType);
        ObjectModel objectModel = new ObjectModel();
        int index = -1;
        List<String> constructorParams = new ArrayList<>();
        boolean isNext = false;
        boolean isIndex = false;
        boolean isConstructor = false;
        boolean isParam = false;

        for (String part : givenConstructors) {
            if (part.equals(StateKey.INDEX.toString())) {
                isIndex = true;
                if (isNext && !constructorParams.isEmpty()) {
                    Optional<Constructor<?>> constructor = Scanner.findConstructor(constructorParams, parameters[index]);

                    if (constructor.isPresent()) {
                        model.getParameters(testName).add(objectModel);
                        model.getGivenInitObjects(testName).add(objectModel);
                        valueCreator.setGivenConstructor(constructor.get());
                        valueCreator.calculateObjectParameters(objectModel);
                    }
                    objectModel = new ObjectModel();
                    isNext = false;
                }
                else {
                    isNext = true;
                }
            }
            else if (part.equals(StateKey.CONSTRUCTOR.toString())) {
                isConstructor = true;
            }
            else if (part.equals(StateKey.PARAM.toString())) {
                isParam = true;
            }
            else if (isIndex) {
                objectModel.setIdentifier("param" + part);
                index = Integer.parseInt(part) - 1;
                isIndex = false;
            }
            else if (isConstructor && (index > -1) && parameters[index].getSimpleName().equals(part)) {
                objectModel.setObjectClass(parameters[index]);
                isConstructor = false;
            }
            else if (isParam) {
                constructorParams.add(part);
                isParam = false;
            }
        }

        if (!constructorParams.isEmpty()) {
            Optional<Constructor<?>> constructor = Scanner.findConstructor(constructorParams, parameters[index]);

            if (constructor.isPresent()) {
                model.getParameters(testName).add(objectModel);
                model.getGivenInitObjects(testName).add(objectModel);

                valueCreator.setGivenConstructor(constructor.get());
                valueCreator.calculateObjectParameters(objectModel);
            }
        }


    }

    /**
     * Calculates all information for the given parameter and add them to the parameterObject.
     * @param parameterObject the parameter object model
     * @param testName the name of the test case
     * @param initType the init type
     */
    public void calculateParameter(ObjectModel parameterObject, String testName, InitType initType) {
        ValueCreator valueCreator = new ValueCreator(model, testName, initType);
        valueCreator.calculateValue(parameterObject);
    }

    /**
     * Calculates the init state.
     * @param initValue the init value.
     * @param initType the init type.
     * @param testName the name of the test case.
     */
    protected void calculateInitState(String initValue, InitType initType, String testName) {
        if (!initValue.equals("")) {
            List<String> parsedValues = AnnotationParser.parseInitStateValue(initValue);
            if (!parsedValues.isEmpty()) calculateInitValues(parsedValues, initType, testName);
        }
        else {
            //List<String> preValues = MethodNameParser.getGivenValue(testName);
            //if (!preValues.isEmpty()) calculateInitValues(preValues, initType, testName);
        }
    }

    /**
     * Calculates the init value.
     * @param parsedValues the parsed values.
     * @param initType the init type.
     * @param testName the name of the test case.
     */
    protected void calculateInitValues(List<String> parsedValues, InitType initType, String testName) {
        ValueCreator valueCreator = new ValueCreator(model, testName, initType);
        List<ObjectModel> notFoundObjects = new ArrayList<>();

        boolean isType = false;
        boolean isName = false;
        boolean isValue = false;
        boolean isFrom = false;
        boolean isField = false;
        String type = "";
        String caller = "";
        List<String> fields = new ArrayList<>();
        String name = "";
        String val = "";
        for (String part : parsedValues) {
            if (part.equals(StateKey.TYPE.toString())) {
                isType = true;
                if (!name.isEmpty()) {
                    addObject(type, name, val, valueCreator, testName);
                }
                else if (!caller.isEmpty()) {
                    addObject(caller, fields, val, notFoundObjects, testName);
                }
                type = "";
                name = "";
                val = "";
                caller = "";
                fields.clear();
            }
            else if (part.equals(StateKey.FROM.toString())) {
                isFrom = true;
                if (!name.isEmpty()) {
                    addObject(type, name, val, valueCreator, testName);
                }
                else if (!caller.isEmpty()) {
                    addObject(caller, fields, val, notFoundObjects, testName);
                }
                type = "";
                name = "";
                val = "";
                caller = "";
                fields.clear();
            }
            else if (part.equals(StateKey.FIELD.toString())) {
                isField = true;
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
            else if (isFrom) {
                caller = part;
                isFrom = false;
            }
            else if (isField) {
                fields.add(part);
                isField = false;
            }
            else if (isName) {
                name = part; // model.getUniqueIdentifier(testName, type, part);
                isName = false;
            }
            else if (isValue) {
                val = part;
                isValue = false;
            }
        }
        if (!name.isEmpty()) {
            addObject(type, name, val, valueCreator, testName);
        }
        else if (!caller.isEmpty()) {
            addObject(caller, fields, val, notFoundObjects, testName);
        }
    }

    /**
     * Adds the new object to the list of not found objects.
     * @param caller the caller name.
     * @param fields the generic types.
     * @param val the given value.
     * @param notFoundObjects the list of not found objects.
     * @param testName the name of the test case.
     */
    protected void addObject(String caller, List<String> fields, String val,
                             List<ObjectModel> notFoundObjects, String testName) {
        ObjectModel callerObject = findObjectInPreState(caller, testName);
        if (callerObject == null) {
            ObjectModel notFound = new ObjectModel("", caller, false);
            notFound.getGenericTypes().addAll(fields);
            notFound.setValue(val);
            notFoundObjects.add(notFound);
            return;
        }

        ObjectModel setObject = new ObjectModel();
        setObject.setIdentifier(callerObject.getIdentifier());
        callerObject.getFieldCalls().add(setObject);
        ObjectModel parent = callerObject;
        for (String field : fields) {
            //Field[] parentFields = parent.getObjectClass().getDeclaredFields();
            List<Field> parentFields = Scanner.findAllFields(parent.getObjectClass());

            for (Field f : parentFields) {
                if (f.getName().equals(field)) {
                    ObjectModel fieldObject = new ObjectModel(f.getType(), f.getName(), false);
                    String getterName = Scanner.getGetterMethod(parent.getObjectClass(), f);
                    String setterName = Scanner.getSetterMethod(parent.getObjectClass(), f);
                    fieldObject.setGetterName(getterName);
                    fieldObject.setSetterName(setterName);
                    setObject.getInstanceFields().add(fieldObject);
                    parent = fieldObject;
                    break;
                }
            }
        }
        ObjectModel found = Helper.findObjectByIdentifier(val, model.getGivenInitObjects(testName));
        if (found != null) {
            setObject.setValue(val);
            if (Utils.isListType(found.getDataType())) setObject.setElementSize(2); // 2 indicates a list
            return;
        }
        else if (Utils.isString(parent.getDataType())) {
            val = "\"" + val + "\"";
        }
        else if (Utils.isChar(parent.getDataType())) {
            val = "'" + val + "'";
        }
        setObject.setValue(val);

    }

    /**
     * Creates and add the new object to the parameter object list.
     * @param type the data type of the object.
     * @param name the name of the object.
     * @param val the value of the object.
     * @param valueCreator the value creator.
     * @param testName the name of the test case.
     */
    protected void addObject(String type, String name, String val,
                           ValueCreator valueCreator, String testName) {
        List<ObjectModel> inits = model.getGivenInitObjects(testName);
        ObjectModel given = Helper.findObjectByIdentifier(name, inits);
        if (given != null && given.getDataType().equals(type)) return;

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
            if (val.equals(StateKey.NULL.toString())) {
                objectModel.setValue(ParseKey.NULL.getKeyword());
                objectModel.setNewInstance(false);
                model.getParameters(testName).add(objectModel);
                return;
            }
            objectModel.setValue(val);
            if (!Utils.isCollectionType(type)) {
                objectModel.setNewInstance(false);
            }
        }
        valueCreator.setInitObjects(inits);
        valueCreator.calculateValue(objectModel);
        model.getParameters(testName).add(objectModel);
    }

    /**
     * Finds the corresponding object in the object init list with help of the name.
     * @param name the name of the object.
     * @param testName the name of the test case.
     * @return the found object.
     */
    private ObjectModel findObjectInPreState(String name, String testName) {
        ObjectModel objectModel;
        if (name.matches("param\\d+")) {
            objectModel = Helper.findObjectByIdentifier(name, model.getParameters(testName));
            if (objectModel == null) objectModel = createInitMethodParam(name, testName);
        } else if (name.equals(StateKey.THIS.toString())) {
            objectModel = model.getCutObject(testName);
        } else {
            objectModel = Helper.findObjectByIdentifier(name, model.getGivenInitObjects(testName));
        }
        return objectModel;
    }

    /**
     * Calculates the objects for the pre state and parses therefor the given initValue.
     * @param initValue the described pre state
     * @param testName the name of the test case
     */
    public void calculateInitMethods(String initValue, String testName) {
        if (!initValue.equals("")) {
            List<String> parsedValues = AnnotationParser.parseInitStateMethods(initValue);
            if (!parsedValues.isEmpty()) calculateInitMethods(parsedValues, testName);
        }
        else {
            List<String> preValues = MethodNameParser.getGivenValue(testName);
            if (!preValues.isEmpty()) calculateInitMethods(preValues, testName);
        }
    }

    /**
     * Calculates the objects for the pre state and uses therefor the given parsedValues.
     * @param parsedValues the parsed pre state
     * @param testName the name of the test case
     */
    public void calculateInitMethods(List<String> parsedValues, String testName) {
        boolean isOf = false;
        boolean isFrom = false;
        boolean isValue = false;
        boolean isParam = false;
        boolean isType = false;
        boolean isVoid = false;
        String methodCallName = "";
        String callingObject = "";
        String value = "";
        List<String> params = new ArrayList<>();

        for (String part : parsedValues) {
            if (part.equals(StateKey.VALUE.toString()) || part.equals(StateKey.VALUES.toString())) {
                if (!methodCallName.isEmpty()) {
                    createMethodCall(methodCallName, callingObject, params, value, isType, testName);
                    callingObject = "";
                    value = "";
                    isType = false;
                    params.clear();
                }
                isValue = true;
            }
            else if (part.equals(StateKey.OF.toString())) {
                isOf = true;
            }
            else if (part.equals(StateKey.FROM.toString())) {
                isFrom = true;
            }
            else if (part.equals(StateKey.PARAM.toString())) {
                isParam = true;
            }
            else if (part.equals(StateKey.TYPE.toString())) {
                isType = true;
            }
            else if (isValue) {
                if (!part.equals(StateKey.VOID.toString())) {
                    value = part;
                }
                isValue = false;
            }
            else if (isOf) {
                methodCallName = part;
                isOf = false;
            }
            else if (isFrom) {
                callingObject = part;
                isFrom = false;
            }
            else if (isParam) {
                params.add(part);
                isParam = false;
            }
            else {

            }
        }
        if (!methodCallName.isEmpty()) {
            createMethodCall(methodCallName, callingObject, params, value, isType, testName);
        }
    }

    /**
     * Creates a method model for the given methodCallName
     * and uses the information of the calling object in the preStates.
     * @param methodCallName the name of the called method
     * @param preStates the preStates with calling objects
     * @param testName the name of the test case
     */
    /*public void createMethodCall(String methodCallName, List<ObjectModel> preStates, String testName) {
        //createMethodCall(methodCallName, preStates, null, testName);
    }*/

    /**
     * Creates a method model for the given methodCallName
     * and uses the information of the calling object in the preStates.
     * A new calling object is created and added to the preStates and the parameter list.
     * @param methodCallName the name of the called method
     * //@param preStates the preStates with calling objects
     * @param callingObjectName the name of the calling object
     * @param testName the name of the test case
     */
    public void createMethodCall(String methodCallName, String callingObjectName, List<String> params, String value,
                                 boolean isType, String testName) {
        if (callingObjectName.isEmpty()) {
            createInitHelperMethod(methodCallName, params, value, testName);
            return;
        }

        ObjectModel cut = model.getCutObject(testName);
        ObjectModel callingObject = null;

        if (callingObjectName.equals(StateKey.THIS.toString())) {
            callingObject = cut;
        }
        else if (callingObjectName.matches("param\\d+")) {
            callingObject = Helper.findObjectByIdentifier(callingObjectName, model.getParameters(testName));
            if (callingObject == null) callingObject = createInitMethodParam(callingObjectName, testName);
        }
        else if (isType) {
            Optional<Class<?>> optionalClass = Scanner.findCorrespondingClass(callingObjectName, model.getProjectPackageUriWithDots());
            if (optionalClass.isPresent()) {
                callingObject = new ObjectModel(optionalClass.get(), callingObjectName, false);
                callingObject.setNotToGenerate(true);
                model.getGivenInitObjects(testName).add(callingObject);
                model.getParameters(testName).add(callingObject);
                model.getTestClassModel().getImports().add(optionalClass.get().getName());
            }
        }
        else {
            callingObject = Helper.findObjectByIdentifier(callingObjectName, model.getGivenInitObjects(testName));
        }
        if (callingObject == null) return;
        validateAndAddInitMethodStatement(methodCallName, params.size(), callingObject);
        if (callingObject.getObjectClass() == null) return;
        validateParams(callingObject, methodCallName, params, testName);
        validateReturnValue(callingObject, value, testName);
    }

    /**
     * Creates the helper method statement.
     * @param methodCallName the method name.
     * @param params the method parameters.
     * @param value the return value.
     * @param testName the name of the test case.
     */
    protected void createInitHelperMethod(String methodCallName, List<String> params, String value, String testName) {
        TestCaseModel initMethod = new TestCaseModel();
        initMethod.setGiven(true);
        initMethod.setTestMethodName(methodCallName);
        initMethod.setParameters(createInitMethodParams(params, testName));
        createInitReturnObject(initMethod, value, testName);
        getModel().getTestClassModel().getPrivateHelperMethods().add(initMethod);
    }

    /**
     * Creates the return object of the init method.
     * @param initMethod the init method.
     * @param value the return value.
     * @param testName the name of the test case.
     */
    protected void createInitReturnObject(TestCaseModel initMethod, String value, String testName) {
        MethodModel methodModel = new MethodModel();
        methodModel.setName(initMethod.getTestMethodName());
        methodModel.setParameterObjects(initMethod.getParameters());

        if (value.isEmpty()) {
            model.getCutObject(testName).getMethodCalls().add(methodModel);
            return;
        }

        ObjectModel parent;
        if (value.equals(StateKey.THIS.toString())) {
            parent = model.getCutObject(testName);
            // todo: fields
        }
        else if (value.matches("param\\d+")) {
            parent = Helper.findObjectByIdentifier(value, model.getParameters(testName));
            if (parent == null) parent = createInitMethodParam(value, testName);

            // todo: fields
        }
        else {
            parent = Helper.findObjectByIdentifier(value, model.getGivenInitObjects(testName));
        }

        if (parent != null) {
            ObjectModel copy = new ObjectModel(parent.getObjectClass(), parent.getIdentifier(), true);
            copy.setValue(parent.getValue());
            initMethod.setReturnObject(copy);
            handleMethodWithReturnValue(methodModel, parent, testName);
        }
        else {
            // todo: add type information for return value
        }
    }

    /**
     * Creates the statement for methods with a return value.
     * @param methodModel the model of the method.
     * @param parent the parent object.
     * @param testName the name of the test case.
     */
    private void handleMethodWithReturnValue(MethodModel methodModel, ObjectModel parent, String testName) {
        String val = methodModel.generateMethodStatement(false);
        val = val.replaceAll("\t", "").replace(";\n", "");
        parent.setValue(val);
        parent.setNewInstance(false);
        deleteInstances(parent, testName);
        parent.getInstanceParameters().clear();
        parent.getInstanceFields().clear();
    }

    /**
     * Creates the init method parameters.
     * @param params the parameters.
     * @param testName the name of the test case.
     * @return the list of the method parameter objects.
     */
    protected List<ObjectModel> createInitMethodParams(List<String> params, String testName) {
        List<ObjectModel> parameters = new ArrayList<>();
        List<ObjectModel> preStates = getModel().getGivenInitObjects(testName);
        for (String param : params) {
            ObjectModel found = Helper.findObjectByIdentifier(param, preStates);
            if (found != null) {
                parameters.add(found);
            }
            else {
                if (param.matches("param\\d+")) {
                    ObjectModel parameter = Helper.findObjectByIdentifier(param, model.getParameters(testName));
                    if (parameter == null) parameter = createInitMethodParam(param, testName);
                    if (parameter != null) {
                        parameters.add(parameter);
                    }
                }
                else if (param.equals(StateKey.THIS.toString())) {
                    parameters.add(model.getCutObject(testName));
                }
                else {
                    ObjectModel newParameter = new ObjectModel(SimpleDataType.getTypeClass("String"), param, true);
                    parameters.add(newParameter);
                    preStates.add(newParameter);
                }
            }
        }
        return parameters;
    }

    /**
     * Creates the init method parameter object.
     * @param param the name of the parameter.
     * @param testName the name of the test case.
     * @return the parameter object.
     */
    protected ObjectModel createInitMethodParam(String param, String testName) {
        int index;
        String indexPart = param.replace("param", "");
        try {
            index = Integer.parseInt(indexPart);
            if (index > 0) {
                index--;
                Class<?>[] paramsOfMethodToTest = model.getMethodOfCUT(testName).getParameters();
                if (index < paramsOfMethodToTest.length) {
                    ObjectModel objectModel = new ObjectModel(paramsOfMethodToTest[index], param, true);
                    model.getGivenInitObjects(testName).add(objectModel);
                    model.getParameters(testName).add(objectModel);
                    return objectModel;
                }
            }
            else return null;
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Validates and adds the init method statement to the list of method calls of the calling object.
     * @param methodCallName the method name.
     * @param paramCount the count of parameters of the method.
     * @param callingObject the calling object.
     */
    protected void validateAndAddInitMethodStatement(String methodCallName, int paramCount, ObjectModel callingObject) {
        Optional<Method> optMeth = Scanner.findMethodOfClass(callingObject.getObjectClass(), paramCount, methodCallName);
        if (!optMeth.isPresent()) return;

        Method method = optMeth.get();
        MethodModel methodModel = new MethodModel();
        methodModel.setName(methodCallName);
        methodModel.setObject(callingObject.getIdentifier());
        methodModel.setReturnType(method.getReturnType().getSimpleName());
        methodModel.setReturnObject(new ObjectModel(method.getReturnType(), method.getReturnType().getSimpleName(), false));
        model.getTestClassModel().addImport(method.getReturnType().getName());
        createInitMethodGenerics(methodModel, method);
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            methodModel.setParameters(method.getParameterTypes());
        }
        callingObject.getMethodCalls().add(methodModel);
    }

    /**
     * Creates the generic type of the init method.
     * @param methodModel the model of the method
     * @param method the method.
     */
    protected void createInitMethodGenerics(MethodModel methodModel, Method method) {
        List<Class<?>> generics = Scanner.findGenericOfMethodReturnType(method);
        List<String> genericNames = new ArrayList<>();
        if (!generics.isEmpty()) {
            for (Class<?> generic : generics) {
                genericNames.add(generic.getSimpleName());
                if (!Utils.isSimpleType(generic.getSimpleName())) {
                    model.getTestClassModel().addImport(generic.getName());
                }
            }
            methodModel.setGenerics(genericNames);
        }
    }

    /**
     * Validates the return value.
     * @param callingClass the calling class.
     * @param value the return value.
     * @param testName the name of the test case.
     */
    protected void validateReturnValue(ObjectModel callingClass, String value, String testName) {
        ObjectModel objectModel;
        if (value.matches("param\\d+")) {
            objectModel = Helper.findObjectByIdentifier(value, model.getParameters(testName));
            if (objectModel == null) objectModel = createInitMethodParam(value, testName);
        }
        else if (value.equals(StateKey.THIS.toString())) {
            objectModel = model.getCutObject(testName);
        }
        else {
            objectModel = Helper.findObjectByIdentifier(value, model.getGivenInitObjects(testName));
        }
        if (objectModel == null) return;

        MethodModel found = null;
        for (MethodModel methodModel : callingClass.getMethodCalls()) {
            if (methodModel.getReturnType() != null && methodModel.getReturnObject() != null
                    && (methodModel.getReturnType().equals(objectModel.getDataType()) ||
                    methodModel.getReturnObject().getObjectClass().isAssignableFrom(objectModel.getObjectClass()))) {
                found = methodModel;
                //methodModel.setReturnIdentifier(value);
                break;
            }
        }
        if (found != null) {
            handleMethodWithReturnValue(found, objectModel, testName);
            callingClass.getMethodCalls().remove(found);
        }
    }

    /**
     * Deletes all created objects for the given parent object.
     * @param parent the parent object.
     * @param testName the name of the test case.
     */
    private void deleteInstances(ObjectModel parent, String testName) {
        List<ObjectModel> parameters = model.getParameters(testName);
        List<ObjectModel> instances = parent.getInstanceParameters();
        if (instances.isEmpty()) {
            instances = parent.getInstanceFields();
            if (instances.isEmpty()) return;
            for (ObjectModel instance : instances) {
                parameters.remove(instance);
                deleteInstances(instance, testName);
            }
        }
        for (ObjectModel instance : instances) {
            parameters.remove(instance);
            deleteInstances(instance, testName);
        }
    }

    /**
     * Validates the parameters of the given method.
     * @param callingClass the calling class.
     * @param methodName the name of the method.
     * @param params the parameters of the method.
     * @param testName the name of the test case.
     */
    protected void validateParams(ObjectModel callingClass, String methodName, List<String> params, String testName) {
        if (params.isEmpty()) return;
        MethodModel methodModel = null;
        for (MethodModel m : callingClass.getMethodCalls()) {
            if (m.getName().equals(methodName) && m.getParameters().length == params.size()) {
                methodModel = m;
                break;
            }
        }
        if (methodModel == null) return;

        Class<?>[] types = methodModel.getParameters();
        methodModel.setParameters(new Class[0]);

        for (int i = 0; i < params.size(); i++) {
            ObjectModel objectModel;
            String param = params.get(i);
            if (param.matches("param\\d+")) {
                objectModel = Helper.findObjectByIdentifier(param, model.getParameters(testName));
                if (objectModel == null) objectModel = createInitMethodParam(param, testName);
            } else if (param.equals(StateKey.THIS.toString())) {
                objectModel = model.getCutObject(testName);
            } else {
                objectModel = Helper.findObjectByIdentifier(param, model.getGivenInitObjects(testName));
            }
            if (objectModel == null) {
                objectModel = new ObjectModel(types[i], param, true);
                model.getGivenInitObjects(testName).add(objectModel);
            }
            methodModel.getParameterObjects().add(objectModel);
        }
    }

    /**
     * Creates setter and getter test cases if they should be generated.
     * @param tgClass the tg class.
     */
    public void createSetterGetterTests(Class<?> tgClass) {
        if (!Scanner.hasClassAnnotation(tgClass, AnnoType.ENTITY.getType())) {
            return;
        }
        boolean notToGenerate = tgClass.getDeclaredAnnotation(EntityTG.class).skipGetterSetterTests();
        if (notToGenerate) return;

        Class<?> cut = model.getCut();
        //Field[] fields = cut.getDeclaredFields();
        List<Field> fields = Scanner.findAllFields(cut);
        if (fields.size() == 0) return;

        assertionCreator.setModel(model);
        List<Field> fieldsForTest = new ArrayList<>();
        for (Field field : fields) {
            if (Scanner.hasGetterMethod(cut, field) && Scanner.hasSetterMethod(cut, field)) {
                fieldsForTest.add(field);
            }
        }
        if (fieldsForTest.isEmpty()) return;

        for (Field field : fieldsForTest) {
            createSetGetMethod(field);
        }

    }

    /**
     * Creates a setter and a getter test case for the given field.
     * @param field the field for the test case.
     */
    public void createSetGetMethod(Field field) {
        Class<?> cut = model.getCut();
        String fieldName = field.getName();
        String testName = "generatedSetGetTestOf" + Utils.setUpperCaseFirstChar(fieldName);
        String setterMethod = Scanner.getSetterMethod(cut, field);
        String getterMethod = Scanner.getGetterMethod(cut, field);
        calculateMethodModel(testName,  setterMethod);

        String identifier = model.getMethodOfCUT(testName).getObject();
        ObjectModel objectModel = new ObjectModel(cut, identifier, true);
        objectModel.setInitType(InitType.NONE);
        model.setCutObject(objectModel, testName);
        model.getObjectModels(testName).add(objectModel);
        calculatesParameters(testName);
        model.getObjectModels(testName).addAll(model.getParameters(testName));
        ((SimpleAssertionCreator) assertionCreator).calculateSetterGetterAssertion(testName, getterMethod, field.getType().getSimpleName());
    }

    /**
     * Creates the complex constructor test cases, if they should be generated.
     * @param tgClass the tg class.
     */
    public void createConstructors(Class<?> tgClass) {
        if (!Scanner.hasClassAnnotation(tgClass, AnnoType.ENTITY.getType())) {
            return;
        }
        boolean notToGenerate = tgClass.getDeclaredAnnotation(EntityTG.class).skipComplexConstructorCreation();
        if (notToGenerate) return;

        Class<?> cut = model.getCut();
        Constructor<?>[] constructors = cut.getDeclaredConstructors();
        if (constructors.length == 0) return;

        assertionCreator.setModel(model);
        int index = 1;
        if (constructors.length == 1) {
            index = 0;
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) continue;
            createConstructor(constructor, index);
            index++;
        }

    }

    /**
     * Creates a complex constructor test case for the given constructor.
     * @param constructor the constructor.
     * @param index the index for the constructor test case name.
     */
    public void createConstructor(Constructor<?> constructor, int index) {
        Class<?> cut = model.getCut();
        String testName = "generatedConstructorOf" + Utils.setUpperCaseFirstChar(cut.getSimpleName());
        testName += index > 0 ? index : "";

        String identifier = model.getTestMethodModel(testName).getUniqueIdentifier(cut.getSimpleName());
        ObjectModel objectModel = new ObjectModel(cut, identifier, true);
        objectModel.setInitType(InitType.NONE);
        model.setCutObject(objectModel, testName);
        model.getObjectModels(testName).add(objectModel);
        ValueCreator valueCreator = new ValueCreator(model, testName, InitType.NONE);
        valueCreator.setGivenConstructor(constructor);
        valueCreator.calculateValue(objectModel);
        model.getObjectModels(testName).addAll(model.getParameters(testName));

        TestCaseModel initMethod = new TestCaseModel();
        initMethod.setGiven(true);
        initMethod.setTestMethodName(testName);
        initMethod.setObjectModels(model.getParameters(testName));
        initMethod.setReturnObject(objectModel);
        model.getTestClassModel().getPrivateHelperMethods().add(initMethod);
        model.getTestClassModel().getTestCaseModels().remove(testName);
    }

    /**
     * Checks if the given parameter is already given in the given init objects of the test case.
     * @param parameter a parameter
     * @param index the parameter index
     * @param testName the name of the test case
     * @return true if the parameter is already given otherwise false
     */
    private boolean checkDefinedInPreState(Class<?> parameter, int index, String testName) {
        if (model.getGivenInitObjects(testName).isEmpty()) {
            return false;
        }

        for (ObjectModel given : model.getGivenInitObjects(testName)) {
            if (given.getDataType() == null || given.getIdentifier() == null) return false;
            if (given.getDataType().equalsIgnoreCase(parameter.getSimpleName()) &&
                    given.getIdentifier().equals("param" + index)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the test class name depending on the tgClassName.
     * Example: "ErsteKlasseTG" leads to "ErsteKlasseTests".
     * "ErsteKlasse" leads to "ErsteKlasseTests".
     * @param tgClassName the name of the test generation interface
     * @return the test class name
     */
    private String getTestclassName(String tgClassName) {
        String name = tgClassName;
        if (name.endsWith("TG")) {
            name = name.replace("TG", "Tests");
        }
        else {
            name = name.concat("Tests");
        }
        return name;
    }

    /**
     * Converts the packageUri to the needed form.
     * @param packageUri the package uri
     * @param isTest is uri of the test package
     * @param isTestGen is uri for the testGen folder
     */
    private void convertPackageUris(String packageUri, boolean isTest, boolean isTestGen) {
        String uri = packageUri;
        // if uri contains dots
        uri = uri.replace(".", "/");
        uri = uri.endsWith("/")? uri.substring(0, uri.length()-1) : uri;
        if (isTest) {
            model.setTestPackageUriWithSlashs(uri);
            model.setTestGenPackageUri("src/" + uri);
        }
        else if (isTestGen) {
            model.setTestGenPackageUri(uri);
        }
        else {
            model.setProjectPackageUriWithSlashs(uri);
        }

        uri = packageUri;
        // if uri contains slashs
        uri = uri.replace("/", ".");
        uri = uri.endsWith(".")? uri.substring(0, uri.length()-1) : uri;
        if (isTest) {
            model.setTestPackageUriWithDots(uri + ".testGen");
        } else if (isTestGen) {
            String pathInMaven = uri.startsWith("main.java.")? uri.replace("main.java.", "") :
                    uri.startsWith("test.java.") ? uri.replace("test.java.", "") : uri;
            model.setTestPackageUriWithDots(pathInMaven + ".testGen");
        }
        else {
            model.setProjectPackageUriWithDots(uri);
        }
    }

    /**
     *
     * @return the information for the test generators
     */
    public GeneratorModel getModel() {
        return model;
    }

    /**
     *
     * @param model the information for the test generators
     */
    public void setModel(GeneratorModel model) {
        this.model = model;
    }


    /**
     *
     * @param absolutTestGenPath the absolut path for the testGen folder
     */
    public void setAbsolutTestGenPath(String absolutTestGenPath) {
        model.setTestGenPackageUri(absolutTestGenPath);
    }

    @Override
    public void setAbsolutTestPath(String absolutTestPath) {
        model.setTestPackageUriWithDots(absolutTestPath.replace(".", "/"));
        model.setTestPackageUriWithDots(absolutTestPath.replace("/", "."));
    }

    @Override
    public void setAbsolutProjectPath(String absolutProjectPath) {
        model.setProjectPackageUriWithDots(absolutProjectPath.replace(".", "/"));
        model.setProjectPackageUriWithDots(absolutProjectPath.replace("/", "."));
    }

    @Override
    public String getTestGenPath() {
        return model.getTestGenPackageUri();
    }

    @Override
    public String getTestPath() {
        return model.getTestPackageUriWithSlashs();
    }

    @Override
    public String getProjectPath() {
        return model.getProjectPackageUriWithSlashs();
    }

    /**
     *
     * @return the assertion creator
     */
    public AssertionCreator getAssertionCreator() {
        return assertionCreator;
    }

    /**
     *
     * @param assertionCreator the assertion creator
     */
    public void setAssertionCreator(AssertionCreator assertionCreator) {
        this.assertionCreator = assertionCreator;
    }

    @Override
    public Class<?> getGivenCut() {
        return this.givenCut;
    }

    @Override
    public void setGivenCut(Class<?> givenCut) {
        this.givenCut = givenCut;
    }
}
