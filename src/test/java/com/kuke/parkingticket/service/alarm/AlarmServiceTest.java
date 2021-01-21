package com.kuke.parkingticket.service.alarm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.message.MessageCreateRequestDto;
import com.kuke.parkingticket.model.dto.message.MessageDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.message.MessageService;
import com.kuke.parkingticket.service.sign.SignService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AlarmServiceTest {

    static final String WEBSOCKET_TOPIC = "/sub/";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired MessageService messageService;
    @LocalServerPort Integer port;

    @BeforeEach
    public void beforeEach() {
        Region region = regionRepository.save(Region.createRegion("AlarmControllerTest"));
        Town town = townRepository.save(Town.createTown("AlarmControllerTest", region));
        signService.registerUser(new UserRegisterRequestDto("sender", "1234", "sender", town.getId()));
        signService.registerUser(new UserRegisterRequestDto("receiver", "1234", "receiver", town.getId()));
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void connectionFailedByInvalidateTokenTest() {

        // given
        StompHeaders headers = new StompHeaders();
        headers.add("token", "invalidate token");

        // when, then
        Assertions.assertThatThrownBy(() -> {
            stompClient
                    .connect(getWsPath(), new WebSocketHttpHeaders() ,headers, new StompSessionHandlerAdapter() {})
                    .get(10, SECONDS);
        }).isInstanceOf(ExecutionException.class);
    }

    @Test
    public void alarmByMessageTest() throws Exception {

        // given
        UserLoginResponseDto sender = signService.loginUser(new UserLoginRequestDto("sender", "1234"));
        UserLoginResponseDto receiver = signService.loginUser(new UserLoginRequestDto("receiver", "1234"));
        StompHeaders headers = new StompHeaders();
        headers.add("token", sender.getToken());
        StompSession session = stompClient
                .connect(getWsPath(), new WebSocketHttpHeaders() ,headers, new StompSessionHandlerAdapter() {})
                .get(20, SECONDS);
        session.subscribe(WEBSOCKET_TOPIC + receiver.getId(), new DefaultStompFrameHandler());

        // when
        MessageCreateRequestDto requestDto = new MessageCreateRequestDto(sender.getId(), receiver.getId(), "MESSAGE TEST");
        MessageDto messageDto = messageService.createMessage(requestDto);

        // then
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = blockingQueue.poll(20, SECONDS);
        Map<String, String> result = mapper.readValue(jsonResult, Map.class);
        assertThat(result.get("message")).isEqualTo(messageDto.getMessage());
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }

    private String getWsPath() {
        return String.format("ws://localhost:%d/ws-stomp", port);
    }
}