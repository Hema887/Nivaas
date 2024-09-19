package com.juvarya.nivaas.customer.populator;

import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.NotificationDTO;
import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.model.constants.NotificationType;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class NotificationPopulator implements Populator<NotificationModel, NotificationDTO> {

	@Override
	public void populate(NotificationModel source, NotificationDTO target) {
		target.setId(source.getId());
		target.setCreationTime(source.getCreationTime());
		target.setMessage(source.getMessage());
		target.setModificationTime(source.getModificationTime());

		if (null != source.getFlatModel()) {
			target.setFlatId(source.getFlatModel().getId());
		}
		if (null != source.getUserId()) {
			target.setUserId(source.getUserId());
		}

		if (null != source.getType()) {
			if (source.getType().equals(NotificationType.SOCIETY_DUE)) {
				target.setType("SOCIETY_DUE");
			} else if (source.getType().equals(NotificationType.FLAT_APPROVED)) {
				target.setType("FLAT_APPROVED");
			}
		}

	}

}
