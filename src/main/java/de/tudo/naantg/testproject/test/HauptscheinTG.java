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

import de.tudo.naantg.annotations.*;
import org.springframework.data.repository.query.Param;

/**
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.scheinboot.Hauptschein}.
 * <p>The class annotation "@InitiState" with the attribute "WithAnnotations adds additional annotations
 * to all generated test cases of the given class."</p>
 */
@InitState(withAnnotations = Params.class)
public interface HauptscheinTG {


    /**
     * Exception test. Tests the correct creation of the Exception assertion.
     */
    @TG
    @Params("p1 = one, p2 = one")
    void whenCalc_thenReturnException();

    /**
     * Adds additional annotations to the generated test case.
     */
    @TG
    @InitState(withAnnotations = {Mocking.class, InitState.class})
    void whenFindGoodEntity_anno2();

    /**
     * Adds no additional annotation to the generated test case.
     */
    @TG
    @InitState(withoutAnnotations = Params.class)
    void whenFindGoodEntity_no_anno();

    /**
     * Extended Assertion test. Tests the attributes of the returned value.
     */
    @TG
    @AssertState(returned = "name = good, password = dtrfzujhil, scheinId = 7")
    void whenFindGoodEntity();

    /**
     * Extended Assertion test for the Optional type. Tests the attributes of the returned Optional.
     */
    @TG
    @AssertState(returned = "name = good, password = dtrfzujhil, scheinId = 7")
    void whenFindOptGoodEntity();

    /**
     * Extended Assertion test for the Map type. Tests the value of the returned Map.
     */
    @TG
    @AssertState(returned = "good = 7")
    void whenCreateGoodMap();

}
