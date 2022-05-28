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
 * For specific values of the request.
 */
@Target( { METHOD } )
@Retention( value = RUNTIME )
public @interface MappingValue {

    /**
     * Defines the mapping value replacement.
     * Use this pattern: "dataType identifier = value" or
     * "dataType identifier" if the value can be 0 (default) or random (initType = none / random).
     * @return the mapping value replacement
     */
    String value() default "";

    /**
     * Defines the specific path value.
     * @return the specific path value
     */
    String path() default "";

}
