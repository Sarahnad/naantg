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

import java.util.Comparator;

/**
 * Comparator for ConstructorHeapElement. Compares the paramSize, primitiveParamSize and objectParamSize.
 * An element with an higher paramSize is greater than an element with a lower paramSize.
 * An element with an higher objectParamSize is greater than an element with an higher primitiveParamSize.
 */
public class ConstructorComparator implements Comparator<ConstructorHeapElement> {

    @Override
    public int compare(ConstructorHeapElement o1, ConstructorHeapElement o2) {
        if (o1.getParamSize() == o2.getParamSize() &&
                o1.getObjectParamSize() == o2.getObjectParamSize()) return 0;
        if (o1.getParamSize() <= o2.getParamSize() &&
                o1.getObjectParamSize() < o2.getObjectParamSize()) return -1;
        if (o1.getPrimitiveParamSize() == o1.getParamSize() &&
                o2.getPrimitiveParamSize() == o2.getParamSize())
            return o1.getParamSize() - o2.getParamSize();
        return 1;
    }

}
