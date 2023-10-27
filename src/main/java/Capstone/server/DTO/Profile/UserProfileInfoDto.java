package Capstone.server.DTO.Profile;

import java.util.List;

public class UserProfileInfoDto {
    List<String> departments;
    String introduction;
    List<String> currentCourses;
    List<String> pastCourses;
    String profileImage; //이미지를 Base64로 인코딩한 문자열

    int point;
    int question;
    int answer;
    int studyCnt;
}
