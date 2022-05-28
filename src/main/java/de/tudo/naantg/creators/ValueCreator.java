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

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.MethodNameParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Creates the values of the object models.
 */
public class ValueCreator {

    /**
     * the generator model
     */
    private final GeneratorModel model;

    /**
     * the name of the test case
     */
    private final String testName;

    /**
     * the init type
     */
    private final InitType initType;

    /**
     * given Constructors
     */
    private Constructor<?> givenConstructor;

    /**
     * the context of the logger information
     */
    private final String LOG_INFO = "[ValueCreator] ";

    /**
     * whether the ending of the creation statement should be generated
     */
    private boolean withTypeEnding;

    /**
     * the maximal recursion depth
     */
    private int recursiveDeep;

    /**
     * the random configs
     */
    private RandomConfigs randomConfigs;

    /**
     * the init objects
     */
    private List<ObjectModel> initObjects;

    /**
     * Creates the ValueCreator.
     * @param model the generator model
     * @param testName the name of the test case
     * @param initType the init type
     */
    public ValueCreator(GeneratorModel model, String testName, InitType initType) {
        this.model = model;
        this.testName = testName;
        this.initType = initType;
        randomConfigs = model.getTestMethodModel(testName).getRandomConfigs();
        if (randomConfigs == null) {
            initDefaultConfigs();
        }
        this.withTypeEnding = true;
        this.recursiveDeep = 3;
        this.initObjects = new ArrayList<>();
    }

    /**
     * Init the random configs with default values.
     */
    private void initDefaultConfigs() {
        randomConfigs = new RandomConfigs();
        randomConfigs.setMinValue("");
        randomConfigs.setMaxValue("");
        randomConfigs.setMinListSize(0);
        randomConfigs.setMaxListSize(10);
        randomConfigs.setMinStringLength(0);
        randomConfigs.setMaxStringLength(20);
        randomConfigs.setAlphabet(new Alphabet[] {Alphabet.ALL});
        model.getTestMethodModel(testName).setRandomConfigs(randomConfigs);
    }

    /**
     * Calculates the value for the given object model and its fields if the init type is
     * ALL_DEFAULT or ALL_RANDOM.
     * @param parent an object model
     */
    public void calculateValue(ObjectModel parent) {
        parent.setInitType(initType);

        // skip method calls
        if (parent.getValue() != null && parent.getValue().contains("(")) return;

        if (Utils.isSimpleType(parent.getDataType())) {
            calculatePrimitiveParameters(parent);
        }
        else if (parent.isEnum()) {
            calculateEnumParameter(parent);
        }
        else if (Utils.isCollectionType(parent.getDataType())) {
            calculateCollectionParameters(parent);
        }
        else {
            calculateObjectParameters(parent);
            if (initType.equals(InitType.ALL_DEFAULT) || initType.equals(InitType.ALL_RANDOM)) {
                calculateFields(parent);
            }
        }
    }

    /**
     * Calculates all fields of the parent object.
     * @param parent the parent object
     */
    public void calculateFields(ObjectModel parent) {
        if (initType.equals(InitType.NONE)) return;

        //Field[] fields = parent.getObjectClass().getDeclaredFields();
        //int len = fields.length;
        List<Field> fields = Scanner.findAllFields(parent.getObjectClass());
        int len = fields.size();
        if (len == 0) return;

        for (Field field : fields) {
            String identifier = model.getUniqueIdentifier(testName, field.getType().getSimpleName(), field.getName());
            ObjectModel newObject = new ObjectModel(field.getType(), identifier, true);
            if (len > 2) newObject.setPrivate(true);
            newObject.setFieldOfParent(parent);

            if (Scanner.hasGetterMethod(parent.getObjectClass(), field)) {
                newObject.setGetterName(Scanner.getGetterMethod(parent.getObjectClass(), field));
            }

            if (Scanner.hasSetterMethod(parent.getObjectClass(), field)) {
                parent.getInstanceFields().add(newObject);
                newObject.setSetterName(Scanner.getSetterMethod(parent.getObjectClass(), field));

                calculateField(field, newObject, 3);
                model.getParameters(testName).add(newObject);
            }
        }
    }

