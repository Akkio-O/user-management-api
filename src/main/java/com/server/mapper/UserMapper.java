package com.server.mapper;

import com.server.dto.request.CreateAddressReq;
import com.server.dto.request.CreateUserReq;
import com.server.dto.response.AddressResponse;
import com.server.dto.response.UserResponse;
import com.server.entity.Address;
import com.server.entity.User;

public final class UserMapper {
    private UserMapper() {}
    public static UserResponse mapToUserResponse(User user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                mapToAddressResponse(user.getAddress())
        );
    }

    public static AddressResponse mapToAddressResponse(Address address) {
        if (address == null) return null;

        return new AddressResponse(
                address.getCity(),
                address.getStreet(),
                address.getBuilding()
        );
    }

    public static User mapToUserEntity(CreateUserReq userReq, User user) {
        if (userReq == null) return null;

        user.setLogin(userReq.login());
        user.setFirstName(userReq.firstName());
        user.setLastName(userReq.lastName());
        user.setAge(userReq.age());
        user.setAddress(mapToAddressEntity(userReq.address()));
        return user;
    }

    public static User mapToUserEntity(CreateUserReq userReq) {
        if (userReq == null) return null;
        User user = new User();
        return mapToUserEntity(userReq, user);
    }

    public static Address mapToAddressEntity(CreateAddressReq addressReq) {
        if (addressReq == null) return null;

        Address address = new Address();
        address.setStreet(addressReq.street());
        address.setCity(addressReq.city());
        address.setBuilding(addressReq.building());
        return address;
    }
}
