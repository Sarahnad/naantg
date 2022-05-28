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
 * Types for the spring boot controller test status assertions.
 */
public enum StatusType {
    OK ("isOk"),
    FOUND ("isFound"),
    INFO ("is1xxInformational"),
    SUCCESS ("is2xxSuccessful"),
    REDIRECT ("is3xxRedirection"),
    CLIENT_ERROR ("is4xxClientError"),
    SERVER_ERROR ("is5xxServerError"),
    IS ("is");

    private final String type;

    StatusType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
