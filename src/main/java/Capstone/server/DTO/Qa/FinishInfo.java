package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FinishInfo {
    int point;
    String questioner;
    String solver;
}
