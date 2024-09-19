package com.juvarya.nivaas.customer.service.impl;

import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.customer.dto.response.CurrentApartmentDto;
import com.juvarya.nivaas.customer.model.CurrentApartment;
import com.juvarya.nivaas.customer.proxy.AccessMgmtClientProxy;
import com.juvarya.nivaas.customer.repository.CurrentApartmentRepository;
import com.juvarya.nivaas.customer.service.CurrentApartmentService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class CurrentApartmentServiceImpl implements CurrentApartmentService {
    @Autowired
    private CurrentApartmentRepository currentApartmentRepository;

    @Autowired
    private OnboardingRequestService onboardingRequestService;

    @Autowired
    private AccessMgmtClientProxy accessMgmtClientProxy;

    @Override
    @Transactional
    public CurrentApartment setCurrentApartment(Long userId, Long apartmentId) {
        LoggedInUser loggedInUser = accessMgmtClientProxy.getCurrentCustomer();
        if (userId == null || loggedInUser == null || !userId.equals(loggedInUser.getId())) {
            log.warn("Invalid request userId {}", userId);
            return null;
        }
        if (!onboardingRequestService.isValidApartmentUserMap(userId, apartmentId)) {
            log.warn("Invalid request apartmentId {}", apartmentId);
            return null;
        }
        // Remove any existing current apartment for the user
        currentApartmentRepository.deleteByUserId(userId);

        // Set the new current apartment
        CurrentApartment currentApartment = new CurrentApartment();
        currentApartment.setUserId(userId);
        currentApartment.setApartmentId(apartmentId);
        return currentApartmentRepository.save(currentApartment);
    }

    /**
     * call this method for new onboarding requests. If user don't have any current apartment configured then this method sets current apartment
     * @param userId
     * @param apartmentId
     */
    @Override
    @Transactional
    public void setCurrentApartmentIfNotExists(Long userId, Long apartmentId) {
        // Save if not exists the current apartment for the user
        Optional<CurrentApartment> currentApartment = currentApartmentRepository.findByUserId(userId);
        if (currentApartment.isEmpty()) {
            // Set the new current apartment
            CurrentApartment newRequest = new CurrentApartment();
            newRequest.setUserId(userId);
            newRequest.setApartmentId(apartmentId);
            currentApartmentRepository.save(newRequest);
        }
    }

    @Override
    public CurrentApartmentDto getCurrentApartment() {
        LoggedInUser loggedInUser = accessMgmtClientProxy.getCurrentCustomer();
        if (loggedInUser == null) {
            return null;
        }
        Optional<CurrentApartment> currentApartment = currentApartmentRepository.findByUserId(loggedInUser.getId());
        return currentApartment.map(apartment ->
                onboardingRequestService.findByUserAndApartmentId(loggedInUser, apartment.getApartmentId())).orElse(null);
    }
}
