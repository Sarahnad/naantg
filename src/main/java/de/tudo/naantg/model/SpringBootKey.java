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
 * Special keywords of the Spring Boot framework.
 */
public enum SpringBootKey {

    GENERATED ("GeneratedValue", ""),
    AUTOWIRED ("Autowired", ""),
    MAPPING ("Mapping", "org.springframework.web.bind.annotation"),
    GET ("GetMapping",
            "static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get"),
    POST ("PostMapping",
            "static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post"),
    REQUEST ("RequestMapping", "org.springframework.web.bind.annotation.RequestMapping"),
    SERVICE ("Service", ""),
    MODEL ("ModelAttribute", ""),
    NOT_NULL ("notNull()", "static org.mockito.ArgumentMatchers.notNull"),
    THROW ("doThrow", "static org.mockito.Mockito.doThrow"),
    ASSERT_THROWN ("assertThatThrownBy", "static org.assertj.core.api.Assertions.assertThatThrownBy"),
    SAVE_AND_FLUSH ("saveAndFlush", ""),
    DATA_EXCEPTION ("DataIntegrityViolationException", "org.springframework.dao.DataIntegrityViolationException"),
    FIND_BY_ID ("findById", ""),
    WHEN ("when", "static org.mockito.Mockito.when"),
    ENTITY ("Entity", "javax.persistence.Entity"),
    SUPERCLASS ("MappedSuperclass", "javax.persistence.MappedSuperclass"),
    CONTROLLER ("Controller", ""),
    HAS_PROPERTY ("hasProperty", "static org.hamcrest.Matchers.*")
    ;

    private final String keyword;

    /**
     * the needed import for the perform and check statements
     */
    private final String importPath;

    SpringBootKey(String keyword, String importPath) {
        this.keyword = keyword;
        this.importPath = importPath;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getImportPath() {
        return importPath;
    }

}
