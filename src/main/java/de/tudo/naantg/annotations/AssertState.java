/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Information about the assertions of the test case.
 * For the key word "thenReturn" in the method name: simple write the expected string.
 */
@Target( { METHOD } )
@Retention( value = RUNTIME )
public @interface AssertState {

    /**
     * Defines the expected return value.
     * @return the expected return value
     */
    String value() default "";

    /**
     * Defines a rule pattern: "inputs -> condition for output"
     * @return a rule pattern for the inputs and the output
     */
    String rule() default "";

    /**
     * Defines the expected content of the returned objects or list elements of the method to test.
     * <P>For lists you can write: "[5] = valueX".</P>
     * <P>It is possible to list several elements like "[0] = elem1, [1] = elem2, [2] = elem3".</P>
     * <P>You can also use "or" like: "[5] = 6 or 7".</P>
     * <P>It is possible to write: "[5] != 6", "[6] < 7", "[8] > 9".</P>
     * <P>For boolean lists you can write: "[5]" for true and "![5]" for false.</P>
     * @return the expected content of the returned objects list elements.
     */
    String returned() default "";

    /**
     * Defines the expected size of the returned value of the method to test.
     * <P>It is possible to use "or" like "10 or 20".</P>
     * <P>You can also use "!=", "<" or ">" and "and" like "< 20 and > 10".</P>
     * @return the expected size of the returned value
     */
    String returnedSize() default "";

    /**
     * Defines the expected content of the returned collection of the method to test.
     * <P>It is possible to list several elements like "elem1, elem2, elem3".</P>
     * <P>For elements that should not be in the collection use "!" like "!elem1, elem2, !elem3"
     * if elem1 and elem3 are no elements of the list but elem2 is.</P>
     * @return the expected content of the returned collection
     */
    String returnedContains() default "";

    /**
     * Defines if the returned collection is empty or not. The default value is false.
     * @return if the returned collection is empty or not
     */
    boolean returnedIsEmpty() default false;
}
