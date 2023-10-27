package Capstone.server.Controller;

import Capstone.server.DTO.Profile.UserProfileInfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {
    @ResponseBody
    @GetMapping("/user/profile")
    public UserProfileInfoDto getProfileInfo(@RequestParam String nickname) {
        UserProfileInfoDto userProfileInfoDto = new UserProfileInfoDto();
        //정보를 찾을 때 nickname을 이용해서 정보를 찾는데,
        //user 테이블과 take 테이블에서 정보들을 가져온다.
        //userProfileInfo 변수에 가져온 정보들을 담는다.

        return userProfileInfoDto;
    }
}
