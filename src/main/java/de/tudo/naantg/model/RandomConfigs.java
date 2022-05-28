/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.model;

import de.tudo.naantg.annotations.Alphabet;

/**
 * Saves the random configurations.
 */
public class RandomConfigs {

    private int minListSize;

    private int maxListSize;

    private int minStringLength;

    private int maxStringLength;

    private Alphabet[] alphabet;

    private String minValue;

    private String maxValue;


    public int getMinListSize() {
        return minListSize;
    }

    public void setMinListSize(int minListSize) {
        this.minListSize = minListSize;
    }

    public int getMaxListSize() {
        return maxListSize;
    }

    public void setMaxListSize(int maxListSize) {
        this.maxListSize = maxListSize;
    }

    public int getMinStringLength() {
        return minStringLength;
    }

    public void setMinStringLength(int minStringLength) {
        this.minStringLength = minStringLength;
    }

    public int getMaxStringLength() {
        return maxStringLength;
    }

    public void setMaxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
    }

    public Alphabet[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet[] alphabet) {
        this.alphabet = alphabet;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }
}
