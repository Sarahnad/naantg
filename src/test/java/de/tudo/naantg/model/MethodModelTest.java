package de.tudo.naantg.model;

import de.tudo.naantg.testproject.DritteKlasse;
import de.tudo.naantg.testproject.weiter.NochEineKlasse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MethodModelTest {

    @Test
    public void generateMethodStatementTest() {
        MethodModel methodModel = new MethodModel();
        methodModel.setObject("ersteKlasse");
        methodModel.setName("act");
        methodModel.setReturnType("void");
        String expected = "\t\tersteKlasse.act();\n";
        String actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);

        methodModel.setReturnType("boolean");
        expected = "\t\tboolean actual = ersteKlasse.act();\n";
        actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);

        methodModel.setReturnType("List");
        methodModel.getGenerics().add("String");
        expected = "\t\tList<String> actual = ersteKlasse.act();\n";
        actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);
    }

    @Test
    public void generateMethodStatementWithParameters() {
        MethodModel methodModel = new MethodModel();
        methodModel.setObject("nochEineKlasse");
        methodModel.setName("sum");
        methodModel.setReturnType("int");
        Method[] methods = NochEineKlasse.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("sum")) {
                Class<?>[] ptypes = method.getParameterTypes();
                methodModel.setParameters(ptypes);
            }
        }
        String expected = "\t\tint actual = nochEineKlasse.sum(param1, param2);\n";
        String actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);
        assertEquals("int", methodModel.getParameters()[0].getSimpleName());
        assertEquals("int", methodModel.getParameters()[1].getSimpleName());
    }

    @Test
    public void generateMethodStatementOfPreState() {
        MethodModel methodModel = new MethodModel();
        methodModel.setObject("dritteKlasse");
        methodModel.setName("createPerson");
        methodModel.setReturnType("Person");
        methodModel.setReturnIdentifier("param1");
        String expected = "\t\tPerson param1 = dritteKlasse.createPerson();\n";
        String actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);
    }

    @Test
    public void generateMethodStatementOfPreStateWithParameters() throws NoSuchMethodException {
        MethodModel methodModel = new MethodModel();
        methodModel.setObject("dritteKlasse");
        methodModel.setName("createPersonWithBoxes");
        methodModel.setReturnType("Person");
        methodModel.setReturnIdentifier("param1");
        Method method = DritteKlasse.class.getMethod("createPersonWithBoxes", List.class);
        methodModel.setParameters(method.getParameterTypes());
        methodModel.setArgList(new String[] {"boxes"});
        String expected = "\t\tPerson param1 = dritteKlasse.createPersonWithBoxes(boxes);\n";
        String actual = methodModel.generateMethodStatement();
        assertEquals(expected, actual);
    }

}