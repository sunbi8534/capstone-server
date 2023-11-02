package Capstone.server.DTO.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserCourseInfo {
    List<String> currentCourses;
    List<String> pastCourses;
}
