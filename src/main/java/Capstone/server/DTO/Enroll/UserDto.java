package Capstone.server.DTO.Enroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    String email;
    String password;
    String nickname;
    String dept_name1;
    String dept_name2;
    List<String> course_name;
}
