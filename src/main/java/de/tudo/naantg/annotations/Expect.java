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
 * Spring boot specific checks.
 */
@Target( { TYPE, METHOD } )
@Retention( value = RUNTIME )
public @interface Expect {

    /**
     * Specifies the check type. Default is all.
     * @return the check type
     */
    CheckType[] checkTypes() default CheckType.ALL;

    /**
     * Specifies the status types.
     * @return the status type
     */
    StatusType statusType() default StatusType.OK;

    /**
     * Specifies the result view path.
     * @return the view path
     */
    String getView() default "";

    /**
     * Specifies the redirected view path.
     * @return the redirected view path
     */
    String redirectToView() default "";

    /**
     * Specifies the contained content.
     * @return the contained content
     */
    String contentContains() default "";

    /**
     * Checks if the listed attributes exist or do not exist.
     * <p>Examples:</p>
     * <p>.andExpect(model().attributeExists()): modelAttrExists = "attr1; attr2"</p>
     * <p>.andExpect(model().attributeDoesNotExist()): modelAttrExists = "!attr1; !attr2"</p>
     * @return check if attributes exist
     */
    String modelAttrExists() default "";

    /**
     * Checks the attribute values.
     * <p>Examples:</p>
     * <p>.andExpect(model().attribute()):
     * modelAttr = "attr1 = value1; attr3 = *;"
     * <p>.andExpect(model().attribute("attr", hasProperty("field", is("value")))):
     * modelAttr = "attr2.field = value2; attr4.field = *;"</p>
     * <p>attr5: all = *" (set value = get value)</p>
     * <p>.andExpect(model().attributeHasNoErrors()):
     * modelAttr = "attr1; attr2"</p>
     * @return check of the attribute values
     */
    String modelAttr() default "";

    /**
     * Checks the attribute errors.
     * <p>Examples:</p>
     * <p>.andExpect(model().attributeHasErrors()): modelAttrErrors = "attr1; attr2"</p>
     * <p>.andExpect(model().attributeHasFieldErrors()): modelAttrErrors = "attr1.field"</p>
     * <p>.andExpect(model().attributeHasFieldErrorCode()): modelAttrErrors = "attr1.field = error message"</p>
     * @return check of the attribute errors
     */
    String modelAttrErrors() default "";

    /**
     * Checks the jsonPath:
     * <p>"type: path = value"</p>
     * <p>like "json: $._embedded.guides[0].projects[0] = spring-boot".</p>
     * @return check of the path
     */
    String path() default "";

}
