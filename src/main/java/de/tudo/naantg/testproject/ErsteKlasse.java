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


import de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse;
import de.tudo.naantg.testproject.weiter.NochEineKlasse;

/**
 * The top class of the test project. The test project serves as test environment for the test generation tests.
 * Provides simple methods with different return values.
 */
public class ErsteKlasse {

    /**
     * Calls simpleMethod() of ZweiteClasse,
     * calls sum(5, -4) of NochEineKlasse,
     * calls doThings() of KlasseMitInnererKlasse.
     */
    public void act() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();
        System.out.println("Erste Klasse");
        zweiteKlasse.simpleMethod();
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        int sum = nochEineKlasse.sum(5, -4);
        System.out.println("Summe 5 + (-4) = " + sum);
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        klasseMitInnererKlasse.doThings();
    }

    /**
     * Returns 5 as a constant.
     * @return 5
     */
    public int getFive() {
        return 5;
    }

    /**
     * Returns 5.6 as a constant.
     * @return 5.6
     */
    public double getFivePointSix() {
        return 5.6;
    }

}
