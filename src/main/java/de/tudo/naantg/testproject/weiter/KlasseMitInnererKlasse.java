/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.weiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Contains an inner static class, methods with list parameters or list return values
 * and prints nice things.
 */
public class KlasseMitInnererKlasse {

    /**
     * array for 7 integers
     */
    private int[] seven;

    /**
     * index for saving the actual array position
     */
    private int index;

    /**
     * list of animals
     */
    private List<String> animals;

    /**
     * Prints the value of isNice() of InnereKlasse.
     */
    public void doThings() {
        InnereKlasse innereKlasse = new InnereKlasse();
        System.out.println("Is all nice: " + innereKlasse.isNice());
    }

    /**
     * Returns the first value of the numbers.
     * If the given list is null or empty it returns 0.
     * @param numbers a list with Integers
     * @return the first list entry or 0
     */
    public int getFirstValue(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) return 0;
        return numbers.get(0);
    }

    /**
     * Returns a list with ten elements with content "Name i".
     * @return a list with ten elements
     */
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            names.add("Name" + i);
        }
        return names;
    }

    /**
     * Returns a list with ten numbers 0-9.
     * @return a list with ten elements
     */
    public List<Integer> getNumbers() {
        List<Integer> names = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            names.add(i);
        }
        return names;
    }

    /**
     * Returns a list with ten doubles: 1.0, 1.5, 2.0, 2.5 ...
     * @return a list with ten elements
     */
    public List<Double> getDoubles() {
        List<Double> names = new ArrayList<>();
        double d = 1.0;
        for (int i = 0; i < 10; i++) {
            names.add(d);
            d += 0.5;
        }
        return names;
    }

    /**
     * Returns a list with ten booleans: true, false, true...
     * @return a list with ten elements
     */
    public List<Boolean> getBooleans() {
        List<Boolean> names = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                names.add(true);
            }
            else {
                names.add(false);
            }
        }
        return names;
    }

    /**
     * Returns an int array with seven numbers 1-7.
     * @return an array with seven elements
     */
    public int[] getIntArray() {
        return new int[] {1, 2, 3, 4, 5, 6, 7};
    }

    /**
     * Returns an empty list.
     * @return an empty list
     */
    public List<String> getList() {
        return new ArrayList<>();
    }

    /**
     * Checks if all elements of positives are positive.
     * @param positives array of integers
     * @return return true if all elements are positive
     */
    public boolean isPositive(int[] positives) {
        for (int i : positives) {
            if (i < 0) return false;
        }
        return true;
    }

    /**
     * If seven is null it returns a new list with 1-7.
     * @return returns 7 integers
     */
    public int[] getSeven() {
        if (seven == null) return new int[]{1, 2, 3, 4, 5, 6, 7};
        return seven;
    }

    /**
     * If the given list is to short it will be filled with values.
     * If it is to long, the last values will be removed.
     * @param seven 7 integers
     */
    public void setSeven(int[] seven) {
        if (seven == null) this.seven = new int[]{1, 2, 3, 4, 5, 6, 7};
        else if (seven.length < 7) {
            this.seven = new int[7];
            System.arraycopy(seven,0, this.seven, 0, seven.length);
            for (int i = seven.length; i < 7; i++) {
                this.seven[i] = i + 1;
            }
        }
        else if (seven.length > 7) {
            this.seven = new int[7];
            System.arraycopy(seven, 0, this.seven, 0, 7);
        }
        else {
            this.seven = seven;
        }
    }

    /**
     *
     * @return the actual array position.
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index the actual index position
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     *
     * @return list of animals
     */
    public List<String> getAnimals() {
        return animals;
    }

    /**
     *
     * @param animals list of animals
     */
    public void setAnimals(List<String> animals) {
        this.animals = animals;
    }

    /**
     * Returns a collection with elements of the given count size:
     * 0.0, 1.1, 2.2, ...
     * @param count the size of the collection
     * @return a collection with elements of the given count size
     */
    public Collection<Double> getCollection(int count) {
        Collection<Double> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i + 0.1 * i);
        }
        return list;
    }

    /**
     * Returns true if the size of the collection is even.
     * @param collection a collection
     * @return true if the size is even
     */
    public boolean isEvenSize(Collection<Byte> collection) {
        return (collection.size() % 2 == 0);
    }

    /**
     * An inner static class that is nice.
     */
    public static class InnereKlasse {

        /**
         * Checks if it is nice.
         * @return true
         */
        public boolean isNice() {
            return true;
        }

    }

}
