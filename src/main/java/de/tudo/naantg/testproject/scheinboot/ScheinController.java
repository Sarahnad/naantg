/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.scheinboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public class ScheinController {

    @Autowired
    private ScheinService scheinService;

    private TestController testController;

    private TestRepository testRepository;

    private Hauptschein hauptschein;

    private ScheinJob scheinJob;

    /**
     *
     */
    @GetMapping("/register")
    public String getRegister(@ModelAttribute("scheinEntity") ScheinEntity scheinEntity) {
        return "register";
    }

    /**
     *
     */
    @PostMapping("/register")
    public String postRegister(Model model, @ModelAttribute("scheinEntity") ScheinEntity scheinEntity) {

        try {
            scheinService.createScheinEntity(scheinEntity);
        } catch (Exception e)  {
            model.addAttribute("error", "The name '" + scheinEntity.getName() + "' is already taken.");
            return "register";
        }
        return "redirect:/login";
    }

    /**
     *
     */
    @GetMapping("/")
    public String lobby(Model model) {

        List<ScheinEntity> actives = scheinService.findActiveShines();

        model.addAttribute("actives", actives);
        return "lobby";
    }

}
