package com.example.Demo.service.impl;

import com.example.Demo.exception.UserServiceException;
import com.example.Demo.io.entity.PasswordResetTokenEntity;
import com.example.Demo.io.repository.PasswordResetTokenRepository;
import com.example.Demo.io.repository.UserRepository;
import com.example.Demo.io.entity.UserEntity;
import com.example.Demo.service.UserService;
import com.example.Demo.shared.AmazonSES;
import com.example.Demo.shared.Utils;
import com.example.Demo.shared.dto.AddressDto;
import com.example.Demo.shared.dto.UserDto;
import com.example.Demo.ui.model.response.ErrorMessage;
import com.example.Demo.ui.model.response.ErrorMessages;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findUserByEmail(user.getEmail()) != null)
            throw new RuntimeException("Record already exists ");

        for (int i=0;i<user.getAddresses().size();i++)
        {

            AddressDto addressDto=user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId((utils.generateAddressId(30)));
            user.getAddresses().set(i,addressDto);

        }


     //   BeanUtils.copyProperties(user, userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
    userEntity.setEmailVerificationStatus(false);
        UserEntity storedUserDetails = userRepository.save(userEntity);
        //BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        new AmazonSES().verifyEmail(returnValue);
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
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue=new ArrayList<>();
        if (page>0)page=page-1;
        Pageable pageablRequest= PageRequest.of(page, limit);
        Page<UserEntity> usersPage= userRepository.findAll(pageablRequest);
List<UserEntity> users=usersPage.getContent();

    for (UserEntity userEntity:users){
        UserDto userDto=new UserDto();
        BeanUtils.copyProperties(userEntity,userDto);
        returnValue.add(userDto);
    }
        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue =false;
        UserEntity userEntity=userRepository.findUserByEmailVerificationToken(token);
        if (userEntity!=null)
        {
            boolean hoastokenExpired=Utils.hasTokenExpired(token);
            if (!hoastokenExpired){
                userEntity.setEmailVerificationToken((null));
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue= true;

            }
        }
        return returnValue;

    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

return new  User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
        true, true,
        true, new ArrayList<>());


            //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());

    }
    @Override
    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue = new AmazonSES().sendPasswordResetRequest(
                userEntity.getFirstName(),
                userEntity.getEmail(),
                token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        return false;
    }


}

