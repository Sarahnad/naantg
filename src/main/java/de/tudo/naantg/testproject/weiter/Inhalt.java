/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.weiter;

import java.util.ArrayList;
import java.util.List;

public class Inhalt {

    private List<Dinge> content;

    public Inhalt() {
        this.content = new ArrayList<>();
    }

    public List<Dinge> getContent() {
        return content;
    }

    public void setContent(List<Dinge> content) {
        this.content = content;
    }
}
