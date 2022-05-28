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

import java.util.HashMap;
import java.util.Map;

public class ScheinDB {

    private Map<Long, ScheinEntity> entityMap;

    public ScheinDB() {
        entityMap = new HashMap<>();
    }

    public Map<Long, ScheinEntity> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<Long, ScheinEntity> entityMap) {
        this.entityMap = entityMap;
    }
}
