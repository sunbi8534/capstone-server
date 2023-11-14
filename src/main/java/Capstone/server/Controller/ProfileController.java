package Capstone.server.Controller;

import Capstone.server.DTO.Profile.*;
import Capstone.server.Service.ProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfileController {
    ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @ResponseBody
    @GetMapping("/user/profile")
    public UserProfileInfoDto getProfileInfo(@RequestParam String nickname) {
        return profileService.getProfileInfo(nickname);
    }

    @ResponseBody
    @GetMapping("/user/profile/forShow")
    public UserProfileInfoForShowDto getSomeoneProfile(@RequestParam String nickname, @RequestParam String friendNickname) {
        return profileService.getProfileInfoForShow(nickname, friendNickname);
    }

    @ResponseBody
    @PostMapping("/user/profile/setImage/{nickname}")
    public String setImage(@PathVariable("nickname") String nickname,
                           @RequestBody ImgDto image) {

        profileService.storeProfileImage(nickname, image);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setIntroduction/{nickname}")
    public String setIntroduction(@PathVariable("nickname") String nickname,
                                  @RequestParam String introduction) {
        profileService.setIntroduction(nickname, introduction);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/nowToPastCourse/{nickname}")
    public String changeCourseNowToPast(@PathVariable("nickname") String nickname,
                                        @RequestParam String courseName) {
        profileService.setNowCourseToPast(nickname, courseName);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/PastToNowCourse/{nickname}")
    public String changeCoursePastToNow(@PathVariable("nickname") String nickname,
                                        @RequestParam String courseName) {
        profileService.setPastCourseToNow(nickname, courseName);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setDepartment/{nickname}")
    public String setMajor(@PathVariable("nickname") String nickname,
                           @RequestBody DepartmentDto dept) {

        profileService.setDepartments(nickname, dept);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setCourse/{nickname}")
    public String setCourse(@PathVariable("nickname") String nickname,
                            @RequestBody CourseDto course) {
        profileService.setCourse(nickname, course.getCourse());
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setCourseAll/{nickname}")
    public String setCourseAll(@PathVariable String nickname, @RequestBody CourseAllDto course) {
        profileService.setCourseAll(nickname, course);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/removeCourse/{nickname}")
    public String deleteCourse(@PathVariable("nickname") String nickname,
                               @RequestBody CourseDto courseDto) {

        profileService.deleteCourse(nickname, courseDto.getCourse());
        return "ok";
    }

    @ResponseBody
    @GetMapping("/user/profile/point")
    public int getUserPoint(@RequestParam String nickname) {
        return profileService.getUserHavingPoint(nickname);
    }

    @ResponseBody
    @PostMapping("/user/quit")
    public String userQuit(@RequestParam String nickname) {

        profileService.userQuit(nickname);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setPick/{nickname}")
    public String setPick(@PathVariable("nickname") String nickname,
                          @RequestParam String otherNickname) {
        profileService.setPick(nickname, otherNickname);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setFriend/{nickname}")
    public String setFriend(@PathVariable("nickname") String nickname,
                            @RequestParam String otherNickname) {
        profileService.setFriend(nickname, otherNickname);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/user/profile/setBlock/{nickname}")
    public String setBlock(@PathVariable("nickname") String nickname,
                           @RequestParam String otherNickname) {
        profileService.setBlock(nickname, otherNickname);
        return "ok";
    }

    @ResponseBody
    @GetMapping("/user/profile/friend")
    public List<UserInfoMinimumDto> getFriends(@RequestParam String nickname) {
        return profileService.getFriendInfoList(nickname);
    }

    @ResponseBody
    @GetMapping("/user/profile/block")
    public List<UserInfoMinimumDto> getBlocks(@RequestParam String nickname) {
        return profileService.getBlockInfoList(nickname);
    }

    @ResponseBody
    @GetMapping("/user/profile/pick")
    public List<UserInfoMinimumDto> getPicks(@RequestParam String nickname) {
        return profileService.getPickInfoList(nickname);
    }

    @ResponseBody
    @GetMapping("/user/profile/course")
    public List<String> getUserCourse(@RequestParam String nickname) {
        return profileService.getUserCourse(nickname);
    }

    @ResponseBody
    @GetMapping("/user/profile/image")
    public String getProfileImage(@RequestParam String nickname) {
        return profileService.getProfileImage(nickname);
    }
}
