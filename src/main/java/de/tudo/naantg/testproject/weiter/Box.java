/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.weiter;

public class Box {

    private int boxId;

    private Inhalt content;

    private boolean hasGames;


    public Box(int boxId) {
        this.boxId = boxId;
    }

    public Inhalt getContent() {
        return content;
    }

    public void setContent(Inhalt content) {
        this.content = content;
    }

    public boolean isHasGames() {
        return hasGames;
    }

    public void setHasGames(boolean hasGames) {
        this.hasGames = hasGames;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }
}
