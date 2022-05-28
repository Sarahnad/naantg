/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.main;



import de.tudo.naantg.annotations.*;
import de.tudo.naantg.creators.*;
import de.tudo.naantg.generator.*;
import de.tudo.naantg.model.GeneratorModel;
import de.tudo.naantg.helpers.Logger;
import de.tudo.naantg.helpers.Scanner;
import de.tudo.naantg.model.Utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Start point for the test generation.
 * It generates tests for all methods that are annotated with @TG and can be found in the testPackage.
 * Therefor TGRunner searches after the corresponding classes and methods in the projectPackage.
 * A suitable test de.tudo.naantg.generator is selected depending on the class annotation of the test generation interfaces.
 * The tests are generated in the testGenPath/testGen or testPackage/testGen folder.
 */
public class TGRunner {

    private final Object testClass;

    /**
     * the context of the logger information
     */
    private final String LOG_INFO = "[TGRunner] ";

    /**
     * test package that contains all test de.tudo.naantg.generator interfaces and methods with @TG
     */
    private String testPackage;

    /**
     * project package that contains all classes under test
     */
    private String projectPackage;

    /**
     * path for the testGen folder where the generated tests can be found
     */
    private String testGenPath;

    /**
     * absolut path for the testGen folder where the generated tests can be found
     */
    private String absolutTestGenPath;

    /**
     * absolut path for the test folder
     */
    private String absolutTestPath;

    /**
     * absolut path for the project folder
     */
    private String absolutProjectPath;

    /**
     * Creates the start point for the test generation.
     * Tests can be generated for all methods of the test generation interfaces annotated with @TG,
     * that can be found in the testPackage and that have correspondig classes and methods in the projectPackage.
     * The tests are generated in the testPackage/testGen folder.
     * @param projectPackage the project package that contains all classes under test
     * @param testPackage the test package that contains all test generation interfaces and methods
     */
    public TGRunner(String projectPackage, String testPackage) {
        this.testClass = null;
        this.testPackage = testPackage;
        this.projectPackage = projectPackage;
    }

    /**
     * Creates the start point for the test generation.
     * Tests can be generated for all methods of the test generation interfaces annotated with @TG,
     * that can be found in the testPackage and that have corresponding classes and methods in the projectPackage.
     * The tests are generated in the testGenPath/testGen folder.
     * @param projectPackage the project package that contains all classes under test
     * @param testPackage the test package that contains all test generation interfaces and methods
     * @param testGenPath the path for the testGen folder
     */
    public TGRunner(String projectPackage, String testPackage, String testGenPath) {
        this(projectPackage, testPackage);
        this.testGenPath = testGenPath;
    }

    /**
     * Not in use!
     * @param testClass the test generation interface
     */
    public TGRunner(Object testClass) {
        this.testClass = testClass;
    }

    /**
     * Starts the generation process.
     * Creates GeneratorModels with all necessary information for the generation process.
     * Selects suitable test generators depending on the class de.tudo.naantg.annotations of the test generation interfaces.
     * If no class annotation is given then the SimpleGenerator is selected.
     */
    public void run() {
        try {
            if ((projectPackage == null || projectPackage.equals("")) &&
                    (absolutProjectPath != null && !absolutProjectPath.equals(""))) {
                projectPackage = absolutProjectPath;
            }
            if (projectPackage == null || projectPackage.equals("")) {
                Logger.logWarning(LOG_INFO + "Please set a path for the project package!");
                return;
            }
            List<Class<?>> classesInTest = Scanner.getClasses(testPackage);
            if (classesInTest.isEmpty()) Logger.logWarning(LOG_INFO + "No class in " + testPackage + " found!");

            for(Class<?> tgClass : classesInTest) {
                List<Method> methods = Scanner.findMethodsWithTG(tgClass);
                if (methods.isEmpty() && !Scanner.hasClassAnnotation(tgClass, AnnoType.ENTITY.getType())) continue;

                Logger.logInfo(LOG_INFO + "Generating tests for " + tgClass.getName());
                ModelCreator modelCreator = createCreator(tgClass);
                TestGenerator testGenerator = createGenerator(tgClass);
                if (modelCreator == null) continue;
                GeneratorModel model = modelCreator.createGeneratorModel(tgClass);
                testGenerator.generateTests(tgClass, model);
            }
        } catch (ClassNotFoundException | IOException e) {
            Logger.logWarning(LOG_INFO + "Error in de.tudo.naantg.generator creation!\n" + e.getMessage());
        }
    }

    /**
     * Creates a suitable test de.tudo.naantg.generator for the tgClass.
     * If no class annotation is given then the SimpleGenerator is selected.
     * @param tgClass the test generation interface that contains test generation methods.
     * @return a suitable TestGenerator
     */
    private TestGenerator createGenerator(Class<?> tgClass) {
        TestGenerator testGenerator;

        if (Scanner.hasClassAnnotation(tgClass, AnnoType.REPOSITORY.getType())) {
            testGenerator = new RepositoryGenerator();
        }
        else if (Scanner.hasClassAnnotation(tgClass, AnnoType.CONTROLLER.getType())) {
            testGenerator = new ControllerGenerator();
        }
        else if (Scanner.hasClassAnnotation(tgClass, AnnoType.SERVICE.getType())) {
            testGenerator = new ServiceGenerator();
        }
        else {
            testGenerator = new SimpleGenerator();
        }

        return testGenerator;
    }

