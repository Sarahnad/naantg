package de.tudo.naantg.parser;


import de.tudo.naantg.testproject.scheinboot.Hauptschein;
import de.tudo.naantg.testproject.scheinboot.ScheinEntity;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationParserTest {

    @Test
    public void parseAssertStateRuleTest() {
        String[] expected = new String[]{"x", "y", "x + y"};
        String[] actual = AnnotationParser.parseAssertStateRule("x, y -> x + y");
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }

        expected = new String[]{"c", "b", "s", "l", "d", "f", "\"<c, b, s, l, d, f> :D\""};
        actual = AnnotationParser.parseAssertStateRule("c, b, s, l, d, f -> \"<c, b, s, l, d, f> :D\"");
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void parseParamsTest() {
        String expected = "val987";
        String[] actual = AnnotationParser.parseParams("val987", 1);
        assert actual != null;
        assertEquals(2, actual.length);
        assertEquals(expected, actual[1]);

        expected = "val987";
        actual = AnnotationParser.parseParams("val987", 2);
        assert actual != null;
        assertEquals(3, actual.length);
        assertEquals(expected, actual[1]);

        expected = "val987";
        actual = AnnotationParser.parseParams("val987, hello, 77", 2);
        assert actual != null;
        assertEquals(4, actual.length);
        assertEquals(expected, actual[1]);
        assertEquals("hello", actual[2]);
        assertEquals("77", actual[3]);

        String expected2 = "789";
        actual = AnnotationParser.parseParams("p1 = val987, p5 = 789", 6);
        assert actual != null;
        assertEquals(7, actual.length);
        assertEquals(expected, actual[1]);
        assertEquals("", actual[2]);
        assertEquals(expected2, actual[5]);

        expected = "{1;4;6;3}";
        actual = AnnotationParser.parseParams("[1,4,6,3]", 1);
        assert actual != null;
        assertEquals(2, actual.length);
        assertEquals(expected, actual[1]);
    }

    @Test
    public void removeBeginningSpaceCharactersTest() {
        String word = "    without beginning spaces";
        word = AnnotationParser.removeBeginningSpaceCharacters(word);
        assertEquals("without beginning spaces", word);
    }

    @Test
    public void parseStringRuleTest() {
        String input = "funny(6, 7, %str(yeah!), 8, %str(with %t and %w), 9)";
        String expected = "funny(6, 7, \"yeah!\", 8, \"with \" + t + \" and \" + w + \"\", 9)";
        String actual = AnnotationParser.parseStringRule(input);
        assertEquals(expected, actual);
    }

    @Test
    public void parseAssertStateReturnedSizeTest() {
        List<String> actual = AnnotationParser.parseAssertStateReturnedSize("5");
        checkMultiParts("SIZE,IS,5", actual);

        actual = AnnotationParser.parseAssertStateReturnedSize("< 5");
        checkMultiParts("SIZE,SMALLER,5", actual);

        actual = AnnotationParser.parseAssertStateReturnedSize("> 5");
        checkMultiParts("SIZE,GREATER,5", actual);

        actual = AnnotationParser.parseAssertStateReturnedSize("!= 5");
        checkMultiParts("SIZE,NOT,5", actual);

        actual = AnnotationParser.parseAssertStateReturnedSize("> 9 and < 11");
        checkMultiParts("SIZE,GREATER,9,AND,SIZE,SMALLER,11", actual);
    }

    @Test
    public void parseAssertStateReturnedTest() {
        List<String> actual = AnnotationParser.parseAssertStateReturned("[5] = 3");
        checkMultiParts("GET,5,IS,3", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1] != 3");
        checkMultiParts("GET,1,NOT,3", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1] < 3");
        checkMultiParts("GET,1,SMALLER,3", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1] > 3");
        checkMultiParts("GET,1,GREATER,3", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1]");
        checkMultiParts("GET,1,IS,TRUE", actual);

        actual = AnnotationParser.parseAssertStateReturned("![1]");
        checkMultiParts("GET,1,IS,FALSE", actual);

        actual = AnnotationParser.parseAssertStateReturned("![1], [2]");
        checkMultiParts("GET,1,IS,FALSE,AND,GET,2,IS,TRUE", actual);

        actual = AnnotationParser.parseAssertStateReturned("![1] or [2]");
        checkMultiParts("GET,1,IS,FALSE,OR,GET,2,IS,TRUE", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1] > 3 or < 9");
        checkMultiParts("GET,1,GREATER,3,OR,GET,1,SMALLER,9", actual);

        actual = AnnotationParser.parseAssertStateReturned("[1] > 3, [1] < 9");
        checkMultiParts("GET,1,GREATER,3,AND,GET,1,SMALLER,9", actual);

        actual = AnnotationParser.parseAssertStateReturned("name = Alex:a");
        checkMultiParts("GET,PARAM,name,IS,Alex:a", actual);
    }

    @Test
    public void parseAssertStateReturnedContainsTest() {
        List<String> actual = AnnotationParser.parseAssertStateReturnedContains("3");
        checkMultiParts("CONTAINS,3", actual);

        actual = AnnotationParser.parseAssertStateReturnedContains("!3");
        checkMultiParts("CONTAINS,NOT,3", actual);

        actual = AnnotationParser.parseAssertStateReturnedContains("3 or 6");
        checkMultiParts("CONTAINS,3,OR,CONTAINS,6", actual);

        actual = AnnotationParser.parseAssertStateReturnedContains("3, 6");
        checkMultiParts("CONTAINS,3,AND,CONTAINS,6", actual);

        actual = AnnotationParser.parseAssertStateReturnedContains("3 or !6");
        checkMultiParts("CONTAINS,3,OR,CONTAINS,NOT,6", actual);

        actual = AnnotationParser.parseAssertStateReturnedContains("!3 and 6");
        checkMultiParts("CONTAINS,NOT,3,AND,CONTAINS,6", actual);
    }

    private void checkMultiParts(String expected, List<String> actual) {
        String[] parts = expected.split(",");
        assertEquals(parts.length, actual.size());
        for (int i = 0; i < parts.length; i++) {
            assertEquals(parts[i], actual.get(i));
        }
    }

    @Test
    public void parseInitStateMethodsTest() {
        String initValue = "p1 = createPersonWithBoxes(boxes); boxes = createBoxes()";
        String expected = "VALUE,param1,OF,createPersonWithBoxes,PARAM,boxes,VALUE,boxes,OF,createBoxes";
        List<String> actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p1 = createPersonWithBoxes(p2); content = createBoxes()";
        expected = "VALUE,param1,OF,createPersonWithBoxes,PARAM,param2,VALUE,content,OF,createBoxes";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "person = createPersonWithBoxes(boxes, p2); content = createBoxes()";
        expected = "VALUE,person,OF,createPersonWithBoxes,PARAM,boxes,PARAM,param2,VALUE,content,OF,createBoxes";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p1 = createPerson";
        expected = "VALUE,param1,OF,createPerson";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p1 = createPerson()";
        expected = "VALUE,param1,OF,createPerson";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "initPerson";
        expected = "VALUE,VOID,OF,initPerson";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "initPerson()";
        expected = "VALUE,VOID,OF,initPerson";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p1.init";
        expected = "VALUE,VOID,FROM,param1,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p1.init(this)";
        expected = "VALUE,VOID,FROM,param1,OF,init,PARAM,THIS";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "this.init()";
        expected = "VALUE,VOID,FROM,THIS,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "this.field.field2.init()";
        expected = "VALUE,VOID,FROM,THIS,FIELD,field,FIELD,field2,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "p3.field.field2.init()";
        expected = "VALUE,VOID,FROM,param3,FIELD,field,FIELD,field2,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "identifier = StaticClass.init()";
        expected = "VALUE,identifier,FROM,TYPE,StaticClass,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);

        initValue = "identifier.init()";
        expected = "VALUE,VOID,FROM,identifier,OF,init";
        actual = AnnotationParser.parseInitStateMethods(initValue);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseConstructorsTest() {
        String definition = "p2 = ScheinEntity(String, String); Hauptschein(int)";
        String expected = "INDEX,2,CONSTRUCTOR,ScheinEntity,PARAM,String,PARAM,String,INDEX,1,CONSTRUCTOR,Hauptschein,PARAM,int";
        List<String> actual = AnnotationParser.parseConstructors(definition, new Class<?>[] {Hauptschein.class, ScheinEntity.class});
        checkMultiParts(expected, actual);

        definition = "p2 = ScheinEntity(String, String)";
        expected = "INDEX,2,CONSTRUCTOR,ScheinEntity,PARAM,String,PARAM,String";
        actual = AnnotationParser.parseConstructors(definition, new Class<?>[] {Model.class, ScheinEntity.class});
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseMockingValueTest() {
        String definition = "ScheinService : createScheinEntity(notNull) = Exception";
        String expected = "MOCK_CLASS,ScheinService,MOCK_METHOD,createScheinEntity,PARAM,NOT_NULL,VALUE,EXCEPTION";
        List<String> actual = AnnotationParser.parseMockingValue(definition);
        checkMultiParts(expected, actual);

        definition = "createScheinEntity(notNull) = Exception";
        expected = "MOCK_CLASS,DEFAULT,MOCK_METHOD,createScheinEntity,PARAM,NOT_NULL,VALUE,EXCEPTION";
        actual = AnnotationParser.parseMockingValue(definition);
        checkMultiParts(expected, actual);

        definition = "OwnerRepository: findById(OWNER_ID) = Owner; " +
                "PetRepository: findById(TEST_PET_ID) = Pet; " +
                "findPetTypes() = List(cat)";
        expected = "MOCK_CLASS,OwnerRepository,MOCK_METHOD,findById,PARAM,OWNER_ID,VALUE,Owner," +
                "MOCK_CLASS,PetRepository,MOCK_METHOD,findById,PARAM,TEST_PET_ID,VALUE,Pet," +
                "MOCK_CLASS,DEFAULT,MOCK_METHOD,findPetTypes,PARAM,,VALUE,List(cat)";
        actual = AnnotationParser.parseMockingValue(definition);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseInitStateControllerParamsTest() {
        String input = "name = ale; age = 33; password = dfe763tj";
        String expected = "name,ale,age,33,password,dfe763tj";
        List<String> actual = AnnotationParser.parseInitStateControllerParams(input);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseExpectModelAttrExistTest() {
        String input = "name; password; !pet";
        String expected = "name,password,NOT,pet";
        List<String> actual = AnnotationParser.parseExpectModelAttrExist(input);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseExpectModelAttrAndErrorsTest() {
        String input = "attr1 = value1; attr2.field = value2;";
        String expected = "ATTRIBUTE,attr1,VALUE,value1,ATTRIBUTE,attr2,FIELD,field,VALUE,value2";
        List<String> actual = AnnotationParser.parseExpectModelAttrAndErrors(input);
        checkMultiParts(expected, actual);

        input = "attr3 = *; attr4.field = *; attr5: all = *";
        expected = "ATTRIBUTE,attr3,VALUE,*,ATTRIBUTE,attr4,FIELD,field,VALUE,*,ATTRIBUTE,attr5,TYPE,ALL,VALUE,*";
        actual = AnnotationParser.parseExpectModelAttrAndErrors(input);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseExpectPathTest() {
        String input = "json: $._embedded.guides[0].projects[0] = spring-boot";
        String expected = "TYPE,json,PATH,$._embedded.guides[0].projects[0],VALUE,spring-boot";
        List<String> actual = AnnotationParser.parseExpectPath(input);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseMappingValueTest() {
        String input = "int name = 2";
        String expected = "TYPE,int,PARAM,name,VALUE,2";
        List<String> actual = AnnotationParser.parseMappingValue(input);
        checkMultiParts(expected, actual);

        input = "int name";
        expected = "TYPE,int,PARAM,name";
        actual = AnnotationParser.parseMappingValue(input);
        checkMultiParts(expected, actual);

        input = "int name; String password = rhiuef";
        expected = "TYPE,int,PARAM,name,TYPE,String,PARAM,password,VALUE,rhiuef";
        actual = AnnotationParser.parseMappingValue(input);
        checkMultiParts(expected, actual);

        input = "String name = Uye Jeya";
        expected = "TYPE,String,PARAM,name,VALUE,Uye Jeya";
        actual = AnnotationParser.parseMappingValue(input);
        checkMultiParts(expected, actual);
    }

    @Test
    public void parseInitStateValueTest() {
        String input = "int name = 2";
        String expected = "TYPE,int,PARAM,name,VALUE,2";
        List<String> actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "int name";
        expected = "TYPE,int,PARAM,name";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "int name; String password = rhiuef";
        expected = "TYPE,int,PARAM,name,TYPE,String,PARAM,password,VALUE,rhiuef";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "String name = Uye Jeya";
        expected = "TYPE,String,PARAM,name,VALUE,Uye Jeya";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "name.field1.field2 = name2";
        expected = "FROM,name,FIELD,field1,FIELD,field2,VALUE,name2";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "p3.field1.field2 = name2";
        expected = "FROM,param3,FIELD,field1,FIELD,field2,VALUE,name2";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "this.field1.field2 = name2";
        expected = "FROM,THIS,FIELD,field1,FIELD,field2,VALUE,name2";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "this.field1 = null";
        expected = "FROM,THIS,FIELD,field1,VALUE,NULL";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "int a = 3; int b = 4";
        expected = "TYPE,int,PARAM,a,VALUE,3,TYPE,int,PARAM,b,VALUE,4";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "List boxes";
        expected = "TYPE,List,PARAM,boxes";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "List<Box> boxes = null";
        expected = "TYPE,List<Box>,PARAM,boxes,VALUE,NULL";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "this.points = 50";
        expected = "FROM,THIS,FIELD,points,VALUE,50";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);

        input = "Person p; List ps = [p]; p.name = Jan*a; this.komplex.persons = ps";
        expected = "TYPE,Person,PARAM,p,TYPE,List,PARAM,ps,VALUE,[p],FROM,p,FIELD,name,VALUE,Jan*a," +
                "FROM,THIS,FIELD,komplex,FIELD,persons,VALUE,ps";
        actual = AnnotationParser.parseInitStateValue(input);
        checkMultiParts(expected, actual);
    }

}