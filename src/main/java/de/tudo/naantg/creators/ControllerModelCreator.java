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
import de.tudo.naantg.annotations.MappingValue;
import de.tudo.naantg.helpers.Helper;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.*;
import de.tudo.naantg.parser.AnnotationParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the SimpleModelCreator for controller aspects.
 */
public class ControllerModelCreator extends SimpleModelCreator {

    /**
     * the mock creator for mocking
     */
    private MockCreator mockCreator;

    /**
     * the global mapping path given in the RequestMapping annotation
     */
    private String globalMappingPath;

    /**
     * Creates the ControllerModelCreator by calling the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test package
     */
    public ControllerModelCreator(String projectPackageUri, String testPackageUri) {
        super(projectPackageUri, testPackageUri);
    }

    /**
     * Creates the ControllerModelCreator by calling the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test package with the TG interfaces
     * @param testGenPath the uri of the test generation package
     */
    public ControllerModelCreator(String projectPackageUri, String testPackageUri, String testGenPath) {
        super(projectPackageUri, testPackageUri, testGenPath);
    }

    /**
     * Creates the GeneratorModel for the given test generation interface and its methods.
     * @param tgClass the test generation interface
     * @return the GeneratorModel
     */
    @Override
    public GeneratorModel createGeneratorModel(Class<?> tgClass) {
        mockCreator = new MockCreator(getModel(), tgClass);
        GeneratorModel generatorModel = super.createGeneratorModel(tgClass);
        mockCreator.addAndCalculateMocking();
        return generatorModel;
    }

    /**
     * Selects the information about the method to test and the mapping values.
     * @param testName the name of the test case
     */
    @Override
    public void calculateMethodModel(String testName) {
        super.calculateMethodModel(testName);

        String replacement = "";
        String mappingValue = "";

        try {
            Method tgMethod = getModel().getTgClass().getMethod(testName);
            InitType initType = InitType.NONE;
            if (Scanner.hasInitStateAnnotation(tgMethod)) {
                initType = tgMethod.getDeclaredAnnotation(InitState.class).type();
            }
            replacement = determineGivenReplacement(tgMethod, initType);
            if (Scanner.hasAnnotation(tgMethod, AnnoType.MAPPING_VALUE.getType())) {
                mappingValue = tgMethod.getDeclaredAnnotation(MappingValue.class).path();
            }
        }
        catch (Exception e) {
            Logger.logWarning("[ControllerModelCreator] Test generation method " + testName + " does not exist!");
        }

        MethodModel methodModel = getModel().getMethodOfCUT(testName);
        if (globalMappingPath == null) {
            getGlobalMapping();

        }
        addMappingValues(methodModel, SpringBootKey.GET, replacement, mappingValue, testName);
        addMappingValues(methodModel, SpringBootKey.POST, replacement, mappingValue, testName);
        addMappingValues(methodModel, SpringBootKey.REQUEST, replacement, mappingValue, testName);
    }

    /**
     * Gets the global mapping path of the cut if it exists.
     */
    private void getGlobalMapping() {
        if (Scanner.hasClassAnnotation(getModel().getCut(), SpringBootKey.REQUEST.getKeyword())) {
            String[] mappingValues = getModel().getCut().getAnnotation(RequestMapping.class).value();
            if (mappingValues.length == 1) {
                globalMappingPath = mappingValues[0];
            }
        }
    }

