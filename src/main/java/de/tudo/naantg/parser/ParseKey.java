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

/**
 * Key words for the method name parsing.
 */
public enum ParseKey {

    WHEN ("when"),

    RETURN ("thenReturn"),
    RETURN_NEG ("thenReturnNeg"),
    RETURNED ("thenReturned"),
    RETURN_VALUE ("thenReturnValue"),
    RETURN_NULL ("thenReturnNull"),
    RETURN_EXCEPTION ("thenReturnException"),
    GREATER ("isGreater"),
    SMALLER ("isSmaller"),

    RETURN_LIST("thenReturnList"),
    EMPTY("isEmpty"),
    SIZE ("size"),
    CONTAINS ("contains"),
    NOT_CONTAINS ("notContains"),
    AND ("and"),
    OR ("or"),
    IS ("is"),
    IS_NOT ("isNot"),
    GET ("get"),
    NOT ("not"),
    TRUE ("true"),
    FALSE ("false"),
    NEG ("neg"),
    ALL ("all"),

    THEN_GET ("thenGet"),
    HAS_VALUES ("hasValues"),
    HAS_VALUE ("hasValue"),
    HAS_EQUAL_VALUES ("hasEqualValues"),
    HAS_EQUAL_VALUE ("hasEqualValue"),

    WITH ("with"),
    WITH_VALUES ("withValues"),
    WITH_VALUE ("withValue"),
    VALUES ("values"),

    OF ("of"),
    THIS ("this"),
    PARAM ("param"),
    GIVEN ("given"),
    GIVEN_THIS ("givenThis"),
    GIVEN_TEST ("givenTest"),
    GIVEN_PARAM ("givenParam"),
    GIVEN_STATIC ("givenStatic"),
    OF_THIS ("ofThis"),
    OF_TEST ("ofTest"),
    OF_PARAM ("ofParam"),
    OF_STATIC ("ofStatik"),

    NULL ("null"),
    LIST ("List"),

    /* spring boot specific */

    // Repository
    FIND ("whenFindBy"),

    // Controller
    VIEW ("thenGetView"),
    REDIRECT ("thenRedirectTo"),
    ERROR ("thenDisplayError"),
    NOT_NULL ("notNull")

    ;

    private String keyword;

    ParseKey(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /*@Override
    public String toString() {
        return switch (this) {
            case SMALLER -> "isSmaller";
            case GREATER -> "isGreater";
            case RETURN -> "thenReturn";
            case RETURNED -> "thenReturned";
            case RETURN_NEG -> "thenReturnNeg";
            case RETURN_NULL -> "thenReturnNull";
            case RETURN_VALUE -> "thenReturnValue";
            case RETURN_EXCEPTION -> "thenReturnException";

            case RETURN_LIST -> "thenReturnList";
            case IS -> "is";
            case IS_NOT -> "isNot";
            case OR -> "or";
            case AND -> "and";
            case GET -> "get";
            case SIZE -> "size";
            case EMPTY -> "isEmpty";
            case CONTAINS -> "contains";
            case NOT_CONTAINS -> "notContains";
            case NOT -> "not";
            case TRUE -> "true";
            case FALSE -> "false";
            case NEG -> "neg";
            case ALL -> "all";

            case THEN_GET -> "thenGet";
            case HAS_VALUES -> "hasValues";
            case HAS_VALUE -> "hasValue";
            case HAS_EQUAL_VALUES -> "hasEqualValues";
            case HAS_EQUAL_VALUE -> "hasEqualValue";

            case WITH -> "with";
            case VALUES -> "values";
            case WITH_VALUES -> "withValues";
            case WITH_VALUE -> "withValue";
            case OF -> "of";
            case AFTER -> "after";
            case FROM -> "from";

            // spring boot specific

            // Repository
            case FIND -> "whenFindBy";

            // Controller
            case VIEW -> "thenGetView";
            case REDIRECT -> "thenRedirectTo";
            case ERROR -> "thenDisplayError";
            case NOT_NULL -> "notNull";
        };
    }*/
}
