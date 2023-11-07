package Capstone.server.Service;

import Capstone.server.DTO.Enroll.VerificationDto;
import Capstone.server.Repository.EnrollRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {
    private JavaMailSender javaMailSender;
    private EnrollRepository enrollRepository;

    public EmailService(JavaMailSender javaMailSender,
                        EnrollRepository enrollRepository) {
        this.javaMailSender = javaMailSender;
        this.enrollRepository = enrollRepository;
    }

    public void sendMail(String toEmailAddr, int verificationNum) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("sunbi8534@gmail.com");
        msg.setTo(toEmailAddr);
        msg.setSubject("[유니스 인증번호 메일입니다]");
        msg.setText("인증번호는 \"" + Integer.toString(verificationNum) + "\" 입니다.");

        javaMailSender.send(msg);
    }

    public Boolean checkEmailError(String email, VerificationDto verificationDto, Boolean type) {
        if (!checkEmailForm(email)) {  //이메일 형식이 맞는지 체크
            verificationDto.setMsg("noEmailForm");
            return true;
        }
        else if (enrollRepository.checkDuplicateEmail(email)) {   //중복되는 이메일이 있는지 체크
            if (type) {
                verificationDto.setMsg("duplicate");
                return true;
            }
            else {
                return false;
            }
        } else {
            if (type)
                return false;
            else {
                verificationDto.setMsg("noId");
                return true;
            }
        }
    }

    public boolean checkEmailForm(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@cau.ac.kr$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
