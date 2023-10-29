package Capstone.server.Repository;

import Capstone.server.DTO.Chat.Msg;
import Capstone.server.DTO.Chat.SendMsgDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatRepository {
    JdbcTemplate jdbcTemplate;
    public ChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Msg> getAllMsgs(String nickname1, String nickname2) {
        if(!checkIsExistChat(nickname1, nickname2))
            makeNewChat(nickname1, nickname2);

        String chatRoomName = getChatRoomName(nickname1, nickname2);
        String getMsgSql = "select msg_num, nickname, type, msg, image from ?";
        List<Msg> msgs = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
            return new Msg(rs.getInt("msg_num"), rs.getString("nickname"), rs.getString("type"),
                    rs.getString("msg"), rs.getString("image"));
        });

        if(msgs.isEmpty()) {
            return null;
        }

        //chat테이블에서 메세지를 읽었으니 nickname1이 가장 최근에 본 메세지 넘버를 업데이트 해준다.
        Msg lastMsg = msgs.get(msgs.size() - 1);
        int updateMsgNum = lastMsg.getMsg_num();
        updateMsgNum(nickname1, nickname2, updateMsgNum);

        return msgs;
    }

    public List<Msg> getUnreadMsg(String nickname1, String nickname2) {
        String chatRoomName = getChatRoomName(nickname1, nickname2);
        int readMsgNum = getReadMsgNum(nickname1, nickname2);

        String getUnreadMsgSql = "select msg_num, nickname, type, msg, image from ? where msg_num > ? order by msg_num asc;";
        List<Msg> msgs = jdbcTemplate.query(getUnreadMsgSql, (rs, rowNum) -> {
            return new Msg(rs.getInt("msg_num"), rs.getString("nickname"),
                    rs.getString("type"), rs.getString("msg"), rs.getString("image"));
        }, chatRoomName, readMsgNum);

        return msgs;
    }

    public int getReadMsgNum(String nickname1, String nickname2) {
        String getReadMsgNumSql = "select msg_num from chat_in where nickname = ? and friend_nickname = ?;";
        List<Integer> msgNum = jdbcTemplate.query(getReadMsgNumSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("msg_num"));
        }, nickname1, nickname2);

        return msgNum.get(0);
    }


    public void updateSendMsg(SendMsgDto sendMsg) {
        String chatRoomName = getChatRoomName(sendMsg.getSender(), sendMsg.getReceiver());
        String updateSendMsgSql = "insert into ? (nickname, type, msg, image) values (?, ?, ?, ?);";
        jdbcTemplate.update(updateSendMsgSql, chatRoomName, sendMsg.getSender(),
                sendMsg.getType(), sendMsg.getMsg(), sendMsg.getImg());
    }

    public void updateMsgNum(String nickname1, String nickname2, int updateMsgNum) {
        String updateMsgNumSql = "update chat_in set msg_num = ? where nickname = ? and friend_nickname = ?;";
        jdbcTemplate.update(updateMsgNumSql, updateMsgNum, nickname1, nickname2);
    }

    public String getChatRoomName(String nickname1, String nickname2) {
        String getMsgInfoSql = "select chat_table_key from chat_in where nickname = ? and friend_nickname = ?;";
        List<Integer> keyNum = jdbcTemplate.query(getMsgInfoSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("chat_table_key"));
        }, nickname1, nickname2);
        return "chat_" + String.valueOf(keyNum.get(0));
    }

    public boolean checkIsExistChat(String nickname1, String nickname2) {
        String checkSql = "select chat_table_key from chat_in where nickname = ? and friend_nickname = ?;";
        List<Integer> key = jdbcTemplate.query(checkSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("chat_table_key"));
        });

        if(key.isEmpty())
            return false;
        else
            return true;
    }

    public void makeNewChat(String nickname1, String nickname2) {
        //chat_in 테이블 내에 정보 생성
        String sql = "select max(chat_table_key) as key from chat_in;";
        List<Integer> maxKey = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return Integer.valueOf("key");
        });

        int keyNum = maxKey.get(0) + 1;
        String makeChatInfoSql = "insert into chat_in (nickname, msg_num, is_on, friend_nickname, chat_table_key) values" +
                " (?, null, false, ?, ?);";
        jdbcTemplate.update(makeChatInfoSql, nickname1, nickname2, keyNum);
        jdbcTemplate.update(makeChatInfoSql, nickname2, nickname1, keyNum);

        //chat_ + key의 값을 가진 실질적인 대화가 담기는 테이블 생성
        String chatRoomName = "chat_" + String.valueOf(keyNum);
        String makeChatSql = "create table ? (msg_num integer, nickname varchar, type varchar," +
                " msg varchar, image varchar, primary key(msg_num));";
        jdbcTemplate.update(makeChatSql, chatRoomName);
    }

    public void setChatIsOn(String nickname1, String nickname2, boolean isOn) {
        String setChatOnSql = "update chat_in set is_on = ? where nickname = ? and friend_nickname = ?;";
        jdbcTemplate.update(setChatOnSql, isOn, nickname1, nickname2);
    }

    public boolean checkUserIsOn(String nickname1, String nickname2) {
        String checkUserSql = "select is_on from chat_in where nickname = ? and friend_nickname = ?;";
        List<Boolean> isOn = jdbcTemplate.query(checkUserSql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_on"));
        });
        return isOn.get(0);
    }

    public void sendAlarm(SendMsgDto sendMsg) {

    }
}
