package org.capstone.water.repository.entity.mldata;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public interface MldataMapping {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    LocalDateTime getTime();

    String getTankid();
    Float getWdo();
    Float getWt();
    Float getPh();
    Float getSa();
    Float getSwt();
    Float getWdir();
    Float getWs();
    Float getSsa();
    Float getSat();
    Float getSap();
    Float getSwh();
    Float getScd();
    Float getScs();
    Float getOs();
}