    /**
     * Calculates recursive the given field and its children.
     * @param field a field of the cut
     * @param parent the parent object
     */
    public void calculateField(Field field, ObjectModel parent) {
        calculateField(field, parent, recursiveDeep);
    }

    /**
     * Calculates recursive the given field and its children.
     * @param field a field of the cut
     * @param parent the parent object
     * @param maxDepth maximal recursion depth
     */
    public void calculateField(Field field, ObjectModel parent, int maxDepth) {
        parent.setInitType(initType);
        List<Class<?>> generics = Scanner.findGenericOfField(field);
        parent.setGenericClasses(generics);
        calculateObjectParameters(parent);

        if (Utils.isSimpleType(field.getType().getSimpleName())) return;
        if (maxDepth == 0) return;

        //Field[] fields = field.getType().getDeclaredFields();
        List<Field> fields = Scanner.findAllFields(parent.getObjectClass());
        for (Field value : fields) {
            String identifier = model.getUniqueIdentifier(testName, value.getType().getSimpleName(), value.getName());
            ObjectModel newObject = new ObjectModel(value.getType(), identifier, true);
            newObject.setPrivate(parent.isPrivate());
            newObject.setFieldOfParent(parent);

            if (Scanner.hasSetterMethod(parent.getObjectClass(), value)) {
                parent.getInstanceFields().add(newObject);
                newObject.setSetterName(Scanner.getSetterMethod(parent.getObjectClass(), value));

                calculateField(value, newObject, maxDepth-1);
                model.getParameters(testName).add(newObject);
            }
        }
    }

    /**
     * Calculates the primitive value for the given object model.
     * @param objectModel an object model with a primitive data type
     */
    public void calculatePrimitiveParameters(ObjectModel objectModel) {
        String type = objectModel.getDataType();
        String value = "";
        boolean isDefault = initType.equals(InitType.ALL_DEFAULT) || initType.equals(InitType.DEFAULT);
        String givenValue = objectModel.getValue();
        boolean isGiven = givenValue != null;

        if (Utils.isInt(type)) {
            value = isGiven && Utils.matchesIntFormat(givenValue) ? givenValue :
                    isDefault ? "0" : "" + Utils.getRandomInt();
        }
        else if (Utils.isBoolean(type)) {
            value = isGiven && Utils.matchesBooleanFormat(givenValue) ? givenValue :
                    isDefault ? "false" : "" + Utils.getRandomBoolean();
        }
        else if (Utils.isString(type)) {
            RandomConfigs randomConfigs = model.getTestMethodModel(testName).getRandomConfigs();
            value = isGiven ? givenValue : isDefault ? "\"\"" :
                    Utils.getRandomStringAsString(randomConfigs.getMinStringLength(),
                            randomConfigs.getMaxStringLength(),
                            Utils.getAlphabet(randomConfigs.getAlphabet()));
            if (withTypeEnding && !value.contains("\"")) {
                value = "\"" + value + "\"";
            }
            else if (!withTypeEnding && value.contains("\"")) {
                value = value.replace("\"", "");
            }
        }
        else if (Utils.isByte(type)) {
            // has a minimum value of -128 and a maximum value of 127 (inclusive)
            value = isGiven && Utils.matchesIntFormat(givenValue) ? givenValue :
                    isDefault ? "0" : "" + Utils.getRandomInt(127);
        }
        else if (Utils.isChar(type)) {
            value = isGiven && Utils.matchesCharFormat(givenValue) ? givenValue :
                    isDefault ? "'0'" : Utils.getRandomCharAsString();
            if (withTypeEnding && !value.contains("'")) {
                value = "'" + value + "'";
            }
            else if (!withTypeEnding && value.contains("'")) {
                value = value.replace("'", "");
            }
        }
        else if (Utils.isDouble(type)) {
            value = isGiven && Utils.matchesDoubleFormat(givenValue) ? givenValue :
                    isDefault ? "0.0d" : Utils.getRandomDoubleAsString();
            if (withTypeEnding && !value.contains("d")) {
                value += "d";
            }
            else if (!withTypeEnding && value.contains("d")) {
                value = value.replace("d", "");
            }
        }
        else if (Utils.isFloat(type)) {
            value = isGiven && Utils.matchesDoubleFormat(givenValue) ? givenValue :
                    isDefault ? "0.0f" : Utils.getRandomFloatAsString();
            if (withTypeEnding && !value.contains("f")) {
                value += "f";
            }
            else if (!withTypeEnding && value.contains("f")) {
                value = value.replace("f", "");
            }
        }
        else if (Utils.isShort(type)) {
            // has a minimum value of -32,768 and a maximum value of 32,767 (inclusive)
            value = isGiven && Utils.matchesIntFormat(givenValue) ? givenValue :
                    isDefault ? "0" : "" + Utils.getRandomInt();
        }
        else if (Utils.isLong(type)) {
            value = isGiven && Utils.matchesIntFormat(givenValue) ? givenValue :
                    isDefault ? "0L" : Utils.getRandomLongAsString();
            if (withTypeEnding && !value.contains("L")) {
                value += "L";
            }
            else if (!withTypeEnding && value.contains("L")) {
                value = value.replace("L", "");
            }
        }

        objectModel.setValue(value);

        // todo replace work around!!!
        if (initType.equals(InitType.NONE)) {
            calculateSimpleObjectInstance(objectModel);
        }
    }

