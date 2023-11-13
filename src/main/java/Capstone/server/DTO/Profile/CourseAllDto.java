package Capstone.server.DTO.Profile;

import lombok.Getter;

import java.util.List;

@Getter
public class CourseAllDto {
    List<String> currentCourses;
    List<String> pastCourses;
}
