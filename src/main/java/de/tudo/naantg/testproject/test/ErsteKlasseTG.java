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
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.ErsteKlasse}.
 *
 * <p> This methods generate simple tests for methods with no
 * parameters and primitive or void return values. </p>
 *
 * <p> The key word "when" introduces the method to test.
 * For example: to test the method "public void act()"
 * the method should start with "whenAct". </p>
 *
 * <p> The key word "then" introduces the assertion part.
 * It can be followed by the key word "Return" or "Returned" with a value
 * or with "Value" (if the concrete value is not important) in the first case,
 * or with additional key words like "_isGreater", or "is_Smaller" in the second case.
 * Values with dots like "5.6" are writeable with "p" like "5p6". </p>
 * (Todo: change p to d)
 *
 * <p> All words or numbers that are no key words or do not match
 * the defined regular expressions are ignored like "NoError" or
 * "_1" at the end after "Value". </p>
 *
 * <p> With the annotation "AssertState" it is possible to define directly the
 * expected return value. </p>
 */
public interface ErsteKlasseTG {

    @TG
    public void whenAct_thenNoError();

    @TG
    public void whenGetFive_thenReturn5();

    @TG
    public void whenGetFive_thenReturned_isGreater4();

    @TG
    public void whenGetFive_thenReturned_isSmaller6();

    @TG
    public void whenGetFivePointSix_thenReturn5p6();

    @TG
    public void whenGetFivePointSix_thenReturned_isGreater5();

    @TG
    public void whenGetFivePointSix_thenReturnValue();

    @TG
    @AssertState("5.6")
    public void whenGetFivePointSix_thenReturnValue_1();

    @TG
    public void whenGetFive_thenReturnValue();

    @TG
    @AssertState("5")
    public void whenGetFive_thenReturnValue_1();

}
