package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, CustomTicketRepository {

    @Query("select t from Ticket t join fetch t.writer join fetch t.town where t.id = :id")
    Optional<Ticket> findTicketByIdWithWriterAndTown(@Param("id") Long id);
}
