package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse.InnereKlasse;
import de.tudo.naantg.testproject.test.InnereKlasseTG;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class InnereKlasseTests /*implements InnereKlasseTG*/ {


    @Test
    public void whenIsNice_thenReturnTrue() {
        InnereKlasse innereKlasse = new InnereKlasse();

        boolean actual = innereKlasse.isNice();

        assertTrue(actual);
    }


    @Test
    public void whenIsNice_thenReturnValue() {
        InnereKlasse innereKlasse = new InnereKlasse();

        boolean actual = innereKlasse.isNice();

        assertTrue(actual);
    }


    @Test
    public void whenIsNice_thenReturnValue_1() {
        InnereKlasse innereKlasse = new InnereKlasse();

        boolean actual = innereKlasse.isNice();

        assertEquals(true, actual);
    }


}
