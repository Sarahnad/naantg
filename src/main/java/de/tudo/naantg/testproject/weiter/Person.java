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

public class Person {

    private List<Box> boxes;

    private Key key;

    private String name;

    public Person(String name) {
        this.name = name;
        this.boxes = new ArrayList<>();
    }

    public void init() {
        this.key = new Key();
        key.setKeyId(100);
        this.boxes.add(new Box(100));
    }

    public void init(int keyId, int boxId) {
        this.key = new Key();
        key.setKeyId(keyId);
        this.boxes.add(new Box(boxId));
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
