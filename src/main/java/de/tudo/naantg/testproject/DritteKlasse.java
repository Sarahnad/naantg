/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject;

import de.tudo.naantg.testproject.weiter.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DritteKlasse {

    private int points;

    private long aMillion;

    private Komplex komplex;

    private Komplex2 komplex2;

    private Komplex3 komplex3;

    private Komplex4 komplex4;

    private List<Komplex> komplexes;



    public boolean init(ErsteKlasse firstClass, ZweiteKlasse secondClass) {
        points = firstClass.getFive();
        aMillion = secondClass.getLongMillion();
        return true;
    }

    public Inhalt doComplex() {
        Person alexa = new Person("alexa");
        return doComplexWithPerson(alexa);
    }

    public Inhalt doComplexWithPerson(Person person) {
        komplex = new Komplex(2, 2);
        komplex.addPerson(person);
        komplex.addBox(new Box(45));
        komplex.giveFreeBoxToPerson("alexa");
        return komplex.getContentOfPersonalBox(person);
    }

    public void initBuy() {
        this.aMillion = 100000;
        this.points = 50;
    }

    public boolean canBuyBox() {
        return this.points > 20 && aMillion == 100000;
    }

    public Person createPersonWithBoxes(List<Box> boxes) {
        Person person = new Person("first");
        person.setKey(new Key());
        person.getKey().setKeyId(1);
        person.setBoxes(boxes);
        if (!boxes.isEmpty()) {
            Inhalt content = new Inhalt();
            content.setContent(Arrays.asList(Dinge.TELLER, Dinge.GLAS));
            boxes.get(0).setContent(content);
        }
        return person;
    }

    public Person createPerson() {
        Person person = new Person("first");
        person.setKey(new Key());
        person.getKey().setKeyId(1);
        List<Box> boxes = new ArrayList<>();
        Box box = new Box(1);
        boxes.add(box);
        person.setBoxes(boxes);
        Inhalt content = new Inhalt();
        content.setContent(Arrays.asList(Dinge.TELLER, Dinge.GLAS));
        boxes.get(0).setContent(content);
        return person;
    }

    public List<Box> createBoxes() {
        return Arrays.asList(new Box(1), new Box(2), new Box(3));
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getAMillion() {
        return aMillion;
    }

    public void setAMillion(long aMillion) {
        this.aMillion = aMillion;
    }

    public Komplex getKomplex() {
        return komplex;
    }

    public void setKomplex(Komplex komplex) {
        this.komplex = komplex;
    }

    public Komplex2 getKomplex2() {
        return komplex2;
    }

    public void setKomplex2(Komplex2 komplex2) {
        this.komplex2 = komplex2;
    }

    public Komplex3 getKomplex3() {
        return komplex3;
    }

    public void setKomplex3(Komplex3 komplex3) {
        this.komplex3 = komplex3;
    }

    public Komplex4 getKomplex4() {
        return komplex4;
    }

    public void setKomplex4(Komplex4 komplex4) {
        this.komplex4 = komplex4;
    }

    public List<Komplex> getKomplexes() {
        return komplexes;
    }

    public void setKomplexes(List<Komplex> komplexes) {
        this.komplexes = komplexes;
    }
}
