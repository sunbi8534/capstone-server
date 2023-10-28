package Capstone.server.Service;

import Capstone.server.DTO.Profile.DepartmentDto;
import Capstone.server.DTO.Profile.UserInfoMinimumDto;
import Capstone.server.DTO.Profile.UserProfileInfoDto;
import Capstone.server.DTO.Profile.UserProfileInfoForShowDto;
import Capstone.server.Repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
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
        userInfo.setProfileImage(processImage(nickname, userInfo.getProfileImage()));
        userInfo.setUserCourseInfo(profileRepository.getUserCourseInfo(nickname));
        userInfo.setQuestion(profileRepository.getUserAskCount(nickname));
        userInfo.setAnswer(profileRepository.getUserAnswerCount(nickname));

        return userInfo;
    }

    public UserProfileInfoForShowDto getProfileInfoForShow(String nickname, String friendNickname) {
        UserProfileInfoForShowDto friendInfo = new UserProfileInfoForShowDto();
        friendInfo.setNickname(friendNickname);
        friendInfo.setDepartments(profileRepository.getDepartmentInfo(friendNickname));
        friendInfo.setUserInfo(profileRepository.getUserProfileInfo(friendNickname));
        friendInfo.setProfileImage(processImage(friendNickname, friendInfo.getProfileImage()));
        friendInfo.setQuestion(profileRepository.getUserAskCount(friendNickname));
        friendInfo.setAnswer(profileRepository.getUserAnswerCount(friendNickname));
        profileRepository.getRelationship(nickname, friendInfo);

        return friendInfo;
    }

    public String processImage(String nickname, String profilePath) {
        if (profilePath != null) {
            try {
                byte[] byteFile = Files.readAllBytes(new File("/profileImage/" + nickname + ".png").toPath());
                String img = Base64.getEncoder().encodeToString(byteFile);
                return img;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @AllArgsConstructor
    class Relationship {
        Boolean isPick;
        Boolean isFriend;
        Boolean isBlock;
    }

    public void storeProfileImage(String nickname, String image) {
        try {
            byte[] fileData = Base64Utils.decodeFromString(image);
            String filePath = "/profileImage/" + nickname + ".png";
            File dest = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(dest);
            fileOutputStream.write(fileData);
            fileOutputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
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
        profileRepository.setCourse(nickname, course);
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
            String img = processImage(friend, profileRepository.getProfileImage(friend));
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(friend, img);
            friendInfoList.add(userInfo);
        }

        return friendInfoList;
    }

    public List<UserInfoMinimumDto> getBlockInfoList(String nickname) {
        List<UserInfoMinimumDto> blockInfoList = new ArrayList<>();
        List<String> blockList = profileRepository.getBlockList(nickname);
        for(String block : blockList) {
            String img = processImage(block, profileRepository.getProfileImage(block));
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(block, img);
            blockInfoList.add(userInfo);
        }

        return blockInfoList;
    }

    public List<UserInfoMinimumDto> getPickInfoList(String nickname) {
        List<UserInfoMinimumDto> pickInfoList = new ArrayList<>();
        List<String> pickList = profileRepository.getPickList(nickname);
        for(String pick : pickList) {
            String img = processImage(pick, profileRepository.getProfileImage(pick));
            UserInfoMinimumDto userInfo = new UserInfoMinimumDto(pick, img);
            pickInfoList.add(userInfo);
        }

        return pickInfoList;
    }
}
