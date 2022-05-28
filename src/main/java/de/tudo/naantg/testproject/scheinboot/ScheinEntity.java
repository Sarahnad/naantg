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

import jakarta.persistence.Entity;


@Entity
public class ScheinEntity extends ScheinJob {

    private String name;

    @GeneratedValue
    private Long scheinId;

    private String password;

    public ScheinEntity() {

    }

    public ScheinEntity(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getScheinId() {
        return scheinId;
    }

    public void setScheinId(Long scheinId) {
        this.scheinId = scheinId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
