package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.ErsteKlasse;
import de.tudo.naantg.testproject.test.ErsteKlasseTG;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ErsteKlasseTests /*implements ErsteKlasseTG*/ {


    @Test
    public void whenGetFivePointSix_thenReturnValue_1() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        double actual = ersteKlasse.getFivePointSix();

        assertEquals(5.6, actual);
    }


    @Test
    public void whenGetFive_thenReturned_isGreater4() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        int actual = ersteKlasse.getFive();

        assert actual > 4;
    }


    @Test
    public void whenGetFivePointSix_thenReturned_isGreater5() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        double actual = ersteKlasse.getFivePointSix();

        assert actual > 5;
    }


    @Test
    public void whenGetFive_thenReturned_isSmaller6() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        int actual = ersteKlasse.getFive();

        assert actual < 6;
    }


    @Test
    public void whenGetFivePointSix_thenReturnValue() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        double actual = ersteKlasse.getFivePointSix();

        assertNotEquals(0.0, actual);
    }


    @Test
    public void whenGetFive_thenReturnValue_1() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        int actual = ersteKlasse.getFive();

        assertEquals(5, actual);
    }


    @Test
    public void whenGetFive_thenReturnValue() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        int actual = ersteKlasse.getFive();

        assertNotEquals(0, actual);
    }


    @Test
    public void whenAct_thenNoError() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        ersteKlasse.act();

    }


    @Test
    public void whenGetFivePointSix_thenReturn5p6() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        double actual = ersteKlasse.getFivePointSix();

        assertEquals(5.6, actual);
    }


    @Test
    public void whenGetFive_thenReturn5() {
        ErsteKlasse ersteKlasse = new ErsteKlasse();

        int actual = ersteKlasse.getFive();

        assertEquals(5, actual);
    }


}