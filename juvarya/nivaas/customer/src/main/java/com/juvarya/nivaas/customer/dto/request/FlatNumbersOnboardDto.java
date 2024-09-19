package com.juvarya.nivaas.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlatNumbersOnboardDto {

	@NotNull(message = "ApartmentId must not be null")
	private Long apartmentId;

	@NotNull(message = "Flats list must not be null")
	@Size(min = 1, message = "At least one flats to be onboarded")
	private List<NivaasBasicFlatDTO> flats;

}