    /**
     * Adds the mapping information to the given methodModel.
     * @param methodModel the method model of the method to test
     * @param mapping kind of mapping
     * @param testName the name of the test case
     */
    private void addMappingValues(MethodModel methodModel, SpringBootKey mapping, String replacement,
                                  String mappingValue, String testName) {
        boolean isGet = mapping.equals(SpringBootKey.GET);
        boolean isPost = mapping.equals(SpringBootKey.POST);

        String key = isGet ? SpringBootKey.GET.getKeyword() :
                isPost ? SpringBootKey.POST.getKeyword() :
                        SpringBootKey.REQUEST.getKeyword();

        String path = SpringBootKey.MAPPING.getImportPath();
        Method methodToTest = methodModel.getMethodToTest();
        if (Scanner.hasAnnotation(methodToTest, key, path)) {
            Optional<Annotation> optAnno = Scanner.findAnnotationClass(methodToTest, key);
            if (!optAnno.isPresent()) {
                return;
            }
            if (isGet && optAnno.get() instanceof GetMapping) {
                String[] value = ((GetMapping) optAnno.get()).value();
                addValueToMap(replacement, mappingValue, value, SpringBootKey.GET.getKeyword(), methodModel, testName);
                getModel().getTestClassModel().addImport(SpringBootKey.GET.getImportPath());
            }
            else if (isPost && optAnno.get() instanceof PostMapping) {
                String[] value = ((PostMapping) optAnno.get()).value();
                addValueToMap(replacement, mappingValue, value, SpringBootKey.POST.getKeyword(), methodModel, testName);
                getModel().getTestClassModel().addImport(SpringBootKey.POST.getImportPath());
            }
            else if (optAnno.get() instanceof RequestMapping) {
                String[] value = ((RequestMapping) optAnno.get()).value();
                RequestMethod[] requestMethods = ((RequestMapping) optAnno.get()).method();
                if (requestMethods.length == 1) {
                    if (requestMethods[0].name().equals(SpringBootKey.GET.toString())) {
                        addValueToMap(replacement, mappingValue, value, SpringBootKey.GET.getKeyword(), methodModel, testName);
                        getModel().getTestClassModel().addImport(SpringBootKey.GET.getImportPath());
                    }
                    else if (requestMethods[0].name().equals(SpringBootKey.POST.toString())) {
                        addValueToMap(replacement, mappingValue, value, SpringBootKey.POST.getKeyword(), methodModel, testName);
                        getModel().getTestClassModel().addImport(SpringBootKey.POST.getImportPath());
                    }
                }
            }
        }
    }

    /**
     * Adds the mapping value to annotation map of the methodModel.
     * @param value the mapping values
     * @param key the mapping type
     * @param methodModel the method model
     * @param testName the name of the test case
     */
    private void addValueToMap(String replacement, String mappingValue, String[] value, String key,
                               MethodModel methodModel, String testName) {
        List<String> values = new ArrayList<>();
        if (!mappingValue.isEmpty()) {
            if (!replacement.isEmpty()) {
                mappingValue = "\"" + mappingValue + "\", " + replacement;
            }
            values.add(mappingValue);
        }
        else {
            for (String val : value) {
                val = addGlobalMapping(val);
                val = replacement.isEmpty() ? calculateReplacements(val, testName) : "\"" + val + "\", " + replacement;
                values.add(val);
            }
        }
        methodModel.getAnnotationValues().put(key, values);
    }

