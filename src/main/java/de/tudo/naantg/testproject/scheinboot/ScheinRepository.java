/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.scheinboot;

import java.util.Optional;
import java.util.zip.DataFormatException;

public interface ScheinRepository {

    Optional<ScheinEntity> findByScheinId(Object id);

    ScheinEntity findById(int id);

    void save(Object entity) throws DataFormatException;

    Hauptschein getShineType(boolean hauptschein);

}
