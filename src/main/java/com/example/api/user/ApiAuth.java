package com.example.api.user;

import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.exception.CustomException;
import com.example.request.UserRequest;
import com.example.response.AuthResponse;
import com.example.service.implement.UserServiceImpl;
import com.example.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController("authOfUser")
@RequestMapping("/api")
public class ApiAuth {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserUtil userUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest user) throws CustomException {
        User check = userService.findUserByEmail(user.getEmail());

        if (check != null) {
            throw new CustomException("Email is already exist !!!");
        } else {
            String pass = user.getPassword();

            User saveUser = userService.registerUser(user);

            Authentication authentication = userUtil.authenticate(saveUser.getEmail(), pass);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(accessToken);
            authResponse.setMessage("Register user success !!!");
            authResponse.setSuccess(true);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        }
    }


}
