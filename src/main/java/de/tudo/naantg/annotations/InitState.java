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
 * Information about the pre and initialization conditions of the test case.
 */
@Target( { METHOD, TYPE } )
@Retention( value = RUNTIME )
public @interface InitState {

    // todo: Use patterns like "box.id <- [0,4]+[6,9], person.name <- [A-Z][a-z]*".
    /**
     * Describes the pre-conditions.
     * Multiple conditions can be separated with ";".
     * List the fields, that should be init.
     * The default is empty.
     * <p>Examples:</p>
     * <p>"dataType name = value" or "dataType name" or "dataType name = null</p>
     * <p>"identifier.field.field2 = identifier2"
     * where identifier and identifier2 are defined before</p>
     * @return the pre conditions
     */
    String value() default "";

    /**
     * Describes the used methods to create the pre-condition.
     * Multiple methods can be separated with ";".
     * The default is empty.
     * <p>Examples for method calls:</p>
     * <p>"methodName()" means a method of the test class</p>
     * "this.methodName()" means a method of the class to test</p>
     * <p>"this.field.field2.methodName()"</p>
     * <p>"p1.field.field2.methodName()"
     * where p1 is the first parameter of the method to test</p>
     * <p>"StaticClass.methodName()"</p>
     * <p>"identifier.methodName()" where identifier is defined in value()
     * <p>"p1 = methodName()"
     * where p1 is the first parameter of the method to test</p>
     * <p>"p2.methodName()"
     * where p2 is the second parameter of the method to test</p>
     * @return the methods of the pre condition
     */
    String methods() default "";

    /**
     * Defines the controller parameter values.
     * Use this syntax:
     * "name = value; name2 = value2; name3 = *"
     * with name, name2 and name3 as identifiers of model attributes
     * and value, value2 or * special or random values.
     * @return the controller parameters
     */
    String controllerParams() default "";

    /**
     * Possible types are: ALL_RANDOM, ALL_DEFAULT, SOME_RANDOM, SOME_DEFAULT, MIXED, PERMUTATION and NONE.
     * Default is NONE.
     * Todo: implement! At the moment only ALL_RANDOM and ALL_DEFAULT is implemented
     * @return the type of the initialization action
     */
    InitType type() default InitType.NONE;

    /**
     * Additional annotations.
     * @return additional annotations
     */
    Class<?>[] withAnnotations() default TG.class;

    /**
     * Exclude annotations.
     * @return exclude annotations
     */
    Class<?>[] withoutAnnotations() default TG.class;

    /**
     * All included fields should be instantiated with random values.
     * Only useful with type=MIXED.
     * Default is no field.
     * Todo: implement
     * @return all included fields for random init
     */
    String mixedIncludeRandom() default "";

    /**
     * All included fields should be instantiated with default values.
     * Only useful with type=MIXED.
     * Default is no field.
     * Todo: implement
     * @return all included fields for default init
     */
    String mixedIncludeDefault() default "";

    /**
     * All included fields should be instantiated with values.
     * Only useful with type=SOME_RANDOM or type=SOME_DEFAULT.
     * Default is no field.
     * Todo: implement
     * @return all included fields for init
     */
    String someInclude() default "";

    /**
     * All excluded fields should not be instantiated.
     * Only useful with type=ALL_RANDOM or type=ALL_DEFAULT.
     * Default is no field.
     * Todo: implement
     * @return all excluded fields
     */
    String allExclude() default "";

    /**
     * The init depth for the recursive field init.
     * The default is 3.
     * @return the init depth for the recursive field init
     * Todo: implement
     */
    int initDepth() default 3;

    /**
     * Creates a test case for all parameter constructors.
     * The test cases will be enumerated with "_v1", "_v2" etc.
     * The default is false.
     *
     * @return whether all parameter constructors should be used
     */
    boolean useAllParamConstructors() default false;
}
