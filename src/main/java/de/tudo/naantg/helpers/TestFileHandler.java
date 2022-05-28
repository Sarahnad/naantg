/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.helpers;



import de.tudo.naantg.parser.TestFileParser;

import java.io.*;
import java.util.List;

/**
 * Handles the create, write and read actions for the test files.
 */
public class TestFileHandler {

    /**
     * the path of the test file
     */
    private String testPath = "";

    /**
     * the context of the logger information
     */
    private final String LOG_INFO = "[TestFileHandler] ";

    /**
     * Creates a new test file with the given name in the "src/.../testGen" folder
     * if it not exists and writes the content.
     * @param name the name of the test file
     * @param content the content of the test file
     */
    public void createTestFile(String name, String content) {
        if (testPath.equals("")) return;
        String folderPath = testPath + "/testGen";
        if (!folderPath.contains("src")) {
            folderPath = "src/" + folderPath;
        }
        testPath = folderPath;
        File testGenFolder = new File(folderPath);
        String nameWithFileExt = name + ".java";
        if (!testGenFolder.exists()) {
            boolean success = testGenFolder.mkdirs();
            if (success) {
                Logger.logInfo(LOG_INFO + "A new testGen folder has been created for " + name + ".");
            }
        }
        String completePath = folderPath + "/" + nameWithFileExt;
        File testClassFile = new File(completePath);
        FileWriter fileWriter = null;
        if (!testClassFile.exists()) {
            try {
                fileWriter = new FileWriter(completePath);
                fileWriter.write(content);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else Logger.logInfo(LOG_INFO + "The File " + nameWithFileExt + " already exists.");
    }

    /**
     * Reads the content of the test file with the given name.
     * @param name the name of the test file
     * @return the content of the test file or null if the file not exists
     */
    public StringBuilder readTestFile(String name) {
        return readTestFile(name, true);
    }

    /**
     * Reads the content of the test file with the given name.
     * @param name the name of the test file
     * @return the content of the test file or null if the file not exists
     */
    public StringBuilder readTestFile(String name, boolean withWarning) {
        String completePath = testPath + "/" + name + ".java";
        File testFile = new File(completePath);
        if (!testFile.exists()) {
            if (withWarning)
                Logger.logWarning(LOG_INFO + "The file with the name " + name + " does not exist!");
            return null;
        }
        BufferedReader reader = null;
        StringBuilder fileContent = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(completePath);
            reader = new BufferedReader(fileReader);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                fileContent.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }

    /**
     * Writes the content of the test case to the file with the given name
     * if the method name not exists in the file.
     * @param name name of the file
     * @param content content of the test case
     * @param methodName name of the test case
     */
    public void writeContent(String name, String content, String methodName) {
        File testFile = getTestClassFile(name);
        if (testFile == null) return;
        StringBuilder oldContent = readTestFile(name);
        if (oldContent == null) return;
        String newContent  = TestFileParser.addMethod(methodName, content, oldContent.toString());
        write(testFile, newContent);
    }

    /**
     * Checks if the test case with the given methodName exists in the test class with the given name.
     * @param name name of the test class
     * @param methodName name of the test case
     * @return true if the methodName exists in the test class
     */
    public boolean containsTestCase(String name, String methodName) {
        StringBuilder oldContent = readTestFile(name, false);
        if (oldContent == null) return false;
        return TestFileParser.methodExists(methodName, oldContent.toString());
    }

    /**
     * Writes the imports to the file with the given name if they not exists in the file.
     * @param name the name of the file
     * @param imports a list of import statements
     */
    public void writeImports(String name, List<String> imports) {
        File testFile = getTestClassFile(name);
        if (testFile == null) return;
        StringBuilder oldContent = readTestFile(name);
        if (oldContent == null) return;
        String newContent = oldContent.toString();
        for (String importStatement : imports) {
            String importName = importStatement
                    .replace("import ", "")
                    .replace(";\n", "");
            newContent = TestFileParser.addImport(importName, importStatement, newContent);
        }
        write(testFile, newContent);
    }

    /**
     * Returns the test class file if it is present.
     * @param name the name of the test class file
     * @return the test class file
     */
    private File getTestClassFile(String name) {
        String completePath = testPath  + "/" + name + ".java";
        File testFile = new File(completePath);
        if (!testFile.exists()) {
            Logger.logWarning(LOG_INFO + "The file with the name " + name + " does not exist!");
            return null;
        }
        return testFile;
    }

    /**
     * Writes the newContent to the testFile.
     * @param testFile a test file
     * @param newContent new content
     */
    private void write(File testFile, String newContent) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(testFile);
            fileWriter.write(newContent);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(fileWriter);
        }
    }

    /**
     * Closes the fileWriter.
     * @param fileWriter a file writer
     */
    private void closeFile(FileWriter fileWriter) {
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the file with the given name.
     * @param name name of the test file
     * @return true if the deletion was successful
     */
    public boolean deleteTestFile(String name) {
        String completePath = testPath + "/" + name + ".java";
        return new File(completePath).delete();
    }

    /**
     * Deletes the testGen folder.
     * @return true if the deletion was successful
     */
    public boolean deleteFolder() {
        return new File(testPath).delete();
    }

    /**
     *
     * @return the path of the testGen folder
     */
    public String getTestPath() {
        return testPath;
    }

    /**
     *
     * @param testPath the path of the testGen folder
     */
    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

}
