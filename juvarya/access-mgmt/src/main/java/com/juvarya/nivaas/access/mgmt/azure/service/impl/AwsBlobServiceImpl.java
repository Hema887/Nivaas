package com.juvarya.nivaas.access.mgmt.azure.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.juvarya.nivaas.access.mgmt.azure.service.AwsBlobService;
import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.services.MediaService;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsBlobServiceImpl implements AwsBlobService {

	private static final String PROFILE_PICTURE = "PROFILE_PICTURE";

	@Value("${secretKey}")
	private String secretKey;

	@Value("${accessKey}")
	private String accessKey;

	@Value("${bucketName}")
	private String bucketName;

	@Value("${region}")
	private String region;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private UserService userService;

	@Override
	public AmazonS3 getClient() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.EU_NORTH_1).build();
	}

	@SuppressWarnings("resource")
	@Override
	public MediaModel uploadFile(MultipartFile file) throws IOException {
		MediaModel mediaModel = new MediaModel();
		File modified = new File(file.getOriginalFilename());
		FileOutputStream os = new FileOutputStream(modified);
		os.write(file.getBytes());
		String fileName = System.currentTimeMillis() + "" + file.getOriginalFilename();
		getClient().putObject(bucketName, fileName, modified);
		modified.delete();
		mediaModel.setFileName(fileName);
		mediaModel.setUrl(getS3Url(fileName));
		return mediaService.saveMedia(mediaModel);
	}

	@Override
	public List<MediaModel> uploadFiles(List<MultipartFile> files) throws IOException {
		List<MediaModel> uploadFiles = new ArrayList<>();
		for (MultipartFile file : files) {
			MediaModel media = uploadFile(file);
			uploadFiles.add(media);
		}
		return uploadFiles;
	}

	private String getS3Url(String fileName) {
		String completeUrl = "https://" + bucketName + "." + region + "/" + fileName;
		return completeUrl;
	}

	@Override
	public MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser)
			throws IOException {
		User user = userService.findById(customerId);
		if (null != user) {

			MediaModel picture = mediaService.findByJtcustomerAndMediaType(customerId, PROFILE_PICTURE);
			if (null == picture) {
				picture = new MediaModel();
			}
			File modified = new File(file.getOriginalFilename());
			try (FileOutputStream os = new FileOutputStream(modified)) {
				os.write(file.getBytes());
			}
			String fileName = System.currentTimeMillis() + "" + file.getOriginalFilename();
			getClient().putObject(bucketName, fileName, modified);
			modified.delete();
			picture.setUrl(getS3Url(fileName));
			picture.setCustomerId(customerId);
			picture.setMediaType(PROFILE_PICTURE);
			picture.setCreatedBy(customerId);
			return mediaService.saveMedia(picture);
		}
		return null;
	}

}
