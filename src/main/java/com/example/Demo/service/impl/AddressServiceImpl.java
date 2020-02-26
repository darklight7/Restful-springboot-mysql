package com.example.Demo.service.impl;

import com.example.Demo.io.entity.AddressEntity;
import com.example.Demo.io.entity.UserEntity;
import com.example.Demo.io.repository.AddressRepository;
import com.example.Demo.io.repository.UserRepository;
import com.example.Demo.service.AddressService;
import com.example.Demo.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.jvm.hotspot.debugger.Address;

import java.util.ArrayList;
import java.util.List;
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Override
    public List<AddressDto> getAddresses(String userId) {
        ModelMapper modelMapper=new ModelMapper();
        List <AddressDto> returnValue =new ArrayList<>();

        UserEntity userEntity=userRepository.findByUserId(userId);
        if (userEntity==null) return returnValue;

    Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

    for (AddressEntity addressEntity:addresses)
    {
        returnValue.add(modelMapper.map(addressEntity,AddressDto.class));
    }
    return returnValue;
    }


    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue = null;

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if(addressEntity!=null)
        {
            returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
        }

        return returnValue;
    }
}
