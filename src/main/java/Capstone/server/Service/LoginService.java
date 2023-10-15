package Capstone.server.Service;

import Capstone.server.DTO.Login.LoginDataDto;
import Capstone.server.DTO.Login.LoginResultDto;
import Capstone.server.Repository.LoginRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    UtilService utilService;
    LoginRepository loginRepository;
    public LoginService(UtilService utilService,
                        LoginRepository loginRepository) {
        this.utilService = utilService;
        this.loginRepository = loginRepository;
    }

    public LoginResultDto checkLoginInfo(LoginDataDto loginDataDto) {
        LoginResultDto loginResultDto = new LoginResultDto();
        String passwordHash = utilService.makeHashcode(loginDataDto.getPassword());
        String result = loginRepository.checkLoginInfo(loginDataDto.getEmail(), passwordHash);

        if(result.equals("false")) {
            loginResultDto.setMsg("error");
        } else {
            loginResultDto.setMsg("ok");
            loginResultDto.setNickname(result);
        }

        return loginResultDto;
    }

}
