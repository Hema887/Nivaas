package com.juvarya.nivaas.core.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;
import com.juvarya.nivaas.core.model.PostalCodeModel;
import com.juvarya.nivaas.core.populator.PostalCodePopulator;
import com.juvarya.nivaas.core.repository.PostalCodeRepository;
import com.juvarya.nivaas.core.service.PostalCodeService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

@SuppressWarnings("rawtypes")
@Service
public class PostalCodeServiceImpl extends JTBaseEndpoint implements PostalCodeService {

	@Autowired
	private PostalCodeRepository postalCodeRepository;

	@Autowired
	private PostalCodePopulator postalCodePopulator;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public PostalCodeDTO save(PostalCodeDTO codeDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		PostalCodeModel postalCodeModel = new PostalCodeModel();
		postalCodeModel.setCode(codeDTO.getCode());
		postalCodeModel.setCreationTime(new Date());

		postalCodeModel = postalCodeRepository.save(postalCodeModel);
		return  (PostalCodeDTO) getConverterInstance().convert(postalCodeModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<PostalCodeModel> postalcodes = postalCodeRepository.findAll(pageable);

		if (CollectionUtils.isEmpty(postalcodes.getContent())) {
			return null;
		}

		Map<String, Object> response = new HashMap<>();
		List<PostalCodeDTO> postalCodeDTOs = getConverterInstance().convertAll(postalcodes.getContent());
		response.put(NivaasConstants.CURRENT_PAGE, postalcodes.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, postalcodes.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, postalcodes.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		response.put(NivaasConstants.PROFILES, postalCodeDTOs);
		return response;
	}

	@Override
	@Transactional
	public PostalCodeModel delete(Long id) {
		Optional<PostalCodeModel> postalCode = postalCodeRepository.findById(id);
		if (postalCode.isPresent()) {
			postalCodeRepository.delete(postalCode.get());
			return postalCode.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PostalCodeDTO findById(Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<PostalCodeModel> postalCode = postalCodeRepository.findById(id);
		if (postalCode.isPresent()) {
			return (PostalCodeDTO) getConverterInstance().convert(postalCode.get());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(postalCodePopulator, PostalCodeDTO.class.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public PostalCodeDTO findByCode(String code)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		PostalCodeModel codeModel = postalCodeRepository.findByCode(code);
		if (Objects.isNull(codeModel)) {
			return null;
		}
		return (PostalCodeDTO) getConverterInstance().convert(codeModel);
	}

}
