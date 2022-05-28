package de.tudo.naantg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMethodModelTest {

    @Test
    public void getUniqueIdentifierTest() {
        TestCaseModel testCaseModel = new TestCaseModel();
        assertEquals("ersteKlasse", testCaseModel.getUniqueIdentifier("ErsteKlasse"));
        assertEquals("iVar", testCaseModel.getUniqueIdentifier("int"));
        assertEquals("lVar", testCaseModel.getUniqueIdentifier("long"));
        assertEquals("sVar", testCaseModel.getUniqueIdentifier("short"));
        assertEquals("fVar", testCaseModel.getUniqueIdentifier("float"));
        assertEquals("dVar", testCaseModel.getUniqueIdentifier("double"));
        assertEquals("ch", testCaseModel.getUniqueIdentifier("char"));
        assertEquals("str", testCaseModel.getUniqueIdentifier("String"));
        assertEquals("bVar", testCaseModel.getUniqueIdentifier("byte"));
        assertEquals("isValid", testCaseModel.getUniqueIdentifier("boolean"));

        assertEquals("iVar2", testCaseModel.getUniqueIdentifier("int"));
        assertEquals("iVar3", testCaseModel.getUniqueIdentifier("int"));
        assertEquals("iVar4", testCaseModel.getUniqueIdentifier("int"));
        assertEquals("ersteKlasse2", testCaseModel.getUniqueIdentifier("ErsteKlasse"));

        assertEquals("list", testCaseModel.getUniqueIdentifier("ArrayList"));
        assertEquals("list2", testCaseModel.getUniqueIdentifier("List"));
        assertEquals("array", testCaseModel.getUniqueIdentifier("int[]"));
        assertEquals("array2", testCaseModel.getUniqueIdentifier("String[]"));

        assertEquals("some", testCaseModel.getUniqueIdentifier("Some", "some"));
        assertEquals("some2", testCaseModel.getUniqueIdentifier("int", "some"));
    }

}