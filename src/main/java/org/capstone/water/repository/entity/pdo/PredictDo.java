package org.capstone.water.repository.entity.pdo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "predictdo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private Long num;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime time;

    // @ManyToOne
    // @JoinColumn(name = "fcid")
    // private Watertank watertank;
    @Column(name = "tankid")
    private String tankid;

    @Column(name = "pdo")
    private Float pdo;
}
