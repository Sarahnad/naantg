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
 * Simple data types.
 */
public enum SimpleDataType {
    INT ("int", int.class),
    INTEGER ("Integer", Integer.class),
    SHORT ("short", short.class),
    B_Short ("Short", Short.class),
    LONG ("long", long.class),
    B_LONG ("Long", Long.class),
    FLOAT ("float", float.class),
    B_FLOAT ("Float", Float.class),
    DOUBLE ("double", double.class),
    B_DOUBLE ("Double", Double.class),
    STRING ("String", String.class),
    CHAR ("char", char.class),
    CHARACTER("Character", Character.class),
    BOOLEAN ("boolean", boolean.class),
    B_BOOLEAN ("Boolean", Boolean.class),
    BYTE ("byte", byte.class),
    B_BYTE ("Byte", Byte.class);

    private String type;

    private Class<?> typeClass;

    SimpleDataType(String type, Class<?> typeClass) {
        this.type = type;
        this.typeClass = typeClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    public static Class<?> getTypeClass(String type) {
        switch (type) {
            case "int": return INT.typeClass;
            case "Integer": return INTEGER.typeClass;
            case "short": return SHORT.typeClass;
            case "Short": return B_Short.typeClass;
            case "long": return LONG.typeClass;
            case "Long": return B_LONG.typeClass;
            case "float": return FLOAT.typeClass;
            case "Float": return B_FLOAT.typeClass;
            case "double": return DOUBLE.typeClass;
            case "Double": return B_DOUBLE.typeClass;
            case "String": return STRING.typeClass;
            case "char": return CHAR.typeClass;
            case "Character": return CHARACTER.typeClass;
            case "boolean": return BOOLEAN.typeClass;
            case "Boolean": return B_BOOLEAN.typeClass;
            case "byte": return BYTE.typeClass;
            case "Byte": return B_BYTE.typeClass;
            default: return NullPointerException.class;
        }
    }
}
