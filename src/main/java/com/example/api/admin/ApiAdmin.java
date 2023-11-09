package com.example.api.admin;

import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.exception.CustomException;
import com.example.request.UserRequest;
import com.example.response.AuthResponse;
import com.example.response.Response;
import com.example.service.implement.UserServiceImpl;
import com.example.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("authOfAdmin")
@RequestMapping("/api/admin")
public class ApiAdmin {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserUtil userUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRequest admin) throws CustomException {
        User check = userService.findUserByEmail(admin.getEmail());

        if(check != null){
            throw new CustomException("Email is already exist !!!");
        }else{
            String pass = admin.getPassword();

            User registerAdmin = userService.registerAdmin(admin);

            Authentication authentication = userUtil.authenticate(registerAdmin.getEmail(), pass);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(accessToken);
            authResponse.setMessage("Register admin success !!!");
            authResponse.setSuccess(true);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> userLogout(){
        ResponseCookie cookie = jwtProvider.cleanTokenCookie();

        Response response = new Response();
        response.setMessage("Logout success");
        response.setSuccess(true);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(response);
    }
}
