package com.juvarya.nivaas.access.mgmt.azure.service;

import com.amazonaws.services.s3.AmazonS3;
import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public interface AwsBlobService {
	AmazonS3 getClient();

	MediaModel uploadFile(MultipartFile file) throws FileNotFoundException, IOException;

	List<MediaModel> uploadFiles(List<MultipartFile> files) throws FileNotFoundException, IOException;

	MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser)
			throws FileNotFoundException, IOException;
}
