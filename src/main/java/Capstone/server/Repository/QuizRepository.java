package Capstone.server.Repository;

import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Quiz.QuizInfo;
import Capstone.server.DTO.Quiz.QuizInfoDto;
import Capstone.server.DTO.Quiz.QuizMakeDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuizRepository {
    JdbcTemplate jdbcTemplate;

    public QuizRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<QuizInfoDto> getMyQuiz(String nickname, String course) {
        List<QuizInfoDto> quizInfoDtos = new ArrayList<>();
        String sql = "select quiz_key, quiz_name, quiz_num from quiz_info where course_name = ? and nickname = ?;";
        int num;
        List<QuizInfo> quiz = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new QuizInfo(rs.getInt("quiz_key"), rs.getString("quiz_name"), rs.getInt("quiz_num"));
        }, course, nickname);
        if(quiz.isEmpty())
            return quizInfoDtos;

        for(QuizInfo q : quiz) {
            String tableName = "quiz_" + String.valueOf(q.getQuizKey());
            String getSql = "select is_solved from " + tableName + ";";
            List<Boolean> isDoing = jdbcTemplate.query(getSql, (rs, rowNum) -> {
                return Boolean.valueOf(rs.getBoolean("is_solved"));
            });

            num = 0;
            if(!isDoing.isEmpty()) {
                for(Boolean b : isDoing) {
                    if(!b)
                        num++;
                }
            }
            QuizInfoDto infoDto = new QuizInfoDto(q.getQuizKey(), q.getQuizName(), q.getQuizNum(), num);
            quizInfoDtos.add(infoDto);
        }

        return quizInfoDtos;
    }

    public List<String> getMyQuizFolderName(String nickname) {
        String sql = "select quiz_name from quiz_info where nickname = ?;";
        List<String> quizName = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return String.valueOf(rs.getString("quiz_name"));
        }, nickname);

        return quizName;
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

        String insertSql = "insert into quiz_info (my_key, course_name, nickname, quiz_name, quiz_num) values" +
                " (?, ?, ?, ?, 0);";
        jdbcTemplate.update(insertSql, key, quiz.getCourse(), quiz.getNickname(), quiz.getQuizName());
        String getKeySql = "select quiz_key from quiz_info where my_key = ? and nickname = ? and course_name = ?;";

        List<Integer> quizKey = jdbcTemplate.query(getKeySql, (rs, rowNum) -> {
            return Integer.valueOf(rs.getInt("quiz_key"));
        }, key, quiz.getNickname(), quiz.getCourse());
        int qKey = quizKey.get(0);

        String tableName = "quiz_" + String.valueOf(qKey);
        String makeSql = "create table " + tableName + "(" +
                "num integer, question varchar(300), answer varchar(200), is_solved BOOL);";
        jdbcTemplate.update(makeSql);
    }

    public List<QuizDto> quiz(int quizKey) {
        String quizTable = "quiz_" + String.valueOf(quizKey);
        String getQuizSql = "select num, question, answer, is_solved from " + quizTable + " order by num asc;";
        List<QuizDto> quiz = jdbcTemplate.query(getQuizSql, (rs, rowNum) -> {
            return new QuizDto(rs.getInt("num"), rs.getString("question"), rs.getString("answer"),
                    rs.getBoolean("is_solved"));
        });

        return quiz;
    }

    public void deleteQuizKey(int quizKey) {
        String tableName = "quiz_" + String.valueOf(quizKey);
        String dropSql = "drop table " + tableName + ";";
        String deleteSql = "delete from quiz_info where quiz_key = ?;";

        jdbcTemplate.update(dropSql);
        jdbcTemplate.update(deleteSql, quizKey);
    }

    public void makeQuiz(int quizKey, List<QuizDto> quiz) {
        String quizTable = "quiz_" + String.valueOf(quizKey);
        String deleteSql = "delete from " + quizTable + " where num > -1;";
        jdbcTemplate.update(deleteSql);

        String insertQuizSql = "insert into " + quizTable + " (num, question, answer, is_solved) values (?, ?, ?, ?);";
        for(QuizDto q : quiz) {
            jdbcTemplate.update(insertQuizSql, q.getQuizNum(), q.getQuestion(), q.getAnswer(), q.getIsSolved());
        }

        String updateSql = "update quiz_info set quiz_num = ? where quiz_key = ?;";
        jdbcTemplate.update(updateSql, quiz.size(), quizKey);
    }
}
