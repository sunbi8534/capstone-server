package Capstone.server.Repository;

import Capstone.server.DTO.Etc.AlarmDto;
import Capstone.server.DTO.Etc.NotifyDto;
import Capstone.server.DTO.Etc.ReportDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EtcRepository {
    JdbcTemplate jdbcTemplate;
    public EtcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public boolean checkAlarm(String nickname) {
        String checkAlarmSql = "select alarm_name from alarm where nickname = ?;";
        List<String> alarm = jdbcTemplate.query(checkAlarmSql, (rs, rowNum) -> {
            return new String(rs.getString("alarm_name"));
        }, nickname);

        if(alarm.isEmpty())
            return false;
        else
            return true;
    }

    public List<AlarmDto> getAlarms(String nickname) {
        String getAlarmSql = "select alarm_name, sender, time, description, contents from alarm where nickname = ?;";
        List<AlarmDto> alarms = jdbcTemplate.query(getAlarmSql, (rs, rowNum) -> {
            return new AlarmDto(rs.getString("alarm_name"), rs.getString("sender"),
                    rs.getString("time"), rs.getString("description"), rs.getString("content"));
        }, nickname);

        deleteAlarm(nickname);
        return alarms;
    }

    public void deleteAlarm(String nickname) {
        String delAlarmSql = "delete from alarm where nickname = ?;";
        jdbcTemplate.update(delAlarmSql, nickname);
    }

    public List<NotifyDto> getNotify() {
        String getNotifySql = "select image, contents from notify;";
        List<NotifyDto> notifies = jdbcTemplate.query(getNotifySql, (rs, rowNum) -> {
            return new NotifyDto(rs.getString("image"), rs.getString("contents"));
        });
        return notifies;
    }

    public void storeReport(ReportDto report) {
        String storeReportSql = "insert into report (nickname, reporter, report_kind, report_msg) values " +
                "(?, ?, ?, ?);";
        jdbcTemplate.update(storeReportSql, report.getNickname(), report.getReporter(), report.getReportKind(), report.getReportMsg());
    }
}
