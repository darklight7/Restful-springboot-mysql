package com.example.Demo.service;

import com.example.Demo.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddresses (String userId);
    AddressDto getAddress(String addressId);
}
