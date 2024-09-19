package com.juvarya.nivaas.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlatOnboardStatusDto {
	private Long id;
    private Long flatId;
    private String flatNo;
    private String residentType;
    private Long userId;
    private String name;
    private String contactNumber;
    private boolean approved;
    private Long relatedUserId;

    public static FlatOnboardStatusDto buildFlatOwnerStatusDto(final OnboardingRequest onboardingRequest,
                                                               final UserDTO user,
                                                               final String residentType,
                                                               final boolean approved,
                                                               final Long relatedUserId) {
        return FlatOnboardStatusDto.builder()
        		.id(onboardingRequest.getId())
                .flatId(onboardingRequest.getFlat().getId())
                .flatNo(onboardingRequest.getFlat().getFlatNo())
                .residentType(residentType)
                .approved(approved)
                .userId(user.getId())
                .name(user.getFullName())
                .contactNumber(user.getPrimaryContact())
                .relatedUserId(relatedUserId)
                .build();
    }

}
