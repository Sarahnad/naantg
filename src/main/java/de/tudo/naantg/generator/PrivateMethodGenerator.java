/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.generator;

import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.model.*;

import java.util.ArrayList;
import java.util.List;

public class PrivateMethodGenerator {

    /**
     * Generates the fields of the parent and recursive all fields and parameters of them.
     * @param model the generator model
     * @param parent the parent object
     * @param builder the current generated objects
     * @return a helper method if the parent has more than two fields
     */
    public static TestCaseModel generateFields(GeneratorModel model, ObjectModel parent, StringBuilder builder) {
        List<ObjectModel> objectModels = parent.getInstanceFields();
        TestCaseModel helperMethod = null;
        if (!objectModels.isEmpty()) {
            if (objectModels.size() > 2) {
                helperMethod = addPrivateInitMethod(model, parent, builder);
            }
            else {
                for (ObjectModel field : objectModels) {
                    if (field.isPrivate()) {
                        for (ObjectModel param : field.getInstanceParameters()) {
                            generateFieldParameters(model, param, builder);
                        }
                        builder.append(ObjectGenerator.generateObjectStatement(field, model));
                    }
                    generateSetStatement(parent, field, builder);
                }
            }
        }
        return helperMethod;
    }

    /**
     * Generates recursive the parameters of the given field parameter and its parameters.
     * @param model the generator model
     * @param parameter an object model
     * @param builder the generated content
     */
    public static void generateFieldParameters(GeneratorModel model, ObjectModel parameter, StringBuilder builder) {
        if (!Utils.isCollectionType(parameter.getDataType())) {
            for (ObjectModel param : parameter.getInstanceParameters()) {
                generateFieldParameters(model, param, builder);
            }
        }
        builder.append(ObjectGenerator.generateObjectStatement(parameter, model));
        TestCaseModel helperMethod = generateFields(model, parameter, builder);
        if (helperMethod != null) {
            String content = generatePrivateInitMethod(model, helperMethod.getObjectModels().get(0));
            helperMethod.setHelperMethodContent(content);
        }
    }

    /**
     * Adds a helper method call to the given input for creating the given parent object.
     * @param model the generator model
     * @param parent the object to create with a helper method
     * @param input the current object creation statement
     */
    public static void addPrivateMethodForListInit(GeneratorModel model, ObjectModel parent, StringBuilder input) {
        if (model == null) return;

        String objectName = !parent.getGenericTypes().isEmpty() ? parent.getGenericTypes().get(0) : "";
        String name = "createNew" + objectName + parent.getDataType() + "()";
        input.append(name);

        TestCaseModel helperMethod = new TestCaseModel();
        model.getTestClassModel().getPrivateHelperMethods().add(helperMethod);
        helperMethod.setTestMethodName(name.replace("()", ""));
        helperMethod.getObjectModels().add(parent);
    }

    /**
     * Generates the private init method for the given object.
     * @param model the generator model
     * @param object an object model
     * @return the private init method
     */
    public static String generatePrivateInitMethod(GeneratorModel model, ObjectModel object) {
        StringBuilder builder = new StringBuilder();
        builder.append("\tprivate void init");
        builder.append(object.getDataType());
        if (object.getInitType().equals(InitType.ALL_RANDOM) || object.getInitType().equals(InitType.RANDOM)) {
            builder.append("WithRandomValues(");
        }
        else {
            builder.append("WithDefaultValues(");
        }
        builder.append(object.getDataType()).append(" ").append(object.getIdentifier());
        builder.append(") {\n");
        for (ObjectModel objectModel : object.getInstanceFields()) {
            if (Utils.isCollectionType(objectModel.getDataType())) {

            }
            else {
                for (ObjectModel param : objectModel.getInstanceParameters()) {
                    generateFieldParameters(model, param, builder);
                }
            }
            builder.append(ObjectGenerator.generateObjectStatement(objectModel, model));
        }
        for (ObjectModel objectModel : object.getInstanceFields()) {
            generateSetStatement(object, objectModel, builder);
            TestCaseModel helperMethod = generateFields(model, objectModel, builder);
            if (helperMethod != null) {
                String content = generatePrivateInitMethod(model, helperMethod.getObjectModels().get(0));
                helperMethod.setHelperMethodContent(content);
            }
        }
        builder.append("\t}\n");
        return builder.toString();
    }

