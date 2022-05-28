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
import java.util.ArrayList;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Information about the parameters of the method to test.
 */
@Target( { METHOD } )
@Retention( value = RUNTIME )
public @interface Params {

    /**
     * Defines the parameter values.
     * Use this syntax: "p1 = x, p2 = hello, p4 = 3".
     * So the first parameter is set to 'x', the second to "hello" and the fourth to 3.
     * For all other parameters it is used a random value.
     * Or write the value alone like "val" if there is only one parameter.
     * @return the parameter values
     */
    String value() default "";

    /**
     * Defines the data types of the parameters.
     * For example: if the parameter is of type list then you can force the
     * generator to choose the given list type like LinkedList.
     * Default for lists is ArrayList.
     * @return the data types to use
     */
    Class<?>[] type() default ArrayList.class;

    /**
     * If the parameter is an object you can force the generator to use a specific constructor.
     * Use this syntax: "p3 = constructorOfObject(typeX, typeY)"
     * to define that the third parameter should be constructed with a constructor which excepts
     * two parameters of the given types.
     * Separate more definitions with ";".
     * @return specific constructors
     */
    String constructor() default "";

}
