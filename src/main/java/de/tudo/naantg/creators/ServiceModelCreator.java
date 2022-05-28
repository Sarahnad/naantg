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

import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.model.ObjectModel;

/**
 * Extends the SimpleModelCreator for service aspects.
 */
public class ServiceModelCreator extends SimpleModelCreator {

    /**
     * the mock creator for mocking
     */
    private MockCreator mockCreator;

    /**
     * Creates the ServiceModelCreator by calling the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test package
     */
    public ServiceModelCreator(String projectPackageUri, String testPackageUri) {
        super(projectPackageUri, testPackageUri);
    }

    /**
     * Creates the ServiceModelCreator by calling the super constructor.
     * @param projectPackageUri the uri of the project package
     * @param testPackageUri the uri of the test package with the TG interfaces
     * @param testGenPath the uri of the test generation package
     */
    public ServiceModelCreator(String projectPackageUri, String testPackageUri, String testGenPath) {
        super(projectPackageUri, testPackageUri, testGenPath);
    }

    /**
     * Creates the GeneratorModel and the Mocking for the
     * given test generation interface and its methods.
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
     * Calculates mocking and all fields of the parent object.
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
     * Calculates init values and removes the cut from the model object list.
     * @param initValue the described pre state
     * @param testName the name of the test case
     */
    @Override
    public void calculateInitMethods(String initValue, String testName) {
        // service instance not needed
        if (!getModel().getObjectModels(testName).isEmpty()) {
            getModel().getObjectModels(testName).remove(0);
        }
        super.calculateInitMethods(initValue, testName);
    }

    /**
     * Sets the mockCreator.
     * @param mockCreator the mock creator
     */
    public void setMockCreator(MockCreator mockCreator) {
        this.mockCreator = mockCreator;
    }

}