    /**
     * Replaces the random or default value of the objectModel by the value
     * given in the test name if it exists.
     * @param objectModel an object model
     */
    private void calculateSimpleObjectInstance(ObjectModel objectModel) {
        List<String> paramValues = MethodNameParser.getPreValue(testName);
        String dataType = objectModel.getDataType();
        String value = "";
        if (!paramValues.isEmpty()) {
            String paramValue = "";

            for (String part : paramValues) {
                if (part.startsWith(StateKey.OF.toString()) ||
                        part.startsWith(StateKey.FROM.toString())) {
                    paramValue = "";
                    break;
                }
                else if (!part.equals(StateKey.VALUES.toString())) {
                    paramValue = part;
                }

            }
            if (paramValue.equals("") || paramValue.equalsIgnoreCase("null")) {
                objectModel.setValue("null");
                return;
            }

            String[] values = Utils.parse(paramValue);
            if ((Utils.isInt(values[0]) || Utils.isBoolean(values[0])) &&
                    (Utils.isInt(dataType)) || Utils.isShort(dataType) ||
                    Utils.isByte(dataType) || Utils.isBoolean(dataType)) {
                value = values[1];
            }
            else if (Utils.isString(values[0]) && Utils.isString(dataType)) {
                value = "\"" + values[1] + "\"";
            }
            else if (Utils.isString(values[0]) && Utils.isChar(dataType)) {
                value = "'" + values[1] + "'";
            }
            else if (Utils.isDouble(values[0]) && Utils.isFloat(dataType)) {
                value = values[1] + "f";
            }
            else if (Utils.isDouble(values[0]) && Utils.isDouble(dataType)) {
                value = values[1] + "d";
            }
            else if (Utils.isInt(values[0]) && Utils.isLong(dataType)) {
                value = values[1] + "L";
            }

            if (!value.equals("")) {
                objectModel.setValue(value);
            }
        }

    }

    /**
     * Calculates the enum value statement.
     * @param value the given enum value.
     * @param enumClass the enum class.
     * @return the value statement.
     */
    private String checkEnumValue(String value, Class<?> enumClass) {
        if (value.equalsIgnoreCase("null")) {
            return "null";
        }
        else {
            Field[] enumValues = enumClass.getDeclaredFields();
            for (Field field : enumValues) {
                if (field.getName().equalsIgnoreCase(value)) {
                    value = enumClass.getSimpleName() + "." + field.getName();
                }
            }
        }
        if (!value.isEmpty() && !value.contains(".")) value = "";
        return value;
    }

