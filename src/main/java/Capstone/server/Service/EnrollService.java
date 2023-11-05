package Capstone.server.Service;

import Capstone.server.DTO.Enroll.UserDto;
import Capstone.server.DTO.Enroll.VerificationCheckDto;
import Capstone.server.DTO.Enroll.VerificationDto;
import Capstone.server.DTO.Login.LoginDataDto;
import Capstone.server.Repository.EnrollRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EnrollService {
    EmailService emailService;
    EnrollRepository enrollRepository;
    UtilService utilService;
    public EnrollService(EmailService emailService,
                         EnrollRepository enrollRepository,
                         UtilService utilService) {
        this.emailService = emailService;
        this.enrollRepository = enrollRepository;
        this.utilService = utilService;
    }
    public int makeVerificatinoNum() {
        return (int) (Math.random() * 8999) + 1000;
    }

    public Boolean checkVerificationHashcode(VerificationCheckDto checkDto) {
        String code = checkDto.getEmail() + Integer.toString(checkDto.getVerificationNum()) + "7101";
        System.out.println(code);
        String hashcode = utilService.makeHashcode(code);
        if (hashcode.equals(checkDto.getVerificationHashcode()))
            return true;
        else
            return false;
    }

    public Boolean checkVerificationTime(long previousEpochSecond) {
        long currentEpochSecond = Instant.now().getEpochSecond();
        if(currentEpochSecond - previousEpochSecond <= 300)
            return true;
        else
            return false;
    }

    public void verificationProcess(String email, VerificationDto verificationDto) {
        //인증번호를 생성하고 메일을 전송함.
        int verificationNum = makeVerificatinoNum();
        emailService.sendMail(email, verificationNum);

        //이메일을 토대로 해시코드를 생성하고 현재시간을 EpochSecond변수에 저장함.
        String code = email + Integer.toString(verificationNum) + "7101";
        String vericationHashcode = utilService.makeHashcode(code);
        long currentEpochSecond = Instant.now().getEpochSecond();

        verificationDto.setVerificationHashcode(vericationHashcode);
        verificationDto.setEpochSecond(currentEpochSecond);
        verificationDto.setMsg("ok");
    }

    public List<String> findCourse(String keyword) {
        return enrollRepository.findCourse(keyword);
    }

    public List<String> findDepartment(String keyword) {
        return enrollRepository.findDepartment(keyword);
    }

    public String checkDuplicateNickname(String nickname) {
        if (enrollRepository.checkDuplicateNickname(nickname))
            return "false";
        else
            return "ok";
    }

    public void enrollUser(UserDto userDto) {
        enrollRepository.enrollUser(userDto);
    }

    public void changePassword(LoginDataDto loginDataDto) {
        enrollRepository.changePassword(loginDataDto);
    }
}
