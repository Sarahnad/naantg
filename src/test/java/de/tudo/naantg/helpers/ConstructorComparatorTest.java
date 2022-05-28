package de.tudo.naantg.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorComparatorTest {

    @Test
    public void compareTest() {
        ConstructorComparator comparator = new ConstructorComparator();

        ConstructorHeapElement simplestElem = new ConstructorHeapElement( 1, 1);
        ConstructorHeapElement nextSimple = new ConstructorHeapElement( 2, 2);
        ConstructorHeapElement komplexElem = new ConstructorHeapElement( 3, 0);
        ConstructorHeapElement nextElem = new ConstructorHeapElement( 3, 1);
        ConstructorHeapElement nextElem2 = new ConstructorHeapElement( 2, 0);
        ConstructorHeapElement nextElem3 = new ConstructorHeapElement( 2, 0);

        assertTrue(comparator.compare(simplestElem, nextSimple) < 0);
        assertTrue(comparator.compare(nextSimple, simplestElem) > 0);
        assertTrue(comparator.compare(nextSimple, komplexElem) < 0);
        assertEquals(0, comparator.compare(nextElem2, nextElem3));
        assertTrue(comparator.compare(nextElem, komplexElem) < 0);
    }

}