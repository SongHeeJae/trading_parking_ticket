package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNicknameAlreadyException;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.service.sign.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SignServiceTest {

    @Autowired RegionRepository regionRepository;
    @Autowired TownRepository townRepository;
    @Autowired
    SignService signService;

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