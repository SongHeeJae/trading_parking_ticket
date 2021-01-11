package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNicknameAlreadyException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
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
import com.kuke.parkingticket.service.sign.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SignServiceTest {

    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired SignService signService;
    @Autowired UserRepository userRepository;

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
        UserRegisterResponseDto registerResponse = signService.registerUser(new UserRegisterRequestDto(uid, password, "쿠케캬캬", town.getId()));
        userRepository.findById(registerResponse.getId()).orElseThrow(UserNotFoundException::new);

        // when
        UserLoginResponseDto loginResponse = signService.loginUser(new UserLoginRequestDto(uid, password));

        // then
        assertThat(loginResponse.getToken().length()).isPositive();

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
}