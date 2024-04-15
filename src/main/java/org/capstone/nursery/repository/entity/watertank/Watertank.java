package org.capstone.nursery.repository.entity.watertank;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watertank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Watertank {
    @Id
    private String fcid;

    private String fcn;

}
