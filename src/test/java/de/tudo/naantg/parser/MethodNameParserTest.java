package de.tudo.naantg.parser;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MethodNameParserTest {

    @Test
    public void testGetMethodName() {
        assertEquals("act", MethodNameParser.getMethodName("whenAct_thenNoError"));
        assertEquals("getSum", MethodNameParser.getMethodName("whenGetSum_thenReturnSum"));
        assertEquals("get5", MethodNameParser.getMethodName("whenGet5_thenReturn5"));
    }

    @Test
    public void testGetReturnAssertion() {
        returnTest(new String[]{"RETURN", "Five"}, "whenGetFive_thenReturnFive");
        returnTest(new String[]{"RETURN", "5"}, "whenGetFive_thenReturn5");

        assertTrue(MethodNameParser.getReturnAssertion("whenGetFive_thenGetFive").isEmpty());
        assertTrue(MethodNameParser.getReturnAssertion("whenGetFive_thenReturnedFive").isEmpty());

        returnTest(new String[]{"RETURN", "s"}, "whenGetCharS_thenReturn_s");
        returnTest(new String[]{"RETURN", "hello"}, "whenGetCharS_thenReturn_hello");
        returnTest(new String[]{"RETURN", "-10"}, "whenGetShortNegTen_thenReturnNeg10");
        returnTest(new String[]{"RETURN", "VALUE"}, "whenGetShortNegTen_thenReturnValue");
        returnTest(new String[]{"RETURN", "NULL"}, "whenGetName_thenReturnNull");
        returnTest(new String[]{"RETURN", "LIST"}, "whenGetNames_thenReturnList");
        returnTest(new String[]{"RETURN", "EXCEPTION"}, "whenCreateUser_thenReturnException");
    }

    @Test
    public void getReturnedAssertionTest() {
        returnTest(new String[]{"RETURNED", "GREATER", "Five"}, "whenGetFive_thenReturned_isGreaterFive");
        returnTest(new String[]{"RETURNED", "GREATER", "5"}, "whenGetFive_thenReturned_isGreater_5");
        returnTest(new String[]{"RETURNED", "SMALLER", "5"}, "whenGetFive_thenReturned_isSmaller5");

        assertTrue(MethodNameParser.getReturnAssertion("whenGetFive_thenGetFive").isEmpty());
        assertTrue(MethodNameParser.getReturnAssertion("whenGetFive_thenReturned5").isEmpty());

        returnTest(new String[]{"RETURNED", "EMPTY"}, "whenGetNames_thenReturned_isEmpty");
        returnTest(new String[]{"RETURNED", "CONTAINS", "Name1"}, "whenGetNames_thenReturned_containsName1");
        returnTest(new String[]{"RETURNED", "CONTAINS", "name2"}, "whenGetNames_thenReturned_contains_name2");
        returnTest(new String[] {"RETURNED", "SIZE", "IS", "10"}, "whenGetNames_thenReturned_size_is10");
        returnTest(new String[] {"RETURNED", "SIZE", "GREATER", "10"}, "whenGetNames_thenReturned_size_isGreater10");
        returnTest(new String[] {"RETURNED", "SIZE", "SMALLER", "10"}, "whenGetNames_thenReturned_size_isSmaller10");
        returnTest(new String[] {"RETURNED", "GET", "1", "IS", "Name3"},
                "whenGetNames_thenReturned_get1_isName3");
        returnTest(new String[] {"RETURNED", "GET", "2", "IS", "Name3"},
                "whenGetNames_thenReturned_get_2_isName3");
        returnTest(new String[] {"RETURNED", "GET", "1", "IS", "name4"},
                "whenGetNames_thenReturned_get1_is_name4");
        returnTest(new String[] {"RETURNED", "GET", "1", "IS", "Name5", "OR", "IS", "Name6"},
                "whenGetNames_thenReturned_get1_isName5_or_isName6");
        returnTest(new String[] {"RETURNED", "GET", "1", "IS", "name4", "AND", "GET", "2", "IS", "Name7"},
                "whenGetNames_thenReturned_get1_is_name4_and_get2_isName7");
        returnTest(new String[] {"RETURNED", "SIZE", "NOT", "5"},
                "whenGetNames_thenReturned_size_isNot5");
        returnTest(new String[] {"RETURNED", "NOT", "CONTAINS", "-7"},
                "whenGetNumbers_thenReturned_notContains_neg7");
        returnTest(new String[] {"RETURNED", "NOT", "CONTAINS", "-5.5"},
                "whenGetDoubles_thenReturned_notContains_neg5p5");
        returnTest(new String[] {"RETURNED", "GET", "2", "NOT", "-3"},
                "whenGetIntArray_thenReturned_get2_isNot_neg3");
    }


    private void returnTest(String[] expected, String method) {
        List<String> actual = MethodNameParser.getReturnAssertion(method);
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    @Test
    public void getPrePartTest() {
        String tgMethod = "whenGetOneOrTwo_withTrue_thenReturn1";
        List<String> actual = MethodNameParser.getPreValue(tgMethod);
        String[] expected = new String[]{"true"};
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }

        tgMethod = "whenTest_withNeg5p5_thenReturn1";
        actual = MethodNameParser.getPreValue(tgMethod);
        expected = new String[]{"-5.5"};
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }

        tgMethod = "whenGetOneOrTwo_withValues_thenReturn1";
        actual = MethodNameParser.getPreValue(tgMethod);
        expected = new String[]{"VALUES"};
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }

        tgMethod = "whenGetOneOrTwo_with_7_values_thenReturn1";
        actual = MethodNameParser.getPreValue(tgMethod);
        expected = new String[]{"7", "VALUES"};
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }

        tgMethod = "whenFindById_withUser_thenReturnUser";
        actual = MethodNameParser.getPreValue(tgMethod);
        expected = new String[]{"User"};
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

    private void checkValues(String testName, String expected) {
        List<String>  actual = MethodNameParser.getGivenValue(testName);
        String[] expectedValues = expected.split(",");
        assertFalse(actual.isEmpty());
        assertEquals(expectedValues.length, actual.size());
        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], actual.get(i));
        }
    }

    @Test
    public void givenAndWithPartTest() {
        String tgMethod = "givenThisInitBuy_whenCanBuyBox_thenReturnTrue";
        String expected = "VALUE,VOID,FROM,THIS,OF,initBuy";
        checkValues(tgMethod, expected);

        tgMethod = "givenThisinitBuy_whencanBuyBox_thenReturntrue";
        expected = "VALUE,VOID,FROM,THIS,OF,initBuy";
        checkValues(tgMethod, expected);

        tgMethod = "givenTestInitialBoxes_with_this_whenCanBuyBox_thenReturnFalse";
        expected = "VALUE,VOID,OF,initialBoxes,PARAM,THIS";
        checkValues(tgMethod, expected);

        tgMethod = "givenStaticStatistik_work_whenCanBuyBox_thenReturnFalse";
        expected = "VALUE,VOID,FROM,TYPE,Statistik,OF,work";
        checkValues(tgMethod, expected);

        tgMethod = "given_param_ofThisCreatePerson_whenDoComplexWithPerson_thenReturnValue_simple";
        expected = "VALUE,param1,FROM,THIS,OF,createPerson";
        checkValues(tgMethod, expected);

        tgMethod = "given_this_ofTestInitialBoxes_whenCanBuyBox_thenReturnFalse";
        expected = "VALUE,THIS,OF,initialBoxes";
        checkValues(tgMethod, expected);

        tgMethod = "givenParamInit_whenDoComplexWithPerson_thenReturnNull";
        expected = "VALUE,VOID,FROM,param1,OF,init";
        checkValues(tgMethod, expected);

        tgMethod = "givenScheinEntity_whenFindByScheinId_thenReturnNull";
        expected = "scheinEntity";
        checkValues(tgMethod, expected);

        tgMethod = "given_ScheinEntity_whenFindByScheinId_thenReturnNull";
        expected = "VALUE,ScheinEntity";
        checkValues(tgMethod, expected);
    }

    @Test
    public void getGetAssertionsTest() {
        getTest(new String[]{"GET", "Seven", "VALUES"}, "whenSetSeven_thenGetSeven_hasValues");
        List<String> actual = MethodNameParser.getReturnAssertion("whenSetSeven_thenGetSeven_hasValues");
        assertTrue(actual.isEmpty());

        getTest(new String[]{"GET", "Seven", "IS", "VALUES"}, "whenSetSeven_thenGetSeven_hasEqualValues");
        actual = MethodNameParser.getReturnAssertion("whenSetSeven_thenGetSeven_hasEqualValues");
        assertTrue(actual.isEmpty());

        getTest(new String[]{"GET", "Index", "VALUE", "55"}, "whenSetIndex_with55_thenGetIndex_hasValue55");
        getTest(new String[]{"GET", "Index", "VALUE", "-5.5"}, "whenSetIndex_thenGetIndex_hasValueNeg5p5");
        getTest(new String[]{"GET", "Index", "IS", "VALUE"}, "whenSetIndex_thenGetIndex_hasEqualValue");
    }

    private void getTest(String[] expected, String method) {
        List<String> actual = MethodNameParser.getGetAssertion(method);
        assertFalse(actual.isEmpty());
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }

}