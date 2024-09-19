package com.juvarya.nivaas.customer.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.customer.dto.BulkFlatOnboardDto;
import com.juvarya.nivaas.customer.dto.response.AccessDto;
import com.juvarya.nivaas.customer.dto.response.CurrentApartmentDto;
import com.juvarya.nivaas.customer.dto.response.FlatOnboardStatusDto;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.OnboardType;
import com.juvarya.nivaas.customer.model.constants.RelatedType;

public interface OnboardingRequestService {

	void bulkAdd(final BulkFlatOnboardDto flatOnboardDto);

	Map<String, Object> getFlatOwners(Long apartmentId, int pageNo, int pageSize);
	OnboardingRequest save(OnboardingRequest jtonboardingRequest);

	void bulkOnBoardFlat(final List<OnboardingRequest> onboardingRequests);

	OnboardingRequest findById(Long id);

	Page<OnboardingRequest> findByStatus(boolean status, Pageable pageble);

	Page<OnboardingRequest> findByFlat(NivaasFlatModel nivaasFlatModel, Pageable pageable);

	OnboardingRequest findByFlatAndAdminApproved(NivaasFlatModel nivaasFlatModel);

	List<OnboardingRequest> findByRequestCustomer(Long userId);

	CurrentApartmentDto findByUserAndApartmentId(LoggedInUser loggedInUser, Long apartmentId);

	boolean isValidApartmentUserMap(final Long userId, final Long apartmentId);

	void onBoardApartmentAdmin(NivaasApartmentModel nivaasApartmentModel, Long customerId);

	void onBoardCoAdmin(final Long apartmentId, final Long userId);

	void flatRelatedOnboarding(final OnboardingRequestDTO onboardingRequestDTO,
							   final NivaasFlatModel flatModel, final RelatedType relatedType);

	void approveFlatRelatedUsers(final OnboardingRequest onboardingRequest, final Long userId, final RelatedType relatedType
			, final Long relatedUser);

	AccessDto getOnboardRequests()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	List<FlatOnboardStatusDto> getApartmentByflatRequests(Long apartmentId);
	
	Map<String, Object> getFlatsWithOutOwners(Long apartmentId, int pageNo, int pageSize);
	
	void flatOwnerOnboardingRequest(final OnboardingRequestDTO onboardingRequestDTO,
			   						final NivaasFlatModel flatModel, final OnboardType onboardType);
	
	void approveFlatOwner(final OnboardingRequest onboardingRequest, final Long userId, 
						  final NivaasFlatModel flatModel, final OnboardType onboardType);
}
