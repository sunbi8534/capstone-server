package Capstone.server.Controller;

import Capstone.server.DTO.Enroll.UserDto;
import Capstone.server.DTO.Enroll.VerificationCheckDto;
import Capstone.server.DTO.Enroll.VerificationDto;
import Capstone.server.DTO.Login.LoginDataDto;
import Capstone.server.Service.EmailService;
import Capstone.server.Service.EnrollService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EnrollController {
    EmailService emailService;
    EnrollService enrollService;

    public EnrollController(EmailService emailService,
                            EnrollService enrollService) {
        this.emailService = emailService;
        this.enrollService = enrollService;
    }

    @ResponseBody
    @GetMapping("/find/department")
    public List<String> findDepartment(@RequestParam String keyword) {
        return enrollService.findDepartment(keyword);
    }

    @ResponseBody
    @GetMapping("/find/course")
    public List<String> findCourse(@RequestParam String keyword) {
        return enrollService.findCourse(keyword);
    }

    @ResponseBody
    @PostMapping("/user/authenticate")
    public VerificationDto checkEmailEnroll(@RequestBody String email) {
        VerificationDto verificationDto = new VerificationDto();
        //이메일과 관련해서 에러가 존재한다면
        if(emailService.checkEmailError(email, verificationDto, true))
            return verificationDto;

        enrollService.verificationProcess(email, verificationDto);
        return verificationDto;
    }

    @ResponseBody
    @PostMapping("/find/authenticate/pw")
    public VerificationDto checkEmailPw(@RequestBody String email) {
        VerificationDto verificationDto = new VerificationDto();

        if(emailService.checkEmailError(email, verificationDto, false))
            return verificationDto;

        enrollService.verificationProcess(email, verificationDto);
        return verificationDto;
    }

    @ResponseBody
    @PostMapping("/user/changePassword")
    public String changePassword(@RequestBody LoginDataDto loginDataDto) {
        enrollService.changePassword(loginDataDto);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/authenticate/check")
    public String checkCode(@RequestBody VerificationCheckDto info) {
        if (!enrollService.checkVerificationTime(info.getEpochSecond())
        || !enrollService.checkVerificationHashcode(info))
            return "false";
        else
            return "ok";
    }

    @ResponseBody
    @GetMapping("/user/nickname/dupCheck")
    public String dupCheckNickname(@RequestParam String nickname) {
        return enrollService.checkDuplicateNickname(nickname);
    }

    @ResponseBody
    @PostMapping("/user/enroll")
    public String enroll(@RequestBody UserDto userDto) {
        enrollService.enrollUser(userDto);
        return "ok";
    }
}
