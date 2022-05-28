/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.annotations;

/**
 * Types for the spring boot controller test assertions.
 */
public enum CheckType {

    PRINT ("static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print"),
    STATUS ("static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*"),
    VIEW ("static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*"),
    REDIRECT ("static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*"),
    ERROR ("static org.mockito.Mockito.doThrow"),
    CONTENT_CONTAINS ("static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*," +
            "static org.hamcrest.Matchers.containsString"),
    ALL (""),
    NONE ("")
    ;

    /**
     * the needed import information
     */
    private final String importPaths;

    CheckType(String importPaths) {
        this.importPaths = importPaths;
    }

    /**
     * Returns the paths of the needed imports.
     * Multiple paths are separated with comma.
     * @return the paths of the needed imports
     */
    public String getImportPaths() {
        return importPaths;
    }

}
