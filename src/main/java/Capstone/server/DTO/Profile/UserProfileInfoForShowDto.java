package Capstone.server.DTO.Profile;

import Capstone.server.Repository.ProfileRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfileInfoForShowDto {
    String nickname;
    List<String> departments;
    String introduction;
    String profileImage;

    Boolean isPick;
    Boolean isFriend;
    Boolean isBlock;

    int question;
    int answer;
    int studyCnt;
    double review;

    public void setUserInfo(ProfileRepository.UserProfileInfo userProfileInfo) {
        this.setIntroduction(userProfileInfo.getIntroduction());
        this.setProfileImage(userProfileInfo.getProfile_image());
        this.setStudyCnt(userProfileInfo.getStudy_cnt());
    }
    public void setDepartments(DepartmentDto departments) {
        this.departments = new ArrayList<>();
        if(departments.getDept_name1() != null)
            this.departments.add(departments.getDept_name1());
        if(departments.getDept_name2() != null)
            this.departments.add(departments.getDept_name2());
    }
}
