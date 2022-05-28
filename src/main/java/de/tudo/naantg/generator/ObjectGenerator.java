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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Generates the model objects.
 */
public class ObjectGenerator {


    /**
     * Creates an object creation or setting statement based on the stored information
     * in this ObjectModel.
     * If the object has a collection data type the collection
     * is created with values if they are given.
     * @param objectModel an object model
     * @return an object creation or setting statement
     */
    public static String generateObjectStatement(ObjectModel objectModel) {
        return generateObjectStatement(objectModel, true, true);
    }

    public static String generateObjectStatement(ObjectModel objectModel, boolean isEmpty) {
        if (objectModel == null) return "";
        if (objectModel.getDataType() == null || objectModel.getDataType().isEmpty()) return "";
        if (objectModel.getIdentifier() == null || objectModel.getIdentifier().isEmpty()) return  "";

        StringBuilder input = new StringBuilder();
        input.append("\t\t").append(objectModel.getDataType());
        input.append(" ").append(objectModel.getIdentifier()).append(" = ");
        String type = objectModel.getDataType();
        if (Utils.isObjectType(type) || Utils.isCollectionType(type)) {
            input.append("null");
        }
        else if (Utils.isString(type)) {
            input.append("\"\"");
        }
        else if (Utils.isChar(type)) {
            input.append("''");
        }
        else if (Utils.isBoolean(type)) {
            input.append("false");
        }
        else if (Utils.isSimpleType(type)) {
            input.append("0");
        }

        input.append(";\n");

        return input.toString();
    }

    /**
     * Creates an object creation or setting statement based on the stored information
     * in this ObjectModel.
     * If the object has a collection data type the collection
     * is created with values if they are given.
     * @param objectModel an object model
     * @return an object creation or setting statement
     */
    public static String generateObjectStatement(ObjectModel objectModel, GeneratorModel model) {
        return generateObjectStatement(objectModel, model,true);
    }

    /**
     * Creates an object creation or setting statement based on the stored information
     * in this ObjectModel.
     * @param objectModel an object model
     * @param withValues if the object has a collection data type the collection
     *                   should be created with values if they are given
     * @return an object creation or setting statement
     */
    public static String generateObjectStatement(ObjectModel objectModel, boolean withValues, boolean withIdentifier) {
        return generateObjectStatement(objectModel, null, withValues, withIdentifier);
    }

    /**
     * Creates an object creation or setting statement based on the stored information
     * in this ObjectModel.
     * @param objectModel an object model
     * @param withValues if the object has a collection data type the collection
     *                   should be created with values if they are given
     * @return an object creation or setting statement
     */
    public static String generateObjectStatement(ObjectModel objectModel, GeneratorModel model, boolean withValues) {
        return generateObjectStatement(objectModel, model, withValues, true);
    }

    /**
     * Creates an object creation or setting statement based on the stored information
     * in this ObjectModel.
     * @param objectModel an object model
     * @param withValues if the object has a collection data type the collection
     *                   should be created with values if they are given
     * @param withIdentifier whether the identifier part should be generated
     * @return an object creation or setting statement
     */
    public static String generateObjectStatement(ObjectModel objectModel, GeneratorModel model, boolean withValues, boolean withIdentifier) {
        if (objectModel == null) return "";

        StringBuilder input = new StringBuilder();
        String dataType = objectModel.getDataType();
        if (dataType == null) return "";
        List<String> genericTypes = objectModel.getGenericTypes();
        String value = objectModel.getValue();
        List<ObjectModel> instanceParameters = objectModel.getInstanceParameters();

        if (withIdentifier) {
            input.append("\t\t");
            input.append(dataType);
            if (!genericTypes.isEmpty() &&
                    (dataType.contains("List") || Utils.isCollectionType(dataType))) {
                input.append("<").append(genericTypes.get(0));
                if (genericTypes.size() == 2 && Utils.isMapType(dataType)) {
                    input.append(",").append(genericTypes.get(1));
                }
                input.append(">");
            }
            input.append(" ").append(objectModel.getIdentifier());
            input.append(" = ");
        }

        if (objectModel.isEnum()) {
            input.append(objectModel.getValue());
            if (withIdentifier) {
                input.append(";\n");
            }
            return input.toString();
        }

        if (objectModel.isNewInstance()) {
            if (objectModel.isSimpleType()) {
                input.append(value);
                if (withIdentifier) {
                    input.append(";\n");
                }
                return input.toString();
            }
            else if (Utils.isCollectionType(dataType)) {
                if (Utils.isOptional(dataType)) {
                    input.append("Optional.");
                    if (instanceParameters.size() == 1) {
                        input.append("of(");
                        input.append(instanceParameters.get(0).getIdentifier());
                        input.append(")");
                    }
                    else {
                        input.append("empty()");
                    }
                }
                else if (Utils.isMapType(dataType)) {
                    input.append("new HashMap<>()");
                }
                else {
                    addCollectionNewInstance(input, objectModel, model, withValues);
                }
                if (withIdentifier) {
                    input.append(";\n");
                }
                return input.toString();
            }
            input.append("new ");
            input.append(dataType);
            input.append("(");
            if (withValues && instanceParameters != null && !instanceParameters.isEmpty()) {
                String[] identifiers = new String[instanceParameters.size()];
                for (int i = 0; i < instanceParameters.size(); i++) {
                    identifiers[i] = instanceParameters.get(i).getIdentifier();
                }
                String params = Arrays.toString(identifiers);
                params = params.replace("[", "").replace("]", "");
                input.append(params);
            }
            input.append(")");
            if (withIdentifier) {
                input.append(";\n");
            }
        }
        else {
            input.append(value).append(";\n");
        }
        return input.toString();
    }

