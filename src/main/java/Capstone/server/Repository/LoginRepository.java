package Capstone.server.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LoginRepository {
    private JdbcTemplate jdbcTemplate;

    public LoginRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String checkLoginInfo(String id, String passwordHash) {
        String sql = "SELECT nickname FROM user WHERE email = ? and password = ?";

        //user테이블 안에 (email, password의 해시코드 값)과 일치하는 튜플이 있는지 확인한다.
        List<String> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new String(
                    rs.getString("nickname"));
        }, id, passwordHash);

        if(result.isEmpty())
            return "false";
        else
            return result.get(0);
    }
}
