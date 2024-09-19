package com.juvarya.nivaas.access.mgmt.services.impl;

import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import com.juvarya.nivaas.access.mgmt.services.MediaService;
import com.juvarya.nivaas.access.mgmt.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MediaServiceImpl implements MediaService {

	@Autowired
	private MediaRepository mediaRepository;

	@Override
	@Transactional
	public MediaModel saveMedia(MediaModel mediaModel) {
		return mediaRepository.save(mediaModel);
	}

	@Override
	public MediaModel findByJtcustomerAndMediaType(Long userId, String mediaType) {
		return mediaRepository.findByCustomerIdAndMediaType(userId, mediaType);
	}
}
