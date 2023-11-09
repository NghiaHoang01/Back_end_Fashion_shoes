package com.example.service;

import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.request.PasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;

import java.util.List;

public interface UserService {
    User findUserById(Long id) throws CustomException;

    User findUserProfileByJwt(String token) throws CustomException;

    User findUserByEmail(String email) throws CustomException;

    User registerUser(UserRequest user) throws CustomException;

    User registerAdmin(UserRequest admin) throws CustomException;

    List<User> getAllUser(int pageIndex, int pageSize);

    String deleteUser(Long id) throws CustomException;

    User updateInformation(UserRequest user) throws CustomException;

    Boolean confirmPassword(PasswordRequest passwordRequest) throws CustomException;

    Response changePassword(PasswordRequest passwordRequest) throws CustomException;
}
