package com.kuke.parkingticket.repository.town;

import com.kuke.parkingticket.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TownRepository extends JpaRepository<Town, Long> {
    Optional<Town> findByName(String name);
}
