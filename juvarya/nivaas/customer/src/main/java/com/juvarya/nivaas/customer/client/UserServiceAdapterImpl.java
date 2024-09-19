package com.juvarya.nivaas.customer.client;

import com.juvarya.nivaas.auth.UserServiceAdapter;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.proxy.AccessMgmtClientProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceAdapterImpl implements UserServiceAdapter {

    @Autowired
    private AccessMgmtClientProxy accessMgmtClientProxy;

    @Cacheable(value = "users", key = "#userId")
    @Override
    public UserDTO getUserById(Long userId) {
        return accessMgmtClientProxy.getUserById(userId);
    }
}
