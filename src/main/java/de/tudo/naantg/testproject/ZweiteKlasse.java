/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject;

/**
 * Simple class that implements an interface and provides simple Methods.
 */
public class ZweiteKlasse implements ErstesInterface {

    /**
     * Prints a text.
     */
    public void simpleMethod() {
        System.out.println(getText());
    }

    @Override
    public String getText() {
        return "simple Method";
    }

    public char getCharS() {
        return 's';
    }

    public short getShortNegTen() {
        return -10;
    }

    public float getAFloat() {
        return -10.5f;
    }

    public long getLongMillion() {
        return 1000*1000;
    }

    public byte getOneByte() {
        return 7;
    }

    public String noText() {
        return null;
    }

}
