package com.example.service.implement;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null){
            try {
                throw new CustomException("Invalid username !!!");
            } catch (CustomException e) {
                throw new RuntimeException(e);
            }
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUser(user);
        return userDetails;
    }
}
