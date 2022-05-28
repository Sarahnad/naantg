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

import java.lang.reflect.Method;

/**
 * Selects all information for the assertions generation.
 */
public interface AssertionCreator {

    /**
     * Selects the information about the assertions.
     * @param method the test case
     */
    void calculateAssertions(Method method);

    /**
     * Sets the model for the generation information.
     * @param model the model for the generation information
     */
    void setModel(GeneratorModel model);

}
