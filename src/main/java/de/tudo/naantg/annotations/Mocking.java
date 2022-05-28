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
 * Information about the Mocking.
 */
@Target( { TYPE, METHOD } )
@Retention( value = RUNTIME )
public @interface Mocking {

    /**
     * Describe the mocking.
     * Several descriptions are separated with ";".
     * Describe it like this:
     * "UserService: createUser(notNull) = Exception".
     * First part only needed if there are mocked classes with
     * identical method signatures.
     * @return the mocking
     */
    String value() default "";

    /**
     * Defines given objects like:
     * "dataType name = value" or "dataType name"
     * @return defines given objects
     */
    String initState() default "";

    /**
     * Like init all default but only for the init state in mocking with depth 1.
     * @return init fields with default values
     */
    boolean initFieldsWithDefault() default false;

    /**
     * Like init all random but only for the init state in mocking with depth 1.
     * @return init fields with random values
     */
    boolean initFieldsWithRandom() default false;

}
