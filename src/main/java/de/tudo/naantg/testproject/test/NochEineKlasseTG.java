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
import de.tudo.naantg.annotations.Params;
import de.tudo.naantg.annotations.TG;

/**
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.weiter.NochEineKlasse}.
 *
 * <p> This methods generate simple tests for methods with primitive
 * parameters and return types. </p>
 *
 * <p> The basics are explained in {@link ErsteKlasseTG} and {@link ZweiteKlasseTG}. </p>
 *
 * <p> With the AssertState annotation it is possible to define a rule for the assertion like
 * "x, y -> x+y" (the return value should be the same as the sum of the parameters).
 * For Strings in the rule statement it is possible to use a simplification like
 * "%str(<%c, %b, %s, %l, %d, %f> :D)". All strings in "%str()" are interpreted as String excepts the
 * strings that starts with %. </p>
 *
 * <p> The key word "with" introduces the parameter value (actual for one parameter).
 * For example: "withTrue" means, that the boolean parameter of the method to test should be "true". </p>
 *
 * <p> With the annotation "@Params" it is also possible to define the parameter values.
 * Several parameters are separated with commas.
 * To define only specific parameters it is possible to write "p1 = value, p4 = anotherValue".
 * p1 is the first parameter and p4 the fourth.
 * The type of the defined value is determined by the signatur of the method to test.
 * If more parameters are listed as the signatur allows, only the first are taken and the last are ignored. </p>
 */
public interface NochEineKlasseTG {

    @TG
    @AssertState(rule="x, y -> x+y")
    public void whenSum_thenReturnValue();

    @TG
    public void whenGetOneOrTwo_withTrue_thenReturn1();

    @TG
    public void whenGetOneOrTwo_withFalse_thenReturn2();

    @TG
    public void whenIsOne_thenReturnFalse();

    @TG
    public void whenIsOne_withOne_thenReturnTrue();

    @TG
    @Params("one")
    public void whenIsOne_thenReturnTrue();

    @TG
    void whenIsOne_withNull_thenReturnFalse();

    @TG
    @AssertState(rule = "c, b, s, l, d, f -> " +
            "\"<\" + c + \", \" + b + \", \" + s + \", \" + l + \", \" + d + \", \" + f + \"> :D\"")
    public void whenHaveFun_thenReturnValue();

    @TG
    @Params("p1 = 4, p2 = 6")
    public void whenSum_thenReturn10();

    @TG
    @Params("4, 6")
    public void whenSum_thenReturn10_2();

    @TG
    @Params("4, 6, 7")
    public void whenSum_thenReturn10_3();

    @TG
    @Params("false")
    public void whenGetOneOrTwo_thenReturn2();

    @TG
    @Params("p2 = 8, p3 = 1, p5 = 2.0")
    @AssertState(rule = "c, b, s, l, d, f -> %str(<%c, %b, %s, %l, %d, %f> :D)")
    public void whenHaveFun_thenReturnValue_1();

    @TG
    @Params("8")
    @AssertState(rule = "c, b, s, l, d, f -> %str(<%c, %b, %s, %l, %d, %f> :D)")
    public void whenHaveFun_thenReturnValue_2();

    @TG
    @Params("p1 = 8, p4 = 1, p6 = 2.0")
    @AssertState(rule = "c, b, s, l, d, f -> %str(<%c, %b, %s, %l, %d, %f> :D)")
    public void whenHaveFun_thenReturnValue_3();

}
