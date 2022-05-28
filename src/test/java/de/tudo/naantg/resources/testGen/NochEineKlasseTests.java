package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.weiter.NochEineKlasse;
import de.tudo.naantg.testproject.test.NochEineKlasseTG;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class NochEineKlasseTests /*implements NochEineKlasseTG*/ {


    @Test
    public void whenGetOneOrTwo_withFalse_thenReturn2() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        boolean param1 = false;

        int actual = nochEineKlasse.getOneOrTwo(param1);

        assertEquals(2, actual);
    }


    @Test
    public void whenGetOneOrTwo_withTrue_thenReturn1() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        boolean param1 = true;

        int actual = nochEineKlasse.getOneOrTwo(param1);

        assertEquals(1, actual);
    }


    @Test
    public void whenSum_thenReturnValue() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        int param1 = 44;
        int param2 = 44;
        int expected = param1+param2;

        int actual = nochEineKlasse.sum(param1, param2);

        assertEquals(expected, actual);
    }


    @Test
    public void whenIsOne_withOne_thenReturnTrue() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        String param1 = "One";

        boolean actual = nochEineKlasse.isOne(param1);

        assertTrue(actual);
    }


    @Test
    public void whenIsOne_thenReturnFalse() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        String param1 = "§Puiu";

        boolean actual = nochEineKlasse.isOne(param1);

        assertFalse(actual);
    }


    @Test
    public void whenHaveFun_thenReturnValue() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        char param1 = 'G';
        byte param2 = 119;
        short param3 = -808;
        long param4 = 1628253323520968601L;
        double param5 = -0.4397059479246579d;
        float param6 = 0.65997547f;
        String expected = "<" + param1 + ", " + param2 + ", " + param3 + ", " + param4 + ", " + param5 + ", " + param6 + "> :D";

        String actual = nochEineKlasse.haveFun(param1, param2, param3, param4, param5, param6);

        assertEquals(expected, actual);
    }


    @Test
    public void whenSum_thenReturn10() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        int param1 = 4;
        int param2 = 6;

        int actual = nochEineKlasse.sum(param1, param2);

        assertEquals(10, actual);
    }


    @Test
    public void whenGetOneOrTwo_thenReturn2() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        boolean param1 = false;

        int actual = nochEineKlasse.getOneOrTwo(param1);

        assertEquals(2, actual);
    }


    @Test
    public void whenHaveFun_thenReturnValue_1() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        char param1 = 'G';
        byte param2 = 119;
        short param3 = -808;
        long param4 = 1628253323520968601L;
        double param5 = -0.4397059479246579d;
        float param6 = 0.65997547f;
        String expected = "<" + param1 + ", " + param2 + ", " + param3 + ", " + param4 + ", " + param5 + ", " + param6 + "> :D";

        String actual = nochEineKlasse.haveFun(param1, param2, param3, param4, param5, param6);

        assertEquals(expected, actual);
    }


    @Test
    public void whenHaveFun_thenReturnValue_2() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        char param1 = 'G';
        byte param2 = 119;
        short param3 = -808;
        long param4 = 1628253323520968601L;
        double param5 = -0.4397059479246579d;
        float param6 = 0.65997547f;
        String expected = "<" + param1 + ", " + param2 + ", " + param3 + ", " + param4 + ", " + param5 + ", " + param6 + "> :D";

        String actual = nochEineKlasse.haveFun(param1, param2, param3, param4, param5, param6);

        assertEquals(expected, actual);
    }


    @Test
    public void whenHaveFun_thenReturnValue_3() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        char param1 = 'G';
        byte param2 = 119;
        short param3 = -808;
        long param4 = 1628253323520968601L;
        double param5 = -0.4397059479246579d;
        float param6 = 0.65997547f;
        String expected = "<" + param1 + ", " + param2 + ", " + param3 + ", " + param4 + ", " + param5 + ", " + param6 + "> :D";

        String actual = nochEineKlasse.haveFun(param1, param2, param3, param4, param5, param6);

        assertEquals(expected, actual);
    }


    @Test
    public void whenIsOne_thenReturnTrue() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        String param1 = "one";

        boolean actual = nochEineKlasse.isOne(param1);

        assertTrue(actual);
    }


    @Test
    public void whenSum_thenReturn10_2() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        int param1 = 4;
        int param2 = 6;

        int actual = nochEineKlasse.sum(param1, param2);

        assertEquals(10, actual);
    }


    @Test
    public void whenSum_thenReturn10_3() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        int param1 = 4;
        int param2 = 6;

        int actual = nochEineKlasse.sum(param1, param2);

        assertEquals(10, actual);
    }


    @Test
    public void whenIsOne_withNull_thenReturnFalse() {
        NochEineKlasse nochEineKlasse = new NochEineKlasse();
        String param1 = null;

        boolean actual = nochEineKlasse.isOne(param1);

        assertFalse(actual);
    }


}
