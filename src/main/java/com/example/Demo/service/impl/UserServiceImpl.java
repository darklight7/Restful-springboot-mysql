package com.example.Demo.service.impl;

import com.example.Demo.exception.UserServiceException;
import com.example.Demo.io.repository.UserRepository;
import com.example.Demo.io.entity.UserEntity;
import com.example.Demo.service.UserService;
import com.example.Demo.shared.Utils;
import com.example.Demo.shared.dto.UserDto;
import com.example.Demo.ui.model.response.ErrorMessage;
import com.example.Demo.ui.model.response.ErrorMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findUserByEmail(user.getEmail()) != null)
            throw new RuntimeException("Record already exists ");
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        String publicUserId = utils.generateUserId(30);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;


    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException("User with ID "+userId+" not found ");
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }
//update user

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails=  userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity=userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);

    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);



        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());

    }
}
