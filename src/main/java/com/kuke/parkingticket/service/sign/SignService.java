package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.*;
import com.kuke.parkingticket.common.cache.CacheKey;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Access;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TownRepository townRepository;
    private final RedisTemplate redisTemplate;

    @Transactional
    public UserLoginResponseDto loginUser(UserLoginRequestDto requestDto) {
        User user = userRepository.findByUid(requestDto.getUid()).orElseThrow(LoginFailureException::new);
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new LoginFailureException();
        user.changeRefreshToken(jwtTokenProvider.createRefreshToken());
        return new UserLoginResponseDto(user.getId(), jwtTokenProvider.createToken(String.valueOf(user.getId())), user.getRefreshToken());
    }

    @Transactional
    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        validateDuplicateUser(requestDto.getUid(), requestDto.getNickname());
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        User user = userRepository.save(
                User.createUser(
                        requestDto.getUid(),
                        passwordEncoder.encode(requestDto.getPassword()),
                        requestDto.getNickname(),
                        town));
        return new UserRegisterResponseDto(user.getId(), user.getUid(), user.getNickname());
    }

    @Transactional
    public UserLoginResponseDto refreshToken(String token, String refreshToken) {
        // 아직 만료되지 않은 토큰으로는 refresh 할 수 없음
        if(!jwtTokenProvider.validateTokenExceptExpiration(token)) throw new AccessDeniedException("");
        User user = userRepository.findById(Long.valueOf(jwtTokenProvider.getUserPk(token))).orElseThrow(UserNotFoundException::new);
        if(!jwtTokenProvider.validateToken(user.getRefreshToken()) || !refreshToken.equals(user.getRefreshToken()))
            throw new AccessDeniedException("");
        user.changeRefreshToken(jwtTokenProvider.createRefreshToken());
        return new UserLoginResponseDto(user.getId(), jwtTokenProvider.createToken(String.valueOf(user.getId())), user.getRefreshToken());
    }

    private void validateDuplicateUser(String uid, String nickname) {
        if(userRepository.findByUid(uid).isPresent()) throw new UserIdAlreadyExistsException();
        if(userRepository.findByNickname(nickname).isPresent()) throw new UserNicknameAlreadyException();
    }

    @Transactional
    public void logoutUser(String token) {
        redisTemplate.opsForValue().set(CacheKey.TOKEN + ":" + token, "v", jwtTokenProvider.getRemainingSeconds(token));
        User user = userRepository.findById(Long.valueOf(jwtTokenProvider.getUserPk(token))).orElseThrow(UserNotFoundException::new);
        user.changeRefreshToken("invalidate");
    }
}
