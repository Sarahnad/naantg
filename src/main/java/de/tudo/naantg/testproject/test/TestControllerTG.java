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

import de.tudo.naantg.annotations.ControllerTG;
import de.tudo.naantg.annotations.InitState;
import de.tudo.naantg.annotations.MappingValue;
import de.tudo.naantg.annotations.Mocking;

@ControllerTG
public interface TestControllerTG {

    @Mocking("SonnenscheinRepository: findById(OWNER_ID) = ScheinEntity")
    @MappingValue(value = "int OWNER_ID = 1", path = "/other/path/with/{id}")
    void whenCreateThings();

    @Mocking("SonnenscheinRepository: findById(OWNER_ID) = ScheinEntity")
    @MappingValue(value = "int OWNER_ID")
    void whenCreateThings_2();

    @Mocking("SonnenscheinRepository: findById(OWNER_ID) = ScheinEntity;" +
            "ScheinRepository: findById(SCHEIN_ID) = ScheintEntity;" +
            "findSonnenscheinTypes() = List(firstJob)")
    void whenCreateThings_3();

    @Mocking(value = "SonnenscheinRepository: findById(OWNER_ID) = ScheinEntity;" +
            "ScheinRepository: findById(SCHEIN_ID) = ScheintEntity;" +
            "getFirstJob() = firstJob",
            initState = "ScheinJob firstJob")
    void whenCreateThings_4();

    @Mocking(value = "SonnenscheinRepository: findById(OWNER_ID) = ScheinEntity;",
            initState = "Optional<ScheinJob> firstJob")
    void whenCreateThings_5();

}
