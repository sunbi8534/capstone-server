package Capstone.server.Repository;

import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Quiz.QuizInfoDto;
import Capstone.server.DTO.Quiz.QuizMakeDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuizRepository {
    JdbcTemplate jdbcTemplate;

    public QuizRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<QuizInfoDto> getMyQuiz(String nickname, String course) {
        String sql = "select quiz_key, quiz_name, quiz_num, cur_num from quiz_info where course_name = ? and nickname = ?;";
        List<QuizInfoDto> quiz = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new QuizInfoDto(rs.getInt("quiz_key"), rs.getString("quiz_name"),
                    rs.getInt("quiz_num"), rs.getInt("cur_num"));
        }, course, nickname);

        return quiz;
    }

    public void makeQuizFolder(QuizMakeDto quiz) {
        String getSql = "select my_key from quiz_info where course_name = ? and nickname = ? order by my_key asc;";
        List<Integer> myKey = jdbcTemplate.query(getSql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("my_key"));
        }, quiz.getCourse(), quiz.getNickname());

        int key;
        if(myKey.isEmpty())
            key = 1;
        else
            key = myKey.get(myKey.size() - 1) + 1;

        String insertSql = "insert into quiz_info (my_key, course_name, nickname, quiz_name, quiz_num, cur_num) values" +
                " (?, ?, ?, ?, 0, 0);";
        jdbcTemplate.update(insertSql, key, quiz.getCourse(), quiz.getNickname(), quiz.getQuizName());
        String getKeySql = "select quiz_key from quiz_info where my_key = ? and nickname = ?;";

        List<Integer> quizKey = jdbcTemplate.query(getKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("quiz_key"));
        }, key, quiz.getNickname());
        int qKey = quizKey.get(0);

        String tableName = "quiz_" + String.valueOf(qKey);
        String makeSql = "create table " + tableName + " (" +
                "num integer, question varchar(300), answer varchar(200));";
        jdbcTemplate.update(makeSql);
    }

    public List<QuizDto> quiz(int quizKey) {
        String quizTable = "quiz_" + String.valueOf(quizKey);
        String getQuizSql = "select num, question, answer from " + quizTable + " order by num asc;";
        List<QuizDto> quiz = jdbcTemplate.query(getQuizSql, (rs, rowNum) -> {
            return new QuizDto(rs.getInt("num"), rs.getString("question"), rs.getString("answer"));
        });

        return quiz;
    }

    public void makeQuiz(int quizKey, List<QuizDto> quiz) {
        String quizTable = "quiz_" + String.valueOf(quizKey);
        String deleteSql = "delete from " + quizTable + ";";
        jdbcTemplate.update(deleteSql);

        String insertQuizSql = "insert into " + quizTable + " (num, question, answer) values (?, ?, ?);";
        for(QuizDto q : quiz) {
            jdbcTemplate.update(insertQuizSql, q.getQuizNum(), q.getQuestion(), q.getAnswer());
        }

        String updateSql = "update quiz_info set quiz_num = ?, cur_num = 1 where quiz_key = ?;";
        jdbcTemplate.update(updateSql, quiz.size(), quizKey);
    }

    public void updateCurNum(int quizKey, int curNum) {
        String sql = "update quiz_info set cur_num = ? where quiz_key = ?;";
        jdbcTemplate.update(sql, curNum, quizKey);
    }
}
