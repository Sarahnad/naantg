package de.tudo.naantg.testutils;

import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.testproject.test.DritteKlasseTG;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class TestUtilTest {

    //@Test
    public void compareClassAndMethodsTest() {
        String actualPath = "src/main/java/de/tudo/naantg/testproject/test/testGen";
        String expectedPath= "src/test/java/de/tudo/naantg/resources/testGen";
        TestUtil testUtil = new TestUtil(expectedPath, actualPath);
        ArrayList<Method> tgMethods = Scanner.findMethodsWithTG(DritteKlasseTG.class);
        String[] methods = new String[tgMethods.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = tgMethods.get(i).getName();
        }

        List<String> randomMethods = new ArrayList<>();
        randomMethods.add("whenSetKomplex_thenGetKomplex_hasEqualValue");
        randomMethods.add("whenSetKomplex2_thenGetKomplex2_hasEqualValue");
        randomMethods.add("whenSetKomplex3_with_2_values_thenGetKomplex3_hasEqualValue");
        randomMethods.add("list");
        randomMethods.add("list2");
        randomMethods.add("whenDoComplexWithPerson_thenReturnNull_komplex");
        randomMethods.add("whenDoComplexWithPerson_afterInit_fromPerson_thenReturnNull");

        testUtil.compareClassAndMethods("DritteKlasseTests", methods, randomMethods);
    }

}