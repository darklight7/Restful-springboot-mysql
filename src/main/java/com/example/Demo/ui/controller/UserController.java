package com.example.Demo.ui.controller;

import com.example.Demo.service.UserService;
import com.example.Demo.shared.dto.UserDto;
import com.example.Demo.ui.model.request.UserDetailsRequestModel;
import com.example.Demo.ui.model.response.OperationStatusModel;
import com.example.Demo.ui.model.response.RequestOperationStatus;
import com.example.Demo.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping (path = "/{id}",
    produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue= new UserRest();
       UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)throws Exception
    {


        UserRest returnValue = new UserRest();
        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);


        return returnValue;
    }

    @PutMapping( path = "/{id}",
    consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel userDetails)
    {

        UserRest returnValue = new UserRest();
        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updateUser = userService.updateUser(id,userDto);
        BeanUtils.copyProperties(updateUser, returnValue);


        return returnValue;
    }

    @DeleteMapping( path = "/{id}",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {

        OperationStatusModel returnValue =new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;

    }


}