    /**
     * Calculates the enum value for the given object model.
     * @param objectModel an enum object model
     */
    public void calculateEnumParameter(ObjectModel objectModel) {
        String value = objectModel.getValue();
        List<String> paramValues = MethodNameParser.getPreValue(testName);
        model.getTestClassModel().addImport(objectModel.getObjectClass().getName());
        if (value != null && !value.contains(objectModel.getDataType())) {
            //value = objectModel.getDataType() + "." + value;
            value = checkEnumValue(value, objectModel.getObjectClass());
        }
        else {
            if (paramValues.size() == 1) {
                value = paramValues.get(0);
                value = checkEnumValue(value, objectModel.getObjectClass());
            }
        }
        if (value != null && !value.isEmpty()) {
            objectModel.setValue(value);
            return;
        }
        boolean isDefault = (initType.equals(InitType.ALL_DEFAULT) || initType.equals(InitType.DEFAULT)) &&
                objectModel.getObjectClass().getDeclaredFields().length != 0;
        value = isDefault? Utils.getFirstEnumAsEnum(objectModel.getObjectClass().getFields()) :
                Utils.getRandomEnumAsEnum(objectModel.getObjectClass().getFields());
        objectModel.setValue(value);
    }

    /**
     * Calculates the collection values for the given object model.
     * @param objectModel an object model with a collection data type
     */
    public void calculateCollectionParameters(ObjectModel objectModel) {
        Class<?> parameter = objectModel.getObjectClass();
        List<Class<?>> genericClasses = objectModel.getGenericClasses();
        String value = objectModel.getValue();
        //List<String> generics = objectModel.getGenericTypes();

        if (genericClasses.isEmpty()) {
            if (objectModel.getIdentifier().startsWith("param")) {
                Method methodToTest = model.getMethodOfCUT(testName).getMethodToTest();
                Map<String, List<Class<?>>> genericParameters = Scanner.findGenericOfMethodParameters(methodToTest);

                if (genericParameters.get(parameter.getName()) != null
                        && !genericParameters.get(parameter.getName()).isEmpty()) {
                    genericClasses = genericParameters.get(parameter.getName());
                }
            }
            else if (value != null && !value.isEmpty()) {
                if (value.contains("[")) {
                    value = value.replace("[", "").replace("]", "");
                    if (value.contains(",")) {
                        value = value.split(",")[0];
                    }
                    else if (value.contains(";")) {
                        value = value.split(";")[0];
                    }
                }
                ObjectModel found = Helper.findObjectByIdentifier(value, initObjects);
                if (found != null) {
                    genericClasses.add(found.getObjectClass());
                }
            }
            objectModel.setGenericClasses(genericClasses);
        }

        List<String> imports = model.getTestClassModel().getImports();

        String type = objectModel.getDataType();
        boolean isArray = false;

        if (!Utils.isArray(type)) {
            calculateImports(type, true, imports);
            if (Utils.isList(type)) {
                model.getTestClassModel().addImport(CollectionDataType.LIST.toString());
            }
            else if (Utils.isCollection(type)) {
                model.getTestClassModel().addImport(CollectionDataType.COLLECTION.toString());
            }
            else if (Utils.isOptional(type)) {
                model.getTestClassModel().addImport(CollectionDataType.OPTIONAL.toString());
            }
            else if (Utils.isMapType(type)) {
                model.getTestClassModel().addImport(CollectionDataType.MAP.toString());
                model.getTestClassModel().addImport(CollectionDataType.HASH_MAP.toString());
            }
            if (!genericClasses.isEmpty() && !Utils.isSimpleType(genericClasses.get(0).getSimpleName())) {
                model.getTestClassModel().addImport(genericClasses.get(0).getName());
            }
        }
        else if (Utils.isArray(type)) {
            model.getTestClassModel().addImport(CollectionDataType.ARRAYS.toString());
            if (!Utils.isSimpleType(parameter.getComponentType().getSimpleName())) {
                model.getTestClassModel().addImport(parameter.getComponentType().getName());
            }
            isArray = true;
        }

        if (objectModel.getValue() != null) {
            calculateGivenCollectionValue(objectModel);
        }
        else if (isArray){
            calculateArrayObject(objectModel);
        }
        else {
            calculateListObjectInstance(objectModel);
        }

    }

