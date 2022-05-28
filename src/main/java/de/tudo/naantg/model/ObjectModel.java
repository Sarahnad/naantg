/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.model;

import de.tudo.naantg.annotations.InitType;

import java.util.*;

/**
 * Container for the information of an object creation or setting statement in the pre condition
 * where the necessary state is created for the execution of the method to test.
 */
public class ObjectModel {

    /**
     * data type of the object
     */
    private String dataType;

    /**
     * the generic classes
     */
    private List<Class<?>> genericClasses;

    /**
     * name of the object
     */
    private String identifier;

    /**
     * parent object identifier
     */
    private String parentIdentifier;

    /**
     * create a new object
     */
    private boolean newInstance;

    /**
     * parameters for the object creation
     */
    private final List<ObjectModel> instanceParameters;

    /**
     * fields of the object to init
     */
    private final List<ObjectModel> instanceFields;

    /**
     * the concrete value of a simple data type
     */
    private String value;

    /**
     * should be generated in a private Method
     */
    private boolean isPrivate;

    /**
     * object is an enum
     */
    private boolean isEnum;

    /**
     * the corresponding class
     */
    private Class<?> objectClass;

    /**
     * is the parameter of the parent
     */
    private ObjectModel parameterOfParent;

    /**
     * is the field of the parent
     */
    private ObjectModel fieldOfParent;

    /**
     * there exists a setter for this object with this name
     */
    private String setterName;

    /**
     * there exists a getter for this object with this name
     */
    private String getterName;

    /**
     * all method calls of this object
     */
    private List<MethodModel> methodCalls;

    /**
     * all field calls of this object
     */
    private List<ObjectModel> fieldCalls;

    /**
     * the amount of elements
     */
    private int elementSize;

    /**
     * the object annotations
     */
    private final List<String> annotations;

    /**
     * the given object annotations
     */
    private final List<String> givenAnnotations;

    /**
     * whether this object model should not be generated
     * (because of autowired or mocked)
     */
    private boolean notToGenerate;

    /**
     * the initType
     */
    private InitType initType;


    /**
     * Creates a container for the information of an object creation or setting statement.
     * Creates an empty instanceParameters list.
     */
    public ObjectModel() {
        this.genericClasses = new ArrayList<>();
        this.instanceParameters = new ArrayList<>();
        this.instanceFields = new ArrayList<>();
        this.methodCalls = new ArrayList<>();
        this.fieldCalls = new ArrayList<>();
        this.annotations = new ArrayList<>();
        this.givenAnnotations = new ArrayList<>();
        this.initType = InitType.NONE;
    }

    /**
     * Creates a container for the information of an object creation or setting statement
     * with the given data type and identifier of the object
     * and with newInstance to distinguish between an instance creation or a setting statement.
     * Creates an empty instanceParameters list.
     * @param dataType the data type of the object
     * @param identifier the identifier of the object
     * @param newInstance statement is a new instance creation of the object
     */
    public ObjectModel(String dataType, String identifier, boolean newInstance) {
        this();
        this.dataType = dataType;
        this.newInstance = newInstance;
        this.identifier = identifier;
    }

    /**
     * Creates a container for the information of an object creation or setting statement
     * with the given objectClass and identifier of the object
     * and with newInstance to distinguish between an instance creation or a setting statement.
     * Creates an empty instanceParameters list.
     * @param objectClass the corresponding object class
     * @param identifier the identifier of the object
     * @param newInstance statement is a new instance creation of the object
     */
    public ObjectModel(Class<?> objectClass, String identifier, boolean newInstance) {
        this(objectClass.getSimpleName(), identifier, newInstance);
        this.objectClass = objectClass;
        this.dataType = objectClass.getSimpleName();
        this.isEnum = objectClass.isEnum();
    }

    /**
     * Creates an object creation or setting statement based on the stored information in this ObjectModel.
     * @return an object creation or setting statement
     */
    public String generateObjectStatement() {
        StringBuilder input = new StringBuilder();
        input.append("\t\t");
        input.append(dataType);
        if (!getGenericTypes().isEmpty() &&
                (dataType.contains("List") || Utils.isCollection(dataType))) {
            input.append("<").append(getGenericTypes().get(0)).append(">");
        }
        input.append(" ").append(identifier);
        input.append(" = ");

        if (newInstance) {
            if (isSimpleType()) {
                input.append(value).append(";\n");
                return input.toString();
            }
            else if (Utils.isCollectionType(dataType)) {
                return addCollectionNewInstance(input);
            }
            input.append("new ").append(dataType).append("(");
            if (instanceParameters != null && !instanceParameters.isEmpty()) {
                String[] identifiers = new String[instanceParameters.size()];
                for (int i = 0; i < instanceParameters.size(); i++) {
                    identifiers[i] = instanceParameters.get(i).getIdentifier();
                }
                String params = Arrays.toString(identifiers);
                params = params.replace("[", "").replace("]", "");
                input.append(params);
            }
            input.append(");\n");
        }
        else {
            input.append(value).append(";\n");
        }
        return input.toString();
    }

