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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ScheinService {

    @Autowired
    private ScheinRepository scheinRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createScheinEntity(ScheinEntity scheinEntity) throws ScheinEntityException {
        scheinEntity.setPassword(passwordEncoder.encode(scheinEntity.getPassword()));
        try {
            scheinRepository.save(scheinEntity);
        } catch (Exception e) {
            throw new ScheinEntityException();
        }
    }

    public List<ScheinEntity> findActiveShines() {
        return Collections.singletonList(new ScheinEntity());
    }

    public boolean actWithShineTyps() {
        if (scheinRepository == null) return true;
        return scheinRepository.getShineType(true) != null;
    }

}
