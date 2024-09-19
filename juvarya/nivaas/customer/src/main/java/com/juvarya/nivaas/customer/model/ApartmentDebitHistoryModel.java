package com.juvarya.nivaas.customer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juvarya.nivaas.customer.dto.ApartmentDebitDto;
import com.juvarya.nivaas.customer.model.constants.DebitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "ApartmentDebitHistory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentDebitHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DebitType type;

    private String description;

    private Double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    private Date updatedAt;

    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "nivaas_apartment_id")
    private NivaasApartmentModel apartmentModel;

    @JsonIgnore
    public static ApartmentDebitHistoryModel converter(final ApartmentDebitDto debitHistory) {
        ApartmentDebitHistoryModel apartmentDebitHistoryModel = new ApartmentDebitHistoryModel();
        apartmentDebitHistoryModel.setAmount(debitHistory.getAmount());
        apartmentDebitHistoryModel.setTransactionDate(debitHistory.getTransactionDate());
        apartmentDebitHistoryModel.setDescription(debitHistory.getDescription());
        apartmentDebitHistoryModel.setType(debitHistory.getType());
        apartmentDebitHistoryModel.setUpdatedAt(new Date());
        return apartmentDebitHistoryModel;
    }

}
