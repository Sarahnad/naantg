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
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.ZweiteKlasse}.
 *
 * <p> This methods generate simple tests for methods with no
 * parameters and primitive different data types like String, char, float, long and byte. </p>
 *
 * <p> Most of the possibilities are explained in {@link ErsteKlasseTG}. </p>
 *
 * <p> With the key word "ReturnNull" it is possible to check if the return value is null. </p>
 * <p> With "ReturnNeg" it is possible to check if the returned value is negative. </p>
 */
public interface ZweiteKlasseTG {

    @TG
    @AssertState("simple Method")
    void whenGetText_thenReturnValue();

    @TG
    void whenGetText_thenReturnValue_1();

    @TG
    void whenGetCharS_thenReturn_s();

    @TG
    @AssertState("s")
    void whenGetCharS_thenReturnValue();

    @TG
    void whenGetCharS_thenReturnValue_1();

    @TG
    void whenGetShortNegTen_thenReturnNeg10();

    @TG
    void whenGetAFloat_thenReturnNeg10p5();

    @TG
    void whenGetLongMillion_thenReturn1000000();

    @TG
    void whenGetOneByte_thenReturn7();

    @TG
    void whenGetAFloat_thenReturnValue();

    @TG
    @AssertState("-10.5")
    void whenGetAFloat_thenReturnValue_1();

    @TG
    void whenNoText_thenReturnNull();

}
