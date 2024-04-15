package org.capstone.nursery.repository.entity.waterdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.capstone.nursery.repository.entity.watertank.WaterId;
import org.capstone.nursery.repository.entity.watertank.Watertank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "waterdata")
@Builder
@IdClass(WaterId.class)
public class Waterdata {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime dtime;

    @Id
    @ManyToOne
    @JoinColumn(name = "fcid")
    private Watertank watertank;

    private float wdo;

    private float wt;

    private float ph;

    private float sa;
}
