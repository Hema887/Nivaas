package com.juvarya.nivaas.core.service;

import java.util.List;

import com.juvarya.nivaas.core.dto.AppDetailsDto;
import com.juvarya.nivaas.core.model.AppInfo;

public interface AppInfoService {
	
	AppInfo save(AppDetailsDto appDetailsDto);
	
	AppInfo findById(Long id);
	
	List<AppInfo> findAll();
	
	AppInfo updateAppInfo(AppDetailsDto appDetailsDto);
	
	AppInfo deleteAppInfo(Long id);

}
