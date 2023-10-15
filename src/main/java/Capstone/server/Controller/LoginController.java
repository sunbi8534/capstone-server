package Capstone.server.Controller;

import Capstone.server.DTO.Login.LoginDataDto;
import Capstone.server.DTO.Login.LoginResultDto;
import Capstone.server.Service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController {
    LoginService loginService;
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @ResponseBody
    @PostMapping("/user/login")
    public LoginResultDto userLogin(@RequestBody LoginDataDto loginDataDto) {
        return loginService.checkLoginInfo(loginDataDto);
    }
}
