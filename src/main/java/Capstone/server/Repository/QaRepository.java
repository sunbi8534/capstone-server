package Capstone.server.Repository;

import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Qa.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QaRepository {
    JdbcTemplate jdbcTemplate;
    SimpleJdbcInsert simpleJdbcInsert; //study
    NamedParameterJdbcTemplate namedParameterJdbcTemplate; //study
    ProfileRepository profileRepository;

    QaRepository(JdbcTemplate jdbcTemplate,
                 ProfileRepository profileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.profileRepository = profileRepository;
    }
    public int enrollQa(QaDto q) {
        int myKey;
        String getMynumSql = "select my_key from handle_ask where nickname = ? order by my_key ASC;";
        List<Integer> key = jdbcTemplate.query(getMynumSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("my_key"));
        }, q.getNickname());

        if(key.isEmpty()) {
            myKey = 1;
        } else
            myKey = key.get(key.size() - 1) + 1;
        String insertQaSql = "insert into qa (my_key, qa_type, course_name, qa_num, point, questioner, solver," +
                " is_anonymity, is_watching, is_solving, status, review, time) values (?, ?, ?, ?, ?, ?, null, ?, false," +
                " false, false, 0, 0);";
        jdbcTemplate.update(insertQaSql, myKey, q.getType(), q.getCourse(), q.getMsg().size(), q.getPoint(), q.getNickname(),
                q.getIsAnonymity());
        String getQaKeySql = "select qa_key from qa where my_key = ? and questioner = ?;";
        List<Integer> qaKey = jdbcTemplate.query(getQaKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        }, myKey, q.getNickname());

        String insertQaChatInSql = "insert into qa_chat_in (qa_key, nickname, msg_num, is_on, solver_nickname, " +
                "solver_msg_num, solver_is_on) values (?, ?, ?, false, null, 0, false);";
        jdbcTemplate.update(insertQaChatInSql, qaKey.get(0), q.getNickname(), q.getMsg().size());

        String chatRoomName = "qa_chat_" + String.valueOf(qaKey.get(0));
        String makeChatSql = "create table " + chatRoomName + " (msg_num integer AUTO_INCREMENT, nickname varchar(60), type varchar(40)," +
                " msg varchar(200), image MEDIUMTEXT, time varchar(40), primary key(msg_num));";
        jdbcTemplate.update(makeChatSql);

        String insertMsgSql = "insert into " + chatRoomName + " (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        for(MsgDto m : q.getMsg()) {
            jdbcTemplate.update(insertMsgSql, m.getNickname(), m.getType(), m.getMsg(),
                    m.getImage(), m.getTime());
        }

        String insertHandleAskSql = "insert into handle_ask (nickname, qa_key, my_key) values (?, ?, ?);";
        jdbcTemplate.update(insertHandleAskSql, q.getNickname(), qaKey.get(0), myKey);
        return qaKey.get(0);
    }

    public List<QaMsgDto> getQa(int qaKey) {
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String getMsgSql = "select nickname, type, msg, image, time from " + chatRoomName + " order by msg_num ASC;";
        List<QaMsgDto> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new QaMsgDto(rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        });

        return msgs;
    }

    public String deleteQa(int qaKey) {
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String deleteQaChatSql = "delete from qa where qa_key = ?;";
        String deleteQaSql = "delete from qa_chat_in where qa_key = ?;";
        String deleteHandleSql = "delete from handle_ask where qa_key = ?;";
        String dropTableSql = "drop table " + chatRoomName + ";";
        String getIsWatchingSql = "select is_watching, is_solving from qa where qa_key = ?;";

        List<IsWatch> isW = jdbcTemplate.query(getIsWatchingSql, (rs, rowNum) -> {
            return new IsWatch(rs.getBoolean("is_watching"), rs.getBoolean("is_solving"));
        }, qaKey);
        IsWatch watch = isW.get(0);
        if(watch.getIs_watching() || watch.getIs_solving())
            return "no";

        jdbcTemplate.update(dropTableSql);
        jdbcTemplate.update(deleteQaSql, qaKey);
        jdbcTemplate.update(deleteQaChatSql, qaKey);
        jdbcTemplate.update(deleteHandleSql, qaKey);
        return "ok";
    }

    public List<QaBriefDto> getQaList(String nickname, List<String> course) {
        List<QaBriefDto> qaBriefDtos = new ArrayList<>();
        String getQaBriefSql = "select qa_key, qa_type, course_name, point from qa where course_name = ? and is_watching = false;";
        for(String c : course) {
            List<QaBriefDto> qaBrief = jdbcTemplate.query(getQaBriefSql, (rs, rowNum) -> {
                return new QaBriefDto(rs.getInt("qa_key"), rs.getString("qa_type"), rs.getString("course_name"), rs.getInt("point"));
            }, c);
            qaBriefDtos.addAll(qaBrief);
        }

        List<QaBriefDto> removeDto = new ArrayList<>();
        String getGiveUpQaSql = "select qa_key from handle_giveup where nickname = ?;";
        List<Integer> qaKey1 = jdbcTemplate.query(getGiveUpQaSql, (rs, rowNum) -> {
            Integer v = rs.getInt("qa_key");
            return Integer.valueOf(rs.getInt("qa_key"));
        }, nickname);
        if(!qaKey1.isEmpty()) {
            for(QaBriefDto q : qaBriefDtos) {
                if(qaKey1.contains(q.getQaKey()))
                    removeDto.add(q);
            }
        }
        String getAskQaSql = "select qa_key from handle_ask where nickname = ?;";
        List<Integer> qaKey2 = jdbcTemplate.query(getAskQaSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        }, nickname);
        if(!qaKey2.isEmpty()) {
            for(QaBriefDto q : qaBriefDtos) {
                if(qaKey2.contains(q.getQaKey()))
                    removeDto.add(q);
            }
        }
        if(!removeDto.isEmpty())
            qaBriefDtos.removeAll(removeDto);

        return qaBriefDtos;
    }

    public List<QaAskListDto> getQaAskList(String nickname) {
        List<QaAskListDto> list = new ArrayList<>();
        String getAskSql = "select qa_key from handle_ask where nickname = ?;";
        List<Integer> qaKeys = jdbcTemplate.query(getAskSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        }, nickname);

        String getInfoSql = "select qa_type, course_name, is_watching, is_solving, status from qa where qa_key = ?;";
        for(int key : qaKeys) {
            List<QaAskList> tempList = jdbcTemplate.query(getInfoSql, (rs, rowNum) -> {
                return new QaAskList(key, rs.getString("qa_type"),
                        rs.getString("course_name"), rs.getBoolean("is_watching"), rs.getBoolean("is_solving"), rs.getBoolean("status"));
            }, key);
            String status = "미답";

            QaAskList tmp = tempList.get(0);
            if(tmp.getIsSolving() || tmp.getIsWatching())
                status = "진행";
            if(tmp.getStatus())
                status = "완료";
            QaAskListDto dto = new QaAskListDto(tmp.getQaKey(), tmp.getType(), tmp.getCourse(), status);
            list.add(dto);
        }

        return list;
    }

    public List<QaListDto> getQaAnswerList(String nickname) {
        List<QaListDto> list = new ArrayList<>();
        String getAnswerSql = "select qa_key from handle_answer where nickname = ?;";
        List<Integer> qaKeys = jdbcTemplate.query(getAnswerSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_key"));
        }, nickname);

        String getInfoSql = "select qa_type, course_name, status from qa where qa_key = ?;";
        for(int key : qaKeys) {
            List<QaListDto> tempList = jdbcTemplate.query(getInfoSql, (rs, rowNum) -> {
                return new QaListDto(key, rs.getString("qa_type"),
                        rs.getString("course_name"), rs.getBoolean("status"));
            }, key);
            list.add(tempList.get(0));
        }

        return list;
    }

    public void qaFinish(int qaKey, int review) {
        String sql = "select point, questioner, solver from qa where qa_key = ?;";
        String updateSql1 = "update qa set status = true where qa_key = ?;";
        String addUserPointSql = "update user set point = point + ? where nickname = ?;";
        String minusUserPointSql = "update user set point = point - ? where nickname = ?;";
        String updateReviewSql = "update qa set review = ? where qa_key = ?;";

        List<FinishInfo> finishInfos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new FinishInfo(rs.getInt("point"), rs.getString("questioner"),
                    rs.getString("solver"));
        }, qaKey);
        FinishInfo info = finishInfos.get(0);

        jdbcTemplate.update(updateSql1, qaKey);
        jdbcTemplate.update(addUserPointSql, info.getPoint(), info.getSolver());
        jdbcTemplate.update(minusUserPointSql, info.getPoint(), info.getQuestioner());
        jdbcTemplate.update(updateReviewSql, review, qaKey);
    }

    public boolean isWatching(int qaKey) {
        String sql = "select is_watching from qa where qa_key = ?;";
        List<Boolean> watch = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_watching"));
        }, qaKey);

        return watch.get(0);
    }

    public boolean isAnonymity(int qaKey) {
        String sql = "select is_anonymity from qa where qa_key = ?;";
        List<Boolean> isAnonymity = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_anonymity"));
        }, qaKey);
        return isAnonymity.get(0);
    }

    public List<QaMsgDto> getQuestion(int qaKey, String nickname) {
        if(isWatching(qaKey))
            return null;

        String setQaSql = "update qa set solver = ?, is_watching = true where qa_key = ?;";
        jdbcTemplate.update(setQaSql, nickname, qaKey);
        String setQaChatSql = "update qa_chat_in set solver_nickname = ? where qa_key = ?;";
        jdbcTemplate.update(setQaChatSql, nickname, qaKey);

        String getQaMsgSql = "select msg_num, nickname from qa_chat_in where qa_key = ?;";
        List<QaMaker> qaMakers = jdbcTemplate.query(getQaMsgSql, (rs, rowNum) -> {
            return new QaMaker(rs.getInt("msg_num"), rs.getString("nickname"));
        }, qaKey);
        QaMaker maker = qaMakers.get(0);

        Boolean anony = isAnonymity(qaKey);

        String setMsgNumSql = "update qa_chat_in set solver_msg_num = ?, solver_is_on = true where qa_key = ?;";
        jdbcTemplate.update(setMsgNumSql, maker.getMsgNum(), qaKey);

        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String getMsgSql = "select nickname, type, msg, image, time from " + chatRoomName + " order by msg_num ASC;";
        List<QaMsgDto> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new QaMsgDto(rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        });

        for(QaMsgDto q : msgs) {
            if(anony)
                q.setIsAnonymity(true);
        }

        return msgs;
    }

    public void qaGiveUp(int qaKey, String nickname) {
        String setGiveUpSql1 = "update qa set is_watching = false, solver = null where qa_key = ?;";
        String setGiveUpSql2 = "update qa_chat_in set solver_nickname = null, solver_msg_num = 0," +
                " solver_is_on = false where qa_key = ?;";
        String getMsgNumSql = "select qa_num from qa where qa_key = ?;";
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String resetQaChatSql = "delete from " + chatRoomName + " where msg_num >= ?;";
        String updateHandleGiveupSql = "insert into handle_giveup (nickname, qa_key) values (?, ?);";

        List<Integer> num = jdbcTemplate.query(getMsgNumSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("qa_num"));
        }, qaKey);
        int msgNum = num.get(0);


        jdbcTemplate.update(setGiveUpSql1, qaKey);
        jdbcTemplate.update(setGiveUpSql2, qaKey);
        jdbcTemplate.update(resetQaChatSql, msgNum+1);
        jdbcTemplate.update(updateHandleGiveupSql, nickname, qaKey);
    }

    public void qaSolve(int qaKey) {
        long epochTime = Instant.now().getEpochSecond();
        String solveQaSql = "update qa set is_solving = true, time = ? where qa_key = ?;";
        String getSql = "select solver from qa where qa_key = ?;";
        List<String> solver = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("solver"));
        }, qaKey);
        String nickname = solver.get(0);
        String updateSql = "insert into handle_answer (nickname, qa_key) values (?, ?);";


        jdbcTemplate.update(solveQaSql, epochTime, qaKey);
        jdbcTemplate.update(updateSql, nickname, qaKey);
    }

    public void sendQaMsg(int qaKey, QaSendMsgDto msg) {
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String sendSql = "insert into " + chatRoomName + " (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(sendSql, msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImg(), msg.getTime());
    }

    public List<QaMsgDto> getQaMsgs(int qaKey, String nickname) {
        String chatRoomName = "qa_chat_" + String.valueOf(qaKey);
        String getChatInfoSql = "select nickname, msg_num, solver_nickname, solver_msg_num from qa_chat_in where qa_key = ?;";
        List<QaChatUserInfo> qaUserInfo = jdbcTemplate.query(getChatInfoSql, (rs, rowNum) -> {
            return new QaChatUserInfo(rs.getString("nickname"), rs.getInt("msg_num"), rs.getString("solver_nickname"),
                    rs.getInt("solver_msg_num"));
        }, qaKey);
        QaChatUserInfo userInfo = qaUserInfo.get(0);

        String getMaxNum = "select msg_num from " + chatRoomName + ";";
        List<Integer> maxNum = jdbcTemplate.query(getMaxNum, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("msg_num"));
        });
        int lastNum = maxNum.get(maxNum.size() - 1);

        int mNum;
        if(nickname.equals(userInfo.getNickname()))
            mNum = userInfo.getMsgNum();
        else
            mNum = userInfo.getSolverMsgNum();

        Boolean anony = isAnonymity(qaKey);

        String getMsgSql = "select nickname, type, msg, image, time from " + chatRoomName + " where msg_num > ? order by msg_num ASC;";
        List<QaMsgDto> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new QaMsgDto(rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        }, mNum);

        if(msgs.isEmpty())
            return msgs;

        for(QaMsgDto q : msgs) {
            if(anony)
                q.setIsAnonymity(true);
        }

        String setAskerSql = "update qa_chat_in set msg_num = ? where qa_key = ?;";
        String setSolverSql = "update qa_chat_in set solver_msg_num = ? where qa_key = ?;";

        if(nickname.equals(userInfo.getNickname()))
            jdbcTemplate.update(setAskerSql, lastNum, qaKey);
        else
            jdbcTemplate.update(setSolverSql, lastNum, qaKey);

        return msgs;
    }

    public long getTime(int qaKey) {
        String getSql = "select time from qa where qa_key = ?;";
        List<Long> time = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return Long.valueOf(rs.getLong("time"));
        }, qaKey);

        return time.get(0);
    }
}
