package com.userservuce.userservice.services;

import com.userservuce.userservice.dtos.UserDto;
import com.userservuce.userservice.dtos.ValidateTokenRequestDto;
import com.userservuce.userservice.models.Session;
import com.userservuce.userservice.models.SessionStatus;
import com.userservuce.userservice.models.User;
import com.userservuce.userservice.repositories.SessionRepository;
import com.userservuce.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.HashMap;
import java.util.Optional;




@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()){
            return null;
        }
        User user = userOptional.get();

        if (!user.getPassword().equals(password)){
            return null;
        }

        String token = RandomStringUtils.randomAlphanumeric(30);

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = new UserDto();

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);

        return response;
    }

    public ResponseEntity<Void> logout(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,userId);

        if(sessionOptional.isEmpty()){
            return  null;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }
    public UserDto signUp(String email, String password){
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    public SessionStatus validateToken(String token, Long id){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token,id);

        if(sessionOptional.isEmpty()){
            return null;
        }
        return null;



    }
}
