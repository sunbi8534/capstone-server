package Capstone.server.Repository;

import Capstone.server.DTO.Chat.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatRepository {
    JdbcTemplate jdbcTemplate;
    public ChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Msg> getAllMsgs(String nickname1, String nickname2) {
        String chatRoomName = getChatRoomName(nickname1, nickname2);
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

    public List<Msg> getUnreadMsg(String nickname1, String nickname2) {
        String chatRoomName = getChatRoomName(nickname1, nickname2);
        int readMsgNum = getReadMsgNum(nickname1, nickname2);

        String getUnreadMsgSql = "select msg_num, nickname, type, msg, image, time from " + chatRoomName + " where msg_num > ? order by msg_num asc;";
        List<Msg> msgs = jdbcTemplate.query(getUnreadMsgSql, (rs, rowNum) -> {
            return new Msg(rs.getInt("msg_num"), rs.getString("nickname"),
                    rs.getString("type"), rs.getString("msg"), rs.getString("image"), rs.getString("time"));
        }, readMsgNum);

        return msgs;
    }

    public int getReadMsgNum(String nickname1, String nickname2) {
        String getReadMsgNumSql = "select msg_num from chat_in where nickname = ? and friend_nickname = ?;";
        List<Integer> msgNum = jdbcTemplate.query(getReadMsgNumSql, (rs, rowNum) -> {
            Integer v = rs.getInt("msg_num");
            if(v == null)
                return null;
            else
                return v;
        }, nickname1, nickname2);

        if(msgNum.isEmpty())
            return 0;
        else
            return msgNum.get(0);
    }


    public void updateSendMsg(SendMsgDto sendMsg) {
        String chatRoomName = getChatRoomName(sendMsg.getSender(), sendMsg.getReceiver());
        String updateSendMsgSql = "insert into " + chatRoomName + " (nickname, type, msg, image, time) values (?, ?, ?, ?, ?);";
        jdbcTemplate.update(updateSendMsgSql, sendMsg.getSender(),
                sendMsg.getType(), sendMsg.getMsg(), sendMsg.getImg(), sendMsg.getTime());
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
            Integer v = rs.getInt("chat_table_key");
            if(v == null)
                return null;
            else
                return v;
        }, nickname1, nickname2);

        if(key.isEmpty())
            return false;
        else
            return true;
    }

    public void makeNewChat(String nickname1, String nickname2) {
        //chat_in 테이블 내에 정보 생성
        String sql = "select chat_table_key from chat_in order by chat_table_key asc;";
        int keyNum;
        List<Integer> maxKey = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Integer v = rs.getInt("chat_table_key");
            if(v == null)
                return null;
            else
                return v;
        });

        if(maxKey.isEmpty())
            keyNum = 1;
        else
            keyNum = maxKey.get(maxKey.size() - 1) + 1;

        String makeChatInfoSql = "insert into chat_in (nickname, msg_num, is_on, friend_nickname, chat_table_key) values" +
                " (?, 0, false, ?, ?);";
        jdbcTemplate.update(makeChatInfoSql, nickname1, nickname2, keyNum);
        jdbcTemplate.update(makeChatInfoSql, nickname2, nickname1, keyNum);

        //chat_ + key의 값을 가진 실질적인 대화가 담기는 테이블 생성
        String chatRoomName = "chat_" + String.valueOf(keyNum);
        String makeChatSql = "create table " + chatRoomName + " (msg_num integer AUTO_INCREMENT, nickname varchar(50), type varchar(30)," +
                " msg text, image MEDIUMTEXT, time varchar(40), primary key(msg_num));";
        jdbcTemplate.update(makeChatSql);
    }

    public void setChatIsOn(String nickname1, String nickname2, boolean isOn) {
        String setChatOnSql = "update chat_in set is_on = ? where nickname = ? and friend_nickname = ?;";
        jdbcTemplate.update(setChatOnSql, isOn, nickname1, nickname2);
    }

    public boolean checkUserIsOn(String nickname1, String nickname2) {
        String checkUserSql = "select is_on from chat_in where nickname = ? and friend_nickname = ?;";
        List<Boolean> isOn = jdbcTemplate.query(checkUserSql, (rs, rowNum) -> {
            return Boolean.valueOf(rs.getBoolean("is_on"));
        }, nickname1, nickname2);
        return isOn.get(0);
    }

    public void sendAlarm(SendMsgDto sendMsg) {

    }

    public List<ChatListDto> getChatList(String nickname) {
        List<ChatListDto> chatList = new ArrayList<>();
        String getChatListSql = "select friend_nickname, msg_num, chat_table_key from chat_in where nickname = ?;";
        List<ChatList> list = jdbcTemplate.query(getChatListSql, (rs, rowNum) -> {
            return new ChatList(rs.getString("friend_nickname"), rs.getInt("msg_num"),
                    rs.getInt("chat_table_key"));
        }, nickname);

        String getAlarmSql = "select msg_num from chat_in where nickname = ? and friend_nickname = ?;";
        for(ChatList l : list) {
            List<Integer> num = jdbcTemplate.query(getAlarmSql, (rs, rowNum) -> {
                return Integer.valueOf(rs.getInt("msg_num"));
            }, l.getFriendNickname(), nickname);
            int friendMsgNum = num.get(0);
            Boolean alarm = false;
            if(friendMsgNum > l.getMsg_num())
                alarm = true;

            String roomName = "chat_" + l.getChatTableKey();
            String getMsgSql = "select msg, time from " + roomName + " where msg_num = ?;";
            List<MiniMsg> miniMsg = jdbcTemplate.query(getMsgSql, (rs, rowNum) -> {
                return new MiniMsg(rs.getString("msg"), rs.getString("time"));
            }, l.getMsg_num());
            ChatListDto chatListDto = new ChatListDto();
            if(miniMsg.isEmpty()) {
                chatListDto.setNickname(l.getFriendNickname());
                chatListDto.setTime(null);
                chatListDto.setMsg(null);
                chatListDto.setAlarm(alarm);
            } else {
                MiniMsg m = miniMsg.get(0);
                chatListDto = new ChatListDto(l.getFriendNickname(), m.getTime(), m.getMsg(), alarm);
            }
            chatList.add(chatListDto);
        }

        return chatList;
    }


}
