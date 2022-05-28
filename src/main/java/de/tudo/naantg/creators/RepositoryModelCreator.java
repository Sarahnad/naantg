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

import de.tudo.naantg.annotations.AnnoType;
import de.tudo.naantg.annotations.InitState;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.MethodNameParser;
import de.tudo.naantg.parser.ParseKey;
import org.springframework.security.access.method.P;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Extends the SimpleModelCreator to select information for repository test classes.
 */
public class RepositoryModelCreator extends SimpleModelCreator {

    /**
     * Creator calls the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test project package
     */
    public RepositoryModelCreator(String projectPackageUri, String testPackageUri) {
        super(projectPackageUri, testPackageUri);
    }

    /**
     * Creator calls the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test project package
     * @param testGenPath the uri of the testGen folder
     */
    public RepositoryModelCreator(String projectPackageUri, String testPackageUri, String testGenPath) {
        super(projectPackageUri, testPackageUri, testGenPath);
    }

    /**
     * If the method starts with "whenFindBy" and the with part contains an object,
     * than this object will be created with all its parameters and fields.
     * Otherwise the super method is called.
     * @param initValue the described pre state
     * @param testName the name of the test case
     */
    @Override
    public void calculateInitMethods(String initValue, String testName) {
        // repository instance not needed
        if (!getModel().getObjectModels(testName).isEmpty()) {
            //getModel().getObjectModels(testName).remove(0);
            getModel().getObjectModels(testName).get(0).setNotToGenerate(true);
        }
        List<String> givenState = MethodNameParser.getGivenValue(testName);
        String parsedEntity = "";
        if (givenState.size() == 1) {
            parsedEntity = Utils.setUpperCaseFirstChar(givenState.get(0));
        }
        else if (givenState.size() == 2 && givenState.get(0).equals(StateKey.VALUE.toString())) {
            parsedEntity = Utils.setUpperCaseFirstChar(givenState.get(1));
        }

        if (!parsedEntity.isEmpty() && testName.contains(ParseKey.FIND.getKeyword())) {
            Optional<Class<?>> optObject = Scanner.findCorrespondingClass(parsedEntity,
                    getModel().getProjectPackageUriWithDots());
            if (!optObject.isPresent()) {
                return;
            }
            String identifier = getModel().getUniqueIdentifier(testName, optObject.get().getSimpleName());
            ObjectModel objectModel = new ObjectModel(optObject.get(), identifier, true);
            getModel().getParameters(testName).add(objectModel);
            calculateParameter(objectModel, testName, InitType.NONE);
            calculateFields(objectModel, testName, InitType.ALL_RANDOM);
            findParamsForMethodToTest(objectModel, testName);

            ObjectModel returnObject = getModel().getMethodOfCUT(testName).getReturnObject();
            if (Utils.isCollectionType(returnObject.getDataType())) {
                returnObject.getGenericClasses().clear();
                returnObject.getGenericClasses().add(optObject.get());
                getModel().getMethodOfCUT(testName).getGenerics().add(optObject.get().getSimpleName());
            }

        }

        super.calculateInitMethods(initValue, testName);

    }

    /**
     * Calculates all fields of the parent object and adds information about
     * the GeneratedValue annotation.
     * @param parent the parent object
     * @param testName the name of the test case
     * @param initType the init type
     */
    @Override
    public void calculateFields(ObjectModel parent, String testName, InitType initType) {
        if (initType.equals(InitType.NONE)) return;

        List<Field> fields = Scanner.findAllFields(parent.getObjectClass());
        int len = fields.size();
        if (len == 0) return;

        for (Field field : fields) {
            String identifier = getModel().getUniqueIdentifier(testName, field.getType().getSimpleName(), field.getName());
            ObjectModel newObject = new ObjectModel(field.getType(), identifier, true);
            if (len > 2) newObject.setPrivate(true);
            newObject.setFieldOfParent(parent);
            boolean isGeneratedField = false;

            if (Scanner.hasFieldAnnotation(field, SpringBootKey.GENERATED.getKeyword())) {
                newObject.getAnnotations().add(SpringBootKey.GENERATED.getKeyword());
                isGeneratedField = true;
            }

            if (Scanner.hasGetterMethod(parent.getObjectClass(), field)) {
                newObject.setGetterName(Scanner.getGetterMethod(parent.getObjectClass(), field));
            }

            if (Scanner.hasSetterMethod(parent.getObjectClass(), field)) {
                if (!isGeneratedField) {
                    parent.getInstanceFields().add(newObject);
                }
                newObject.setSetterName(Scanner.getSetterMethod(parent.getObjectClass(), field));

                ValueCreator valueCreator = new ValueCreator(getModel(), testName, initType);
                valueCreator.calculateField(field, newObject);
                getModel().getParameters(testName).add(newObject);
            }
        }
    }

