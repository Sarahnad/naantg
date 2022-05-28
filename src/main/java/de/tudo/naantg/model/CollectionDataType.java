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

import java.util.*;
import java.util.List;

/**
 * Data types for collections
 */
public enum CollectionDataType {
    LIST ("List", List.class),
    COLLECTION ("Collection", Collection.class),
    ARRAY_LIST ("ArrayList", ArrayList.class),
    LINKED_LIST ("LinkedList", LinkedList.class),
    ARRAYS ("Arrays", Arrays.class),

    OPTIONAL ("Optional", Optional.class),
    MAP ("Map", Map.class),
    HASH_MAP ("HashMap", HashMap.class);



    private String type;

    private Class<?> typeClass;

    CollectionDataType(String type, Class<?> typeClass) {
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

    @Override
    public String toString() {
        String path = "java.util.";
        switch (this) {
            case LIST: return path + "List";
            case COLLECTION: return path + "Collection";
            case ARRAYS: return path + "Arrays";
            case ARRAY_LIST: return path + "ArrayList";
            case LINKED_LIST: return path + "LinkedList";
            case OPTIONAL: return path + "Optional";
            case MAP: return path + "Map";
            case HASH_MAP: return path + "HashMap";
        };
        return "";
    }

    public static Class<?> getTypeClass(String type) {
        switch (type) {
            case "List": return LIST.typeClass;
            case "Collection": return COLLECTION.typeClass;
            case "Arrays": return ARRAYS.typeClass;
            case "ArrayList": return ARRAY_LIST.typeClass;
            case "LinkedList": return LINKED_LIST.typeClass;
            case "Optional": return OPTIONAL.typeClass;
            case "Map": return MAP.typeClass;
            case "HashMap": return HASH_MAP.typeClass;
            default: return NullPointerException.class;
        }
    }

}
