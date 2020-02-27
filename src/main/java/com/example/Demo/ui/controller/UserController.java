package com.example.Demo.ui.controller;

import com.example.Demo.service.AddressService;
import com.example.Demo.service.UserService;
import com.example.Demo.shared.dto.AddressDto;
import com.example.Demo.shared.dto.UserDto;
import com.example.Demo.ui.model.request.UserDetailsRequestModel;
import com.example.Demo.ui.model.response.AddressesRest;
import com.example.Demo.ui.model.response.OperationStatusModel;
import com.example.Demo.ui.model.response.RequestOperationStatus;
import com.example.Demo.ui.model.response.UserRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @GetMapping (path = "/{id}",
    produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue= new UserRest();
       UserDto userDto = userService.getUserByUserId(id);
       // BeanUtils.copyProperties(userDto, returnValue);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto, UserRest.class);
        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)throws Exception
    {


        UserRest returnValue = new UserRest();
        if (userDetails.getFirstName().isEmpty()) throw new NullPointerException("The object is null");
       // UserDto userDto = new UserDto();
      //  BeanUtils.copyProperties(userDetails, userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
       // BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = modelMapper.map(createdUser, UserRest.class);


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

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})

    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0")int page,
                                   @RequestParam(value = "limit", defaultValue = "25")int limit
    )
    {
        List<UserRest>returnValue =new ArrayList<>();
        List<UserDto> users = userService.getUsers(page,limit);

        for (UserDto userDto : users){
            UserRest userModel =new UserRest();
            BeanUtils.copyProperties(userDto,userModel);
            returnValue.add(userModel);
        }
        return returnValue;


    }

    @GetMapping (path = "/{id}/addresses",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE,"application/hal+json"})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {

        List<AddressesRest> addressesListRestModel=new ArrayList<>();

List<AddressDto> addressDto =addressService.getAddresses(id);

if (addressDto !=null && !addressDto.isEmpty()) {
    Type listType = new TypeToken<List<AddressesRest>>() {
    }.getType();
    addressesListRestModel = new ModelMapper().map(addressDto, listType);

    for (AddressesRest addressRest : addressesListRestModel) {
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
                .withSelfRel();
        addressRest.add(addressLink);

        Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
        addressRest.add(userLink);
    }
}
        return new CollectionModel<>(addressesListRestModel);
    }



    @GetMapping (path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE,"application/hal+json"})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDto addressDto =addressService.getAddress(addressId);
        Link addressLink =linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();
        Link userLink =linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink =linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");


        ModelMapper modelMapper=new ModelMapper();
         AddressesRest addressesRestModel =modelMapper.map(addressDto,AddressesRest.class);

         addressesRestModel.add(addressLink);
        addressesRestModel.add(userLink);
        addressesRestModel.add(addressesLink);

        return new EntityModel<>(addressesRestModel);

    }
    @GetMapping (path = "/email-verification",
            produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token")String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified)
        {
            returnValue.setOperationResult((RequestOperationStatus.SUCCESS.name()));

        }
        else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }


        return returnValue;
    }

}
