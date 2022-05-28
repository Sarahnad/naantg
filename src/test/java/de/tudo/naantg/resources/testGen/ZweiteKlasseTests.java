package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.ZweiteKlasse;
import de.tudo.naantg.testproject.test.ZweiteKlasseTG;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class ZweiteKlasseTests /*implements ZweiteKlasseTG*/ {


    @Test
    public void whenGetLongMillion_thenReturn1000000() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        long actual = zweiteKlasse.getLongMillion();

        assertEquals(1000000, actual);
    }


    @Test
    public void whenGetText_thenReturnValue() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        String actual = zweiteKlasse.getText();

        assertEquals("simple Method", actual);
    }


    @Test
    public void whenGetShortNegTen_thenReturnNeg10() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        short actual = zweiteKlasse.getShortNegTen();

        assertEquals(-10, actual);
    }


    @Test
    public void whenGetOneByte_thenReturn7() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        byte actual = zweiteKlasse.getOneByte();

        assertEquals(7, actual);
    }


    @Test
    public void whenGetCharS_thenReturnValue() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        char actual = zweiteKlasse.getCharS();

        assertEquals('s', actual);
    }


    @Test
    public void whenGetAFloat_thenReturnValue() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        float actual = zweiteKlasse.getAFloat();

        assertNotEquals(0.0, actual);
    }


    @Test
    public void whenGetText_thenReturnValue_1() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        String actual = zweiteKlasse.getText();

        assertNotEquals("", actual);
    }


    @Test
    public void whenGetAFloat_thenReturnValue_1() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        float actual = zweiteKlasse.getAFloat();

        assertEquals(-10.5, actual);
    }


    @Test
    public void whenGetAFloat_thenReturnNeg10p5() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        float actual = zweiteKlasse.getAFloat();

        assertEquals(-10.5, actual);
    }


    @Test
    public void whenGetCharS_thenReturn_s() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        char actual = zweiteKlasse.getCharS();

        assertEquals('s', actual);
    }


    @Test
    public void whenGetCharS_thenReturnValue_1() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        char actual = zweiteKlasse.getCharS();

        assert actual > 0;
    }


    @Test
    public void whenNoText_thenReturnNull() {
        ZweiteKlasse zweiteKlasse = new ZweiteKlasse();

        String actual = zweiteKlasse.noText();

        assert actual == null;
    }

}