package com.example.api.user;

import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.exception.CustomException;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.service.implement.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("user")
@RequestMapping("/api/user")
public class ApiUser {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;

    @PutMapping("/")
    public ResponseEntity<?> updateInformation(@RequestBody UserRequest newUser) throws CustomException {
        User user = userService.updateInformation(newUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
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

    @PostMapping("/password")
    public ResponseEntity<?> confirmPassword(@RequestBody PasswordRequest passwordRequest) throws CustomException {
        Boolean result = userService.confirmPassword(passwordRequest);
        Response response = new Response();
        response.setSuccess(result);
        response.setMessage(result ? "Password matched !!!" : "Password not match !!!");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest) throws CustomException {
        Response response = userService.changePassword(passwordRequest);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/information")
    public ResponseEntity<?> getInformation() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);

        User user = userService.findUserProfileByJwt(token);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
}
