package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNicknameAlreadyException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.config.security.JwtTokenProvider;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SignServiceTest {

    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired UserRepository userRepository;
    @Autowired JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void beforeEach() {
        jwtTokenProvider.setTokenValidMillisecond(1000L * 60 * 30);
        jwtTokenProvider.setRefreshTokenValidMillisecond(1000L * 60 * 60 * 24 * 7);
    }

    @Test
    public void registerUserTest() {
        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);

        // when
        UserRegisterResponseDto response = signService.registerUser(new UserRegisterRequestDto("gmlwo308", "1234", "쿠케캬캬", town.getId()));

        // then
        User user = userRepository.findById(response.getId()).orElseThrow(UserNotFoundException::new);
        assertThat(user.getId()).isEqualTo(response.getId());
        assertThat(user.getUid()).isEqualTo(response.getUid());
        assertThat(user.getNickname()).isEqualTo(response.getNickname());
    }

    @Test
    public void loginUserTest() {
        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));

        // when
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // then
        assertThat(jwtTokenProvider.validateToken(loginResponse.getToken())).isTrue();

    }

    @Test
    public void loginUserIssuedRefreshTokenTest() {
        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));

        // when
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));
        User user = userRepository.findById(loginResponse.getId()).orElseThrow(UserNotFoundException::new);

        // then
        assertThat(jwtTokenProvider.validateToken(user.getRefreshToken())).isTrue();
    }

    @Test
    public void duplicateUserRegisterTest() {
        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        UserRegisterRequestDto dto = new UserRegisterRequestDto("gmlwo308", "1234", "희재", town.getId());
        UserRegisterRequestDto uidDupDto = new UserRegisterRequestDto("gmlwo308", "1234", "재희", town.getId());
        UserRegisterRequestDto nicDupDto = new UserRegisterRequestDto("803owlmg", "1234", "희재", town.getId());
        signService.registerUser(dto);

        // when, then
        Assertions.assertThrows(UserIdAlreadyExistsException.class, () -> signService.registerUser(uidDupDto));
        Assertions.assertThrows(UserNicknameAlreadyException.class, () -> signService.registerUser(nicDupDto));
    }

    @Test
    public void refreshTokenTest() {

        // given
        jwtTokenProvider.setTokenValidMillisecond(-1L);
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when
        jwtTokenProvider.setTokenValidMillisecond(3600L);
        UserLoginResponseDto refreshResponseDto = signService.refreshToken(loginResponse.getToken(), loginResponse.getRefreshToken());

        // then
        User user = userRepository.findById(loginResponse.getId()).orElseThrow(UserNotFoundException::new);
        assertThat(jwtTokenProvider.validateToken(user.getRefreshToken())).isTrue();
        assertThat(jwtTokenProvider.validateToken(refreshResponseDto.getToken())).isTrue();

    }

    @Test
    public void logoutUserTest() {

        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when
        signService.logoutUser(loginResponse.getToken());

        // then
        User user = userRepository.findById(loginResponse.getId()).orElseThrow(UserNotFoundException::new);
        assertThat(jwtTokenProvider.validateToken(user.getRefreshToken())).isFalse();
        assertThat(jwtTokenProvider.validateToken(loginResponse.getToken())).isFalse();
    }

    @Test
    public void refreshTokenExceptionByInvalidateTokenTest() {

        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when, then
        assertThatThrownBy(() -> {
            signService.refreshToken("invalidate", responseDto.getRefreshToken());
        }).isInstanceOf(AccessDeniedException.class);

    }

    @Test
    public void refreshTokenExceptionByDifferentRefreshTokenTest() {

        // given
        jwtTokenProvider.setTokenValidMillisecond(-1L);
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when, then
        assertThatThrownBy(() -> {
            signService.refreshToken(responseDto.getToken(), "different refresh token");
        }).isInstanceOf(AccessDeniedException.class);

    }


    @Test
    public void refreshTokenExceptionByValidateTokenTest() {

        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto responseDto = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when, then
        assertThatThrownBy(() -> {
            signService.refreshToken(responseDto.getToken(), responseDto.getRefreshToken());
        }).isInstanceOf(AccessDeniedException.class);

    }

    @Test
    public void refreshTokenExceptionByInvalidateExpirationRefreshTokenTest() {

        // given
        jwtTokenProvider.setRefreshTokenValidMillisecond(-1L);
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when, then
        assertThatThrownBy(() -> {
            signService.refreshToken(loginResponse.getToken(), loginResponse.getRefreshToken());
        }).isInstanceOf(AccessDeniedException.class);
    }


    @Test
    public void refreshTokenExceptionByLogoutTest() {

        // given
        Region region = Region.createRegion("서울");
        regionRepository.save(region);
        Town town = Town.createTown("희재동", region);
        townRepository.save(town);
        String uid = "gmlwo308";
        String password = "1234";
        signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // when
        signService.logoutUser(loginResponse.getToken());

        // then
        assertThatThrownBy(() -> {
            signService.refreshToken(loginResponse.getToken(), loginResponse.getRefreshToken());
        }).isInstanceOf(AccessDeniedException.class);

    }

}