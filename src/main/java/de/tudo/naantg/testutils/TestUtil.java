/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testutils;

import de.tudo.naantg.helpers.TestFileHandler;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {

    private final String expectedFilePath;
    private final String actualFilePath;

    public TestUtil(String expectedFilePath, String actulaFilePath) {
        this.expectedFilePath = expectedFilePath;
        this.actualFilePath = actulaFilePath;
    }

    public void compareClassAndMethods(String filename, String[] methods, List<String> excepts) {
        checkTestFile(filename, methods, excepts);
    }

    private void checkTestFile(String filename, String[] methods, List<String> excepts) {
        TestFileHandler handler = new TestFileHandler();
        handler.setTestPath(expectedFilePath);
        StringBuilder expectedFileContent = handler.readTestFile(filename);
        handler.setTestPath(actualFilePath);
        StringBuilder actualFileContent = handler.readTestFile(filename);
        String expectedContent = expectedFileContent.toString().replaceAll("package .+;\n", "");
        String actualContent = actualFileContent.toString().replaceAll("package .+;\n", "");
        String[] expectedMethods = expectedContent.replaceAll("\\s", "").split("@Test");
        String[] actualMethods = actualContent.replaceAll("\\s", "").split("@Test");
        assertEquals(expectedMethods.length, actualMethods.length);
        String[] actualImports = actualMethods[0].split("import");
        String[] expectedImports = expectedMethods[0].split("import");
        assertTrue(Arrays.asList(actualImports).containsAll(Arrays.asList(actualImports)));
        assertTrue(Arrays.asList(expectedImports).containsAll(Arrays.asList(expectedImports)));
        assertEquals(methods.length, actualMethods.length-1);
        int n = methods.length;
        expectedMethods[n] = expectedMethods[n].substring(0, expectedMethods[n].length()-1);
        actualMethods[n] = actualMethods[n].substring(0, actualMethods[n].length()-1);
        int count = methods.length;

        for (int i = 0; i < count; i++) {
            methods[i] = methods[i] + "()";
        }

        List<String> privateExpMethods = new ArrayList<>();
        List<String> privateActMethods = new ArrayList<>();

        for (int i = 1; i < expectedMethods.length; i++) {
            String[] expSplitted = expectedMethods[i].split("private");
            String exp = expSplitted[0];
            if (expSplitted.length > 1) {
                for (int s = 1; s < expSplitted.length; s++) {
                    privateExpMethods.add("private" + expSplitted[s]);
                }
            }

            if (excepts != null) {
                boolean found = false;
                for (String except : excepts) {
                    if (exp.contains(except)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    count--;
                    continue;
                }
            }
            String meth = "";
            for (String method : methods) {
                if (exp.contains(method)) {
                    meth = method;
                    break;
                }
            }
            if (meth.equals("")) continue;
            for (String testcase : actualMethods) {
                String[] actSplitted = testcase.split("private");
                String actualTestcase = actSplitted[0];
                if (actSplitted.length > 1) {
                    for (int s = 1; s < actSplitted.length; s++) {
                        privateActMethods.add("private" + actSplitted[s]);
                    }
                }


                if (testcase.contains(meth)) {
                    assertEquals(exp, actualTestcase);
                    count--;
                    break;
                }
            }
        }
        assertEquals(0, count);

        //assertEquals(privateExpMethods.size(), privateActMethods.size());
        /*Collections.sort(privateActMethods);
        Collections.sort(privateExpMethods);
        for (String expMeth : privateExpMethods) {
            assertTrue(privateActMethods.contains(expMeth));
        }
        for (String actMeth : privateActMethods) {
            assertTrue(privateExpMethods.contains(actMeth));
        }*/
    }

}
