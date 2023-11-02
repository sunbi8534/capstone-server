package Capstone.server.Repository;

import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Qa.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QaRepository {
    JdbcTemplate jdbcTemplate;
    ProfileRepository profileRepository;

    QaRepository(JdbcTemplate jdbcTemplate,
                 ProfileRepository profileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.profileRepository = profileRepository;
    }
    public int enrollQa(QaDto q) {
        int myKey;
        String getMynumSql = "select max(my_key) key from handle_ask where nickname = ?;";
        List<Integer> key = jdbcTemplate.query(getMynumSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("key"));
        }, q.getNickname());

        if(key.isEmpty()) {
            myKey = 1;
        } else
            myKey = key.get(0) + 1;
        String insertQaSql = "insert into qa (my_key, qa_type, course_name, qa_num, point, questioner, solver," +
                " is_anonimity, is_watching, is_solving, status) values (?, ?, ?, ?, ?, ?, null, ?, false," +
                " false, false);";
        jdbcTemplate.update(insertQaSql, myKey, q.getType(), q.getCourse(), q.getMsg().size(), q.getPoint(), q.getNickname(),
                q.getIsAnonymity());
        String getQaKeySql = "select qa_key from qa where my_key = ? and questioner = ?;";
        List<Integer> qaKey = jdbcTemplate.query(getQaKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });

        String insertQaChatInSql = "insert into qa_chat_in (qa_key, nickname, msg_num, is_on, solver_nickname, " +
                "solver_msg_num, solver_is_on) values (?, ?, ?, false, null, 0, false);";
        jdbcTemplate.update(insertQaChatInSql, qaKey.get(0), q.getNickname(), q.getMsg().size());

        String chatRoomName = "qa_chat_" + String.valueOf(qaKey.get(0));
        String makeChatSql = "create table ? (msg_num integer, nickname varchar, type varchar," +
                " msg varchar, image varchar, time varchar, primary key(msg_num));";
        jdbcTemplate.update(makeChatSql, chatRoomName);

        String insertMsgSql = "insert into ? (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        for(MsgDto m : q.getMsg()) {
            jdbcTemplate.update(insertMsgSql, chatRoomName, m.getNickname(), m.getType(), m.getMsg(),
                    m.getImage(), m.getTime());
        }

        return qaKey.get(0);
    }

    public String deleteQa(int qaKey) {
        String deleteQaSql = "delete from ? where qa_key = ?;";
        String dropTableSql = "drop table ?;";
        String getIsWatchingSql = "select is_watching, is_solving from qa where qa_key = ?;";

        List<IsWatch> isW = jdbcTemplate.query(getIsWatchingSql, (rs, rowNum) -> {
            return new IsWatch(rs.getBoolean("is_watching"), rs.getBoolean("is_solving"));
        });
        IsWatch watch = isW.get(0);
        if(watch.getIs_watching() || watch.getIs_solving())
            return "no";

        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        jdbcTemplate.update(dropTableSql, chatRoomName);
        jdbcTemplate.update(deleteQaSql, "qa_chat_in", qaKey);
        jdbcTemplate.update(deleteQaSql, "qa", qaKey);
        jdbcTemplate.update(deleteQaSql, "handle_ask", qaKey);
        return "ok";
    }

    public List<QaBriefDto> getQaList(String nickname, List<String> course) {
        List<QaBriefDto> qaBriefDtos = new ArrayList<>();
        String getQaBrifSql = "select qa_key, qa_type, course_name, point from qa where course_name = ?;";
        for(String c : course) {
            List<QaBriefDto> qaBrif = jdbcTemplate.query(getQaBrifSql, (rs, rowNum) -> {
                return new QaBriefDto(rs.getInt("qa_key"), rs.getString("qa_type"), rs.getString("course"), rs.getInt("point"));
            }, c);
            qaBriefDtos.addAll(qaBrif);
        }

        String getGiveUpQaSql = "select qa_key from handle_giveup where nickname = ?;";
        List<Integer> qaKey1 = jdbcTemplate.query(getGiveUpQaSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });
        String getAskQaSql = "select qa_key from handle_ask where nickname = ?;";
        List<Integer> qaKey2 = jdbcTemplate.query(getAskQaSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        });
        for(QaBriefDto q : qaBriefDtos) {
            if(qaKey1.contains(q.getQaKey()) || qaKey2.contains(q.getQaKey()))
                qaBriefDtos.remove(q);
        }

        return qaBriefDtos;
    }

    public List<QaMsgDto> getQuestion(int qaKey, String nickname) {
        String setQaSql = "update qa set solver = ?, is_watching = true where qa_key = ?;";
        jdbcTemplate.update(setQaSql, nickname, qaKey);
        String setQaChatSql = "update qa_chat_in set solver_nickname = ? where qa_key = ?;";
        jdbcTemplate.update(setQaChatSql, nickname, qaKey);

        String getQsMsgSql = "select msg_num, nickname from qa_chat_in where qa_key = ?;";
        List<QaMaker> qaMakers = jdbcTemplate.query(getQsMsgSql, (rs, rowNum) -> {
            return new QaMaker(rs.getInt("msg_num"), rs.getString("nickname"));
        }, qaKey);
        QaMaker maker = qaMakers.get(0);

        String isAnonimitySql = "select is_anonimity from qa where qa_key = ?;";
        List<Boolean> isAnonimity = jdbcTemplate.query(isAnonimitySql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_anonimity"));
        }, qaKey);
        Boolean anony = isAnonimity.get(0);

        String setMsgNumSql = "update qa_chat_in set solver_msg_num = ?, solver_is_on = true where qa_key = ?;";
        jdbcTemplate.update(setMsgNumSql, maker.getMsgNum(), qaKey);

        String myProfileImg = profileRepository.getProfileImage(nickname);
        String questionerImg = profileRepository.getProfileImage(maker.getNickname());

        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String getMsgSql = "select nickname, type, msg, image from ? order by msg_num ASC;";
        List<QaMsgDto> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new QaMsgDto(rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"));
        }, chatRoomName);
        for(QaMsgDto q : msgs) {
            if(q.getNickname().equals(nickname))
                q.setProfileImg(myProfileImg);
            else
                q.setProfileImg(questionerImg);

            if(anony)
                q.setIsAnonimity(true);
        }

        return msgs;
    }

    public void qaGiveUp(int qaKey, String nickname) {
        String setGiveUpSql1 = "update qa set is_watching = false where qa_key = ?;";
        String setGiveUpSql2 = "update qa_chat_in set solver_nickname = null, solver_msg_num = 0," +
                " solver_is_on = false where qa_key = ?;";
        String getMsgNumSql = "select msg_num from qa_chat_in where qa_key = ?;";
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String resetQaChatSql = "delete from ? where msg_num >= ?;";

        List<Integer> num = jdbcTemplate.query(getMsgNumSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("msg_num"));
        }, qaKey);
        int msgNum = num.get(0);

        jdbcTemplate.update(setGiveUpSql1, qaKey);
        jdbcTemplate.update(setGiveUpSql2, qaKey);
        jdbcTemplate.update(resetQaChatSql, chatRoomName, msgNum+1);
    }

    public void qaSolve(int qaKey) {
        String solveQaSql = "update qa set is_watching = false, is_solving = true where qa_key = ?;";
        jdbcTemplate.update(solveQaSql, qaKey);
    }

    public void sendQaMsg(int qaKey, QaSendMsgDto msg) {
        String sendSql = "insert into ? (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        jdbcTemplate.update(sendSql, chatRoomName, msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImg(), msg.getTime());
    }

    public boolean getAnonymity(int qaKey) {
        String getAnonymitySql = "select is_anonymity from qa where qa_key = ?;";
        List<Boolean> isAnony = jdbcTemplate.query(getAnonymitySql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_anonymity"));
        }, qaKey);
        return isAnony.get(0);
    }

    public List<QaMsgDto> getQaMsgs(int qaKey, String nickname) {
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String getChatInfoSql = "select nickname, msg_num, solver_nickname, solver_msg_num from qa_chat_in where qa_key = ?;";
        List<QaChatUserInfo> qaUserInfo = jdbcTemplate.query(getChatInfoSql, (rs, rowNum) -> {
            return new QaChatUserInfo(rs.getString("nickname"), rs.getInt("msg_num"), rs.getString("solver_nickname"),
                    rs.getInt("solver_msg_num"));
        }, qaKey);
        QaChatUserInfo userInfo = qaUserInfo.get(0);

        String getMaxNum = "select max(msg_num) as num from ?;";
        List<Integer> maxNum = jdbcTemplate.query(getMaxNum, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("num"));
        }, chatRoomName);

        int mNum;
        if(nickname.equals(userInfo.getNickname())) {
            mNum = userInfo.getMsgNum();

        } else
            mNum = userInfo.getSolverMsgNum();

        String myImg = profileRepository.getProfileImage(userInfo.getNickname());
        String solverImg = profileRepository.getProfileImage(userInfo.getSolverNickname());
        Boolean anony = getAnonymity(qaKey);

        String getMsgSql = "select nickname, type, msg, image from ? where msg_num > ? order by msg_num ASC;";
        List<QaMsgDto> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new QaMsgDto(rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"));
        }, chatRoomName, mNum);

        for(QaMsgDto q : msgs) {
            if(q.getNickname().equals(userInfo.getNickname()))
                q.setProfileImg(myImg);
            else
                q.setProfileImg(solverImg);

            if(anony)
                q.setIsAnonimity(true);
        }

        String setMySql = "update qa_chat_in set msg_num = ? where qa_key = ?;";
        String setSolverSql = "update qa_chat_in set solver_msg_num = ? where qa_key = ?;";

        if(nickname.equals(userInfo.getNickname()))
            jdbcTemplate.update(setMySql, maxNum.get(0), qaKey);
        else
            jdbcTemplate.update(setSolverSql, maxNum.get(0), qaKey);

        return msgs;
    }
}