    /**
     * Calculates the collection statement value.
     * @param objectModel the object that contains the collection.
     */
    private void calculateGivenCollectionValue(ObjectModel objectModel) {
        Class<?> parameter = objectModel.getObjectClass();
        String value = objectModel.getValue();
        String type = objectModel.getDataType();
        List<Class<?>> genericClasses = objectModel.getGenericClasses();
        String generic = !genericClasses.isEmpty() ? genericClasses.get(0).getSimpleName() : "";
        if (Utils.isOptional(type)) {
            for (ObjectModel initObj : initObjects) {
                if (initObj.getIdentifier().equals(objectModel.getValue()) &&
                        initObj.getDataType().equals(generic)) {
                    objectModel.getInstanceParameters().add(initObj);
                    initObj.setParameterOfParent(objectModel);
                    return;
                }
            }
            if (objectModel.getValue().equals(generic)) {
                ObjectModel newObject = new ObjectModel(genericClasses.get(0),
                        model.getUniqueIdentifier(testName, generic), true);
                objectModel.getInstanceParameters().add(newObject);
                newObject.setParameterOfParent(objectModel);
                initObjects.add(newObject);
                model.getParameters(testName).add(newObject);
            }
        }
        else {
            value = value.replace(";", ",");
            value = value.replace("[", "(").replace("]", ")");
            value = Utils.convertListValues(value, generic, initObjects);
            objectModel.setValue(value);
        }
    }

    /**
     * Calculates the list values for the given object model.
     * @param objectModel an object model with a list type
     */
    public void calculateListObjectInstance(ObjectModel objectModel) {
        String value;
        String generic = objectModel.getGenericTypes().isEmpty()? "" : objectModel.getGenericTypes().get(0);
        Class<?> genericClass = objectModel.getGenericClasses().isEmpty()? null : objectModel.getGenericClasses().get(0);
        //boolean isInitDefault = initType.equals(InitType.ALL_DEFAULT) || initType.equals(InitType.DEFAULT) || initType.equals(InitType.NONE);
        boolean isInitRandom = initType.equals(InitType.RANDOM) || initType.equals(InitType.ALL_RANDOM);
        boolean isValues = withValues() || isInitRandom;
        boolean isEnum = genericClass != null && genericClass.isEnum();
        boolean isObjectGeneric = Utils.isObjectType(generic) && !isEnum;

        model.getTestClassModel().addImport(CollectionDataType.ARRAYS.toString());

        if (isValues) {
            int listSize = withValueSize();
            if (listSize != -1) {
                if (isObjectGeneric) {
                    calculateObjectInitForCollection(objectModel, listSize);
                    return;
                }
                if (genericClass != null) {
                    value = Utils.getRandomList(genericClass, listSize, listSize);
                    objectModel.setValue(value);
                }
            }
            else {
                if (isObjectGeneric) {
                    calculateObjectInitForCollection(objectModel);
                    return;
                }
                value = Utils.getRandomList(genericClass, randomConfigs.getMinListSize(), randomConfigs.getMaxListSize());
                objectModel.setValue(value);
            }
        }

    }

    /**
     * Calculates random object elements within the bounds of the randomConfigs
     * for the given object model.
     * @param objectModel an object model with a collection type and object generic
     */
    public void calculateObjectInitForCollection(ObjectModel objectModel) {
        int len = Utils.getRandomInt(randomConfigs.getMinListSize(), randomConfigs.getMaxListSize());
        calculateObjectInitForCollection(objectModel, len);
    }

    /**
     * Calculates random object elements for the given object model with the maximal given size.
     * @param objectModel an object model with a collection type and object generic
     * @param listSize the size of the list
     */
    public void calculateObjectInitForCollection(ObjectModel objectModel, int listSize) {
        Class<?> genericClass = objectModel.getGenericClasses().isEmpty()? null : objectModel.getGenericClasses().get(0);
        if (genericClass == null) return;

        for (int i = 0; i < listSize; i++) {
            String identifier = model.getUniqueIdentifier(testName, genericClass.getSimpleName());
            ObjectModel newObject = new ObjectModel(genericClass, identifier, true);
            newObject.setPrivate(true);
            newObject.setInitType(initType);
            objectModel.getInstanceParameters().add(newObject);
            model.getParameters(testName).add(newObject);

            calculateObjectParameters(newObject);

        }

    }

