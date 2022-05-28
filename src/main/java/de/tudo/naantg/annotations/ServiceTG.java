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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Test generation information for spring boot service classes.
 */
@Target( { TYPE } )
@Retention( value = RUNTIME )
public @interface ServiceTG {

    /**
     * Defines the service tg class.
     * This is useful, if the name of the service tg class does not match the standard pattern:
     * classname + TG.
     * @return the service tg class.
     */
    Class<?> value() default String.class;

}
