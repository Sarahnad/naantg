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
 * For additional mocks.
 */
@Target( { TYPE } )
@Retention( value = RUNTIME )
public @interface AddMocks {

    /**
     * List of additional classes to mock that are not automatically detected.
     * @return list of additional mock classes
     */
    Class<?>[] value();

}
