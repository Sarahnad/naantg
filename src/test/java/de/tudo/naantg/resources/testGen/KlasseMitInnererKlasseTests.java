package de.tudo.naantg.resources.testGen;

import de.tudo.naantg.testproject.weiter.KlasseMitInnererKlasse;
import de.tudo.naantg.testproject.test.KlasseMitInnererKlasseTG;

import java.util.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class KlasseMitInnererKlasseTests /*implements KlasseMitInnererKlasseTG*/ {

    @Test
    public void whenGetFirstValue_thenReturn0() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        List<Integer> param1 = new ArrayList<>();

        int actual = klasseMitInnererKlasse.getFirstValue(param1);

        assertEquals(0, actual);
    }


    @Test
    public void whenGetFirstValue_thenReturn0_1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        LinkedList<Integer> param1 = new LinkedList<>();

        int actual = klasseMitInnererKlasse.getFirstValue(param1);

        assertEquals(0, actual);
    }


    @Test
    public void whenGetNames_thenReturnList() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertFalse(actual.isEmpty());
    }


    @Test
    public void whenGetNames_thenReturned_get6_is_Name6_and_get_9_is_Name9() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals("Name6", actual.get(6));
        assertEquals("Name9", actual.get(9));
    }


    @Test
    public void whenGetNames_thenReturned_size_isGreater9_and_size_isSmaller11() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.size() > 9);
        assertTrue(actual.size() < 11);
    }


    @Test
    public void whenGetNames_thenReturned_size_is10_or_is20() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.size() == 10 || actual.size() == 20);
    }


    @Test
    public void whenGetNames_thenReturned_size_is10() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals(10, actual.size());
    }


    @Test
    public void whenGetNames_thenReturned_get_5_isName5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals("Name5", actual.get(5));
    }


    @Test
    public void whenGetNames_thenReturned_get1_isName1_or_get1_isName2() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.get(1).equals("Name1") || actual.get(1).equals("Name2"));
    }


    @Test
    public void whenGetList_thenReturned_isEmpty() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getList();

        assert actual != null;
        assertTrue(actual.isEmpty());
    }


    @Test
    public void whenGetNames_thenReturned_contains_Name0() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.contains("Name0"));
    }


    @Test
    public void whenGetBooleans_thenReturned_get4_isTrue_and_get7_isFalse() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(4));
        assertFalse(actual.get(7));
    }


    @Test
    public void whenGetBooleans_thenReturned_get5_isTrue_or_get7_isFalse() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(5) || !actual.get(7));
    }


    @Test
    public void whenGetBooleans_thenReturned_get8_isTrue() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(8));
    }


    @Test
    public void whenGetDoubles_thenReturned_contains_5p5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Double> actual = klasseMitInnererKlasse.getDoubles();

        assert actual != null;
        assertTrue(actual.contains(5.5));
    }


    @Test
    public void whenGetNumbers_thenReturned_contains_8() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Integer> actual = klasseMitInnererKlasse.getNumbers();

        assert actual != null;
        assertTrue(actual.contains(8));
    }


    @Test
    public void whenGetIntArray_thenReturned_size_is7_and_contains_7() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getIntArray();

        assert actual != null;
        assertEquals(7, actual.length);
        assertTrue(Arrays.stream(actual).anyMatch(x -> x == 7));
    }


    @Test
    public void whenGetIntArray_thenReturned_get2_is3() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getIntArray();

        assert actual != null;
        assertEquals(3, actual[2]);
    }


    @Test
    public void whenIsPositive_thenReturnTrue() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int[] param1 = new int[]{1, 4, 6, 3};

        boolean actual = klasseMitInnererKlasse.isPositive(param1);

        assertTrue(actual);
    }


    @Test
    public void whenIsPositive_thenReturnFalse() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int[] param1 = new int[]{1, 4, -6, 3};

        boolean actual = klasseMitInnererKlasse.isPositive(param1);

        assertFalse(actual);
    }


    @Test
    public void whenGetDoubles_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Double> actual = klasseMitInnererKlasse.getDoubles();

        assert actual != null;
        assertTrue(actual.contains(5.5));
    }


    @Test
    public void whenGetBooleans_test2() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(5) || !actual.get(7));
    }


    @Test
    public void whenGetBooleans_test3() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(8));
    }


    @Test
    public void whenGetIntArray_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getIntArray();

        assert actual != null;
        assertEquals(7, actual.length);
        assertTrue(Arrays.stream(actual).anyMatch(x -> x == 7));
    }


    @Test
    public void whenGetNames_test33() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.size() == 5 || actual.size() == 20 || actual.size() == 10);
    }


    @Test
    public void whenGetNames_test34() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertNotEquals(5, actual.size());
    }


    @Test
    public void whenGetIntArray_test2() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getIntArray();

        assert actual != null;
        assertEquals(3, actual[2]);
    }


    @Test
    public void whenGetBooleans_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Boolean> actual = klasseMitInnererKlasse.getBooleans();

        assert actual != null;
        assertTrue(actual.get(4));
        assertFalse(actual.get(7));
    }


    @Test
    public void whenGetNumbers_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Integer> actual = klasseMitInnererKlasse.getNumbers();

        assert actual != null;
        assertTrue(actual.contains(8));
    }


    @Test
    public void whenGetNames_test4() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals("Name5", actual.get(5));
    }


    @Test
    public void whenGetNames_test7() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.contains("Name0"));
    }


    @Test
    public void whenGetNames_test2() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.size() > 9);
        assertTrue(actual.size() < 11);
    }


    @Test
    public void whenGetNames_test6() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.get(1).equals("Name1") || actual.get(1).equals("Name2"));
    }


    @Test
    public void whenGetList_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getList();

        assert actual != null;
        assertTrue(actual.isEmpty());
    }


    @Test
    public void whenGetNames_test3() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertTrue(actual.size() == 10 || actual.size() == 20);
    }


    @Test
    public void whenGetNames_test5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals("Name6", actual.get(6));
        assertEquals("Name9", actual.get(9));
    }


    @Test
    public void whenGetNames_test1() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertEquals(10, actual.size());
    }


    @Test
    public void whenGetNumbers_thenReturned_notContains_neg7() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Integer> actual = klasseMitInnererKlasse.getNumbers();

        assert actual != null;
        assertFalse(actual.contains(-7));
    }


    @Test
    public void whenGetDoubles_thenReturned_notContains_neg5p5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<Double> actual = klasseMitInnererKlasse.getDoubles();

        assert actual != null;
        assertFalse(actual.contains(-5.5));
    }


    @Test
    public void whenGetNames_thenReturned_size_isNot5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        List<String> actual = klasseMitInnererKlasse.getNames();

        assert actual != null;
        assertNotEquals(5, actual.size());
    }


    @Test
    public void whenGetIntArray_thenReturned_get2_isNot_neg3() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getIntArray();

        assert actual != null;
        assertNotEquals(-3, actual[2]);
    }


    @Test
    public void whenSetSeven_thenGetSeven_hasValues() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int[] param1 = new int[]{1, -1, 2, -3, 4, -3, 8};

        klasseMitInnererKlasse.setSeven(param1);

        int[] actual = klasseMitInnererKlasse.getSeven();
        assertNotEquals(0, actual.length);
    }


    @Test
    public void whenSetSeven_withValues_thenGetSeven_hasEqualValues() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int[] param1 = new int[]{2, 2, 3, 3, 4, 4, 5};

        klasseMitInnererKlasse.setSeven(param1);

        int[] actual = klasseMitInnererKlasse.getSeven();
        assertArrayEquals(param1, actual);
    }


    @Test
    public void whenGetSeven_thenReturned_size_is7() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();

        int[] actual = klasseMitInnererKlasse.getSeven();

        assert actual != null;
        assertEquals(7, actual.length);
    }


    @Test
    public void whenSetSeven_with_7_values_thenGetSeven_hasEqualValues() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int[] param1 = new int[]{1, -1, 2, -3, 4, -3, 8};

        klasseMitInnererKlasse.setSeven(param1);

        int[] actual = klasseMitInnererKlasse.getSeven();
        assertArrayEquals(param1, actual);
    }


    @Test
    public void whenSetAnimals_thenGetAnimals_hasValues() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        List<String> param1 = Arrays.asList("cat", "dog", "bird", "horse", "cow", "butterfly");

        klasseMitInnererKlasse.setAnimals(param1);

        List<String> actual = klasseMitInnererKlasse.getAnimals();
        assertFalse(actual.isEmpty());
    }


    @Test
    public void whenSetAnimals_thenGetAnimals_hasEqualValues() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        List<String> param1 = Arrays.asList("cat", "dog", "bird", "horse", "cow", "butterfly");

        klasseMitInnererKlasse.setAnimals(param1);

        List<String> actual = klasseMitInnererKlasse.getAnimals();
        assertEquals(param1, actual);
    }


    @Test
    public void whenSetIndex_thenGetIndex_hasEqualValue() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int param1 = 44;

        klasseMitInnererKlasse.setIndex(param1);

        int actual = klasseMitInnererKlasse.getIndex();
        assertEquals(param1, actual);
    }


    @Test
    public void whenSetIndex_with55_thenGetIndex_hasValue55() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int param1 = 55;

        klasseMitInnererKlasse.setIndex(param1);

        int actual = klasseMitInnererKlasse.getIndex();
        assertEquals(55, actual);
    }


    @Test
    public void whenGetCollection_with5_thenReturned_size_is5() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        int param1 = 5;

        Collection<Double> actual = klasseMitInnererKlasse.getCollection(param1);

        assert actual != null;
        assertEquals(5, actual.size());
    }


    @Test
    public void whenIsEvenSize_thenReturnFalse() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        Collection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4, 5});

        boolean actual = klasseMitInnererKlasse.isEvenSize(param1);

        assertFalse(actual);
    }


    @Test
    public void whenIsEvenSize_thenReturnTrue() {
        KlasseMitInnererKlasse klasseMitInnererKlasse = new KlasseMitInnererKlasse();
        Collection<Byte> param1 = Arrays.asList(new Byte[] {1, 2, 3, 4});

        boolean actual = klasseMitInnererKlasse.isEvenSize(param1);

        assertTrue(actual);
    }


}
