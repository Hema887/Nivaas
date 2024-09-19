package com.juvarya.nivaas.customer.service;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

public interface ReportService {

    byte[] generateDebitReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month);

    byte[] generateCreditReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month);

    byte[] generateMonthlyReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month);
    
    byte[] generateBalanceSheet(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month);
    
}
