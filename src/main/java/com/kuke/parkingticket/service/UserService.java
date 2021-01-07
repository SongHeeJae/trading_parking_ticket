package com.kuke.parkingticket.service;

import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.TownDto;
import com.kuke.parkingticket.model.dto.UserDto;
import com.kuke.parkingticket.model.dto.UserUpdateRequestDto;
import com.kuke.parkingticket.repository.TownRepository;
import com.kuke.parkingticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TownRepository townRepository;

    public List<UserDto> findAll() {
        return userRepository.findAllWithTown().stream()
            .map(u -> new UserDto(u.getId(), u.getUid(), u.getNickname(),
                    new TownDto(u.getTown().getId(), u.getTown().getName()), u.getCreatedAt(), u.getModifiedAt())).collect(Collectors.toList());
    }

    public UserDto findUser(Long userId) {
        return userRepository.findUser(userId).map(u -> new UserDto(u.getId(), u.getUid(), u.getNickname(), new TownDto(u.getTown().getId(), u.getTown().getName()), u.getCreatedAt(), u.getModifiedAt())).orElseThrow(UserNotFoundException::new);
    }

    public UserDto updateUser(Long userId, UserUpdateRequestDto requestDto) {
        validateDuplicateUserNickname(requestDto.getNickname());
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        user.updateUser(requestDto.getNickname(), town);
        return new UserDto(user.getId(), user.getUid(), user.getNickname(), new TownDto(user.getTown().getId(), user.getTown().getName()), user.getCreatedAt(), user.getModifiedAt());
    }

    private void validateDuplicateUserNickname(String nickname) {
        if(userRepository.findByUid(nickname).isPresent()) throw new UserIdAlreadyExistsException();
    }
}