    /**
     * Determines the given replacement of the MappingValue annotation if it exists.
     * @param tgMethod the test generation method
     * @param initType the init type
     * @return the replacement
     */
    public String determineGivenReplacement(Method tgMethod, InitType initType) {
        if (!Scanner.hasAnnotation(tgMethod, AnnoType.MAPPING_VALUE.getType())) return "";
        String value = tgMethod.getDeclaredAnnotation(MappingValue.class).value();
        if (value.equals("")) return "";

        List<String> parsed = AnnotationParser.parseMappingValue(value);
        ValueCreator valueCreator = new ValueCreator(getModel(), tgMethod.getName(), initType);
        StringBuilder replacements = new StringBuilder();
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
                    addReplacementObject(replacements, type, name, val, valueCreator, tgMethod.getName());
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
                // expected is a primitive data type such as int, long or String
                if (Utils.isSimpleType(part)) {
                    type = part;
                }
                // default int
                else {
                    type = SimpleDataType.INT.getType();
                }
                isType = false;
            }
            else if (isName) {
                name = getModel().getUniqueIdentifier(tgMethod.getName(), type, part);
                isName = false;
            }
            else if (isValue) {
                val = part;
                isValue = false;
            }
        }
        if (!name.isEmpty()) {
            addReplacementObject(replacements, type, name, val, valueCreator, tgMethod.getName());
        }
        return replacements.toString();
    }

    /**
     * Adds the object for the replacement.
     * @param replacements the actual replacement.
     * @param type the data type of the object.
     * @param name the name of the object.
     * @param val the value of the object.
     * @param valueCreator the value creator.
     * @param testName the name of the testcase.
     */
    private void addReplacementObject(StringBuilder replacements, String type, String name, String val,
                                      ValueCreator valueCreator, String testName) {
        ObjectModel objectModel = new ObjectModel(type, name, true);
        if (replacements.toString().isEmpty()) {
            replacements.append(name);
        }
        else {
            replacements.append(", ").append(name);
        }
        if (val.isEmpty()) {
            valueCreator.calculateValue(objectModel);
        }
        else {
            objectModel.setValue(val);
        }
        getModel().getParameters(testName).add(objectModel);
        getModel().getGivenInitObjects(testName).add(objectModel);
    }

    /**
     * Adds the global mapping path as value prefix.
     * @param value a mapping path
     */
    private String addGlobalMapping(String value) {
        if (globalMappingPath != null && !globalMappingPath.isEmpty()) {
            return globalMappingPath + value;
        }
        return value;
    }

    /**
     * Adds int ids with default value 1 to the values that contain values to replace.
     * @param value the mapping value
     * @param testName the name of the test case
     * @return the value with replacements
     */
    protected String calculateReplacements(String value, String testName) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(value).append("\"");
        Pattern pattern = Pattern.compile("\\{[^{}]+}");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            String stringPart = matcher.group().substring(1);
            stringPart = stringPart.substring(0, stringPart.length()-1);
            stringPart = stringPart.toUpperCase();
            builder.append(", ").append(stringPart);
            String identifier = getModel().getUniqueIdentifier(testName, SimpleDataType.INT.getType(), stringPart);
            ObjectModel objectModel = new ObjectModel(int.class, identifier, true);
            objectModel.setValue("1");
            getModel().getParameters(testName).add(objectModel);
        }
        return builder.toString();
    }

    /**
     * Calculates all information for the given parameter and add them to the parameterObject.
     * Filters parameters with ModelAttribute annotation and adds the annotation information
     * to the corresponding parameter model object.
     * @param parameterObject the parameter object model
     * @param testName the name of the test case
     * @param initType the init type
     */
    @Override
    public void calculateParameter(ObjectModel parameterObject, String testName, InitType initType) {

        super.calculateParameter(parameterObject, testName, initType);

        Method methodToTest = getModel().getMethodOfCUT(testName).getMethodToTest();
        Parameter[] parameters = methodToTest.getParameters();
        String identifier = parameterObject.getIdentifier();
        if (identifier.contains("param")) {
            int index = Integer.parseInt(identifier.replace("param", ""));
            index--;
            if (index >= parameters.length) return;
            Parameter param = parameters[index];
            if (Scanner.hasParameterAnnotation(param, SpringBootKey.MODEL.getKeyword())) {
                parameterObject.getGivenAnnotations().add(SpringBootKey.MODEL.getKeyword());
            }
        }
    }

    /**
     * Selects the information about the objects and primitive values of the test case.
     * Calculates also given controller parameters.
     * @param method the test case
     */
    @Override
    public void calculateObjects(Method method) {
        super.calculateObjects(method);
        calculateControllerParameter(method);
    }

    /**
     * Calculates the controller parameters given in the InitState annotation.
     * @param method the test case
     */
    public void calculateControllerParameter(Method method) {
        if (!Scanner.hasAnnotation(method, AnnoType.INIT_STATE.getType())) {
            return;
        }
        String params = method.getDeclaredAnnotation(InitState.class).controllerParams();
        if (params.equals("")) return;

        String testName = method.getName();
        List<String> parsedValues = AnnotationParser.parseInitStateControllerParams(params);
        ValueCreator valueCreator = new ValueCreator(getModel(), testName, InitType.RANDOM);
        valueCreator.setWithTypeEnding(false);
        boolean isName = false;
        ObjectModel objectModel = null;
        for (String part : parsedValues) {
            if (!isName) {
                isName = true;
                Optional<Class<?>> attribute = Scanner.findCorrespondingField(part, getModel().getProjectPackageUriWithDots());
                if (attribute.isPresent()) {
                    objectModel = new ObjectModel(attribute.get(), part, false);
                }
                else {
                    objectModel = new ObjectModel(part, part, false);
                }
            }
            else {
                if (!part.equals("*")) {
                    objectModel.setValue(part);
                }
                valueCreator.calculateValue(objectModel);
                getModel().getCutObject(testName).getInstanceParameters().add(objectModel);
                isName = false;
            }
        }

    }

    /**
     * Calculates mocks and all fields of the parent object.
     * @param parent the parent object
     * @param testName the name of the test case
     * @param initType the init type
     */
    @Override
    public void calculateFields(ObjectModel parent, String testName, InitType initType) {
        mockCreator.detectAndAddMocks(parent);
        super.calculateFields(parent, testName, initType);
    }

    /**
     * Sets the mockCreator.
     * @param mockCreator the mock creator
     */
    public void setMockCreator(MockCreator mockCreator) {
        this.mockCreator = mockCreator;
    }

}
