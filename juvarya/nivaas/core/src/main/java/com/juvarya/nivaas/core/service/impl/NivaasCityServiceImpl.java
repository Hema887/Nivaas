package com.juvarya.nivaas.core.service.impl;

import java.util.ArrayList;
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

import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.core.model.NivaasCityModel;
import com.juvarya.nivaas.core.model.PostalCodeModel;
import com.juvarya.nivaas.core.populator.NivaasCityPopulator;
import com.juvarya.nivaas.core.repository.NivaasCityRepository;
import com.juvarya.nivaas.core.repository.PostalCodeRepository;
import com.juvarya.nivaas.core.service.NivaasCityService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

@SuppressWarnings("rawtypes")
@Service
public class NivaasCityServiceImpl extends JTBaseEndpoint implements NivaasCityService {

	@Autowired
	private NivaasCityRepository nivaasCityRepository;

	@Autowired
	private NivaasCityPopulator nivaasCityPopulator;

	@Autowired
	private PostalCodeRepository codeRepository;

	@Override
	@Transactional
	public NivaasCityModel save(NivaasCityDTO cityDTO) {
		NivaasCityModel cityModel = new NivaasCityModel();
		cityModel.setName(cityDTO.getName());
		cityModel.setCreationTime(new Date());
		cityModel.setCountry(cityDTO.getCountry());
		cityModel.setRegion(cityDTO.getRegion());
		cityModel.setDistrict(cityDTO.getDistrict());
		cityModel.setIsoCode(cityDTO.getIsoCode());

		List<PostalCodeModel> codeModels = new ArrayList<>();
		if (!CollectionUtils.isEmpty(cityDTO.getCodes())) {
			for (String code : cityDTO.getCodes()) {
				PostalCodeModel codeModel = codeRepository.findByCode(code);
				codeModels.add(codeModel);
			}
			cityModel.setPostalCodes(codeModels);
		}
		cityModel = nivaasCityRepository.save(cityModel);
		return cityModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public NivaasCityDTO findById(Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<NivaasCityModel> niviaasCity = nivaasCityRepository.findById(id);
		if (niviaasCity.isPresent()) {
			return (NivaasCityDTO) getConverterInstance().convert(niviaasCity.get());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NivaasCityModel> cities = nivaasCityRepository.findAll(pageable);

		if (CollectionUtils.isEmpty(cities.getContent())) {
			return null;
		}

		Map<String, Object> response = new HashMap<>();
		List<NivaasCityDTO> cityDTO = getConverterInstance().convertAll(cities.getContent());
		response.put(NivaasConstants.CURRENT_PAGE, cities.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, cities.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, cities.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		response.put(NivaasConstants.PROFILES, cityDTO);
		return response;
	}

	@Override
	@Transactional
	public void delete(Long id) {
		Optional<NivaasCityModel> city = nivaasCityRepository.findById(id);
        city.ifPresent(nivaasCityModel -> nivaasCityRepository.delete(nivaasCityModel));
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(nivaasCityPopulator, NivaasCityDTO.class.getName());
	}

}
