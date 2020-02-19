package com.example.Demo.ui.controller;

import com.example.Demo.service.UserService;
import com.example.Demo.shared.dto.UserDto;
import com.example.Demo.ui.model.request.UserDetailsRequestModel;
import com.example.Demo.ui.model.response.UserRest;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getUser()
    {
        return "Get user was called ";
    }
    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)
    {


        UserRest returnValue =new UserRest();
        UserDto userDto=new UserDto();
        BeanUtils.copyProperties(userDetails,userDto);

        UserDto createdUser= userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnValue);


        return returnValue;
    }
    @PutMapping
    public String updateUser()
    {
        return "Update user was called ";
    }
    @DeleteMapping
    public String deleteUser()
    {
        return "Delete user was called ";
    }


}