    /**
     * Creates a suitable test de.tudo.naantg.generator for the tgClass.
     * If no class annotation is given then the SimpleGenerator is selected.
     * @param tgClass the test generation interface that contains test generation methods.
     * @return a suitable TestGenerator
     */
    private ModelCreator createCreator(Class<?> tgClass) {
        ModelCreator modelCreator;
        if (Scanner.hasClassAnnotation(tgClass, AnnoType.REPOSITORY.getType())) {
            modelCreator = new RepositoryModelCreator(projectPackage, testPackage, testGenPath);
            modelCreator.setAssertionCreator(new RepositoryAssertionCreator());
            modelCreator.setGivenCut(tgClass.getDeclaredAnnotation(RepositoryTG.class).value());

        }
        else if (Scanner.hasClassAnnotation(tgClass, AnnoType.CONTROLLER.getType())) {
            modelCreator = new ControllerModelCreator(projectPackage, testPackage, testGenPath);
            modelCreator.setAssertionCreator(new ControllerAssertionCreator());
            modelCreator.setGivenCut(tgClass.getDeclaredAnnotation(ControllerTG.class).value());
        }
        else if (Scanner.hasClassAnnotation(tgClass, AnnoType.SERVICE.getType())) {
            modelCreator = new ServiceModelCreator(projectPackage, testPackage, testGenPath);
            modelCreator.setAssertionCreator(new ServiceAssertionCreator());
            modelCreator.setGivenCut(tgClass.getDeclaredAnnotation(ServiceTG.class).value());
        }
        else if (Scanner.hasClassAnnotation(tgClass, AnnoType.INTEGRATION.getType())) {
            modelCreator = new ServiceModelCreator(projectPackage, testPackage, testGenPath);
            modelCreator.setAssertionCreator(new ServiceAssertionCreator());
        }
        else {
            modelCreator = new SimpleModelCreator(projectPackage, testPackage, testGenPath);
            modelCreator.setAssertionCreator(new SimpleAssertionCreator());
            if (Scanner.hasClassAnnotation(tgClass, AnnoType.ENTITY.getType())) {
                modelCreator.setGivenCut(tgClass.getDeclaredAnnotation(EntityTG.class).value());
            }
        }

        if (absolutTestGenPath != null && !absolutTestGenPath.equals("")) {
            modelCreator.setAbsolutTestGenPath(absolutTestGenPath);
        }
        if (absolutTestPath != null && !absolutTestPath.equals("")) {
            modelCreator.setAbsolutTestPath(absolutTestPath);
        }
        if (absolutProjectPath != null && !absolutProjectPath.equals("")) {
            modelCreator.setAbsolutProjectPath(absolutProjectPath);
        }

        if (modelCreator.getTestPath() == null || modelCreator.getTestPath().equals("") ) {
            Logger.logWarning(LOG_INFO + "Please set a path for the test package!");
            return null;
        }
        if (modelCreator.getTestGenPath() == null || modelCreator.getTestGenPath().equals("") ) {
            Logger.logWarning(LOG_INFO + "Please set a path for the test generation package!");
            return null;
        }
        return modelCreator;
    }

    /**
     * Returns the absolut path for the testGen folder.
     * @return the absolut path for the testGen folder
     */
    public String getAbsolutTestGenPath() {
        return absolutTestGenPath;
    }

    /**
     * Sets the absolut path for the testGen folder.
     * @param absolutTestGenPath the absolut path for the testGen folder
     */
    public void setAbsolutTestGenPath(String absolutTestGenPath) {
        this.absolutTestGenPath = absolutTestGenPath;
    }

    /**
     * Returns the absolut path of the test folder.
     * @return the absolut path of the test folder
     */
    public String getAbsolutTestPath() {
        return absolutTestPath;
    }

    /**
     * Sets the absolut path of the test folder.
     * @param absolutTestPath the absolut path of the test folder
     */
    public void setAbsolutTestPath(String absolutTestPath) {
        this.absolutTestPath = absolutTestPath;
    }

    /**
     * Returns the absolut path of the project folder.
     * @return the absolut path of the project folder
     */
    public String getAbsolutProjectPath() {
        return absolutProjectPath;
    }

    /**
     * Sets the absolut path of the project folder.
     * @param absolutProjectPath the absolut path of the project folder
     */
    public void setAbsolutProjectPath(String absolutProjectPath) {
        this.absolutProjectPath = absolutProjectPath;
    }

    /**
     * Executes the test generation process on the "de.tudo.naantg.testproject".
     * @param args not needed
     */
    public static void main(String[] args) {

        String indirectProjectPath = "de.tudo.naantg.testproject";
        String indirectTestPath = "de.tudo.naantg.testproject/test";
        String indirectTestGenPath = "main/java/de.tudo.naantg.testproject/test";

        TGRunner tgRunner = new TGRunner(indirectProjectPath,
                indirectTestPath,
                indirectTestGenPath);

        tgRunner.run();

    }



}
