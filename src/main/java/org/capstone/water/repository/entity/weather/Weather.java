package org.capstone.water.repository.entity.weather;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime time;

    private Float swt;

    private Short wdir;

    private Float ws;

    private Float ssa;

    private Float sat;

    private Float sap;

    private Float swh;

    private Float scd;

    private Float scs;
}
