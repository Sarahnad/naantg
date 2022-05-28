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

/**
 * Another class that contains methods with simple parameters.
 */
public class NochEineKlasse {

    /**
     * Calculates the sum of x and y.
     * @param x first int
     * @param y second int
     * @return the sum
     */
    public int sum(int x, int y) {
        return x+y;
    }

    /**
     * Returns 1 if one is true, otherwise 2.
     * @param one true for 1 and false for 2
     * @return 1 if one is true, otherwise 2
     */
    public int getOneOrTwo(boolean one) {
        if (one) return 1;
        else return 2;
    }

    /**
     * Returns true if one is equal "one" ignoring the case.
     * @param one a word
     * @return true if one is "one", otherwise false
     */
    public boolean isOne(String one) {
        if (one == null) return false;
        return one.equalsIgnoreCase("one");
    }

    /**
     * Joins the parameters.
     * @param c a char
     * @param b a byte
     * @param s a short
     * @param l a long
     * @param d a double
     * @param f a float
     * @return "<" + c + ", " + b + ", " + s + ", " + l + ", " + d + ", " + f + "> :D"
     */
    public String haveFun(char c, byte b, short s, long l, double d, float f) {
        return "<" + c + ", " + b + ", " + s + ", " + l + ", " + d + ", " + f + "> :D";
    }

}
