package com.juvarya.nivaas.customer.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.juvarya.nivaas.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;

@Service
@Slf4j
public class ApartmentUserRoleServiceImpl implements ApartmentUserRoleService {

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Override
	@Transactional
	public ApartmentUserRoleModel onBoardApartmentAdminOrHelper(final NivaasApartmentModel nivaasApartmentModel, final Long adminUserId,
																final String role) {
		log.info("Onboarding {} as {} for apartment {}", adminUserId, role, nivaasApartmentModel.getId());
		ApartmentUserRoleModel apartmentUserRoleModel = new ApartmentUserRoleModel();
		apartmentUserRoleModel.setApprove(true);
		apartmentUserRoleModel.setApartmentModel(nivaasApartmentModel);
		apartmentUserRoleModel.setCreatedBy(SecurityUtils.getCurrentUserDetails().getId());
		apartmentUserRoleModel.setCreationTime(new Date());
		apartmentUserRoleModel.setCustomerId(adminUserId);
		apartmentUserRoleModel.setRoleName(role);
		return apartmentUserRoleRepository.save(apartmentUserRoleModel);
	}

	@Override
	public ApartmentUserRoleModel findByApproveCustomer(NivaasApartmentModel nivaasApartmentModel) {
		return apartmentUserRoleRepository.findByApartmentModel(nivaasApartmentModel);
	}

	@Override
	public List<ApartmentUserRoleModel> findByjtCustomerId(Long customerId) {
		return apartmentUserRoleRepository.findByCustomerId(customerId);
	}

	@Override
	public ApartmentUserRoleModel findByApartmentModelAndJtCustomer(NivaasApartmentModel nivaasApartmentModel, Long customerId) {
		return apartmentUserRoleRepository.findByApartmentModelAndCustomerId(nivaasApartmentModel, customerId);
	}

	@Override
	public List<ApartmentUserRoleModel> getByApartmentModel(NivaasApartmentModel nivaasApartmentModele) {
		return apartmentUserRoleRepository.getByApartmentModel(nivaasApartmentModele);
	}

}
