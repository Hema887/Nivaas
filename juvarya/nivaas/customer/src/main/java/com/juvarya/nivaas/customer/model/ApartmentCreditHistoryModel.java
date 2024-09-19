package com.juvarya.nivaas.customer.model;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.customer.dto.ApartmentCreditDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "APARTMENT_CREDIT_HISTORY")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentCreditHistoryModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;
	
	private String creditType;

	private Double amount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate transactionDate;

	private Date updatedAt;

	private Long updatedBy;

	@ManyToOne
	@JoinColumn(name = "nivaas_apartment_id")
	private NivaasApartmentModel apartmentModel;

	@JsonInclude
	public static ApartmentCreditHistoryModel converter(final ApartmentCreditDto creditHistory) {
		ApartmentCreditHistoryModel apartmentCreditHistoryModel = new ApartmentCreditHistoryModel();
		apartmentCreditHistoryModel.setAmount(creditHistory.getAmount());
		apartmentCreditHistoryModel.setTransactionDate(creditHistory.getTransactionDate());
		apartmentCreditHistoryModel.setCreditType(creditHistory.getCreditType());
		apartmentCreditHistoryModel.setUpdatedAt(new Date());
		apartmentCreditHistoryModel.setDescription(creditHistory.getDescription());
		return apartmentCreditHistoryModel;
	}
}
