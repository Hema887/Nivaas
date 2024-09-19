package com.juvarya.nivaas.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentUserDto {
    private Long id;
    private String name;
    private boolean newUser;
    private String mobileNumber;
    private String profilePicture;
    private String email;
    private Set<String> roles = new HashSet<>();

    @JsonIgnore
    public static CurrentUserDto buildCurrentUserDto(final LoggedInUser loggedInUser) {
        return CurrentUserDto.builder()
                .id(loggedInUser.getId())
                .name(loggedInUser.getFullName())
                .newUser(loggedInUser.isNewUser())
                .roles(loggedInUser.getRoles())
                .mobileNumber(loggedInUser.getPrimaryContact())
                .profilePicture(loggedInUser.getProfilePicture())
                .email(loggedInUser.getEmail())
                .build();
    }
}
