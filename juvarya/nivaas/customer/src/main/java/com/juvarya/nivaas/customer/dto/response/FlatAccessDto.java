package com.juvarya.nivaas.customer.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juvarya.nivaas.customer.model.ApartmentAndFlatRelatedUsersModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.RelatedType;

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
public class FlatAccessDto {
    private Long id;
    private String flatNo;
    private String accessType;
    private boolean approved;

    @JsonIgnore
    public static List<FlatAccessDto> buildAccessFlatsDto(final OnboardingRequest onboardingRequest, final Long currentUserId) {
        List<FlatAccessDto> flatAccesslist = new ArrayList<>();
        NivaasFlatModel currentFlat = onboardingRequest.getFlat();
        if (null == currentFlat) {
            log.warn("No associated flats for onboard request {}", onboardingRequest.getId());
            return flatAccesslist;
        }
        if (Objects.equals(onboardingRequest.getRequestedCustomer(), currentUserId)) {
            log.debug("Building flat owner details {}", currentUserId);
            flatAccesslist.add(FlatAccessDto.builder()
                    .accessType("FLAT_OWNER")
                    .id(currentFlat.getId())
                    .flatNo(currentFlat.getFlatNo())
                    .approved(onboardingRequest.isAdminApproved())
                    .build());
        }
        List<ApartmentAndFlatRelatedUsersModel> relatedUsers = onboardingRequest.getRelatedUsers();
        relatedUsers.stream()
                .filter(request -> Objects.equals(request.getUserId(), currentUserId)
                        && !RelatedType.CO_ADMIN.equals(request.getRelatedType()))
                .forEach(relatedRequest -> flatAccesslist.add(FlatAccessDto.builder()
                        .accessType(relatedRequest.getRelatedType().name())
                        .id(currentFlat.getId())
                        .flatNo(currentFlat.getFlatNo())
                        .approved(relatedRequest.isRelatedUserApproved())
                        .build()));
        return flatAccesslist;
    }
}
