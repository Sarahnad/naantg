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

import de.tudo.naantg.model.GeneratorModel;

import java.lang.annotation.Annotation;

/**
 * Selects all information for the pre state and the method to test.
 */
public interface ModelCreator {

    /**
     * Sets the absolut path for the testGen folder.
     * @param absolutTestGenPath the absolut path for the testGen folder
     */
    void setAbsolutTestGenPath(String absolutTestGenPath);

    /**
     * Sets the absolut path for the test folder.
     * @param absolutTestGenPath the absolut path for the test folder
     */
    void setAbsolutTestPath(String absolutTestGenPath);

    /**
     * Sets the absolut path for the project folder.
     * @param absolutTestGenPath the absolut path for the project folder
     */
    void setAbsolutProjectPath(String absolutTestGenPath);

    /**
     * Returns the absolut path for the testGen folder.
     * @return  absolutTestGenPath the absolut path for the testGen folder
     */
    String getTestGenPath();

    /**
     * Returns the absolut path for the test folder.
     * @return  absolutTestGenPath the absolut path for the test folder
     */
    String getTestPath();

    /**
     * Returns the absolut path for the project folder.
     * @return  absolutTestGenPath the absolut path for the project folder
     */
    String getProjectPath();

    /**
     * Creates a generator model for the given test generation interface.
     * @param tgClass a test generation interface
     * @return a generator model
     */
    GeneratorModel createGeneratorModel(Class<?> tgClass);

    /**
     * Returns the model for the generation information.
     * @return the model for the generation information
     */
    GeneratorModel getModel();

    /**
     * Sets the assertion creator.
     * @param assertionCreator the assertion creator
     */
    void setAssertionCreator(AssertionCreator assertionCreator);

    /**
     * Returns the cut of the tgClass annotation.
     * @return the cut of the tgClass annotation
     */
    Class<?> getGivenCut();

    /**
     * Sets the cut of the tgClass annotation.
     * @param givenCut the cut of the tgClass annotation
     */
    void setGivenCut(Class<?> givenCut);


}
