package com.juvarya.nivaas.core.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.core.dto.AppDetailsDto;
import com.juvarya.nivaas.core.model.AppInfo;
import com.juvarya.nivaas.core.repository.AppInfoRepository;
import com.juvarya.nivaas.core.service.AppInfoService;

@Transactional
@Service
public class AppInfoServiceImpl implements AppInfoService {

	@Autowired
	private AppInfoRepository appInfoRepository;

	@Override
	public AppInfo save(AppDetailsDto appDetailsDto) {
		AppInfo appInfo = new AppInfo();
		appInfo.setCurrentVersion(appDetailsDto.getCurrentVersion());
		return appInfoRepository.save(appInfo);
	}

	@Override
	public AppInfo findById(Long id) {
		Optional<AppInfo> appInfo = appInfoRepository.findById(id);
		if (appInfo.isPresent()) {
			return appInfo.get();
		}
		return null;
	}

	@Override
	public List<AppInfo> findAll() {
		List<AppInfo> appInfos = appInfoRepository.findAll();
		if (!CollectionUtils.isEmpty(appInfos)) {
			return appInfos;
		}
		return null;
	}

	@Override
	public AppInfo updateAppInfo(AppDetailsDto appDetailsDto) {
		AppInfo appInfo = findById(appDetailsDto.getId());
		if (Objects.nonNull(appInfo)) {
			if (null != appDetailsDto.getCurrentVersion() && StringUtils.hasText(appDetailsDto.getCurrentVersion())) {
				appInfo.setCurrentVersion(appDetailsDto.getCurrentVersion());
				appInfo = appInfoRepository.save(appInfo);
				return appInfo;
			}
		}
		throw new NivaasCustomerException(ErrorCode.APPINFO_NOT_FOUND);
	}

	@Override
	public AppInfo deleteAppInfo(Long id) {
		AppInfo appInfo = findById(id);
		if (Objects.nonNull(appInfo)) {
			appInfoRepository.deleteById(id);
			return appInfo;
		}
		throw new NivaasCustomerException(ErrorCode.APPINFO_NOT_FOUND);
	}

}
