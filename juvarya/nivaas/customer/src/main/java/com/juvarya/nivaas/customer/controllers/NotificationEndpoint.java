package com.juvarya.nivaas.customer.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.dto.NotificationDTO;
import com.juvarya.nivaas.customer.dto.MessageDTO;
import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.populator.NotificationPopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NotificationService;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/jtnotification")
@Slf4j
public class NotificationEndpoint extends JTBaseEndpoint {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationPopulator notificationPopulator;

	@SuppressWarnings("unchecked")
	@PostMapping("/save")
	public ResponseEntity save(@Valid @RequestBody NotificationDTO jtNotificationDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		{
			log.info("Entering save API with JTNotificationDTO: {}", jtNotificationDTO);
			NotificationModel NotificationModel = new NotificationModel();
			NotificationModel.setCreationTime(new Date());
			NotificationModel.setMessage(jtNotificationDTO.getMessage());
			NotificationModel.setModificationTime(jtNotificationDTO.getModificationTime());
//			NotificationModel.setType(jtNotificationDTO.getType());

			NotificationModel = notificationService.save(NotificationModel);
			NotificationDTO notificationDTO = (NotificationDTO) getConverterInstance().convert(NotificationModel);
			log.info("Notification saved successfully with id: {}", notificationDTO.getId());
			return ResponseEntity.ok().body(notificationDTO);
		}

	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(notificationPopulator, NotificationDTO.class.getName());
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/{id}")
	public ResponseEntity getById(@RequestParam Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering getById API with id: {}", id);
		NotificationModel notificationModel = notificationService.findById(id);
		if (null != notificationModel) {
			NotificationDTO notificationDTO = (NotificationDTO) getConverterInstance().convert(notificationModel);
			log.warn("Notification found with id: {}", id);
			return ResponseEntity.ok().body(notificationDTO);
		}
		log.warn("Notification not found with id: {}", id);
		return ResponseEntity.ok().body(new MessageDTO("Invalid details"));
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	public ResponseEntity getAll(@Valid @RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering getAll API with pageNo: {}, pageSize: {}", pageNo, pageSize);
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NotificationModel> jtNotification = notificationService.getAll(pageable);

		Map<String, Object> response = new HashMap<>();
		response.put(NivaasConstants.CURRENT_PAGE, jtNotification.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, jtNotification.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, jtNotification.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		if (!CollectionUtils.isEmpty(jtNotification.getContent())) {
			List<NotificationDTO> list = getConverterInstance().convertAll(jtNotification.getContent());
			response.put(NivaasConstants.PROFILES, list);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@DeleteMapping("/delete")
	public ResponseEntity delete(@Valid @RequestParam Long notificationId) {
		log.info("Entering delete API with notificationId: {}", notificationId);
		NotificationModel notificationModel = notificationService.findById(notificationId);
		if (null != notificationModel) {
			notificationService.removeNotification(notificationModel);
			log.info("Notification deleted successfully with id: {}", notificationId);
			return ResponseEntity.ok().body(new MessageResponse("Notification Deleted"));
		}
		log.warn("Notification not found with id: {}", notificationId);
		return ResponseEntity.badRequest().body(new MessageResponse("Notification Not Found With Given Id"));
	}

	@SuppressWarnings("unchecked")
	@PutMapping("/update")
	public ResponseEntity update(@Valid @RequestBody NotificationDTO notificationDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering update API with JTNotificationDTO: {}", notificationDTO);
		NotificationModel notificationModel = notificationService.findById(notificationDTO.getId());
		if (Objects.nonNull(notificationModel)) {
			notificationModel.setCreationTime(new Date());
			notificationModel.setMessage(notificationDTO.getMessage());
			notificationModel.setModificationTime(notificationDTO.getModificationTime());
//			notificationModel.setType(notificationDTO.getType());
			notificationModel = notificationService.save(notificationModel);
			NotificationDTO jtNotificationDTO = (NotificationDTO) getConverterInstance().convert(notificationModel);
			log.info("Notification updated successfully with id: {}", jtNotificationDTO.getId());
			return ResponseEntity.ok().body(jtNotificationDTO);
		}
		log.warn("Notification not found with id: {}", notificationDTO.getId());
		return ResponseEntity.ok().body(new MessageDTO("Invalid details"));
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/user/list")
	public ResponseEntity getUserNotifications(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		Page<NotificationModel> notifications = notificationService.findByUserOrTenant(userDetails.getId(), userDetails.getId(), pageable);

		if (null == notifications || CollectionUtils.isEmpty(notifications.getContent())) {
			return null;
		}
		Map<String, Object> response = new HashMap<>();
		response.put(NivaasConstants.CURRENT_PAGE, notifications.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, notifications.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, notifications.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		if (!CollectionUtils.isEmpty(notifications.getContent())) {
			List<NotificationDTO> list = getConverterInstance().convertAll(notifications.getContent());
			response.put(NivaasConstants.PROFILES, list);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