    /**
     * Calculates random or default array values for the given object model.
     * @param objectModel an object model with an array type
     */
    public void calculateArrayObject(ObjectModel objectModel) {
        Class<?> simpleType = objectModel.getObjectClass().getComponentType();
        String value;
        boolean isRandom = initType.equals(InitType.ALL_RANDOM) || initType.equals(InitType.RANDOM);
        //boolean isDefaultInit = initType.equals(InitType.ALL_DEFAULT) || initType.equals(InitType.DEFAULT);
        boolean isValues = withValues() || isRandom; //&& !isDefaultInit;
        if (isValues) {
            int listSize = withValueSize();
            if (listSize != -1) {
                value = Utils.getRandomList(simpleType, listSize, listSize);
            }
            else {
                value = Utils.getRandomList(simpleType, randomConfigs.getMinListSize(), randomConfigs.getMaxListSize());
            }
            objectModel.setValue(value);
        }
        // Todo: also handle object arrays
    }

    /**
     * Adds an import statement for the given data type if the import does not exist.
     * For List or Collection as new object the default import is ArrayList.
     * @param dataType the data type
     * @param forObject true if import is needed for a new object
     * @param imports the imports list
     */
    public void calculateImports(String dataType, boolean forObject, List<String> imports) {
        if (dataType.equals("")) return;

        String importType = "";

        if (Utils.isList(dataType)) {
            importType = forObject? CollectionDataType.ARRAY_LIST.toString() :
                    CollectionDataType.LIST.toString();
        }
        else if (Utils.isArrayList(dataType)) {
            importType = CollectionDataType.ARRAY_LIST.toString();
        }
        else if (Utils.isLinkedList(dataType)) {
            importType = CollectionDataType.LINKED_LIST.toString();
        }
        else if (Utils.isArray(dataType)) {
            importType = CollectionDataType.ARRAYS.toString();
        }
        else if (Utils.isCollection(dataType)) {
            importType = forObject? CollectionDataType.ARRAY_LIST.toString() :
                    CollectionDataType.COLLECTION.toString();
        }

        if (!importType.equals("") && !imports.contains(importType)) {
            imports.add(importType);
        }
    }

    /**
     * Checks if the test case with the given name should have values.
     * @return true if the test case should have values
     */
    public boolean withValues() {
        List<String> withKeys = MethodNameParser.getPreValue(testName);
        if (!withKeys.isEmpty() && withKeys.contains(StateKey.VALUES.toString())) {
            return true;
        }
        List<String> assertKeys = MethodNameParser.getGetAssertion(testName);
        return !assertKeys.isEmpty() && assertKeys.contains(StateKey.VALUES.toString());
    }

    /**
     * Returns the size of elements which the test case with the given name should contain.
     * @return the size of elements which the test case should contain
     */
    public int withValueSize() {
        List<String> withKeys = MethodNameParser.getPreValue(testName);
        if (withKeys.size() > 1 && withKeys.get(1).equals(StateKey.VALUES.toString())) {
            String value = withKeys.get(0);
            int size;
            try {
                size = Integer.parseInt(value);
                return size;
            }
            catch (Exception e) {
                Logger.logWarning(LOG_INFO + "The given list size after 'with' can not be parsed!");
            }
        }
        return -1;
    }

