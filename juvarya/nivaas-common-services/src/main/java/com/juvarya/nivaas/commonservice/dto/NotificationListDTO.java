package com.juvarya.nivaas.commonservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NotificationListDTO {
	private List<NotificationDTO> notifications;
}
