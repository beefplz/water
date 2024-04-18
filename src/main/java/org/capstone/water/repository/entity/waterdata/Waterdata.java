package org.capstone.water.repository.entity.waterdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "waterdata")
public class Waterdata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private Long num;

    @Column(name = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime time;

    /*@ManyToOne
    @JoinColumn(name = "fcid")
    private Watertank watertank;*/
    @Column(name = "tankid")
    private String tankid;

    @Column(name = "wdo")
    private Float wdo;

    @Column(name = "wt")
    private Float wt;

    @Column(name = "ph")
    private Float ph;

    @Column(name = "sa")
    private Float sa;

    @Builder
    public Waterdata(Long num, LocalDateTime time,String tankid, Float wdo, Float wt, Float ph, Float sa){
        this.num = num;
        this.time = time;
        this.tankid = tankid;
        this.wdo = wdo;
        this.wt = wt;
        this.ph = ph;
        this.sa = sa;
    }
}
