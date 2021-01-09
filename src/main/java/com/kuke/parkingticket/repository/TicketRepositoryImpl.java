package com.kuke.parkingticket.repository;

import com.kuke.parkingticket.entity.PlaceType;
import com.kuke.parkingticket.entity.TermType;
import com.kuke.parkingticket.entity.Ticket;
import com.kuke.parkingticket.entity.TicketStatus;
import com.kuke.parkingticket.model.dto.TicketSearchConditionDto;
import com.kuke.parkingticket.model.dto.TicketSimpleDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import static com.kuke.parkingticket.entity.QTicket.*;
import static com.kuke.parkingticket.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

public class TicketRepositoryImpl extends QuerydslRepositorySupport implements CustomTicketRepository {

    private JPAQueryFactory queryFactory;

    public TicketRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Ticket.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<TicketSimpleDto> findAllTicketWithConditions(TicketSearchConditionDto conditionDto, Pageable pageable) {
        JPAQuery<TicketSimpleDto> query = queryFactory
                .select(Projections.constructor(TicketSimpleDto.class,
                        ticket.id, ticket.title, ticket.price, ticket.view, ticket.address,
                        ticket.startDateTime, ticket.endDateTime, ticket.termType, ticket.ticketStatus,
                        ticket.placeType, user.nickname, ticket.createdAt
                )).from(ticket)
                .leftJoin(ticket.writer, user)
                .where(ticketSearchPredicate(conditionDto));
        long count = queryFactory.selectFrom(ticket).where(ticketSearchPredicate(conditionDto)).fetchCount();
        List<TicketSimpleDto> result = getQuerydsl().applyPagination(pageable,
                query).fetch();
        return new PageImpl<>(result, pageable, count);
    }

    private BooleanBuilder ticketSearchPredicate(TicketSearchConditionDto conditionDto) {
        return new BooleanBuilder()
                .and(termTypeOrCondition(conditionDto.getTermTypes(), conditionDto.getDateTime()))
                .and(placeTypeOrCondition(conditionDto.getPlaceTypes()))
                .and(ticketStatusOrCondition(conditionDto.getTicketStatuses()));
    }

    private BooleanBuilder dateTimeCondition(LocalDateTime dateTime) {
        return new BooleanBuilder().and(dateTimeLoe(dateTime)).and(dateTimeGoe(dateTime));
    }

    private BooleanExpression dateTimeLoe(LocalDateTime dateTime) {
        return dateTime != null ? ticket.startDateTime.loe(dateTime) : null;
    }

    private BooleanExpression dateTimeGoe(LocalDateTime dateTime) {
        return dateTime != null ? ticket.endDateTime.goe(dateTime) : null;
    }

    private BooleanBuilder termTypeOrCondition(List<TermType> termTypes, LocalDateTime dateTime) {
        BooleanBuilder builder = new BooleanBuilder();
        termTypes.stream().forEach(t -> builder.or(ticket.termType.eq(t)));
        return termTypes.contains(TermType.INPUT) ? builder.or(dateTimeCondition(dateTime)) : builder;
    }

    private BooleanBuilder placeTypeOrCondition(List<PlaceType> placeTypes) {
        BooleanBuilder builder = new BooleanBuilder();
        placeTypes.stream().forEach(p -> builder.or(ticket.placeType.eq(p)));
        return builder;
    }

    private BooleanBuilder ticketStatusOrCondition(List<TicketStatus> ticketStatuses) {
        BooleanBuilder builder = new BooleanBuilder();
        ticketStatuses.stream().forEach(t -> builder.or(ticket.ticketStatus.eq(t)));
        return builder;
    }

}
