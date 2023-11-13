package Capstone.server.Service;

import Capstone.server.DTO.Profile.*;
import Capstone.server.Repository.ProfileRepository;
import Capstone.server.Repository.QaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileService {
    ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    public UserProfileInfoDto getProfileInfo(String nickname) {
        UserProfileInfoDto userInfo = new UserProfileInfoDto();
        userInfo.setDepartments(profileRepository.getDepartmentInfo(nickname));
        userInfo.setUserInfo(profileRepository.getUserProfileInfo(nickname));
        userInfo.setProfileImage(profileRepository.getProfileImage(nickname));
        userInfo.setUserCourseInfo(profileRepository.getUserCourseInfo(nickname));
        userInfo.setQuestion(profileRepository.getUserAskCount(nickname));
        userInfo.setAnswer(profileRepository.getUserAnswerCount(nickname));
        userInfo.setReview(getUserReview(nickname));
        return userInfo;
    }

    public UserProfileInfoForShowDto getProfileInfoForShow(String nickname, String friendNickname) {
        UserProfileInfoForShowDto friendInfo = new UserProfileInfoForShowDto();
        friendInfo.setNickname(friendNickname);
        friendInfo.setDepartments(profileRepository.getDepartmentInfo(friendNickname));
        friendInfo.setUserInfo(profileRepository.getUserProfileInfo(friendNickname));
        friendInfo.setProfileImage(profileRepository.getProfileImage(friendNickname));
        friendInfo.setQuestion(profileRepository.getUserAskCount(friendNickname));
        friendInfo.setAnswer(profileRepository.getUserAnswerCount(friendNickname));
        friendInfo.setReview(getUserReview(friendNickname));
        profileRepository.getRelationship(nickname, friendInfo);

        return friendInfo;
    }

    @AllArgsConstructor
    class Relationship {
        Boolean isPick;
        Boolean isFriend;
        Boolean isBlock;
    }

    public void storeProfileImage(String nickname, ImgDto image) {
        profileRepository.setProfileImage(nickname, image.getImg());
    }

    public void setIntroduction(String nickname, String introduction) {
        profileRepository.setIntroduction(nickname, introduction);
    }

    public void setNowCourseToPast(String nickname, String courseName) {
        profileRepository.setNowCourseToPast(nickname, courseName);
    }

    public void setPastCourseToNow(String nickname, String courseName) {
        profileRepository.setPastCourseToNow(nickname, courseName);
    }

    public void setDepartments(String nickname, DepartmentDto departments) {
        profileRepository.setDepartment(nickname, departments);
    }

    public void setCourse(String nickname, List<String> course) {
        profileRepository.setCourse(nickname, course, true);
    }

    public void setCourseAll(String nickname, CourseAllDto course) {
        profileRepository.setCourseAll(nickname, course);
    }

    public void deleteCourse(String nickname, List<String> course) {
        profileRepository.deleteCourse(nickname, course);
    }

    public int getUserHavingPoint(String nickname) {
        return profileRepository.getUserHavingPoint(nickname);
    }

    public void userQuit(String nickname) {
        profileRepository.userQuit(nickname);
    }

    public void setPick(String nickname, String otherNickname) {
        profileRepository.setRelationship(nickname, otherNickname, "is_pick");
    }

    public void setFriend(String nickname, String otherNickname) {
        profileRepository.setRelationship(nickname, otherNickname, "is_friend");
    }

    public void setBlock(String nickname, String otherNickname) {
        profileRepository.setRelationship(nickname, otherNickname, "is_block");
    }

    public List<UserInfoMinimumDto> getFriendInfoList(String nickname) {
        List<UserInfoMinimumDto> friendInfoList = new ArrayList<>();
        List<String> friendList = profileRepository.getFriendList(nickname);
        for(String friend : friendList) {
            String img = getProfileImage(friend);
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(friend, img);
            friendInfoList.add(userInfo);
        }

        return friendInfoList;
    }

    public List<UserInfoMinimumDto> getBlockInfoList(String nickname) {
        List<UserInfoMinimumDto> blockInfoList = new ArrayList<>();
        List<String> blockList = profileRepository.getBlockList(nickname);
        for(String block : blockList) {
            String img = getProfileImage(block);
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(block, img);
            blockInfoList.add(userInfo);
        }

        return blockInfoList;
    }

    public List<UserInfoMinimumDto> getPickInfoList(String nickname) {
        List<UserInfoMinimumDto> pickInfoList = new ArrayList<>();
        List<String> pickList = profileRepository.getPickList(nickname);
        for(String pick : pickList) {
            String img = getProfileImage(pick);
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(pick, img);
            pickInfoList.add(userInfo);
        }

        return pickInfoList;
    }

    public String getProfileImage(String nickname) {
        return profileRepository.getProfileImage(nickname);
    }

    public List<String> getUserCourse(String nickname) {
        UserCourseInfo userCourse = profileRepository.getUserCourseInfo(nickname);
        List<String> course = new ArrayList<>();
        course.addAll(userCourse.getPastCourses());
        course.addAll(userCourse.getCurrentCourses());
        return course;
    }

    public double getUserReview(String nickname) {
        List<Integer> reviewValue = profileRepository.getUserReview(nickname);
        int sum = 0;
        if (reviewValue == null)
            return 0;
        else {
            for(int v : reviewValue)
                sum += v;

            return (double) sum / reviewValue.size();
        }
    }
}
