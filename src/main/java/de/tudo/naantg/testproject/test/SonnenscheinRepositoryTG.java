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

import de.tudo.naantg.annotations.RepositoryTG;
import de.tudo.naantg.annotations.TG;

@RepositoryTG
public interface SonnenscheinRepositoryTG {

    void whenFindById_thenReturned_isEmpty();

}
