package Capstone.server.Repository;

import Capstone.server.DTO.Study.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudyRepository {
    JdbcTemplate jdbcTemplate;

    public StudyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StudyInfoDto> getStudyRoomList(String nickname, RoomStatusDto roomStatus) {
        String findCourseSql = "select course_name from take where nickname = ?;";
        List<String> course = jdbcTemplate.query(findCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);

        String findStudySql = "select room_key, room_name, course_name, max_num," +
                " cur_num, leader, start_date, is_open, study_introduction from study_info where course_name = ?;";

        List<StudyInfoDto> studyInfoDtos = new ArrayList<>();
        for(String c : course) {
            List<StudyInfoDto> infos = jdbcTemplate.query(findStudySql, (rs, rowNum) -> {
                return new StudyInfoDto(rs.getInt("room_key"), rs.getString("room_name"),
                        rs.getString("course_name"), rs.getInt("max_num"), rs.getInt("cur_num"),
                        rs.getString("leader"), rs.getString("start_date"), rs.getBoolean("is_open"),
                        rs.getString("study_introduction"));
            }, nickname);
            studyInfoDtos.addAll(infos);
        }

        if (roomStatus.getIsAll())
            return studyInfoDtos;
        else {
            List<StudyInfoDto> removeStudies = new ArrayList<>();
            if (roomStatus.getIsSeatLeft()) {
                for(StudyInfoDto s : studyInfoDtos) {
                    if (s.getMaxNum() == s.getCurNum())
                        removeStudies.add(s);
                }
            }
            if(roomStatus.getIsOpen()) {
                for(StudyInfoDto s : studyInfoDtos) {
                    if (!s.getIsOpen())
                        removeStudies.add(s);
                }
            }
            studyInfoDtos.removeAll(removeStudies);
            return studyInfoDtos;
        }
    }

    public String makeStudyRoom(StudyMakeDto info) {
        if(!checkDupRoomName(info.getRoomName()))
            return "false";

        String makeStudySql = "insert into study_info (room_name, course_name, max_num," +
                " cur_num, leader, start_date, is_open, code, study_introduction) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(makeStudySql, info.getRoomName(), info.getCourse(), info.getMaxNum(), 0,
                info.getLeader(), info.getStartDate(), info.getIsOpen(), info.getCode(), info.getStudyIntroduction());

        String getKeySql = "select room_key from study_info where room_name = ?;";
        List<Integer> key = jdbcTemplate.query(getKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("room_key"));
        }, info.getRoomName());

        String insertChatInSql = "insert into study_chat_in (nickname, msg_num, is_on, roomKey)" +
                " values (?, ?, ?, ?);";
        jdbcTemplate.update(insertChatInSql, info.getLeader(), 0, false, key.get(0));

        makeStudyChatTable(key.get(0));
        return "ok";
    }

    public void makeStudyChatTable(int roomKey) {
        String chatRoomName = "study_chat_" + String.valueOf(roomKey);
        String makeTableSql = "create table " + chatRoomName + " (msg_num integer AUTO_INCREMENT, " +
                "nickname varchar(60), type varchar(50), msg varchar(200), image MEDIUMTEXT, time varchar(50)," +
                " primary key(msg_num));";
        jdbcTemplate.update(makeTableSql);
    }
    public boolean checkDupRoomName(String roomName) {
        String findDupSql = "select room_name from study_info where room_name = ?;";
        List<String> room = jdbcTemplate.query(findDupSql, (rs, rowNum) -> {
            return new String(rs.getString("room_name"));
        }, roomName);

        if(room.isEmpty())
            return true;
        else
            return false;
    }

    public String joinStudy(String nickname, StudyJoinDto joinInfo) {
        String checkCodeSql = "select is_open, code from study_info where room_key = ?;";
        List<RoomJoinInfo> v = jdbcTemplate.query(checkCodeSql, (rs, rowNum) -> {
            return new RoomJoinInfo(rs.getInt("max_num"), rs.getInt("cur_num"),
                    rs.getBoolean("is_open"), rs.getString("code"));
        }, joinInfo.getRoomKey());

        RoomJoinInfo info = v.get(0);
        if (info.getMaxNum() == info.getCurNum()) {
            return "noSheet";
        } else if(!info.getIsOpen()) {
            if(!info.getCode().equals(joinInfo.getCode()))
                return "codeError";
        }

        String insertChatInSql = "insert into study_chat_in (nickname, msg_num, is_on, room_key) values" +
                " (?, ?, ?, ?);";
        jdbcTemplate.update(insertChatInSql, nickname, 0, false, joinInfo.getRoomKey());
        return "ok";
    }

    public List<MyStudyInfoDto> getMyStudyRoomList(String nickname) {
        String getInfoInChatInSql = "select room_key from study_chat_in where nickname = ?;";
        List<Integer> roomKey = jdbcTemplate.query(getInfoInChatInSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("room_key"));
        }, nickname);

        String getInfoSql = "select room_name, course_name, max_num, cur_num, start_date," +
                " study_introduction from study_info where room_key = ?;";
        List<MyStudyInfoDto> infos = new ArrayList<>();
        for(int key : roomKey) {
            List<MyStudyInfoDto> v = jdbcTemplate.query(getInfoSql, (rs, rowNum) -> {
                return new MyStudyInfoDto(key, rs.getString("room_name"), rs.getString("course_name"),
                        rs.getInt("max_num"), rs.getInt("cur_num"), rs.getString("start_date"),
                        rs.getString("study_introduction"));
            }, key);
            infos.add(v.get(0));
        }

        return infos;
    }
}
