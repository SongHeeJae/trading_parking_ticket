package com.kuke.parkingticket.repository.town;

import com.kuke.parkingticket.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TownRepository extends JpaRepository<Town, Long> {
}