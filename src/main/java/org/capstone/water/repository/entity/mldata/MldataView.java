package org.capstone.water.repository.entity.mldata;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name ="mldata")
@Getter
//@IdClass(WaterId.class)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MldataView {
    //@Id
    @Column(name="time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime time;

    @Id
    //@ManyToOne
    //@JoinColumn(name = "fcid")
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
    private Float stl;
}
