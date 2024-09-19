package com.juvarya.nivaas.core.client;

import com.juvarya.nivaas.auth.UserServiceAdapter;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceAdapterImpl implements UserServiceAdapter {

    @Autowired
    private AccessMgmtClient accessMgmtClient;

    @Cacheable(value = "users", key = "#userId")
    @Override
    public UserDTO getUserById(Long userId) {
        return accessMgmtClient.getUserById(userId);
    }
}
