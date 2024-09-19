package com.juvarya.nivaas.customer.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.NoticeBoardDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.NoticeBoardModel;
import com.juvarya.nivaas.customer.populator.NoticeBoardPopulator;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.NivaasApartmentRepository;
import com.juvarya.nivaas.customer.repository.NivaasFlatRepository;
import com.juvarya.nivaas.customer.repository.NoticeBoardRepository;
import com.juvarya.nivaas.customer.service.NoticeBoardService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

@SuppressWarnings("rawtypes")
@Service
public class NoticeBoardServiceImpl extends JTBaseEndpoint implements NoticeBoardService {

	@Autowired
	private NoticeBoardRepository noticeBoardRepository;

	@Autowired
	private NivaasApartmentRepository nivaasApartmentRepository;

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Autowired
	private NoticeBoardPopulator noticeBoardPopulator;

	@Autowired
	private NivaasFlatRepository nivaasFlatRepository;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public NoticeBoardDTO saveNoticeBoard(NoticeBoardDTO boardDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		NoticeBoardModel boardModel = new NoticeBoardModel();

		Optional<NivaasApartmentModel> apartment = nivaasApartmentRepository.findById(boardDTO.getApartmentId());
		if (apartment.isEmpty()) {
			return null;
		}
		List<ApartmentUserRoleModel> apartmentUser = apartmentUserRoleRepository.getByApartmentModel(apartment.get());
		if (!CollectionUtils.isEmpty(apartmentUser)) {
			for (ApartmentUserRoleModel apartmentUserRoleModel : apartmentUser) {
				if (apartmentUserRoleModel.getCustomerId().equals(userDetails.getId())) {
					if (boardDTO.getId() != null) {
						Optional<NoticeBoardModel> noticeBoardModel = noticeBoardRepository.findById(boardDTO.getId());
						NoticeBoardModel notice = noticeBoardModel.get();
						if (noticeBoardModel.isPresent()) {
							if (null != boardDTO.getBody()) {
								notice.setBody(boardDTO.getBody());
							}
							notice = noticeBoardRepository.save(notice);
							NoticeBoardDTO noticeBoardDTO = (NoticeBoardDTO) getConverterInstance().convert(notice);
							return noticeBoardDTO;
						}
					}
					boardModel.setTitle(boardDTO.getTitle());
					boardModel.setBody(boardDTO.getBody());
					boardModel.setPublishTime(new Date());
					boardModel.setApartment(apartment.get());

					boardModel = noticeBoardRepository.save(boardModel);
					NoticeBoardDTO noticeBoardDTO = (NoticeBoardDTO) getConverterInstance().convert(boardModel);
					return noticeBoardDTO;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public NoticeBoardDTO findById(Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<NoticeBoardModel> noticeBoard = noticeBoardRepository.findById(id);
		if (noticeBoard.isPresent()) {
			NoticeBoardDTO boardDTO = (NoticeBoardDTO) getConverterInstance().convert(noticeBoard.get());
			return boardDTO;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllNotices(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NoticeBoardModel> notices = noticeBoardRepository.findAll(pageable);

		if (notices == null || CollectionUtils.isEmpty(notices.getContent())) {
			return null;
		}

		Map<String, Object> response = new HashMap<String, Object>();

		List<NoticeBoardDTO> noticeBoardDTOs = getConverterInstance().convertAll(notices.getContent());
		response.put(NivaasConstants.CURRENT_PAGE, notices.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, notices.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, notices.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		response.put(NivaasConstants.PROFILES, noticeBoardDTOs);
		return response;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Map<String, Object> getAllUserNotices(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		List<NivaasFlatModel> flatModels = nivaasFlatRepository.findByOwnerORTenant(userDetails.getId());
		Map<String, Object> response = new HashMap<String, Object>();
		if (flatModels != null && !CollectionUtils.isEmpty(flatModels)) {
			for (NivaasFlatModel flatModel : flatModels) {
				Page<NoticeBoardModel> notices = noticeBoardRepository.findByApartment(flatModel.getApartment(),
						pageable);
				if (notices != null && !CollectionUtils.isEmpty(notices.getContent())) {
					List<NoticeBoardDTO> boardDTOs = getConverterInstance().convertAll(notices.getContent());
					response.put(NivaasConstants.CURRENT_PAGE, notices.getNumber());
					response.put(NivaasConstants.TOTAL_ITEMS, notices.getTotalElements());
					response.put(NivaasConstants.TOTAL_PAGES, notices.getTotalPages());
					response.put(NivaasConstants.PAGE_NUM, pageNo);
					response.put(NivaasConstants.PAGE_SIZE, pageSize);
					response.put(NivaasConstants.PROFILES, boardDTOs);
					return response;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getApartmentUserNotices(Long apartmentId, int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<NivaasApartmentModel> apartment = nivaasApartmentRepository.findById(apartmentId);
		if (apartment.isEmpty()) {
			return null;
		}
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Map<String, Object> response = new HashMap<String, Object>();
		Page<NoticeBoardModel> notices = noticeBoardRepository.findByApartment(apartment.get(), pageable);
		if (notices != null && !CollectionUtils.isEmpty(notices.getContent())) {
			List<NoticeBoardDTO> boardDTOs = getConverterInstance().convertAll(notices.getContent());
			response.put(NivaasConstants.CURRENT_PAGE, notices.getNumber());
			response.put(NivaasConstants.TOTAL_ITEMS, notices.getTotalElements());
			response.put(NivaasConstants.TOTAL_PAGES, notices.getTotalPages());
			response.put(NivaasConstants.PAGE_NUM, pageNo);
			response.put(NivaasConstants.PAGE_SIZE, pageSize);
			response.put(NivaasConstants.PROFILES, boardDTOs);
			return response;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(noticeBoardPopulator, NoticeBoardDTO.class.getName());
	}

}
