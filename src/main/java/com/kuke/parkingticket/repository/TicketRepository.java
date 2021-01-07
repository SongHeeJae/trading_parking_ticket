package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
