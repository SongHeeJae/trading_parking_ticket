package com.kuke.parkingticket.service.sign;

import com.kuke.parkingticket.advice.exception.*;
import com.kuke.parkingticket.common.cache.CacheKey;
import com.kuke.parkingticket.config.security.JwtTokenProvider;
import com.kuke.parkingticket.entity.Town;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.model.dto.user.*;
import com.kuke.parkingticket.repository.town.TownRepository;
import com.kuke.parkingticket.repository.user.UserRepository;
import com.kuke.parkingticket.service.social.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TownRepository townRepository;
    private final KakaoService kakaoService;
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
                        town,
                        null));
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

    @Transactional
    public UserLoginResponseDto loginUserByProvider(String accessToken, String provider) {
        String uid = getUidByProvider(accessToken, provider);
        User user = userRepository.findUserByUidAndProvider(uid, provider).orElseThrow(LoginFailureException::new);
        user.changeRefreshToken(jwtTokenProvider.createRefreshToken());
        return new UserLoginResponseDto(user.getId(), jwtTokenProvider.createToken(String.valueOf(user.getId())), user.getRefreshToken());
    }

    private String getUidByProvider(String accessToken, String provider) {
        if(provider.equals("kakao")) {
            return kakaoService.getKakaoId(accessToken);
        }
        throw new InvalidateProviderException();
    }

    @Transactional
    public UserRegisterResponseDto registerUserByProvider(UserRegisterByProviderRequestDto requestDto, String accessToken, String provider) {
        String uid = getUidByProvider(accessToken, provider);
        validateDuplicateUserByProvider(uid, provider);
        Town town = townRepository.findById(requestDto.getTownId()).orElseThrow(TownNotFoundException::new);
        User user = userRepository.save(
                User.createUser(
                        uid,
                        null,
                        requestDto.getNickname(),
                        town,
                        provider));
        return new UserRegisterResponseDto(user.getId(), user.getUid(), user.getNickname());
    }

    private void validateDuplicateUserByProvider(String uid, String provider) {
        if(userRepository.findUserByUidAndProvider(uid, provider).isPresent())
            new UserIdAlreadyExistsException();
    }

}
