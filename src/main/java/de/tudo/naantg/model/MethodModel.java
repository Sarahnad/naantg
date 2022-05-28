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

import java.lang.reflect.Method;
import java.util.*;

/**
 * Container for the information of the method to test.
 */
public class MethodModel {

    /**
     * the name of the method to test
     */
    private String name;

    /**
     * the method to test
     */
    private Method methodToTest;

    /**
     * the object identifier of instance of the class under test
     */
    private String object;

    /**
     * the object class
     */
    private Class<?> objectClass;

    /**
     * the return type of the method to test
     */
    private String returnType;

    /**
     * the return object for additional information
     */
    private ObjectModel returnObject;

    /**
     * the generics of the return type
     */
    private List<String> generics;

    /**
     * the parameters of the method to test
     */
    private Class<?>[] parameters;

    /**
     * the parameter object models
     */
    private List<ObjectModel> parameterObjects;

    /**
     * the identifier for the return value;
     */
    private String returnIdentifier;

    /**
     * the argument list for a method in the pre part
     */
    private String[] argList;

    /**
     * the method annotations and its value
     */
    private final Map<String, List<String>> annotationValues;


    public MethodModel() {
        this.parameterObjects = new ArrayList<>();
        this.annotationValues = new HashMap<>();
        this.generics = new ArrayList<>();
    }

    /**
     * Creates a method call statement for the method based on the stored information in this MethodModel.
     * @return a method call statement for the method
     */
    public String generateMethodStatement() {
        return generateMethodStatement(true);
    }

    /**
     * Creates a method call statement for the method based on the stored information in this MethodModel.
     * @param withReturnValue whether to generate the return value
     * @return a method call statement for the method
     */
    public String generateMethodStatement(boolean withReturnValue) {
        String statement = "\t\t";
        if (withReturnValue && returnType != null && !returnType.equals("void")) {
            statement += returnType;
        }
        if (withReturnValue && generics != null) {
            if (!generics.isEmpty() && !generics.get(0).equals("")) {
                statement += "<" + generics.get(0);
                if (generics.size() == 2 && !generics.get(1).equals("")
                        && returnType != null && Utils.isMapType(returnType)) {
                    statement += "," + generics.get(1);
                }
                statement += ">";
            }
        }
        String returnIdentifierValue = returnIdentifier != null && !returnIdentifier.equals("") ?
                returnIdentifier : "actual";
        if (withReturnValue && returnType != null && !returnType.equals("void")) {
            statement += " " + returnIdentifierValue + " = ";
        }
        if (object!= null && !object.equals("")) {
            statement+= object + ".";
        }
        statement += name + "(";
        String params = "";
        if (parameters != null && argList != null && argList.length == parameters.length) {
            params = Arrays.toString(argList);
        }
        else if (parameters != null && parameters.length != 0) {
            String[] paramStrings = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paramStrings[i] = "param" + (i+1);
            }
            params = Arrays.toString(paramStrings);
        }
        else if (!parameterObjects.isEmpty()) {
            String[] paramStrings = new String[parameterObjects.size()];
            for (int i = 0; i < parameterObjects.size(); i++) {
                paramStrings[i] = parameterObjects.get(i).getIdentifier();
            }
            params = Arrays.toString(paramStrings);
        }
        params = params.replace("[", "").replace("]", "");
        statement += params;
        statement += ");\n";
        return statement;
    }


    /**
     *
     * @return the name of the method to test
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name of the method to test
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the return type of the method to test
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     *
     * @param returnType the return type of the method to test
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     *
     * @return the generics of the return type
     */
    public List<String> getGenerics() {
        return generics;
    }

    /**
     *
     * @param generics the generics of the return type
     */
    public void setGenerics(List<String> generics) {
        this.generics = generics;
    }

    /**
     *
     * @return the parameters of the method to test
     */
    public Class<?>[] getParameters() {
        return parameters;
    }

    /**
     *
     * @param parameters the parameters of the method to test
     */
    public void setParameters(Class<?>[] parameters) {
        this.parameters = parameters;
    }

    /**
     *
     * @return the object identifier which method is called
     */
    public String getObject() {
        return object;
    }

    /**
     *
     * @param object the object identifier which method is called
     */
    public void setObject(String object) {
        this.object = object;
    }

    /**
     *
     * @return the object class
     */
    public Class<?> getObjectClass() {
        return objectClass;
    }

    /**
     *
     * @param objectClass the object class
     */
    public void setObjectClass(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    /**
     *
     * @return the method to test
     */
    public Method getMethodToTest() {
        return methodToTest;
    }

    /**
     *
     * @param methodToTest the method to test
     */
    public void setMethodToTest(Method methodToTest) {
        this.methodToTest = methodToTest;
    }

    /**
     *
     * @return the parameter object models
     */
    public List<ObjectModel> getParameterObjects() {
        return parameterObjects;
    }

    /**
     *
     * @param parameterObjects the parameter object models
     */
    public void setParameterObjects(List<ObjectModel> parameterObjects) {
        this.parameterObjects = parameterObjects;
    }

    /**
     *
     * @return the return identifier
     */
    public String getReturnIdentifier() {
        return returnIdentifier;
    }

    /**
     *
     * @param returnIdentifier the return identifier
     */
    public void setReturnIdentifier(String returnIdentifier) {
        this.returnIdentifier = returnIdentifier;
    }

    /**
     *
     * @return the argument list
     */
    public String[] getArgList() {
        return argList;
    }

    /**
     *
     * @param argList the argument list
     */
    public void setArgList(String[] argList) {
        this.argList = argList;
    }

    /**
     *
     * @return the method annotations and its values
     */
    public Map<String, List<String>> getAnnotationValues() {
        return annotationValues;
    }

    /**
     *
     * @return the return model object
     */
    public ObjectModel getReturnObject() {
        return returnObject;
    }

    /**
     *
     * @param returnObject the return model object
     */
    public void setReturnObject(ObjectModel returnObject) {
        this.returnObject = returnObject;
    }
}
