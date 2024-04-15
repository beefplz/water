package org.capstone.nursery.repository.entity.pdo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.capstone.nursery.repository.entity.watertank.WaterId;
import org.capstone.nursery.repository.entity.watertank.Watertank;

import java.time.LocalDateTime;

@Entity
@Table(name = "predicted_do")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(WaterId.class)
public class PredictedDo {
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmm", timezone = "Asia/Seoul")
    private LocalDateTime dtime;

    @Id
    @ManyToOne
    @JoinColumn(name = "fcid")
    private Watertank watertank;

    private Float pdo;
}
