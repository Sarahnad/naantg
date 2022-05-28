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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for the information of the test class.
 */
public class TestClassModel {

    /**
     * the type of the test class
     */
    private TestClassType classType;

    /**
     * the name of the test class
     */
    private String name;

    /**
     * the package uri of the test class
     */
    private String packageUri;

    /**
     * the imports of the test class
     */
    private List<String> imports;

    /**
     * a list of mock model objects
     */
    private final List<ObjectModel> mocks;

    /**
     * all global used identifiers in the test class
     */
    private final List<String> identifiers;

    private int index;

    /**
     * the test cases of the test class,
     * the key is the name of the test case
     */
    private Map<String, TestCaseModel> testCaseModels;

    private List<TestCaseModel> privateHelperMethods;


    /**
     * Creation of the test class container.
     */
    public TestClassModel() {
        this.testCaseModels = new HashMap<>();
        this.privateHelperMethods = new ArrayList<>();
        this.imports = new ArrayList<>();
        this.mocks = new ArrayList<>();
        this.identifiers = new ArrayList<>();
        this.index = 1;
    }

    /**
     *
     * @return the type of the test class
     */
    public TestClassType getClassType() {
        return classType;
    }

    /**
     *
     * @param classType the type of the test class
     */
    public void setClassType(TestClassType classType) {
        this.classType = classType;
    }

    /**
     *
     * @return the name of the test class
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name of the test class
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the package uri of the test class
     */
    public String getPackageUri() {
        return packageUri;
    }

    /**
     *
     * @param packageUri the package uri of the test class
     */
    public void setPackageUri(String packageUri) {
        this.packageUri = packageUri;
    }

    /**
     *
     * @return the imports of the test class
     */
    public List<String> getImports() {
        return imports;
    }

    /**
     * Adds the importName to the imports if it not exists in the list
     * and is no void or simple type.
     * @param importName the import name
     */
    public void addImport(String importName) {
        if (importName == null || importName.equals("")) {
            return;
        }
        if (importName.contains("$")) {
            importName = importName.replace("$", ".");
        }
        if (imports.contains(importName) || Utils.isSimpleType(importName) ||
                Utils.isVoid(importName)) return;

        imports.add(importName);
    }

    /**
     * Adds the importNames to the imports if they not exist in the list
     * and are no void or simple type.
     * @param importNames the names of the imports
     */
    public void addImports(String[] importNames) {
        if (importNames != null && importNames.length != 0) {
            for (String importName : importNames) {
                addImport(importName);
            }
        }
    }

    /**
     * Determine an unique identifier for the given data type.
     * @param type a data type
     * @return an unique identifier for the data type
     */
    public String getUniqueIdentifier(String type) {
        String identifier;
        if (type == null || type.equals("")) return "";

        identifier = Utils.setLowerCaseFirstChar(type);


        if (identifiers.contains(identifier)) {
            identifier += index;
            index += 1;
            identifiers.add(identifier);
        }
        else {
            identifiers.add(identifier);
        }
        return identifier;
    }

    /**
     *
     * @param imports the imports of the test class
     */
    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    /**
     *
     * @return the test cases of the test class
     */
    public Map<String, TestCaseModel> getTestCaseModels() {
        return testCaseModels;
    }

    /**
     *
     * @param testCaseModels the test cases of the test class
     */
    public void setTestCaseModels(Map<String, TestCaseModel> testCaseModels) {
        this.testCaseModels = testCaseModels;
    }

    public List<TestCaseModel> getPrivateHelperMethods() {
        return privateHelperMethods;
    }

    public void setPrivateHelperMethods(List<TestCaseModel> privateHelperMethods) {
        this.privateHelperMethods = privateHelperMethods;
    }

    /**
     *
     * @return a list of mock object models
     */
    public List<ObjectModel> getMocks() {
        return mocks;
    }
}
