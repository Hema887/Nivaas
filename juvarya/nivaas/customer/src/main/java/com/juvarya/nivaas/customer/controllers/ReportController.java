package com.juvarya.nivaas.customer.controllers;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ReportService;
import com.juvarya.nivaas.customer.util.UserRoleHelper;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRoleHelper userRoleHelper;

    @Autowired
    private NivaasApartmentService apartmentService;

    @GetMapping("/debit/apartment/{apartmentId}/year/{year}/month/{month}")
    @PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
    public ResponseEntity<byte[]> getDebitMonthlyReport(@PathVariable Long apartmentId, @PathVariable @Valid @Min(2023) int year,
                                                        @PathVariable @Valid @Min(1) @Max(12) int month) {
    	 log.info("Request received for debit report. Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
        NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(apartmentId);
        if (Objects.isNull(nivaasApartmentModel) || !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
        	log.warn("Unauthorized access attempt for debit report by user: {}", user);
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        byte[] pdfBytes = reportService.generateDebitReport(nivaasApartmentModel, year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "debit_report_" + year + "_" + month + ".pdf");
        log.info("Debit report generated successfully for Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/credit/apartment/{apartmentId}/year/{year}/month/{month}")
    @PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
    public ResponseEntity<byte[]> getCreditMonthlyReport(@PathVariable Long apartmentId, @PathVariable @Valid @Min(2023) int year,
                                                         @PathVariable @Valid @Min(1) @Max(12) int month) {
    	log.info("Request received for credit report. Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
        NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(apartmentId);
        if (Objects.isNull(nivaasApartmentModel) || !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
        	log.warn("Unauthorized access attempt for credit report by user: {}", user);
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        byte[] pdfBytes = reportService.generateCreditReport(nivaasApartmentModel, year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "credit_report_" + year + "_" + month + ".pdf");

        log.info("Credit report generated successfully for Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/apartment/{apartmentId}/year/{year}/month/{month}")
    @PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
    public ResponseEntity<byte[]> getMonthlyReport(@PathVariable Long apartmentId,
                                                   @PathVariable @Valid @Min(2023) int year,
                                                   @PathVariable @Valid @Min(1) @Max(12) int month) {
    	log.info("Request received for monthly report. Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
        NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(apartmentId);
        if (Objects.isNull(nivaasApartmentModel) || !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
        	log.warn("Unauthorized access attempt for monthly report by user: {}", user);
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        byte[] pdfBytes = reportService.generateMonthlyReport(nivaasApartmentModel, year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "monthly_report_" + year + "_" + month + ".pdf");

        log.info("Monthly report generated successfully for Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
	@GetMapping("/balance/apartment/{apartmentId}/year/{year}/month/{month}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity<byte[]> getMonthlyBalanceSheet(@PathVariable Long apartmentId,
														 @PathVariable @Valid @Min(2023) int year, 
														 @PathVariable @Valid @Min(1) @Max(12) int month) {
		log.info("Request received for monthly Balance sheet. Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
        NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(apartmentId);
        if (Objects.isNull(nivaasApartmentModel) || !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
        	log.warn("Unauthorized access attempt for monthly report by user: {}", user);
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        byte[] pdfBytes = reportService.generateBalanceSheet(nivaasApartmentModel, year, month);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "monthly_report_" + year + "_" + month + ".pdf");

        log.info("Monthly report generated successfully for Apartment ID: {}, Year: {}, Month: {}", apartmentId, year, month);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
	}
	
}
