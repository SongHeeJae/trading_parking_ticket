package com.kuke.parkingticket.service.message;

import com.kuke.parkingticket.advice.exception.HistoryNotFoundException;
import com.kuke.parkingticket.advice.exception.MessageNotFoundException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.*;
import com.kuke.parkingticket.model.dto.history.HistoryCreateRequestDto;
import com.kuke.parkingticket.model.dto.history.HistoryDto;
import com.kuke.parkingticket.model.dto.message.MessageCreateRequestDto;
import com.kuke.parkingticket.model.dto.message.MessageDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.history.HistoryRepository;
import com.kuke.parkingticket.repository.message.MessageRepository;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.history.HistoryService;
import com.kuke.parkingticket.service.sign.SignService;
import com.kuke.parkingticket.service.ticket.TicketService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MessageServiceTest {

    @Autowired SignService signService;
    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired MessageService messageService;
    @Autowired UserRepository userRepository;
    @Autowired MessageRepository messageRepository;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("MessageServiceTest"));
        Town town = townRepository.save(Town.createTown("MessageServiceTest", region));
        signService.registerUser(new UserRegisterRequestDto("sender1", "1234", "sender1", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("receiver1", "1234", "receiver1", town.getId()));
    }

    @Test
    public void findSentMessagesByUserIdTest() {
        // given
        User sender = userRepository.findByUid("sender1").orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findByUid("receiver1").orElseThrow(UserNotFoundException::new);
        for(int i=0; i<5; i++) {
            messageService.createMessage(new MessageCreateRequestDto(sender.getId(), receiver.getId(), "message" + i));
        }

        // when
        Slice<MessageDto> result1 = messageService.findSentMessagesByUserId(sender.getId(), null, 2);
        List<MessageDto> content1 = result1.getContent();
        MessageDto lastMessage1 = content1.get(content1.size() - 1);
        Slice<MessageDto> result2 = messageService.findSentMessagesByUserId(sender.getId(), lastMessage1.getId(), 2);
        List<MessageDto> content2 = result2.getContent();
        MessageDto lastMessage2 = content2.get(content2.size() - 1);
        Slice<MessageDto> result3 = messageService.findSentMessagesByUserId(sender.getId(), lastMessage2.getId(), 2);
        List<MessageDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

    @Test
    public void findReceivedMessagesByUserIdTest() {
        // given
        User sender = userRepository.findByUid("sender1").orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findByUid("receiver1").orElseThrow(UserNotFoundException::new);
        for(int i=0; i<5; i++) {
            messageService.createMessage(new MessageCreateRequestDto(sender.getId(), receiver.getId(), "message" + i));
        }

        // when
        Slice<MessageDto> result1 = messageService.findReceivedMessagesByUserId(receiver.getId(), null, 2);
        List<MessageDto> content1 = result1.getContent();
        MessageDto lastMessage1 = content1.get(content1.size() - 1);
        Slice<MessageDto> result2 = messageService.findReceivedMessagesByUserId(receiver.getId(), lastMessage1.getId(), 2);
        List<MessageDto> content2 = result2.getContent();
        MessageDto lastMessage2 = content2.get(content2.size() - 1);
        Slice<MessageDto> result3 = messageService.findReceivedMessagesByUserId(receiver.getId(), lastMessage2.getId(), 2);
        List<MessageDto> content3 = result3.getContent();

        // then
        assertThat(content1.size()).isEqualTo(2);
        assertThat(result1.hasNext()).isTrue();
        assertThat(content2.size()).isEqualTo(2);
        assertThat(result2.hasNext()).isTrue();
        assertThat(content3.size()).isEqualTo(1);
        assertThat(result3.hasNext()).isFalse();
    }

    @Test
    public void createMessageTest() {

        // given
        User sender = userRepository.findByUid("sender1").orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findByUid("receiver1").orElseThrow(UserNotFoundException::new);
        MessageCreateRequestDto requestDto = new MessageCreateRequestDto(sender.getId(), receiver.getId(), "message");

        // when
        MessageDto messageDto = messageService.createMessage(requestDto);

        // then
        Message result = messageRepository.findById(messageDto.getId()).orElseThrow(MessageNotFoundException::new);
        assertThat(result.getId()).isEqualTo(messageDto.getId());
        assertThat(result.getSender().getId()).isEqualTo(sender.getId());
        assertThat(result.getReceiver().getId()).isEqualTo(receiver.getId());
    }

    @Test
    public void deleteMessageTest() {
        // given
        User sender = userRepository.findByUid("sender1").orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findByUid("receiver1").orElseThrow(UserNotFoundException::new);
        MessageDto messageDto = messageService.createMessage(new MessageCreateRequestDto(sender.getId(), receiver.getId(), "message"));

        // when
        messageService.deleteMessage(messageDto.getId());

        // then
        assertThatThrownBy(() -> messageRepository.findById(messageDto.getId()).orElseThrow(MessageNotFoundException::new))
                .isInstanceOf(MessageNotFoundException.class);
    }

    @Test
    public void readMessageTest() {

        // given
        User sender = userRepository.findByUid("sender1").orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findByUid("receiver1").orElseThrow(UserNotFoundException::new);
        MessageDto messageDto = messageService.createMessage(new MessageCreateRequestDto(sender.getId(), receiver.getId(), "message"));

        // when
        MessageDto result = messageService.readMessage(messageDto.getId());

        // then
        assertThat(result.getId()).isEqualTo(messageDto.getId());
        assertThat(result.getSenderId()).isEqualTo(sender.getId());
        assertThat(result.getReceiverId()).isEqualTo(receiver.getId());
    }
}