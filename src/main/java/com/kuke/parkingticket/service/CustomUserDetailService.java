package com.kuke.parkingticket.service;

import com.kuke.parkingticket.advice.exception.UserNotFoundException;
import com.kuke.parkingticket.entity.User;
import com.kuke.parkingticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.valueOf(userPk)).orElseThrow(UserNotFoundException::new);
        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getId()), user.getPassword(), new ArrayList<>());
    }
}