    /**
     * Adds the best new instance statement for the collection type.
     * @param input the prefix statement
     * @param objectModel the object model of the collection
     * @param model the generator model
     * @param withValues whether object should be created with values
     */
    public static void addCollectionNewInstance(StringBuilder input, ObjectModel objectModel, GeneratorModel model, boolean withValues) {
        String dataType = objectModel.getDataType();
        List<String> genericTypes = objectModel.getGenericTypes();
        String value = objectModel.getValue();
        List<ObjectModel> instanceParameters = objectModel.getInstanceParameters();

        if (withValues && !genericTypes.isEmpty() && Utils.isObjectType(genericTypes.get(0)) && instanceParameters.size() != 0) {
            PrivateMethodGenerator.addPrivateMethodForListInit(model, objectModel, input);
            return;
        }
        boolean isValue = (value != null && !value.equals(""));
        boolean isArrayOrLinkedList = (Utils.isArrayList(dataType) || Utils.isLinkedList(dataType));
        if (!isValue && isArrayOrLinkedList) {
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
                if (!genericTypes.isEmpty() && (Utils.isByte(genericTypes.get(0)) || Utils.isShort(genericTypes.get(0)))) {
                    value = value.replaceAll("\\(", "{")
                            .replaceAll("\\)", "}");
                    input.append("(new ").append(genericTypes.get(0)).append("[] ");
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
    }

    /**
     * Generates the field calls.
     * @param objectModel the object model
     * @return the generates field calls statement.
     */
    public static String generateFieldCalls(ObjectModel objectModel) {
        StringBuilder input = new StringBuilder();
        List<ObjectModel> fields = objectModel.getInstanceFields();
        if (fields.isEmpty()) return "";

        if (objectModel.getIdentifier() != null && !objectModel.getIdentifier().isEmpty()) {
            input.append("\t\t").append(objectModel.getIdentifier());
            for (int i = 0; i < fields.size()-1; i++) {
                ObjectModel field = fields.get(i);
                if (field.getGetterName() != null && !field.getGetterName().isEmpty()) {
                    input.append(".").append(field.getGetterName()).append("()");
                }
            }
            ObjectModel field = fields.get(fields.size()-1);
            String setMethod = "";
            String val = objectModel.getValue();
            if (field.getSetterName() != null && !field.getSetterName().isEmpty()) {
                setMethod = field.getSetterName();
            }
            else {
                if (Utils.isListType(field.getDataType()) && val != null && !val.isEmpty()) {
                    if (field.getGetterName() != null && !field.getGetterName().isEmpty()) {
                        input.append(".").append(field.getGetterName()).append("()");
                        setMethod = "add";
                        if (val.contains(",") || objectModel.getElementSize() == 2) {
                            setMethod += "All";
                        }
                    }
                }
            }
            input.append(".").append(setMethod);
            if (val != null && !val.isEmpty()) {
                input.append("(");
                input.append(objectModel.getValue());
                input.append(")");
            }
            input.append(";\n");
        }
        return input.toString();
    }

}
