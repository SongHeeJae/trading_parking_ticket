package com.kuke.parkingticket.service.user;

import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.model.dto.user.UserDto;
import com.kuke.parkingticket.model.dto.user.UserUpdateRequestDto;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TownRepository townRepository;

    public List<UserDto> findAll() {
        return userRepository.findAllWithTown().stream()
            .map(u -> convertUserToDto(u)).collect(Collectors.toList());
    }

    public UserDto findUser(Long userId) {
        return convertUserToDto(userRepository.findUser(userId).orElseThrow(UserNotFoundException::new));
    }

    @Transactional
    public UserDto updateUser(Long userId, UserUpdateRequestDto requestDto) {
        validateDuplicateUserNickname(requestDto.getNickname());
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        user.update(requestDto.getNickname(), town);
        return convertUserToDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserDto convertUserToDto(User user) {
        return new UserDto(user.getId(), user.getUid(), user.getNickname(), new TownDto(user.getTown().getId(), user.getTown().getName()), user.getCreatedAt(), user.getModifiedAt());
    }

    private void validateDuplicateUserNickname(String nickname) {
        if(userRepository.findByUid(nickname).isPresent()) throw new UserIdAlreadyExistsException();
    }
}
