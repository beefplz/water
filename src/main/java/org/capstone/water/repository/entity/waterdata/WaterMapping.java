package org.capstone.water.repository.entity.waterdata;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface WaterMapping {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    LocalDateTime getTime();

    String getTankid();

    Float getWdo();

    Float getWt();

    Float getPh();
    Float getSa();


}
