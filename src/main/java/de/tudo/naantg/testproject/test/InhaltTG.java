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

import de.tudo.naantg.annotations.TG;

/**
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.weiter.Inhalt}.
 *
 * <p> This methods generate tests for methods with enum
 * parameters and return types. </p>
 *
 * <p> The basics are explained in {@link ErsteKlasseTG}, {@link ZweiteKlasseTG}, {@link NochEineKlasseTG}
 * and {@link KlasseMitInnererKlasseTG}. </p>
 *
 * <p> The set and get pattern can be used with the "with" key word to define the size of values. </p>
 */
public interface InhaltTG {

    @TG
    void whenSetContent_thenGetContent_hasEqualValue();

    @TG
    void whenSetContent_with_2_values_thenGetContent_hasEqualValues();

    void whenSetContent_thenNoError();

}
