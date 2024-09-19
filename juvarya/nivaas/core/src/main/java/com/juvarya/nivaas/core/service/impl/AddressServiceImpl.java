package com.juvarya.nivaas.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.core.model.AddressModel;
import com.juvarya.nivaas.core.model.NivaasCityModel;
import com.juvarya.nivaas.core.model.PostalCodeModel;
import com.juvarya.nivaas.core.populator.AddressPopulator;
import com.juvarya.nivaas.core.repository.AddressRepository;
import com.juvarya.nivaas.core.repository.NivaasCityRepository;
import com.juvarya.nivaas.core.service.AddressService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

@SuppressWarnings("rawtypes")
@Service
public class AddressServiceImpl extends JTBaseEndpoint implements AddressService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private AddressPopulator addressPopulator;

	@Autowired
	private NivaasCityRepository cityRepository;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public AddressDTO save(AddressDTO addressDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		Optional<NivaasCityModel> cityModel = cityRepository.findById(addressDTO.getCityId());
		if (cityModel.isEmpty()) {
			return null;
		}
		AddressModel addressModel = new AddressModel();
		addressModel.setCreatedBy(userDetails.getId());
		addressModel.setCreationTime(new Date());
		addressModel.setLine1(addressDTO.getLine1());
		addressModel.setLine2(addressDTO.getLine2());
		addressModel.setLine3(addressDTO.getLine3());
		addressModel.setLocality(addressDTO.getLocality());
		addressModel.setCity(cityModel.get());

		if (!CollectionUtils.isEmpty(cityModel.get().getPostalCodes())) {
			for (PostalCodeModel codeModel : cityModel.get().getPostalCodes()) {
				if (codeModel.getCode().equals(addressDTO.getPostalCode())) {
					addressModel.setPostalCode(codeModel.getCode());
				}
			}
		}

		addressModel = addressRepository.save(addressModel);
		return (AddressDTO) getConverterInstance().convert(addressModel);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Optional<AddressModel> address = addressRepository.findById(id);
		address.ifPresent(addressModel -> addressRepository.delete(addressModel));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<AddressModel> addresses = addressRepository.findAll(pageable);

		if (CollectionUtils.isEmpty(addresses.getContent())) {
			return null;
		}

		Map<String, Object> response = new HashMap<>();
		List<AddressDTO> address = getConverterInstance().convertAll(addresses.getContent());
		response.put(NivaasConstants.CURRENT_PAGE, addresses.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, addresses.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, addresses.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		response.put(NivaasConstants.PROFILES, address);
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AddressDTO findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<AddressModel> address = addressRepository.findById(id);
		if (address.isPresent()) {
			return (AddressDTO) getConverterInstance().convert(address.get());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(addressPopulator, AddressDTO.class.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AddressDTO> findByCity(Long cityId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<NivaasCityModel> cityModel = cityRepository.findById(cityId);
		if (cityModel.isEmpty()) {
			return null;
		}
		List<AddressModel> addressModels = addressRepository.findByCity(cityModel.get());

		if (addressModels == null || CollectionUtils.isEmpty(addressModels)) {
			return null;
		}
		return getConverterInstance().convertAll(addressModels);
	}

}
