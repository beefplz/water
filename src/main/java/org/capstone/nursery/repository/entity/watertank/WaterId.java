package org.capstone.nursery.repository.entity.watertank;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WaterId implements Serializable {
    private LocalDateTime dtime;

    @ManyToOne
    @JoinColumn(name = "fcid")
    private Watertank watertank;
}
