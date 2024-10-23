package org.capstone.water.repository.entity.pdoweek;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface PdoWeekMapping {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    LocalDateTime getTime();

    String getTankid();

    Float getPdoweek();

}
