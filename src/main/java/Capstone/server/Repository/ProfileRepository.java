package Capstone.server.Repository;

import Capstone.server.DTO.Profile.UserProfileInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileRepository {
    JdbcTemplate jdbcTemplate;
    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Departments getDepartmentInfo(String nickname) {
        String findDeptSql = "select dept_name1, dept_name2 from major_in where name = ?;";
        List<Departments> departments = jdbcTemplate.query(findDeptSql, (rs, rowNum) -> {
            return new Departments(rs.getString("dept_name1"),
                    rs.getString("dept_name2"));
        }, nickname);

        return departments.get(0);
    }

    public UserProfileInfo getUserProfileInfo(String nickname) {
        String findUserProfielSql = "select introductio, profile_image, point, study_cnt from user where name = ?;";
        List<UserProfileInfo> userInfo = jdbcTemplate.query(findUserProfielSql, (rs, rowNum) -> {
            return new UserProfileInfo(rs.getString("introduction"),
                    rs.getString("profile_image"),
                    rs.getInt("point"),
                    rs.getInt("study_cnt"));
        }, nickname);

        return userInfo.get(0);
    }

    public UserCourseInfo getUserCourseInfo(String nickname) {
        String findCurrentCourseSql = "select course_name from take where nickname = ? and is_now = True;";
        String findPastCourseSql = "select course_name from take where nickname = ? and is_past = True;";
        List<String> currentCourses = jdbcTemplate.query(findCurrentCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);
        List<String> pastCourses = jdbcTemplate.query(findPastCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);

        return new UserCourseInfo(currentCourses, pastCourses);
    }

    public int getUserAskCount(String nickname) {
        String findAskCountSql = "select qa_key from handle where nickname = ? and is_ask = True;";
        List<Integer> counts = jdbcTemplate.query(findAskCountSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });

        return counts.size();
    }

    public int getUserAnswerCount(String nickname) {
        String findAnswerCountSql = "select qa_key from handle where nickname = ? and is_answer = True;";
        List<Integer> counts = jdbcTemplate.query(findAnswerCountSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });

        return counts.size();
    }




    @AllArgsConstructor
    @Getter
    @Setter
    public class UserCourseInfo {
        List<String> currentCourses;
        List<String> pastCourses;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class UserProfileInfo {
        String introduction;
        String profile_image;
        int point;
        int study_cnt;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class Departments {
        String dept_name1;
        String dept_name2;
    }
}