    /**
     * Adds the best new instance statement for the collection type.
     * @param input the prefix statement
     * @return the best new instance statement for the collection type
     */
    public String addCollectionNewInstance(StringBuilder input) {
        boolean isValue = (value != null && !value.equals(""));
        boolean isArrayOrLinkedList = (Utils.isArrayList(dataType) || Utils.isLinkedList(dataType));
        if (isArrayOrLinkedList) {
            input.append("new ");
            input.append(dataType);
            input.append("<>(");
        }
        if (!Utils.isArray(dataType)) {
            if (isValue) {
                if (!value.contains(", ")) {
                    value = value.replaceAll(",",", ");
                }
                input.append("Arrays.asList");
                if (!getGenericTypes().isEmpty() && (Utils.isByte(getGenericTypes().get(0)) || Utils.isShort(getGenericTypes().get(0)))) {
                    value = value.replaceAll("\\(", "{")
                            .replaceAll("\\)", "}");
                    input.append("(new ").append(getGenericTypes().get(0)).append("[] ");
                    input.append(value).append(")");
                }
                else {
                    value = value.replace("{", "(")
                            .replace("}", ")");
                    input.append(value);
                }
            }
            else {
                if (Utils.isCollection(dataType) || Utils.isList(dataType)) {
                    input.append("new ");
                    input.append(CollectionDataType.ARRAY_LIST.getType());
                    input.append("<>()");
                }
            }
        }
        if (isArrayOrLinkedList) {
            input.append(")");
        }
        if (Utils.isArray(dataType)) {
            input.append("new ");
            if (isValue) {
                input.append(dataType).append(" ");
                if (!value.contains(", ")) {
                    value = value.replaceAll(",", ", ");
                }
                input.append(value);
            }
            else {
                input.append(dataType.replace("[]", "[0]"));
            }
        }
        input.append(";\n");
        return input.toString();
    }


    /**
     *
     * @return the data type of the object
     */
    public String getDataType() {
        if (objectClass != null && dataType == null) return objectClass.getSimpleName();
        return dataType;
    }

    /**
     *
     * @param dataType the data type of the object
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     *
     * @return true if it is a creation statement
     */
    public boolean isNewInstance() {
        return newInstance;
    }

    /**
     *
     * @param newInstance true if it is a creation statement
     */
    public void setNewInstance(boolean newInstance) {
        this.newInstance = newInstance;
    }

    /**
     * Checks if the data type is a simple type.
     * @return true if it is a simple type
     */
    public boolean isSimpleType() {
        if (dataType == null) return false;
        return Utils.isSimpleType(dataType);
    }

    /**
     *
     * @return the concrete value of the object or null
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value the concrete value of the object
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return the parameters for the object instance creation
     */
    public List<ObjectModel> getInstanceParameters() {
        return instanceParameters;
    }

    /**
     *
     * @return the object identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @param identifier the object identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     *
     * @return the parent object identifier
     */
    public String getParentIdentifier() {
        if (parentIdentifier == null) {
            parentIdentifier = parameterOfParent != null ? parameterOfParent.getIdentifier() :
                    fieldOfParent != null ? fieldOfParent.getIdentifier() : null;
        }
        return parentIdentifier;
    }

    /**
     *
     * @return the generic types
     */
    public List<String> getGenericTypes() {
        List<String> genericTypes = new ArrayList<>();
        if (genericClasses == null) return genericTypes;

        if (!genericClasses.isEmpty()) {
            for (Class<?> genericClass : genericClasses) {
                if (genericClass == null) continue;
                genericTypes.add(genericClass.getSimpleName());
            }
        }
        return genericTypes;
    }


    /**
     *
     * @return true if it should be generated in a private method
     */
    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     *
     * @param aPrivate true if it should be generated in a private method
     */
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    /**
     *
     * @return true if the object is an enum
     */
    public boolean isEnum() {
        if (objectClass != null) return objectClass.isEnum();
        return isEnum;
    }

    /**
     *
     * @param anEnum the object is an enum
     */
    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    /**
     *
     * @return the generic class
     */
    public List<Class<?>> getGenericClasses() {
        if (genericClasses == null) return new ArrayList<>();
        return genericClasses;
    }

    /**
     *
     * @param genericClasses the generic classes
     */
    public void setGenericClasses(List<Class<?>> genericClasses) {
        this.genericClasses = genericClasses;
        /*if (genericClasses != null ) {
            for (Class<?> genericClass : genericClasses) {
                this.genericTypes.add(genericClass.getSimpleName());
            }
        }*/
    }