    /**
     * Calculates recursive the object values for the given object model.
     * @param objectModel an object model with an object type
     */
    public void calculateObjectParameters(ObjectModel objectModel) {
        String dataType = objectModel.getDataType();
        if (Utils.isSimpleType(dataType)) {
            calculatePrimitiveParameters(objectModel);
        }
        else if (objectModel.isEnum()) {
            calculateEnumParameter(objectModel);
        }
        else if (Utils.isCollectionType(dataType)) {
            calculateCollectionParameters(objectModel);
        }
        else {
            Class<?> objectClass = objectModel.getObjectClass();
            if (objectClass != null && (objectClass.isInterface() || Modifier.isAbstract(objectClass.getModifiers()))) {
                Optional<Class<?>> assignable = Scanner.findAssignableClass(objectClass, model.getProjectPackageUriWithDots());
                objectClass = assignable.orElse(objectClass);
                objectModel.setObjectClass(objectClass);
            }
            Constructor<?> defaultConstructor = null;
            Constructor<?> constructor = null;
            String constName = givenConstructor != null? givenConstructor.getName() : "";
            String objectName = objectModel.getObjectClass() != null ?
                    objectModel.getObjectClass().getName() : "";
            if (givenConstructor != null && constName.equals(objectName)
                    && givenConstructor.getParameterCount() == 0) {
                defaultConstructor = givenConstructor;
            }
            else if (givenConstructor != null && constName.equals(objectName)
                    && givenConstructor.getParameterCount() > 0) {
                constructor = givenConstructor;
            }
            else {
                Optional<Constructor<?>> optionalDefaultConstructor = Scanner.getDefaultConstructor(objectClass);
                Optional<Constructor<?>> optionalConstructor = Scanner.getSimplestConstructor(objectClass);
                defaultConstructor = optionalDefaultConstructor.orElse(null);
                constructor = optionalConstructor.orElse(null);
            }

            if (defaultConstructor != null) {
                model.getTestClassModel().addImport(defaultConstructor.getName());
            }
            else if (constructor != null) {
                model.getTestClassModel().addImport(constructor.getName());

                Map<String, List<Class<?>>> constrParamGenerics = Scanner.findGenericOfConstructorParameters(constructor);

                Class<?>[] constParams = constructor.getParameterTypes();
                for (Class<?> constParam : constParams) {
                    String identifier = model.getUniqueIdentifier(testName, constParam.getSimpleName());
                    ObjectModel newObject = new ObjectModel(constParam, identifier, true);
                    List<Class<?>> generics = constrParamGenerics.get(constParam.getName());
                    Class<?> paramGeneric = null;
                    if (generics != null && !generics.isEmpty()) {
                        paramGeneric = generics.get(0);
                        generics.remove(0);
                    }
                    newObject.getGenericClasses().clear();
                    newObject.getGenericClasses().add(paramGeneric);
                    objectModel.getInstanceParameters().add(newObject);
                    newObject.setParameterOfParent(objectModel);
                    newObject.setPrivate(objectModel.isPrivate());
                    newObject.setInitType(initType);

                    calculateObjectParameters(newObject);
                    calculateFields(newObject);
                    model.getParameters(testName).add(newObject);
                }
            }
        }
    }

    /**
     *
     * @return the generator model
     */
    public GeneratorModel getModel() {
        return model;
    }

    /**
     *
     * @return whether correct type ending should be used
     */
    public boolean isWithTypeEnding() {
        return withTypeEnding;
    }

    /**
     *
     * @param withTypeEnding whether correct type ending should be used
     */
    public void setWithTypeEnding(boolean withTypeEnding) {
        this.withTypeEnding = withTypeEnding;
    }

    /**
     *
     * @return the given Constructors
     */
    public Constructor<?> getGivenConstructor() {
        return givenConstructor;
    }

    /**
     *
     * @param givenConstructor the given Constructors
     */
    public void setGivenConstructor(Constructor<?> givenConstructor) {
        this.givenConstructor = givenConstructor;
    }

    /**
     *
     * @return the init objects.
     */
    public List<ObjectModel> getInitObjects() {
        return initObjects;
    }

    /**
     *
     * @param initObjects the init objects.
     */
    public void setInitObjects(List<ObjectModel> initObjects) {
        this.initObjects = initObjects;
    }

    /**
     *
     * @return the recursion depth.
     */
    public int getRecursiveDeep() {
        return recursiveDeep;
    }

    /**
     *
     * @param recursiveDeep the recursion depth.
     */
    public void setRecursiveDeep(int recursiveDeep) {
        this.recursiveDeep = recursiveDeep;
    }
}