    /**
     * Adds a private init method for the given object to the helper method list
     * and adds the method call statement to the given builder.
     * @param model the generator model
     * @param object an object model
     * @param builder the generated content
     * @return the helper method
     */
    public static TestCaseModel addPrivateInitMethod(GeneratorModel model, ObjectModel object, StringBuilder builder) {
        String name = "init";
        name += object.getDataType();
        if (object.getInitType().equals(InitType.ALL_RANDOM) || object.getInitType().equals(InitType.RANDOM)) {
            name += "WithRandomValues";
        }
        else {
            name += "WithDefaultValues";
        }
        builder.append("\t\t");
        builder.append(name);
        builder.append("(");
        builder.append(object.getIdentifier());
        builder.append(");\n");

        TestCaseModel helperMethod = new TestCaseModel();
        model.getTestClassModel().getPrivateHelperMethods().add(helperMethod);
        helperMethod.setTestMethodName(name);
        helperMethod.getObjectModels().add(object);
        return helperMethod;
    }

    /**
     * Generated a set statement for the given object and field and adds it to the builder.
     * @param object an object model
     * @param field a field
     * @param builder the generated content
     */
    public static void generateSetStatement(ObjectModel object, ObjectModel field, StringBuilder builder) {
        builder.append("\t\t").append(object.getIdentifier());
        builder.append(".").append(field.getSetterName());
        builder.append("(").append(field.getIdentifier()).append(");\n");
    }

    /**
     * Generates a private helper method for the parent object creation.
     * <p>for example:</p>
     * <p>private List<Box> createNewBoxList() {</p>
     * <p>List<Box> list = new ArrayList<>();</p>
     * <p>int[] boxIVars = new int[] {1, 2, 3, 4, 5, 6};</p>
     * <p>for(int elem : boxIVars) {</p>
     * <p>list.add(new Box(elem));</p>
     * <p>}</p>
     * <p>return list;</p>
     * <p>}</p>
     * @param parent the object to create with the helper method
     * @return the private helper method
     */
    public static String generateHelperMethod(GeneratorModel model, ObjectModel parent) {
        if (parent == null) return "";

        String name = "";
        String returnType;
        String creationStatement = "";
        String returnStatement = "";

        returnType = parent.getDataType();

        if (Utils.isListType(parent.getDataType()) || Utils.isCollection(parent.getDataType())) {
            String objectName = !parent.getGenericTypes().isEmpty() ? parent.getGenericTypes().get(0) : "";
            name = "createNew" + objectName + parent.getDataType();
            returnType += "<" + objectName + ">";
            returnStatement = "return " + parent.getIdentifier() + ";";

            // if generic is interface
            if (!parent.getInstanceParameters().isEmpty()) {
                objectName = parent.getInstanceParameters().get(0).getDataType();
            }

            StringBuilder builder = new StringBuilder();
            // example: List<Box> list = new ArrayList<>();
            builder.append(ObjectGenerator.generateObjectStatement(parent, model,false));

            // example: int[] boxIVars = new int[] {1, 2, 3, 4, 5, 6};
            List<ObjectModel> valueObjects = generateValueArrayObjects(parent);
            for (ObjectModel object : valueObjects) {
                builder.append(ObjectGenerator.generateObjectStatement(object, model));
            }

            if (valueObjects.size() == 1) {
                ObjectModel object = valueObjects.get(0);
                String dataType = object.getDataType().replace("[]", "");
                String identifier = object.getIdentifier();
                // example: list.add(new Box(elem));
                String addStatement = parent.getIdentifier() + ".add(new " + objectName + "(elem))";

                String forLoop = "\t\tfor (" + dataType + " elem : " + identifier + ") {\n" +
                        "\t\t\t" + addStatement + ";\n" +
                        "\t\t}\n";

                builder.append(forLoop);
            }
            else if (valueObjects.size() > 1) {
                StringBuilder params = new StringBuilder();
                for (ObjectModel object : valueObjects) {
                    params.append(object.getIdentifier()).append("[i], ");
                }
                if (params.length() > 2) {
                    params.delete(params.length()-2, params.length());
                }

                // example: list.add(new Box(list[i], list2[i]);
                String addStatement = parent.getIdentifier() + ".add(new " + objectName + "(" + params.toString() + "))";

                String forLoop = "\t\tfor (int i = 0; i < " + valueObjects.get(0).getIdentifier() + ".length; i++) {\n" +
                        "\t\t\t" + addStatement + ";\n" +
                        "\t\t}\n";

                builder.append(forLoop);
            }
            else {
                // example: list.add(new Box());
                String addStatement = parent.getIdentifier() + ".add(new " + objectName + "())";

                String forLoop = "\t\tfor (int i = 0; i < " + parent.getInstanceParameters().size() + "; i++) {\n" +
                        "\t\t\t" + addStatement + ";\n" +
                        "\t\t}\n";

                builder.append(forLoop);
            }
            creationStatement = builder.toString();
        }

        return "\tprivate " + returnType + " " + name + "() {\n" +
                creationStatement +
                "\t\t" + returnStatement + "\n" +
                "\t}\n";
    }

