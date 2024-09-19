package com.juvarya.nivaas.customer.proxy;

import com.google.api.gax.rpc.UnauthenticatedException;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class AccessMgmtClientProxy {
    @Autowired
    private AccessMgmtClient accessMgmtClient;

    public LoggedInUser getCurrentCustomer() {
        try {
            ResponseEntity<LoggedInUser> responseEntity = accessMgmtClient.getCurrentCustomer();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else if (responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Invalid JWT token or user not found");
            }
        } catch (UnauthenticatedException exception) {
            log.error("Invalid JWT token or user not found", exception);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
        return null;
    }

    public UserDTO getUserById(Long userId) {
        try {
            return accessMgmtClient.getUserById(userId);
        } catch (FeignException.Unauthorized e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }
    }
}
