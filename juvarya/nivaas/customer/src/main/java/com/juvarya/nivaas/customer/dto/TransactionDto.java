package com.juvarya.nivaas.customer.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TransactionDto {
	private LocalDate transactionDate;
	private double debit;
	private double credit;
	private Long updatedBy;
	private String flatNo;

	public TransactionDto(LocalDate transactionDate, double debit, double credit, Long updatedBy, String flatNo) {
		this.transactionDate = transactionDate;
		this.debit = debit;
		this.credit = credit;
		this.updatedBy = updatedBy;
		this.flatNo = flatNo;
	}
}
