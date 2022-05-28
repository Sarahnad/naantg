/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.parser;

import de.tudo.naantg.helpers.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for the generated test files.
 */
public class TestFileParser {

    /**
     * the context for the logger information
     */
    private static final String LOG_INFO = "[TestFileParser] ";

    /**
     * Adds a new test case (method) named with methodName to the content if it not exists.
     * @param methodName the name of the test case
     * @param method the new test case
     * @param content the old content of a test class file
     * @return the content of the test class with the new test case
     */
    public static String addMethod(String methodName, String method, String content) {
        String added = content;
        if (methodExists(methodName, content)) {
            Logger.logInfo(LOG_INFO + "The method \"" + methodName + "\" already exists.");
            return added;
        }
        added = added.substring(0, added.length()-2);
        added = added + method + "\n\n}";
        Logger.logInfo(LOG_INFO + "The method \"" + methodName + "\" was added.");
        return  added;
    }

    /**
     * Checks if a test case with the methodName exists in the testFileContent.
     * @param methodName the name of the test case
     * @param testFileContent the content of the test class file
     * @return true if the test case exists
     */
    public static boolean methodExists(String methodName, String testFileContent) {
        if (testFileContent == null) {
            Logger.logWarning(LOG_INFO + "The content of the test file is null!");
            return false;
        }

        String[] methods = testFileContent.split("@Test");
        for (String method : methods) {
            String name = "void " + methodName + "() {";
            if (method.contains(name)) return true;

            String nameWithException = "void " + methodName + "() throws ";
            if (method.contains(nameWithException)) return true;

            Pattern pattern = Pattern.compile("\tprivate .+\\([^()]*\\) \\{\n");
            Matcher matcher = pattern.matcher(method);
            while (matcher.find()) {
                String stringPart = matcher.group();
                if (stringPart.contains(methodName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the importName exists in the testFileContent.
     * @param importName the import
     * @param testFileContent the content of a test class file
     * @return true if the import exists
     */
    public static boolean importExists(String importName, String testFileContent) {
        if (checkContent(testFileContent)) {
            String imp = "import " + importName + ";";
            return testFileContent.contains(imp);
        }
        return false;
    }

    /**
     * Checks if the content is not null.
     * Returns false and logs a warning if the content is null.
     * Otherwise returns true.
     * @param content the content to check
     * @return true if the content is not null
     */
    private static boolean checkContent(String content) {
        if (content == null) {
            Logger.logWarning(LOG_INFO + "The content of the test file is null!");
            return false;
        }
        return true;
    }

    /**
     * Adds the given import to the content if it not exists.
     * @param importName the import name
     * @param importStatement the import statement
     * @param content the old content of a test class file
     * @return the content of the test class with the new import
     */
    public static String addImport(String importName, String importStatement, String content) {
        String added = content;
        if (importExists(importName, content)) {
            return added;
        }

        Pattern pattern = Pattern.compile("import .+;");
        Matcher matcher = pattern.matcher(added);
        List<String> imports = new ArrayList<>();
        while (matcher.find()) {
            String stringPart = matcher.group();
            imports.add(stringPart.replace("import ", "")
                    .replace(";", ""));
        }
        imports.add(importName);
        Collections.sort(imports);
        int index = imports.indexOf(importName);
        if (index > 0) {
            index = index - 1;
            String toReplace ="import " + imports.get(index) + ";\n";
            String replacement = toReplace + importStatement;
            added = added.replace(toReplace, replacement);
        }
        else {
            String toReplace ="import " + imports.get(1) + ";\n";
            String replacement = importStatement + toReplace;
            added = added.replace(toReplace, replacement);
        }

        Logger.logInfo(LOG_INFO + "The import \"" + importName + "\" was added.");
        return  added;
    }






    /**
     * Finds the last importName that has the longest equal prefix of the importName.
     * @param imports list of importNames
     * @param importName an importName
     * @return the last importName that has the longest equal prefix of the importName
     */
    private static int findPositionInImports(List<String> imports, String importName) {
        int lastPosition = 0;
        String[] parts = importName.split("\\.");
        if (parts.length == 0) return 0;
        String prefix = parts[0];
        lastPosition = findLastPosition(imports, prefix);
        if (parts.length == 1) {
            return lastPosition;
        }
        for (int i = 1; i < parts.length; i++) {
            prefix += "." + parts[i];
            int position = findLastPosition(imports, prefix);
            if (position == -1) return lastPosition;
            lastPosition = position;
        }
        return -1;
    }

    /**
     * Finds the first element in the list that starts with part
     * and returns its index.
     * Returns -1 if no list element starts with part.
     * @param list a list of Strings
     * @param part a part
     * @return the first position of prefix matching
     */
    public static int findFirstPosition(List<String> list, String part) {
        for (int i = 0; i < list.size(); i++) {
            String imp = list.get(i);
            if (imp.startsWith(part)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the last element in the list that starts with part
     * and returns its index.
     * Returns -1 if no list element starts with part.
     * @param list a list of Strings
     * @param part a part
     * @return the last position of prefix matching
     */
    public static int findLastPosition(List<String> list, String part) {
        boolean start = false;
        for (int i = 0; i < list.size(); i++) {
            String imp = list.get(i);
            if (imp.startsWith(part) && !start) {
                start = true;
            }
            else if (!imp.startsWith(part) && start) {
                return i -1;
            }
        }
        if (list.get(list.size() - 1).startsWith(part)) return (list.size() -1);
        return -1;
    }


}
