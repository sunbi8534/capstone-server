package Capstone.server.DTO.Etc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AlarmDto {
    String alarmName;
    String sender;
    String time;
    String description;
    String content;
}