    /**
     *
     * @return field of the object
     */
    public List<ObjectModel> getInstanceFields() {
        return instanceFields;
    }

    /**
     *
     * @return the corresponding class
     */
    public Class<?> getObjectClass() {
        return objectClass;
    }

    /**
     *
     * @param objectClass the corresponding class
     */
    public void setObjectClass(Class<?> objectClass) {
        if (objectClass != null) {
            this.dataType = objectClass.getSimpleName();
        }
        this.objectClass = objectClass;
    }

    /**
     *
     * @return the parent of this parameter
     */
    public ObjectModel getParameterOfParent() {
        return parameterOfParent;
    }

    /**
     *
     * @param parameterOfParent the parent of this parameter
     */
    public void setParameterOfParent(ObjectModel parameterOfParent) {
        this.parameterOfParent = parameterOfParent;
        if (parameterOfParent != null) {
            this.parentIdentifier = parameterOfParent.getIdentifier();
        }
    }

    /**
     *
     * @return the parent of this field
     */
    public ObjectModel getFieldOfParent() {
        return fieldOfParent;
    }

    /**
     *
     * @param fieldOfParent the parent of this field
     */
    public void setFieldOfParent(ObjectModel fieldOfParent) {
        this.fieldOfParent = fieldOfParent;
        if (fieldOfParent != null) {
            this.parentIdentifier = fieldOfParent.getIdentifier();
        }
    }

    /**
     *
     * @return the setter name for this field
     */
    public String getSetterName() {
        return setterName;
    }

    /**
     *
     * @param setterName the setter name for this field
     */
    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    /**
     *
     * @return the getter name for this field
     */
    public String getGetterName() {
        return getterName;
    }

    /**
     *
     * @param getterName the getter name for this field
     */
    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    /**
     *
     * @return a list of method call models
     */
    public List<MethodModel> getMethodCalls() {
        return methodCalls;
    }

    /**
     *
     * @param methodCalls a list of method call models
     */
    public void setMethodCalls(List<MethodModel> methodCalls) {
        this.methodCalls = methodCalls;
    }

    /**
     *
     * @return the amount of elements
     */
    public int getElementSize() {
        return elementSize;
    }

    /**
     *
     * @param elementSize the amount of elements
     */
    public void setElementSize(int elementSize) {
        this.elementSize = elementSize;
    }

    /**
     *
     * @return the given annotations
     */
    public List<String> getGivenAnnotations() {
        return givenAnnotations;
    }


    /**
     *
     * @return whether this object model should not be generated
     *      (because of autowired or mocked)
     */
    public boolean isNotToGenerate() {
        return notToGenerate;
    }

    /**
     *
     * @param notToGenerate whether this object model should not be generated
     *      (because of autowired or mocked)
     */
    public void setNotToGenerate(boolean notToGenerate) {
        this.notToGenerate = notToGenerate;
    }

    /**
     *
     * @return the annotations
     */
    public List<String> getAnnotations() {
        return annotations;
    }

    /**
     *
     * @return the init type
     */
    public InitType getInitType() {
        return initType;
    }

    /**
     *
     * @param initType the init type
     */
    public void setInitType(InitType initType) {
        this.initType = initType;
    }

    public List<ObjectModel> getFieldCalls() {
        return fieldCalls;
    }

    public void setFieldCalls(List<ObjectModel> fieldCalls) {
        this.fieldCalls = fieldCalls;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (dataType != null)
            builder.append("dataType: ").append(dataType);
        if (identifier != null)
            builder.append(", identifier: ").append(identifier);
        if (!getGenericTypes().isEmpty())
            builder.append(", genericTypes: ");
        for (String genericType : getGenericTypes()) {
            builder.append(genericType).append(" ");
        }
        if (value != null)
            builder.append(", value: ").append(value);
        if (parameterOfParent != null)
            builder.append(", parameterParent: ").append(parentIdentifier);
        if (fieldOfParent != null)
            builder.append(", fieldParent: ").append(parentIdentifier);
        builder.append(", instanceParameters: ").append(instanceParameters.size());
        builder.append(", instanceField: ").append(instanceFields.size());
        builder.append(", isPrivate: ").append(isPrivate);
        builder.append(", initType: ").append(initType);
        builder.append(", isNewInstance: ").append(isNewInstance());
        if (setterName != null)
            builder.append(", setter: ").append(setterName);
        if (!methodCalls.isEmpty())
            builder.append(", methodCalls: ");
        for (MethodModel methodCall : methodCalls) {
            builder.append(methodCall.getName()).append(" ");
        }
        builder.append(", elementSize: ").append(elementSize);
        if (!annotations.isEmpty())
            builder.append(", annotations: ");
        for (String anno : annotations) {
            builder.append(anno).append(" ");
        }

        return builder.toString();
    }
}
