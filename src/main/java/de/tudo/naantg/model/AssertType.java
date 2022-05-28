/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.model;

/**
 * Assertion types.
 */
public enum AssertType {

    EQUALS ("assertEquals"),
    NOTEQUALS ("assertNotEquals"),
    TRUE ("assertTrue"),
    FALSE ("assertFalse"),
    ARRAY_EQUALS ("assertArrayEquals"),
    GREATER (">"),
    SMALLER ("<"),
    EQ ("=="),
    NEQ ("!="),
    THROWS ("assertThrows"),

    // for spring boot repository
    THAT ("assertThat"),
    THROWN ("assertThatThrownBy")
    ;


    private String type;

    AssertType(String type) {
        this.type = type;
    }

    /*@Override
    public String toString() {
        return switch (this) {
            case EQUALS -> "assertEquals";
            case NOTEQUALS -> "assertNotEquals";
            case TRUE -> "assertTrue";
            case FALSE -> "assertFalse";
            case ARRAY_EQUALS -> "assertArrayEquals";
            case GREATER -> ">";
            case SMALLER -> "<";
            case EQ -> "==";
            case NEQ -> "!=";
            case THROWS -> "assertThrows";

            case THAT -> "assertThat";
            case THROWN -> "assertThatThrownBy";
        };
    }*/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