    public static String generateHelperMethod(TestCaseModel testCaseModel) {
        String returnType = "void";
        if (testCaseModel.getReturnObject() != null) {
            String type = testCaseModel.getReturnObject().getDataType();
            if (type != null && !type.isEmpty()) {
                returnType = type;
            }
        }
        String params = "";
        StringBuilder builder = new StringBuilder();
        if (!testCaseModel.getParameters().isEmpty()) {
            for (ObjectModel objectModel : testCaseModel.getParameters()) {
                builder.append(objectModel.getDataType()).append(" ");
                builder.append(objectModel.getIdentifier()).append(", ");
            }
            builder.deleteCharAt(builder.length()-2);
            builder.deleteCharAt(builder.length()-1);
        }
        params = builder.toString();
        String creationStatement = "";
        if (testCaseModel.getReturnObject() != null) {
            creationStatement = ObjectGenerator.generateObjectStatement(testCaseModel.getReturnObject());
        }
        StringBuilder initStatement = new StringBuilder();
        for (ObjectModel objectModel : testCaseModel.getObjectModels()) {
            initStatement.append(ObjectGenerator.generateObjectStatement(objectModel));
        }
        if (!creationStatement.isEmpty()) creationStatement += "\n";
        String returnStatement = "";
        if (testCaseModel.getReturnObject() != null) {
            returnStatement = "\t\treturn " + testCaseModel.getReturnObject().getIdentifier() + ";\n";
        }

        return "\tprivate " + returnType + " " + testCaseModel.getTestMethodName()
                + "(" + params + ") {\n" +
                initStatement +
                creationStatement +
                returnStatement +
                "\t}\n";
    }

    /**
     * Generates the array with simple values for the creation of the collection objects.
     * <p>for example:</p>
     * <p>ObjectModel(dataType=int[], identifier=boxIVars, newInstance=true, value={1, 2, 3, 4, 5, 6})</p>
     * <p>for the generation of:</p>
     * <p>int[] boxIVars = new int[] {1, 2, 3, 4, 5, 6};</p>
     * @param parent the parent object that potentially needs creation parameters
     * @return an object model with the value array
     */
    public static List<ObjectModel> generateValueArrayObjects(ObjectModel parent) {
        List<ObjectModel> valueArrays = new ArrayList<>();
        List<ObjectModel> elements = parent.getInstanceParameters();
        if (elements.isEmpty()) return valueArrays;
        ObjectModel firstElem = elements.get(0);
        List<ObjectModel> instances = firstElem.getInstanceParameters();
        // objects with default constructor
        if (instances.isEmpty()) {
            return valueArrays;
        }
        // objects have constructor with parameters
        for (int i = 0; i < instances.size(); i++) {
            ObjectModel param = instances.get(i);
            if (Utils.isSimpleType(param.getDataType()) || param.isEnum()) {
                String values = generateValueArray(parent, i); // {val1, val2, val3, ...}
                String identifier = Utils.setLowerCaseFirstChar(firstElem.getDataType()) +
                        Utils.setUpperCaseFirstChar(param.getIdentifier()) + "s";
                String dataType = param.getDataType() + "[]";
                ObjectModel arrayStatement = new ObjectModel(dataType, identifier, true);
                arrayStatement.setValue(values);
                valueArrays.add(arrayStatement);
            }
            else {
                // todo: handle objects and collections
            }
        }

        return valueArrays;
    }

    /**
     * Generates an array as String with values of the given type for the given list.
     * @param list an object model with a list data type
     * @param index the index of the instance parameter
     * @return an array as String with values of the given type for the given list
     */
    public static String generateValueArray(ObjectModel list, int index) {
        StringBuilder valueArray = new StringBuilder();

        List<ObjectModel> listElements = list.getInstanceParameters();
        valueArray.append("{");
        for (ObjectModel element : listElements) {
            String val = element.getInstanceParameters().get(index).getValue();
            valueArray.append(val).append(",");
        }
        valueArray.delete(valueArray.length()-1, valueArray.length());
        valueArray.append("}");

        return valueArray.toString();
    }

}
