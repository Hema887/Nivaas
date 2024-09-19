package com.juvarya.nivaas.auth;

import com.juvarya.nivaas.commonservice.dto.UserDTO;

public interface UserServiceAdapter {
    UserDTO getUserById(Long userId);
}
