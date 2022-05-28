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

import de.tudo.naantg.annotations.Mocking;
import de.tudo.naantg.annotations.ServiceTG;
import de.tudo.naantg.annotations.TG;

/**
 * Example for service class with Mocking.
 */
@ServiceTG
public interface ScheinServiceTG {

    @Mocking("ScheinRepository: save(notNull) = Exception")
    void whenCreateScheinEntity_thenReturnException();

    @TG
    @Mocking(value = "ScheinRepository: getShineType(true) = shineType",
            initState = "Hauptschein shineType")
    void whenActWithShineTyps_thenReturnTrue();

}
