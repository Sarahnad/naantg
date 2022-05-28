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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Configurations for random parameters.
 */
@Target( { TYPE, METHOD } )
@Retention( value = RUNTIME )
public @interface RandomConfigs {

    /**
     * Configures the minimal list size.
     * @return the minimal list size.
     */
    int minListSize() default 0;

    /**
     * Configures the maximal list size.
     * @return the maximal list size.
     */
    int maxListSize() default 10;

    /**
     * Configures the minimal string length.
     * @return the minimal string length.
     */
    int minStringLength() default 0;

    /**
     * Configures the maximal string length.
     * @return the maximal string length.
     */
    int maxStringLength() default 20;

    /**
     * Configures the alphabet for the string generation.
     * @return the alphabet for the string generation.
     */
    Alphabet[] alphabet() default Alphabet.ALL;

    /**
     * Configures the minimal value to generate.
     * @return the minimal value to generate.
     */
    String minValue() default "";

    /**
     * Configures the maximal value to generate.
     * @return the maximal value to generate.
     */
    String maxValue() default "";


}
