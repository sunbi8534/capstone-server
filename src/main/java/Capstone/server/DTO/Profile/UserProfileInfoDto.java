package Capstone.server.DTO.Profile;

import Capstone.server.Repository.ProfileRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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

    public void setDepartments(DepartmentDto departments) {
        this.departments.add(departments.getDept_name1());
        this.departments.add(departments.getDept_name2());
    }

    public void setUserInfo(ProfileRepository.UserProfileInfo userProfileInfo) {
        this.setIntroduction(userProfileInfo.getIntroduction());
        this.setProfileImage(userProfileInfo.getProfile_image_path());
        this.setPoint(userProfileInfo.getPoint());
        this.setStudyCnt(userProfileInfo.getStudy_cnt());
    }

    public void setUserCourseInfo(ProfileRepository.UserCourseInfo userCourseInfo) {
        this.setCurrentCourses(userCourseInfo.getCurrentCourses());
        this.setPastCourses(userCourseInfo.getPastCourses());
    }
}
