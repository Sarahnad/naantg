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
import java.util.Arrays;
import java.util.List;

public class Komplex4 {

    private List<Person> persons;
    private List<Box> freeBoxes;



    public Komplex4(List<Person> persons, List<Box> freeBoxes, String[] namesToGiveBox) {
        this.persons = persons;
        this.freeBoxes = freeBoxes;
        for (Person person : persons) {
            if (Arrays.asList(namesToGiveBox).contains(person.getName())) {
                giveFreeBoxToPerson(person.getName());
            }
        }
    }

    public Inhalt getContentOfPersonalBox(Person person) {
        List<Box> boxes = person.getBoxes();
        for (Box box : boxes) {
            if (box.getBoxId() == person.getKey().getKeyId()) {
                return box.getContent();
            }
        }
        return null;
    }

    public boolean giveFreeBoxToPerson(String name) {
        if (freeBoxes.isEmpty()) return false;
        for (Person person : persons) {
            if (person.getName().equals(name)) {
                person.getBoxes().add(freeBoxes.get(0));
                return true;
            }
        }
        return false;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void addPerson(Person person) {
        this.persons.add(person);
    }

    public List<Box> getFreeBoxes() {
        return freeBoxes;
    }

    public void addBox(Box box) {
        this.freeBoxes.add(box);
    }
}
