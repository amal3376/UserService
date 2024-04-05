package com.userservuce.userservice.services;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.userservuce.userservice.dtos.UserDto;
import com.userservuce.userservice.dtos.ValidateTokenRequestDto;
import com.userservuce.userservice.models.Session;
import com.userservuce.userservice.models.SessionStatus;
import com.userservuce.userservice.models.User;
import com.userservuce.userservice.repositories.SessionRepository;
import com.userservuce.userservice.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;




@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<UserDto> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()){
//            return this.signUp(email, password);
            return null;
        }
        User user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Wrong username password");
//            return null;
        }

        String token = RandomStringUtils.randomAlphanumeric(30);

        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

//        String message ="{\n" +
//                "  \"email\": \"amal\",\n" +
//                "  \"roles\":[\n" +
//                "     \"student\",\n" +
//                "     \"ta\"\n" +
//                "  ],\n" +
//                "  \"expirationDate\": \"29March2024\"\n" +
//                "}";
//        //userid
        //email
        //roles
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);

// Create the compact JWS:

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date( LocalDate.now().plusDays(3).toEpochDay()));
//        jsonForJwt.put("ip": )
//        token = Jwts.builder().content(content, "text/plain").signWith(key, alg).compact();

        token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(key, alg).compact();

//// Parse the compact JWS:
//        content = Jwts.parser().verifyWith(key).build().parseSignedContent(jws).getPayload();


        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);

//        Map<String, String> headers = new HashMap<>();
//        headers.put(HttpHeaders.SET_COOKIE, token);
        //auth-token%3AeyJjdHkiOiJ0ZXh0L3BsYWluIiwiYWxnIjoiSFMyNTYifQ.ewogICJlbWFpbCI6ICJhbWFsIiwKICAicm9sZXMiOlsKICAgICAic3R1ZGVudCIsCiAgICAgInRhIgogIF0sCiAgImV4cGlyYXRpb25EYXRlIjogIjI5TWFyY2gyMDI0Igp9.Saxs9mcp2Py3_wOTJXzxbiUmrwJkHZcXcyZRImqhYU0


        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:"+token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);
//        response.getHeaders().add(HttpHeaders.SET_COOKIE, token);

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

    public SessionStatus validate(String token, Long userId){
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(sessionOptional.isEmpty()){
//            return null;
            return SessionStatus.ENDED;
        }
        Session session = sessionOptional.get();
        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return SessionStatus.ENDED;
        }


        Jwts.parser()
                .build();

//        if(!session)
        return SessionStatus.ACTIVE;

    }
}
//auth-token%3AeyJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE3MTE2NTgxNTI5ODQsInJvbGVzIjpbXSwiZXhwaXJ5QXQiOjE5ODE0LCJlbWFpbCI6ImFtYWwifQ.IYcZIHnOGVaiUzvC-Pm5M6KjUUmjETSpgYpNUB8dk_0