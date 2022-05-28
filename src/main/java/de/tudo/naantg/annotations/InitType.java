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
 * Defines the init conditions.
 */
public enum InitType {
    RANDOM, DEFAULT, ALL_RANDOM, ALL_DEFAULT, SOME_RANDOM, SOME_DEFAULT, MIXED, PERMUTATION, NONE;
}
