package Capstone.server.Repository;

import Capstone.server.DTO.Profile.DepartmentDto;
import Capstone.server.DTO.Profile.UserCourseInfo;
import Capstone.server.DTO.Profile.UserProfileInfoDto;
import Capstone.server.DTO.Profile.UserProfileInfoForShowDto;
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
    public DepartmentDto getDepartmentInfo(String nickname) {
        String findDeptSql = "select dept_name1, dept_name2 from major_in where name = ?;";
        List<DepartmentDto> departments = jdbcTemplate.query(findDeptSql, (rs, rowNum) -> {
            return new DepartmentDto(rs.getString("dept_name1"),
                    rs.getString("dept_name2"));
        }, nickname);

        return departments.get(0);
    }

    public UserProfileInfo getUserProfileInfo(String nickname) {
        String findUserProfielSql = "select introductio, profile_image_path, point, study_cnt from user where name = ?;";
        List<UserProfileInfo> userInfo = jdbcTemplate.query(findUserProfielSql, (rs, rowNum) -> {
            return new UserProfileInfo(rs.getString("introduction"),
                    rs.getString("profile_image"),
                    rs.getInt("point"),
                    rs.getInt("study_cnt"));
        }, nickname);

        return userInfo.get(0);
    }

    public UserCourseInfo getUserCourseInfo(String nickname) {
        String findCurrentCourseSql = "select course_name from take where nickname = ? and is_now = true;";
        String findPastCourseSql = "select course_name from take where nickname = ? and is_past = true;";
        List<String> currentCourses = jdbcTemplate.query(findCurrentCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);
        List<String> pastCourses = jdbcTemplate.query(findPastCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);

        return new UserCourseInfo(currentCourses, pastCourses);
    }

    public int getUserAskCount(String nickname) {
        String findAskCountSql = "select qa_key from handle where nickname = ? and is_ask = true;";
        List<Integer> counts = jdbcTemplate.query(findAskCountSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });

        return counts.size();
    }

    public int getUserAnswerCount(String nickname) {
        String findAnswerCountSql = "select qa_key from handle where nickname = ? and is_answer = true;";
        List<Integer> counts = jdbcTemplate.query(findAnswerCountSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });

        return counts.size();
    }

    public void setIntroduction(String nickname, String introduction) {
        String setIntroductionSql = "update user set introduction = ? where nickname = ?;";
        jdbcTemplate.update(setIntroductionSql, introduction, nickname);
    }

    public void setNowCourseToPast(String nickname, String courseName) {
        String setNowtoPastSql = "update take set is_now = false, is_past = true where nickname = ? and course_name = ?;";
        jdbcTemplate.update(setNowtoPastSql, nickname, courseName);
    }

    public void setPastCourseToNow(String nickname, String courseName) {
        String setPastToNowSql = "update take set is_now = true, is_past = false where nickname = ? and course_name = ?;";
        jdbcTemplate.update(setPastToNowSql, nickname, courseName);
    }

    public void setDepartment(String nickname, DepartmentDto department) {
        String setDepartmentSql = "update major_in set dept_name1 = ?, dept_name2 = ? where nickname = ?;";
        jdbcTemplate.update(setDepartmentSql, department.getDept_name1(), department.getDept_name2(), nickname);
    }

    public void setCourse(String nickname, List<String> course) {
        String setCourseSql = "insert into take (nickname, course_name, is_now, is_past, is_pick) values (?, ?, ?, ?, ?);";
        for(String c : course) {
            jdbcTemplate.update(setCourseSql, nickname, c, true, false, false);
        }
    }

    public void deleteCourse(String nickname, List<String> course) {
        String deleteCourseSql = "delete from take where nickname = ? and course_name = ?;";
        for(String c : course) {
            jdbcTemplate.update(deleteCourseSql, nickname, c);
        }
    }

    public int getUserHavingPoint(String nickname) {
        String getUserHaingPointSql = "select point from user where nickname = ?;";
        List<Integer> point = jdbcTemplate.query(getUserHaingPointSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("point"));
        });

        return point.get(0);
    }

    public void userQuit(String nickname) {
        String[] userQuitSql = {"delete from user where nickname = ?;", "delete from take where nickname = ?;",
        "delete from major_in where nickname = ?;", "delete from handle where nickname = ?;",
        "delete from relationship where nickname = ?;", "delete from alarm where nickname = ?;",
        "delete from report where nickname = ?;", "delete from notify where nickname = ?;"};

        for(String sql : userQuitSql) {
            jdbcTemplate.update(sql, nickname);
        }
    }

    public void getRelationship(String nickname, UserProfileInfoForShowDto friendInfo) {
        checkRelationship(nickname, friendInfo.getNickname());
        String getRelationshipSql = "select is_pick, is_friend, is_block from relationship where nickname = ?" +
                " and other_nickname = ?;";
        List<Relationship> re = jdbcTemplate.query(getRelationshipSql, (rs, rowNum) -> {
            return new Relationship(rs.getBoolean("is_pick"), rs.getBoolean("is_friend"),
                    rs.getBoolean("is_block"));
        }, nickname, friendInfo.getNickname());
        Relationship relationship = re.get(0);
        friendInfo.setIsPick(relationship.getIs_pick());
        friendInfo.setIsFriend(relationship.getIs_friend());
        friendInfo.setIsBlock(relationship.getIs_block());
    }

    public boolean checkRelationship(String nickname, String otherNickname) {
        String checkRelationshipSql = "select nickname from relationship where nickname = ? and other_nickname = ?;";
        List<String> result = jdbcTemplate.query(checkRelationshipSql, (rs, rowNum) -> {
            return new String(rs.getString("nickname"));
        });

        if (result.isEmpty()) {
            String makeRelationshipSql = "insert into relationship (nickname, other_nickname, is_pick, is_friend, is_block) values" +
                    " (?, ?, false, false, false);";
            return false;
        }
        return true;
    }

    public void setRelationship(String nickname, String otherNickname, String relation) {
        checkRelationship(nickname, otherNickname);
        String getRelationshipSql = "select ? from relationship where nickname = ? and other_nickname = ?;";
        List<Boolean> r = jdbcTemplate.query(getRelationshipSql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean(relation));
        });

        Boolean value = r.get(0);
        String sql = "update relationship set ? = ? where nickname = ? and other_nickname = ?;";
        jdbcTemplate.update(sql, relation, !value, nickname, otherNickname);
    }

    public List<String> getFriendList(String nickname) {
        String getFriendListSql = "select other_nickname from relationship where nickname = ? and is_friend = true;";
        return jdbcTemplate.query(getFriendListSql, (rs, rowNum) -> {
            return new String(rs.getString("other_nickname"));
        }, nickname);
    }

    public List<String> getBlockList(String nickname) {
        String getBlockListSql = "select other_nickname from relationship where nickname = ? and is_block = true;";
        return jdbcTemplate.query(getBlockListSql, (rs, rowNum) -> {
            return new String(rs.getString("other_nickname"));
        }, nickname);
    }

    public List<String> getPickList(String nickname) {
        String getPickListSql = "select other_nickname from relationship where nickname = ? and is_pick = true;";
        return jdbcTemplate.query(getPickListSql,(rs, rowNum) -> {
            return new String(rs.getString("other_nickname"));
        }, nickname);
    }

    public String getProfileImage(String nickname) {
        String getProfileImagePathSql = "select profile_image_path from user where nickname = ?;";
        List<String> imagePath = jdbcTemplate.query(getProfileImagePathSql, (rs, rowNum) -> {
            return new String(rs.getString("profile_image_path"));
        }, nickname);

        return imagePath.get(0);
    }

    @AllArgsConstructor
    @Getter
    class Relationship {
        Boolean is_pick;
        Boolean is_friend;
        Boolean is_block;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public class UserProfileInfo {
        String introduction;
        String profile_image_path;
        int point;
        int study_cnt;
    }
}
