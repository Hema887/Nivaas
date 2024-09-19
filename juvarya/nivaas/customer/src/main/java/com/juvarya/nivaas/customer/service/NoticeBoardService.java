package com.juvarya.nivaas.customer.service;

import java.util.Map;

import com.juvarya.nivaas.customer.dto.NoticeBoardDTO;

public interface NoticeBoardService {

	NoticeBoardDTO saveNoticeBoard(NoticeBoardDTO boardDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	NoticeBoardDTO findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Map<String, Object> getAllNotices(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Map<String, Object> getAllUserNotices(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	Map<String, Object> getApartmentUserNotices(Long apartmentId,int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
