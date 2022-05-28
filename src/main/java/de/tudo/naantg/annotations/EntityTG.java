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
 * Test generation information for spring boot entity tests.
 */
@Target( { TYPE } )
@Retention( value = RUNTIME )
public @interface EntityTG {

    /**
     * Defines the entity tg class.
     * This is useful, if the name of the entity tg class does not match the standard pattern:
     * classname + TG.
     * @return the entity tg class.
     */
    Class<?> value() default String.class;

    /**
     * If the value is true, the generation of the getter and setter tests is skipped.
     * The default value is false.
     * @return if true, the generation of the getter and setter tests is skipped.
     */
    boolean skipGetterSetterTests() default false;

    /**
     * If the value is true, the generation of the complex constructor tests is skipped.
     * The default value is false.
     * @return if true, the generation of the complex constructor tests is skipped.
     */
    boolean skipComplexConstructorCreation() default false;

}
