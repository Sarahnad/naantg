/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.testproject.test;

import de.tudo.naantg.annotations.*;
import de.tudo.naantg.testproject.scheinboot.ScheinDetailService;

@ControllerTG
@AddMocks(ScheinDetailService.class)
public interface ScheinControllerTG {

    @Expect(checkTypes = CheckType.STATUS, getView = "register")
    void whenGetRegister_thenSuccess();

    @Expect(redirectToView = "login")
    void whenGetRegister_thenSuccess_2();

    @Expect(contentContains = "duplicated username")
    void whenGetRegister_thenSuccess_3();

    void whenGetRegister_thenGetViewRegister();

    void whenPostRegister_thenRedirectToLogin();

    @Params(constructor = "p2 = scheinEntity(String, String)")
    void whenPostRegister_thenRedirectToLogin_1();

    @Mocking("createScheinEntity(notNull) = Exception")
    @Expect(contentContains = "already taken")
    void whenPostRegister_thenDisplayError();

    void whenPostRegister_andCreateUser_fromUserService_fails_thenDisplayError();

    @Mocking("ScheinService : findActiveShines() = List")
    @Expect(contentContains = "Active shines")
    void whenLobby_thenShowShines();

    @InitState(controllerParams = "name = Alex:a; password = *; scheinId = 7")
    void whenGetRegister_thenSuccessWithParams();

}
