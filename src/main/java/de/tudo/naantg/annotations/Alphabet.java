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

/**
 * Specifies possible alphabets for random string generation.
 */
public enum Alphabet {

    /**
     * [a-z]
     */
    LITTLE_LETTERS,

    /**
     * [A-Z]
     */
    BIG_LETTERS,

    /**
     * [0-9]
     */
    NUMBERS,

    /**
     * [!§$%&/()=?*+-_]
     */
    SYMBOLS,

    /**
     * [ \t]
     */
    SPACES,

    /**
     * All letters, numbers, symbols and spaces.
     */
    ALL

}
