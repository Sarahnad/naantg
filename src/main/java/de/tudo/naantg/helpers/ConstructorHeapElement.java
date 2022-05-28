/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.helpers;

import java.lang.reflect.Constructor;

/**
 * Constructor container for using in a PriorityQueue.
 */
public class ConstructorHeapElement {

    /**
     * the size of constructor parameters
     */
    private final int paramSize;

    /**
     * the size of primitive constructor parameters
     */
    private final int primitiveParamSize;

    /**
     * the size of object parameters
     */
    private final int objectParamSize;

    /**
     * the constructor
     */
    private Constructor<?> constructor;

    /**
     * Creates a container for the given constructor for use in a PriorityQueue.
     * @param constructor a constructor
     */
    public ConstructorHeapElement(Constructor<?> constructor) {
        this.constructor = constructor;
        this.paramSize = constructor.getParameterCount();
        this.primitiveParamSize = Scanner.getPrimitiveParamCount(constructor.getParameterTypes());
        this.objectParamSize = paramSize - primitiveParamSize;
    }

    /**
     * creates a container for the constructor parameter information without the constructor.
     * @param paramSize the parameter size of the constructor
     * @param primitiveParamSize the primitive parameter size of the constructor
     */
    public ConstructorHeapElement(int paramSize, int primitiveParamSize) {
        this.paramSize = paramSize;
        this.primitiveParamSize = primitiveParamSize;
        this.objectParamSize = paramSize - primitiveParamSize;
    }

    /**
     *
     * @return the constructor
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     *
     * @return the parameter size of the constructor
     */
    public int getParamSize() {
        return paramSize;
    }

    /**
     *
     * @return the primitive parameter size
     */
    public int getPrimitiveParamSize() {
        return primitiveParamSize;
    }

    /**
     *
     * @return the object parameter size
     */
    public int getObjectParamSize() {
        return objectParamSize;
    }

}
