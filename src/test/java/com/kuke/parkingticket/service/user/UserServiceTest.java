package com.kuke.parkingticket.service.user;

import com.kuke.parkingticket.TestConfig;
import com.kuke.parkingticket.entity.Region;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.user.UserDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.repository.region.RegionRepository;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired TownRepository townRepository;
    @Autowired RegionRepository regionRepository;
    @Autowired UserService userService;

    @Test
    public void findAllTest() {

        // given
        Region region = regionRepository.save(Region.createRegion("UserServiceTest"));
        Town town = townRepository.save(Town.createTown("UserServiceTest", region));
        userRepository.save(User.createUser("test1", "1234", "test1", town, null));
        userRepository.save(User.createUser("test2", "1234", "test2", town, null));
        userRepository.save(User.createUser("test3", "1234", "test3", town, null));
        userRepository.save(User.createUser("test4", "1234", "test4", town, null));

        // when
        List<UserDto> result = userService.findAll();

        // then
        Assertions.assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void findUserTest() {

        // given
        Region region = Region.createRegion("UserServiceTest");
        regionRepository.save(region);
        Town town = townRepository.save(Town.createTown("UserServiceTest", region));
        User user = userRepository.save(User.createUser("test1", "1234", "test1", town, null));

        // when
        UserDto result = userService.findUser(user.getId());

        // then
        Assertions.assertThat(result.getId()).isEqualTo(user.getId());
        Assertions.assertThat(result.getUid()).isEqualTo(user.getUid());
        Assertions.assertThat(result.getNickname()).isEqualTo(user.getNickname());
        Assertions.assertThat(result.getTown().getId()).isEqualTo(town.getId());
    }

    @Test
    public void updateUserTest() {
        // given
        Region region = regionRepository.save(Region.createRegion("UserServiceTest"));
        Town curTown = townRepository.save(Town.createTown("UserServiceTest1", region));
        Town nextTown = townRepository.save(Town.createTown("UserServiceTest2", region));
        String nextNickname = "test2";
        User user = userRepository.save(User.createUser("test1", "1234", "test1", curTown, null));

        // when
        userService.updateUser(user.getId(), new UserUpdateRequestDto(nextNickname, nextTown.getId()));

        // then
        UserDto result = userService.findUser(user.getId());
        Assertions.assertThat(result.getNickname()).isEqualTo(nextNickname);
        Assertions.assertThat(result.getTown().getId()).isEqualTo(nextTown.getId());
    }
}