    /**
     * Checks if the needed arguments for the method to test are already created and
     * adds the findings to the given init list of the given test case.
     * @param object needed for the parameters identifiers
     * @param testName the name of the test case
     */
    public void findParamsForMethodToTest(ObjectModel object, String testName) {
        MethodModel methodModel = getModel().getMethodOfCUT(testName);
        Class<?>[] methodParameters = methodModel.getParameters();
        if (methodParameters.length == 0) return;
        String[] argList = new String[methodParameters.length];
        methodModel.setArgList(argList);
        List<ObjectModel> foundParameters = new ArrayList<>();
        List<ObjectModel> toRemove = new ArrayList<>();

        for (int i = 0; i < methodParameters.length; i++) {
            Class<?> methodParameter = methodParameters[i];
            String findName = "";
            boolean found = false;

            String[] parts = testName.split("_");
            for (String part : parts) {
                if (part.startsWith(ParseKey.FIND.getKeyword())) {
                    findName = part.substring(ParseKey.FIND.getKeyword().length());
                    findName = Utils.setLowerCaseFirstChar(findName);
                }
            }

            for (ObjectModel objectModel : getModel().getParameters(testName)) {
                if (Utils.isObject(methodParameter.getSimpleName()) &&
                        !findName.equals("") && objectModel.getIdentifier().equals(findName)
                        && objectModel.getGetterName() != null) {

                    methodParameters[i] = objectModel.getObjectClass();
                    found = true;
                }
                else if (objectModel.getDataType().equalsIgnoreCase(methodParameter.getSimpleName()) &&
                        !findName.equals("") && objectModel.getIdentifier().equals(findName) &&
                        objectModel.getGetterName() != null && !foundParameters.contains(objectModel)) {

                    found = true;
                }

                if (found) {
                    String identifier = object.getIdentifier() + "." + objectModel.getGetterName() + "()";
                    argList[i] = identifier;

                    ObjectModel param = Helper.findObjectByIdentifier("param" + (i + 1), getModel().getParameters(testName));
                    toRemove.add(param);

                    ObjectModel copy = new ObjectModel(objectModel.getDataType(), "param" + (i + 1), false);
                    getModel().getGivenInitObjects(testName).add(copy);
                    foundParameters.add(objectModel);
                    found = false;
                }

            }
            if (argList[i] == null) {
                argList[i] = "param" + (i + 1);
            }
        }

        for (ObjectModel param : toRemove) {
            getModel().getParameters(testName).remove(param);
        }
    }

    /**
     * Calculates recursive the object values for the given object model
     * with the given init type and changes the Object type to Long if the method to test
     * contains the "findById" key word.
     * @param objectModel an object model with an object type
     * @param testName name of the test case
     * @param initType the init type
     */
    @Override
    public void calculateParameter(ObjectModel objectModel, String testName, InitType initType) {
        if (Utils.isObject(objectModel.getDataType())) {
            if (getModel().getMethodOfCUT(testName).getName().equals(SpringBootKey.FIND_BY_ID.getKeyword())) {
                objectModel.setObjectClass(Long.class);
                objectModel.setDataType("Long");
            }
        }
        super.calculateParameter(objectModel, testName, initType);
    }

}
