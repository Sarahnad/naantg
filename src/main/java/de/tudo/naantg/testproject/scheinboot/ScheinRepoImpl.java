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

public class ScheinRepoImpl implements ScheinRepository {

    private final ScheinDB scheinDB;

    private Long key;

    public ScheinRepoImpl() {
        this.scheinDB = new ScheinDB();
        this.key = 1L;
    }

    @Override
    public Optional<ScheinEntity> findByScheinId(Object id) {
        Long longId = (Long) id;
        return Optional.of(scheinDB.getEntityMap().get(longId));
    }

    @Override
    public ScheinEntity findById(int id) {
        return null;
    }

    @Override
    public void save(Object entity) throws DataFormatException {
        if (entity instanceof ScheinEntity) {
            scheinDB.getEntityMap().put(key, (ScheinEntity) entity);
            key++;
        }
        else {
            throw new DataFormatException();
        }
    }

    @Override
    public Hauptschein getShineType(boolean hauptschein) {
        return new Hauptschein();
    }


}
