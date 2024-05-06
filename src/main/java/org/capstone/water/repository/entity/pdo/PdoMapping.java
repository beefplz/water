package org.capstone.water.repository.entity.pdo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface PdoMapping {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    LocalDateTime getTime();

    String getTankid();

    Float getPdo();

}
