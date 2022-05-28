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
import java.util.Optional;

@Service
public class Hauptschein {

    public void init() {

    }

    public String calc(String one, String two) throws ScheinEntityException {
        if (!one.equals(two)) {
            return "init " + one + " and " + two;
        } else {
            throw new ScheinEntityException();
        }
    }

    public ScheinEntity findGoodEntity() {
        ScheinEntity scheinEntity = new ScheinEntity();
        scheinEntity.setName("good");
        scheinEntity.setScheinId(7L);
        scheinEntity.setPassword("dtrfzujhil");
        return scheinEntity;
    }

    public Optional<ScheinEntity> findOptGoodEntity() {
        ScheinEntity scheinEntity = new ScheinEntity();
        scheinEntity.setName("good");
        scheinEntity.setScheinId(7L);
        scheinEntity.setPassword("dtrfzujhil");
        return Optional.of(scheinEntity);
    }

    public Map<String, Integer> createGoodMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("good", 7);
        return map;
    }



}
