package Capstone.server.Repository;

import Capstone.server.DTO.Chat.Msg;
import Capstone.server.DTO.Profile.UserInfoMinimumDto;
import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Study.*;
import Capstone.server.Service.ProfileService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudyRepository {
    JdbcTemplate jdbcTemplate;
    ProfileService profileService;

    public StudyRepository(JdbcTemplate jdbcTemplate,
                           ProfileService profileService) {
        this.jdbcTemplate = jdbcTemplate;
        this.profileService = profileService;
    }

    public List<StudyInfoDto> getStudyRoomList(String nickname, RoomStatusDto roomStatus) {
        String findCourseSql = "select course_name from take where nickname = ?;";
        List<StudyInfoDto> studyInfoDtos = new ArrayList<>();
        List<String> course = jdbcTemplate.query(findCourseSql, (rs, rowNum) -> {
            return new String(rs.getString("course_name"));
        }, nickname);

        if(course.isEmpty())
            return studyInfoDtos;

        String findStudySql = "select room_key, room_name, course_name, max_num," +
                " cur_num, leader, start_date, is_open, study_introduction from study_info where course_name = ?;";


        for(String c : course) {
            List<StudyInfoDto> infos = jdbcTemplate.query(findStudySql, (rs, rowNum) -> {
                String leader = rs.getString("leader");
                return new StudyInfoDto(rs.getInt("room_key"), rs.getString("room_name"),
                        rs.getString("course_name"), rs.getInt("max_num"), rs.getInt("cur_num"),
                        leader, rs.getString("start_date"), rs.getBoolean("is_open"),
                        rs.getString("study_introduction"));
            }, c);
            studyInfoDtos.addAll(infos);
        }

        List<StudyInfoDto> remove = new ArrayList<>();
        for(StudyInfoDto s : studyInfoDtos) {
            if(s.getLeader().equals(nickname))
                remove.add(s);
        }
        studyInfoDtos.removeAll(remove);

        String sq = "select room_key from study_chat_in where nickname = ?;";
        List<Integer> k = jdbcTemplate.query(sq, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("room_key"));
        }, nickname);
        if(!k.isEmpty()) {
            List<StudyInfoDto> removeKey = new ArrayList<>();
            for(StudyInfoDto s : studyInfoDtos) {
                if(k.contains(s.getRoomKey()))
                    removeKey.add(s);
            }
            studyInfoDtos.removeAll(removeKey);
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
        if(!checkDup(info.getLeader(), info.getCourse()))
            return "false";

        String makeStudySql = "insert into study_info (room_name, course_name, max_num," +
                " cur_num, leader, start_date, is_open, code, study_introduction) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(makeStudySql, info.getRoomName(), info.getCourse(), info.getMaxNum(), 1,
                info.getLeader(), info.getStartDate(), info.getIsOpen(), info.getCode(), info.getStudyIntroduction());

        String getKeySql = "select room_key from study_info where room_name = ?;";
        List<Integer> key = jdbcTemplate.query(getKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("room_key"));
        }, info.getRoomName());

        String insertChatInSql = "insert into study_chat_in (nickname, msg_num, is_on, room_key)" +
                " values (?, ?, ?, ?);";
        jdbcTemplate.update(insertChatInSql, info.getLeader(), 0, false, key.get(0));

        makeStudyChatTable(key.get(0));
        makeStudyQuizTable(key.get(0));
        return "ok";
    }

    public void makeStudyChatTable(int roomKey) {
        String chatRoomName = "study_chat_" + String.valueOf(roomKey);
        String makeTableSql = "create table " + chatRoomName + " (msg_num integer AUTO_INCREMENT, " +
                "nickname varchar(60), type varchar(50), msg varchar(200), image MEDIUMTEXT, time varchar(50)," +
                " primary key(msg_num));";
        jdbcTemplate.update(makeTableSql);
    }

    public void makeStudyQuizTable(int roomKey) {
        String studyQuizTableName = "study_quiz_" + String.valueOf(roomKey);
        String makeQuizTableSql = "create table " + studyQuizTableName + "(" +
                "folder_key integer, nickname varchar(60), contents LONGTEXT);";
        jdbcTemplate.update(makeQuizTableSql);
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


    public boolean checkDup(String nickname, String course) {
        String sql = "select room_key from study_chat_in where nickname = ?;";
        List<Integer> roomKey = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("room_key"));
        }, nickname);

        if(roomKey.isEmpty())
            return true;
        String getSql = "select course_name from study_info where room_key = ?;";

        String c;
        for(int key : roomKey) {
            List<String> courseName = jdbcTemplate.query(getSql, (rs, rowNum) -> {
                return String.valueOf(rs.getString("course_name"));
            }, key);
            c = courseName.get(0);
            if(c.equals(course))
                return false;
        }

        return true;
    }

    public String joinStudy(String nickname, StudyJoinDto joinInfo) {
        String checkCodeSql = "select max_num, cur_num, is_open, code, course_name from study_info where room_key = ?;";
        List<RoomJoinInfo> v = jdbcTemplate.query(checkCodeSql, (rs, rowNum) -> {
            return new RoomJoinInfo(rs.getInt("max_num"), rs.getInt("cur_num"),
                    rs.getBoolean("is_open"), rs.getString("code"), rs.getString("course_name"));
        }, joinInfo.getRoomKey());

        RoomJoinInfo info = v.get(0);
        if (info.getMaxNum() == info.getCurNum()) {
            return "noSheet";
        } else if(!info.getIsOpen()) {
            if(!info.getCode().equals(joinInfo.getCode()))
                return "codeError";
        } else if(!checkDup(nickname, info.getCourse())) {
            return "dupCourse";
        }

        String insSql = "update study_info set cur_num = cur_num + 1 where room_key = ?;";
        String insertChatInSql = "insert into study_chat_in (nickname, msg_num, is_on, room_key) values" +
                " (?, ?, ?, ?);";
        jdbcTemplate.update(insSql, joinInfo.getRoomKey());
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

    public List<UserInfoMinimumDto> enterStudy(int roomKey, String nickname) {
        String getNicknameSql = "select nickname from study_chat_in where room_key = ?;";
        List<String> friends = jdbcTemplate.query(getNicknameSql, (rs, rowNum) -> {
            return new String(rs.getString("nickname"));
        }, roomKey);

        List<UserInfoMinimumDto> friendInfo = new ArrayList<>();
        for(String name : friends) {
            friendInfo.add(new UserInfoMinimumDto(name, profileService.getProfileImage(name)));
        }
        return friendInfo;
    }

    public String getLeader(int roomKey) {
        String sql = "select leader from study_info where room_key = ?;";
        List<String> leader = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("leader"));
        }, roomKey);

        return leader.get(0);
    }

    public List<Msg> getAllMsgs(int roomKey, String nickname) {
        String chatRoomName = "study_chat_" + String.valueOf(roomKey);
        String getMsgSql = "select msg_num, nickname, type, msg, image, time from " + chatRoomName + ";";
        List<Msg> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new Msg(rs.getInt("msg_num"), rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        });

        if(msgs.isEmpty())
            return null;
        else
            return msgs;
    }

    public void updateMsgNum(int roomKey, int updateMsgNum, String nickname) {
        String updateMsgNumSql = "update study_chat_in set msg_num = ? where nickname = ? and room_key = ?;";
        jdbcTemplate.update(updateMsgNumSql, updateMsgNum, nickname, roomKey);
    }

    public void setChatIsOn(int roomKey, String nickname, boolean isOn) {
        String setChatOnSql = "update study_chat_in set is_on = ? where nickname = ? and room_key = ?;";
        jdbcTemplate.update(setChatOnSql, isOn, nickname, roomKey);
    }

    public void updateSendMsg(StudySendMsgDto sendMsg) {
        String chatRoomName = "study_chat_" + sendMsg.getRoomKey();
        String updateSendMsgSql = "insert into " + chatRoomName + " (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(updateSendMsgSql, sendMsg.getSender(),
                sendMsg.getType(), sendMsg.getMsg(), sendMsg.getImg(), sendMsg.getTime());
    }

    public List<Msg> getUnreadMsg(int roomKey, String nickname) {
        String chatRoomName = "study_chat_" + String.valueOf(roomKey);
        int readMsgNum = getReadMsgNum(roomKey, nickname);

        String getUnreadMsgSql = "select msg_num, nickname, type, msg, image, time from " + chatRoomName + " where msg_num > ? order by msg_num asc;";
        List<Msg> msgs = jdbcTemplate.query(getUnreadMsgSql, (rs, rowNum) -> {
            return new Msg(rs.getInt("msg_num"), rs.getString("nickname"),
                    rs.getString("type"), rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        }, readMsgNum);

        return msgs;
    }

    public int getReadMsgNum(int roomKey, String nickname) {
        String getReadMsgNumSql = "select msg_num from study_chat_in where nickname = ? and room_key = ?;";
        List<Integer> msgNum = jdbcTemplate.query(getReadMsgNumSql, (rs, rowNum) -> {
            Integer v = rs.getInt("msg_num");
            if(v == null)
                return null;
            else
                return v;
        }, nickname, roomKey);

        if(msgNum.isEmpty())
            return 0;
        else
            return msgNum.get(0);
    }

    public String changeInfo(int roomKey, StudyChangeDto info) {
        String getInfoSql = "select cur_num from study_info where room_key = ?;";
        List<Integer> curNum = jdbcTemplate.query(getInfoSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("cur_num"));
        }, roomKey);
        int curN = curNum.get(0);

        if(curN > info.getMaxNum())
            return "no";

        String name = info.getRoomName();
        String newSql = "update study_info set max_num = ?, room_name = ?, course_name = ?, " +
                "is_open = ?, code = ?, study_introduction = ? where room_key = ?;";
        jdbcTemplate.update(newSql, info.getMaxNum(), info.getRoomName(), info.getCourse(),
                info.getIsOpen(), info.getCode(), info.getStudyIntroduction(), roomKey);
        return "ok";
    }

    public void studyCommitLeader(int roomKey, String newLeader) {
        String sql = "update study_info set leader = ? where room_key = ?;";
        jdbcTemplate.update(sql, newLeader, roomKey);
    }

    public void outStudy(int roomKey, String nickname) {
        String checkStudySql = "select cur_num from study_info where room_key = ?;";
        List<Integer> num = jdbcTemplate.query(checkStudySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("cur_num"));
        }, roomKey);

        int curNum = num.get(0);
        if(curNum == 1) {
            String studyChat = "study_chat_" + String.valueOf(roomKey);
            String dropSql = "drop table " + studyChat + ";";
            String delChatInSql = "delete from study_chat_in where room_key = ?;";
            String delStudyInfoSql = "delete from study_info where room_key = ?;";

            jdbcTemplate.update(dropSql);
            jdbcTemplate.update(delChatInSql, roomKey);
            jdbcTemplate.update(delStudyInfoSql, roomKey);
            return;
        }

        String outSql1 = "update study_info set cur_num = cur_num - 1 where room_key = ?;";
        String outSql2 = "delete from study_chat_in where nickname = ? and room_key = ?;";
        //
        jdbcTemplate.update(outSql1, roomKey);
        jdbcTemplate.update(outSql2, nickname, roomKey);
    }

    public String getCode(int roomKey) {
        String sql = "select code from study_info where room_key = ?;";
        List<String> code = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("code"));
        }, roomKey);

        return code.get(0);
    }

    public boolean checkFileEnrollCan(int roomKey, int folderKey, String nickname) {
        String quizTableName = "study_quiz_" + String.valueOf(roomKey);
        String getSql = "select nickname from " + quizTableName + " where folder_key = ? and nickname = ?;";
        List<String> nick = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("nickname"));
        }, folderKey, nickname);

        if(nick.isEmpty())
            return true;
        else
            return false;
    }

    public void enrollFileContent(int roomKey, int folderKey, String nickname, String content) {
        String quizTableName = "study_quiz_" + String.valueOf(roomKey);
        String insertSql = "insert into " + quizTableName + " (folder_key, nickname, contents) values (?, ?, ?);";
        jdbcTemplate.update(insertSql, folderKey, nickname, content);
    }

    public List<StudyQuizListDto> getQuizList(int roomKey) {
        String getSql = "select folder_key, folder_name from study_quiz_info where room_key = ?;";
        List<StudyQuizListDto> list = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return new StudyQuizListDto(rs.getInt("folder_key"), rs.getString("folder_name"));
        }, roomKey);

        return list;
    }

    public List<String> getEnrollUserList(int roomKey, int folderKey) {
        String tableName = "study_quiz_" + String.valueOf(roomKey);
        String sql = "select nickname from " + tableName + " where folder_key = ?;";
        List<String> nickname = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("nickname"));
        }, folderKey);

        return nickname;
    }

    public void deleteUserInFolder(int roomKey, int folderKey, String nickname) {
        String tableName = "study_quiz_" + String.valueOf(roomKey);
        String sql = "delete from " + tableName + " where folder_key = ? and nickname = ?;";
        jdbcTemplate.update(sql, folderKey, nickname);
    }

    public void makeFolder(int roomKey, String folderName) {
        String sql = "insert into study_quiz_info (room_key, folder_name) values (?, ?);";
        jdbcTemplate.update(sql, roomKey, folderName);
    }

    public List<String> getQuizContents(StudyQuizInfoDto info) {
        String studyQuizTableName = "study_quiz_" + String.valueOf(info.getRoomKey());
        String getContentsSql = "select contents from " + studyQuizTableName + " where folder_key = ?;";
        List<String> contents = jdbcTemplate.query(getContentsSql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("contents"));
        }, info.getFolderKey());

        return contents;
    }

    public void plusQuiz(int quizKey, List<QuizDto> quiz) {
        String tableName = "quiz_" + String.valueOf(quizKey);
        String getSql = "select num from " + tableName + " order by num asc;";
        List<Integer> nums = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("num"));
        });
        int lastNum;
        if(nums.isEmpty())
            lastNum = 0;
        else
            lastNum = nums.get(nums.size() - 1);
        String sql = "insert into " + tableName + " (num, question, answer, is_solved)" +
                " values (?, ?, ?, ?);";

        int num = lastNum + 1;
        for(QuizDto q : quiz) {
            jdbcTemplate.update(sql, num, q.getQuestion(), q.getAnswer(), q.getIsSolved());
            num++;
        }
    }
}

