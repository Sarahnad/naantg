/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.test;

import de.tudo.naantg.annotations.Alphabet;
import de.tudo.naantg.annotations.RandomConfigs;
import de.tudo.naantg.annotations.RepositoryTG;

@RepositoryTG
@RandomConfigs(alphabet = {Alphabet.BIG_LETTERS, Alphabet.LITTLE_LETTERS},
        minStringLength = 5,
        maxStringLength = 7)
public interface ScheinRepositoryTG {

    void whenFindByScheinId_withScheinEntity_thenReturnNull();

    void givenScheinEntity_whenFindByScheinId_thenReturnNull();

    void whenFindByScheinId_withScheinEntity_thenReturnValue();

    void givenScheinEntity_whenFindByScheinId_thenReturnValue();

    void whenFindByScheinId_withScheinEntity_thenReturnNotNull();

    void givenScheinEntity_whenFindByScheinId_thenReturnNotNull();

    void whenFindByScheinId_withScheinEntity_thenReturned_isEmpty();

    void givenScheinEntity_whenFindByScheinId_thenReturned_isEmpty();

}
