/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.test;

import de.tudo.naantg.annotations.AssertState;
import de.tudo.naantg.annotations.TG;

/**
 * Test generation interface to test the methods of
 * {@link de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse.InnereKlasse}.
 *
 * <p> This methods generate simple tests for methods with no
 * parameters and primitive return values like boolean. </p>
 *
 * <p> The basics are explained in {@link ErsteKlasseTG} and {@link ZweiteKlasseTG}. </p>
 *
 * <p> Here are three possibilities shown to check the boolean return type of a method to test:
 * Assert that the return type is true, assert that the return type is not false, assert value given in
 * the annotation. </p>
 */
public interface InnereKlasseTG {

    @TG
    void whenIsNice_thenReturnTrue();

    @TG
    void whenIsNice_thenReturnValue();

    @TG
    @AssertState("true")
    void whenIsNice_thenReturnValue_1();

}
