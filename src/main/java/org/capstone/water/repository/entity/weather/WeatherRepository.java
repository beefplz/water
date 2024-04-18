package org.capstone.water.repository.entity.weather;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface WeatherRepository extends JpaRepository<Weather, LocalDateTime> {

}
