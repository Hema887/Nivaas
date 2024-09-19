package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

class MediaServiceTest extends AccessBaseTest {
	
	

	@BeforeEach
    void setUp() {
		 MockitoAnnotations.openMocks(this);
        super.init();
    }

	@Test
	void testSaveAndFindByJtcustomerAndMediaType() {
		// Given: Test Save
		MediaModel mediaModel = new MediaModel();
		mediaModel.setId(1L);

		// Given: Test FindByJtcustomerAndMediaType
		Long userId = 1L;
		String mediaType = "image";
		mediaModel.setCustomerId(userId);
		mediaModel.setMediaType(mediaType);

		// When: Update the media model with additional details and save again
		mediaService.saveMedia(mediaModel);

		// When: Find the media
		MediaModel foundMedia = mediaService.findByJtcustomerAndMediaType(userId, mediaType);

		// Then: Assert found media
		assertEquals(userId, foundMedia.getCustomerId());
		assertEquals(mediaType, foundMedia.getMediaType());
	}
}