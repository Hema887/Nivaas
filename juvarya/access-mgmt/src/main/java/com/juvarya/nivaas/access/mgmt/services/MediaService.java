package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.model.MediaModel;

public interface MediaService {
	
	MediaModel saveMedia(MediaModel mediaModel);
	
	MediaModel findByJtcustomerAndMediaType(Long userId, String mediaType);
}
