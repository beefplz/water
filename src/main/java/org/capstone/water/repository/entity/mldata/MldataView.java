package org.capstone.water.repository.entity.mldata;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name ="mldata")
@Getter
public class MldataView {
    @Column(name="time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime time;

    @Id
    private Long num;

    private String tankid;
    private Float wdo;
    private Float wt;
    private Float ph;
    private Float sa;
    private Float swt;
    private Float wdir;
    private Float ws;
    private Float ssa;
    private Float sat;
    private Float sap;
    private Float swh;
    private Float scd;
    private Float scs;

    @Transient
    @Setter
    private Float Os;

    /*public void setOs(Float Os) {
        this.Os = Os;
    }*/
}
