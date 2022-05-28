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
 * All new defined annotation types of this framework.
 */
public enum AnnoType {

    TG ("TG"),
    CONTROLLER ("ControllerTG"),
    REPOSITORY ("RepositoryTG"),
    SERVICE ("ServiceTG"),
    ENTITY ("EntityTG"),
    INTEGRATION ("IntegrationTG"),

    RANDOM_CONFIGS ("RandomConfigs"),

    ASSERT_STATE ("AssertState"),
    INIT_STATE ("InitState"),
    PARAMS ("Params"),

    ADD_MOCKS ("AddMocks"),
    EXPECT ("Expect"),
    MOCKING ("Mocking"),
    MAPPING_VALUE ("MappingValue")

    ;

    private final String type;

    AnnoType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
