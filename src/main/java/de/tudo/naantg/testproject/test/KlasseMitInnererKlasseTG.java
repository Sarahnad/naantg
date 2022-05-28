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

import java.util.LinkedList;

/**
 * Test generation interface to test the methods of
 * {@link de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse}.
 *
 * <p> This methods generate tests for methods with collection
 * parameters and return types.
 * Handled are actual the following types: Collection, List, ArrayList, LinkedList and arrays. </p>
 *
 * <p> The basics are explained in {@link ErsteKlasseTG}, {@link ZweiteKlasseTG} and {@link NochEineKlasseTG}. </p>
 *
 * <p> For collections there are several "Returned" Statements implemented. It is possible to
 * check the size or length, the containing of elements and the value of specific elements.
 * It is possible to define nearly all statements by method name or annotation. </p>
 *
 * <p> The "Params" annotation has the "type" value to define the collection type. </p>
 * <p> For the use of the "AssertState" annotation values please refer their given explanations. </p>
 *
 * <p> Additional key words in the method name are "not" and "neg" for negation and negative values,
 * "and" and "or" for the connection of multiple conditions, "size", "contains" and "get" for the different
 * types of assertion statements. </p>
 *
 * <p> With the "Params" annotation it possible to define a value list. </p>
 *
 * <p> Additional it is possible to use a set and get pattern like "whenSetField_thenGetField" with "_hasValues"
 * (the field is not null of empty) or "hasEqualValues" (the field contains the set values). </p>
 */
public interface KlasseMitInnererKlasseTG {

    @TG
    void whenGetFirstValue_thenReturn0();

    @TG
    @Params(type = LinkedList.class)
    void whenGetFirstValue_thenReturn0_1();

    @TG
    void whenGetNames_thenReturnList();

    @TG
    void whenGetNames_thenReturned_size_is10();

    @TG
    @AssertState(returnedSize = "10")
    void whenGetNames_test1();

    @TG
    void whenGetNames_thenReturned_size_isGreater9_and_size_isSmaller11();

    @TG
    @AssertState(returnedSize = "> 9 and < 11")
    void whenGetNames_test2();

    @TG
    void whenGetNames_thenReturned_size_is10_or_is20();

    @TG
    @AssertState(returnedSize = "10 or 20")
    void whenGetNames_test3();

    @TG
    void whenGetNames_thenReturned_size_isNot5();

    @TG
    @AssertState(returnedSize = "5 or 20 or 10")
    void whenGetNames_test33();

    @TG
    @AssertState(returnedSize = "!= 5")
    void whenGetNames_test34();

    @TG
    void whenGetNames_thenReturned_get_5_isName5();

    @TG
    @AssertState(returned = "[5] = Name5")
    void whenGetNames_test4();

    @TG
    void whenGetNames_thenReturned_get6_is_Name6_and_get_9_is_Name9();

    @TG
    @AssertState(returned = "[6] = Name6, [9] = Name9")
    void whenGetNames_test5();

    @TG
    void whenGetNames_thenReturned_get1_isName1_or_get1_isName2();

    @TG
    @AssertState(returned = "[1] = Name1 or Name2")
    void whenGetNames_test6();

    @TG
    void whenGetNames_thenReturned_contains_Name0();

    @TG
    @AssertState(returnedContains = "Name0")
    void whenGetNames_test7();

    @TG
    void whenGetList_thenReturned_isEmpty();

    @TG
    @AssertState(returnedIsEmpty = true)
    void whenGetList_test1();

    @TG
    void whenGetNumbers_thenReturned_contains_8();

    @TG
    void whenGetNumbers_thenReturned_notContains_neg7();

    @TG
    @AssertState(returnedContains = "8")
    void whenGetNumbers_test1();

    @TG
    void whenGetDoubles_thenReturned_contains_5p5();

    @TG
    void whenGetDoubles_thenReturned_notContains_neg5p5();

    @TG
    @AssertState(returnedContains = "5.5")
    void whenGetDoubles_test1();

    @TG
    void whenGetBooleans_thenReturned_get4_isTrue_and_get7_isFalse();

    @TG
    @AssertState(returned = "[4], ![7]")
    void whenGetBooleans_test1();

    @TG
    void whenGetBooleans_thenReturned_get5_isTrue_or_get7_isFalse();

    @TG
    @AssertState(returned = "[5] or ![7]")
    void whenGetBooleans_test2();

    @TG
    void whenGetBooleans_thenReturned_get8_isTrue();

    @TG
    @AssertState(returned = "[8]")
    void whenGetBooleans_test3();

    @TG
    void whenGetIntArray_thenReturned_size_is7_and_contains_7();

    @TG
    @AssertState(returnedSize = "7", returnedContains = "7")
    void whenGetIntArray_test1();

    @TG
    void whenGetIntArray_thenReturned_get2_is3();

    @TG
    @AssertState(returned = "[2] = 3")
    void whenGetIntArray_test2();

    @TG
    void whenGetIntArray_thenReturned_get2_isNot_neg3();

    @TG
    @Params("[1,4,6,3]")
    void whenIsPositive_thenReturnTrue();

    @TG
    @Params("[1,4,-6,3]")
    void whenIsPositive_thenReturnFalse();

    @TG
    void whenSetSeven_thenGetSeven_hasValues();

    @TG
    @Params("[2,2,3,3,4,4,5]")
    void whenSetSeven_withValues_thenGetSeven_hasEqualValues();

    @TG
    void whenSetSeven_with_7_values_thenGetSeven_hasEqualValues();

    @TG
    void whenGetSeven_thenReturned_size_is7();

    //@TG todo: generated list is empty
    void whenSetAnimals_thenGetAnimals_hasValues();

    @TG
    void whenSetAnimals_thenGetAnimals_hasEqualValues();

    @TG
    void whenSetIndex_thenGetIndex_hasEqualValue();

    @TG
    void whenSetIndex_with55_thenGetIndex_hasValue55();

    @TG
    void whenGetCollection_with5_thenReturned_size_is5();

    @TG
    @Params("[1,2,3,4]")
    void whenIsEvenSize_thenReturnTrue();

    @TG
    @Params("[1,2,3,4,5]")
    void whenIsEvenSize_thenReturnFalse();

}
