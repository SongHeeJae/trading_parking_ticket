package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.LoginFailureException;
import com.kuke.parkingticket.advice.exception.TownNotFoundException;
import com.kuke.parkingticket.advice.exception.UserIdAlreadyExistsException;
import com.kuke.parkingticket.advice.exception.UserNicknameAlreadyException;
import com.kuke.parkingticket.config.security.JwtTokenProvider;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.town.TownDto;
import com.kuke.parkingticket.model.dto.user.UserLoginRequestDto;
import com.kuke.parkingticket.model.dto.user.UserLoginResponseDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterRequestDto;
import com.kuke.parkingticket.model.dto.user.UserRegisterResponseDto;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TownRepository townRepository;

    public UserLoginResponseDto loginUser(UserLoginRequestDto requestDto) {
        User user = userRepository.findByUid(requestDto.getUid()).orElseThrow(LoginFailureException::new);
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new LoginFailureException();
        return new UserLoginResponseDto(jwtTokenProvider.createToken(String.valueOf(user.getId())));
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        validateDuplicateUser(requestDto.getUid(), requestDto.getNickname());
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        User saveUser = userRepository.save(
                User.createUser(
                        requestDto.getUid(),
                        passwordEncoder.encode(requestDto.getPassword()),
                        requestDto.getNickname(),
                        town));
        return new UserRegisterResponseDto(saveUser.getUid(), new TownDto(town.getId(), town.getName()));
    }

    private void validateDuplicateUser(String uid, String nickname) {
        if(userRepository.findByUid(uid).isPresent()) throw new UserIdAlreadyExistsException();
        if(userRepository.findByNickname(nickname).isPresent()) throw new UserNicknameAlreadyException();
    }
}
