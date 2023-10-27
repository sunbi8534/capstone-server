package Capstone.server.Service;

import Capstone.server.DTO.Profile.UserProfileInfoDto;
import Capstone.server.Repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    ProfileRepository profileRepository;
    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    public UserProfileInfoDto getProfileInfo(String nickname) {
        UserProfileInfoDto userProfileInfoDto = new UserProfileInfoDto();
        ProfileRepository.Departments departments = profileRepository.getDepartmentInfo(nickname);
        ProfileRepository.UserProfileInfo userInfo = profileRepository.getUserProfileInfo(nickname);
        ProfileRepository.UserCourseInfo courseInfo = profileRepository.getUserCourseInfo(nickname);

        return userProfileInfoDto;
    }
}
