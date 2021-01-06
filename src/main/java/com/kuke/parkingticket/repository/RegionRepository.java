package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
