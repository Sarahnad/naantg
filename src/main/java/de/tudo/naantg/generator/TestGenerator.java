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


import de.tudo.naantg.model.GeneratorModel;

/**
 * Creates tests on the basis of the test generation methods in the test generation interfaces.
 */
public interface TestGenerator {

    /**
     * Generates tests for all methods in the tgClass interface that are annotated with @TG.
     * The de.tudo.naantg.model should contain all necessary information for the generation process.
     * The name of the tGClass should contain the name of the class under test.
     * Example: if "ErsteKlasse" is the name of the class under test, the name of the tgClass should be "ErsteKlasseTG".
     * @param tgClass the test generation interface
     * @param generatorModel the GeneratorModel that contains information for the generation process
     * @return true if the generation was successful
     */
    boolean generateTests(Class<?> tgClass, GeneratorModel generatorModel);


}
