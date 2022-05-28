package de.tudo.naantg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssertionTest {

    @Test
    public void generateAssetionTest() {
        testType(AssertType.EQUALS, "assertEquals(1, actual)");
        testType(AssertType.TRUE, "assertTrue(actual)");
        testType(AssertType.FALSE, "assertFalse(actual)");
        testType(AssertType.EQ, "assert actual == 1");
        testType(AssertType.NEQ, "assert actual != 1");
        testType(AssertType.NOTEQUALS, "assertNotEquals(1, actual)");
        testType(AssertType.GREATER, "assert actual > 1");
        testType(AssertType.SMALLER, "assert actual < 1");
    }

    private void testType(AssertType type, String expected) {
        Assertion assertion = new Assertion("1", "actual", type);
        String wrappedExpected = "\t\t" + expected + ";\n";
        String actual = assertion.generateAssertion();
        assertEquals(wrappedExpected, actual);
    }

}