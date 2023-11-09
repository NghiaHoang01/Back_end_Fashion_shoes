package com.example.api;

import com.example.Entity.CustomUserDetails;
import com.example.config.JwtProvider;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.request.LoginRequest;
import com.example.response.AuthResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.UserResponse;
import com.example.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("login")
@RequestMapping("/api")
public class ApiLogin {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private HttpServletRequest request;

    @PostMapping("/account/user/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = userUtil.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseCookie token = jwtProvider.generateTokenCookie(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserResponse userInformation = new UserResponse();
        userInformation.setId(userDetails.getUser().getId());
        userInformation.setAddress(userDetails.getUser().getAddress());
        userInformation.setDistrict(userDetails.getUser().getDistrict());
        userInformation.setProvince(userDetails.getUser().getProvince());
        userInformation.setWard(userDetails.getUser().getWard());
        userInformation.setEmail(userDetails.getUser().getEmail());
        userInformation.setFirstName(userDetails.getUser().getFirstName());
        userInformation.setLastName(userDetails.getUser().getLastName());
        userInformation.setGender(userDetails.getUser().getGender());
        userInformation.setMobile(userDetails.getUser().getMobile());
        userInformation.setCreateAt(userDetails.getUser().getCreatedAt());

        ResponseData<UserResponse> response = new ResponseData<>();
        response.setSuccess(true);
        response.setMessage("Login success !!!");
        response.setResults(userInformation);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, token.toString())
                .body(response);
    }

    @PostMapping("/account/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest loginRequest) throws CustomException {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = userUtil.authenticate(email, password);

        boolean check = false;

        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(RoleConstant.ADMIN)) {
                check = true;
                break;
            }
        }

        if (check) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            ResponseCookie token = jwtProvider.generateTokenCookie(authentication);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token.getValue());
            authResponse.setMessage("Login success !!!");
            authResponse.setSuccess(true);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, token.toString())
                    .body(authResponse);
        }

        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("You not permission to login !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
