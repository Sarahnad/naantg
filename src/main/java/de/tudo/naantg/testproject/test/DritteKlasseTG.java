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

import de.tudo.naantg.annotations.InitState;
import de.tudo.naantg.annotations.InitType;
import de.tudo.naantg.annotations.RandomConfigs;
import de.tudo.naantg.annotations.TG;

/**
 * Test generation interface to test the methods of {@link de.tudo.naantg.testproject.DritteKlasse}.
 *
 * <p> This methods generate tests for methods with object
 *  * parameters and return types. </p>
 *
 *  <p> The basics are explained in {@link ErsteKlasseTG}, {@link ZweiteKlasseTG}, {@link NochEineKlasseTG},
 *  {@link KlasseMitInnererKlasseTG} and {@link InhaltTG}. </p>
 *
 *  <p> With the "InitState" annotation it is possible to define whether and how the objects should be
 *  initialized. /p>
 *
 *  <p> For lists, collections or arrays with multi-level initialisation or objects with more than three field
 *  initializations there will be generated private help methods. </p>
 */
public interface DritteKlasseTG {

    @TG
    void whenInit_thenReturnTrue();

    @TG
    void whenGetKomplex_thenReturnNull();

    @TG
    void whenSetKomplex_thenGetKomplex_hasEqualValue();

    @TG
    void whenSetKomplex2_thenGetKomplex2_hasEqualValue();

    @TG
    void whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue();

    @TG
    @InitState(type = InitType.ALL_DEFAULT)
    void whenDoComplex_thenReturnNull();

    //@TG
    @RandomConfigs(minListSize = 2, maxListSize = 2)
    @InitState(type = InitType.ALL_RANDOM)
    void whenDoComplex_thenReturnValue();

    @TG
    @InitState(type = InitType.ALL_DEFAULT)
    void whenDoComplexWithPerson_thenReturnNull();

    @TG
    @InitState(methods = "this.initBuy()")
    void whenCanBuyBox_thenReturnTrue();

    @TG
    void givenThisInitBuy_whenCanBuyBox_thenReturnTrue();

    @TG
    void whenCanBuyBox_thenReturnFalse();

    @TG
    @InitState(methods = "initBox(this)")
    void whenCanBuyBox_thenReturnFalse_2();

    @TG
    void givenTestInitBox_with_this_whenCanBuyBox_thenReturnFalse();

    @TG
    @InitState(methods = "Statistik.work()")
    void whenCanBuyBox_thenReturnFalse_3();

    @TG
    void givenStaticStatistik_work_whenCanBuyBox_thenReturnFalse();

    @TG
    @InitState(methods = "initBox(this); Statistik.work()")
    void whenCanBuyBox_thenReturnFalse_4();

    //@TG
    //void givenTestInitBox_and_givenStaticStatistikWork_whenCanBuyBox_thenReturnFalse();

    @TG
    @InitState(methods = "p1 = this.createPerson()")
    void whenDoComplexWithPerson_thenReturnValue_simple();

    @TG
    void given_param_ofThisCreatePerson_whenDoComplexWithPerson_thenReturnValue_simple();

    @TG
    @InitState(methods = "this = initialBoxes()")
    void whenCanBuyBox_thenReturnFalse_5();

    @TG
    void given_this_ofTestInitialBoxes_whenCanBuyBox_thenReturnFalse();

    @TG
    @InitState(methods = "p1.init()")
    void whenDoComplexWithPerson_thenReturnNull_komplex();

    @TG
    void givenParamInit_whenDoComplexWithPerson_thenReturnNull();

    @TG
    @InitState(value = "int a = 3; int b = 4", methods = "p1.init(a, b)")
    void whenDoComplexWithPerson_thenReturnNull_komplex_2();

    @TG
    @InitState(value = "List<Box> boxes = null", methods = "createPersonWithBoxes(boxes)")
    void whenDoComplexWithPerson_thenReturnNull_2();

    @TG
    @InitState("this.points = 50; this.aMillion = 100000")
    void whenCanBuyBox_thenReturnTrue_2();

    @TG
    @InitState("Person p; List ps = [p, p]; p.name = Jan*a; Komplex k; this.komplex = k; this.komplex.persons = ps")
    void whenCanBuyBox_thenReturnFalse_6();

}
