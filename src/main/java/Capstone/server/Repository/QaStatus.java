package Capstone.server.Repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaStatus {
    Boolean isWatching;
    Boolean isSolving;
    Boolean status;
}
