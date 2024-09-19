package com.juvarya.nivaas.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juvarya.nivaas.customer.model.ApartmentAndFlatRelatedUsersModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.RelatedType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApartmentAccessDto {
    private Long id;
    private String name;
    private int totalFlats;
    private boolean isAdmin;
    private boolean approved;
    private List<FlatAccessDto> flats;

    @JsonIgnore
    public static ApartmentAccessDto buildAccessApartmentDto(final NivaasApartmentModel apartment,
                                                             final List<FlatAccessDto> flats,
                                                             final List<OnboardingRequest> apartmentTypeRequests,
                                                             final Long userId) {
        //Is apartment approved or not, ideally apartmentTypeRequests count should be one
        boolean approved = apartmentTypeRequests.stream().anyMatch(OnboardingRequest::isAdminApproved);

        boolean isAdmin = apartmentTypeRequests.stream()
                .anyMatch(request -> Objects.equals(request.getRequestedCustomer(), userId));

        boolean asCoAdminApproved = apartmentTypeRequests.stream()
                .flatMap(request -> request.getRelatedUsers().stream())
                .filter(relatedUser -> RelatedType.CO_ADMIN.equals(relatedUser.getRelatedType())
                        && Objects.equals(relatedUser.getUserId(), userId))
                .anyMatch(ApartmentAndFlatRelatedUsersModel::isRelatedUserApproved);
        return ApartmentAccessDto.builder()
                .id(apartment.getId())
                .name(apartment.getName())
                .totalFlats(apartment.getTotalFlats())
                .isAdmin(isAdmin || asCoAdminApproved)
                .approved(approved)
                .flats(flats)
                .build();
    }

    @JsonIgnore
    public static ApartmentAccessDto buildAccessApartmentDtoOnlyFlatRelatedUsers(final NivaasApartmentModel apartment,
                                                                                 final List<FlatAccessDto> flats) {
        return ApartmentAccessDto.builder()
                .id(apartment.getId())
                .name(apartment.getName())
                .totalFlats(apartment.getTotalFlats())
                .isAdmin(false)
                .approved(true) //flats are onboarded means apartment is approved
                .flats(flats)
                .build();
    }
}
