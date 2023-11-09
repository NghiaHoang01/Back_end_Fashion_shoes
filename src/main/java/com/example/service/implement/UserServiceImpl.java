package com.example.service.implement;

import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.repository.UserRepository;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpServletRequest request;

    @Override
    public User findUserById(Long id) throws CustomException {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new CustomException("User not found with id: " + id));
    }

    @Override
    public User findUserProfileByJwt(String token) throws CustomException {
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new CustomException("User not found !!!");
    }

    @Override
    public User findUserByEmail(String email) throws CustomException {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User registerUser(UserRequest userRequest) throws CustomException {
        User user = new User();

        Role role = roleService.findByName(RoleConstant.USER);

        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setMobile(userRequest.getMobile());
        user.setGender(userRequest.getGender().toUpperCase());
        user.setAddress(userRequest.getAddress());
        user.setProvince(userRequest.getProvince());
        user.setDistrict(userRequest.getDistrict());
        user.setWard(userRequest.getWard());

        return userRepository.save(user);
    }

    @Override
    public User registerAdmin(UserRequest adminRequest) throws CustomException {
        User admin = new User();

        String token = jwtProvider.getTokenFromCookie(request);

        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
        Long createdBy = findUserByEmail(email).getId();

        admin.setCreatedBy(createdBy);
        admin.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        admin.setFirstName(adminRequest.getFirstName());
        admin.setLastName(adminRequest.getLastName());
        admin.setGender(adminRequest.getGender().toUpperCase());
        admin.setMobile(adminRequest.getMobile());
        admin.setEmail(adminRequest.getEmail());
        admin.setAddress(adminRequest.getAddress());
        admin.setProvince(adminRequest.getProvince());
        admin.setDistrict(adminRequest.getDistrict());
        admin.setWard(adminRequest.getWard());
        adminRequest.getLstRole().forEach(r -> {
            try {
                Role role = roleService.findByName(r.toUpperCase());
                admin.getRoles().add(role);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return userRepository.save(admin);
    }

    @Override
    public List<User> getAllUser(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex-1, pageSize);
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public String deleteUser(Long id) throws CustomException {
        User user = findUserById(id);
        if (user != null) {
            userRepository.delete(user);
            return "Delete success !!!";
        }
        return "User not found with id: " + id;
    }

    @Override
    public User updateInformation(UserRequest userRequest) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);

        User oldUser = findUserProfileByJwt(token);

        oldUser.setFirstName(userRequest.getFirstName());
        oldUser.setLastName(userRequest.getLastName());
        oldUser.setGender(userRequest.getGender().toUpperCase());
        oldUser.setMobile(userRequest.getMobile());
        oldUser.setUpdateBy(oldUser.getId());
        oldUser.setAddress(userRequest.getAddress());
        oldUser.setProvince(userRequest.getProvince());
        oldUser.setDistrict(userRequest.getDistrict());
        oldUser.setWard(userRequest.getWard());

        return userRepository.save(oldUser);
    }

    @Override
    public Boolean confirmPassword(PasswordRequest password) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);

        User user = findUserProfileByJwt(token);

        return passwordEncoder.matches(password.getPassword(), user.getPassword());
    }

    @Override
    public Response changePassword(PasswordRequest passwordRequest) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);

        User user = findUserProfileByJwt(token);

        Response response = new Response();

        if(passwordRequest.getPassword().equals(passwordRequest.getRepeatPassword())){
            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            user = userRepository.save(user);

            response.setMessage("Change password success !!!");
            response.setSuccess(true);
        }else{
            response.setMessage("Password repeat does not match !!!");
            response.setSuccess(false);
        }
        return response;
    }
}
