package com.juvarya.nivaas.customer.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.dto.TransactionDto;
import com.juvarya.nivaas.customer.model.ApartmentCreditHistoryModel;
import com.juvarya.nivaas.customer.model.ApartmentDebitHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.SocietyDue;
import com.juvarya.nivaas.customer.model.constants.FlatPaymentStatus;
import com.juvarya.nivaas.customer.repository.ApartmentCreditHistoryRepository;
import com.juvarya.nivaas.customer.repository.ApartmentDebitHistoryRepository;
import com.juvarya.nivaas.customer.repository.SocietyDueRepository;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.ReportService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ApartmentDebitHistoryRepository apartmentDebitHistoryRepository;

    @Autowired
    private SocietyDueRepository societyDueRepository;

    @Autowired
    private NivaasFlatService flatService;

    @Autowired
    private AccessMgmtClient accessMgmtClient;
    
    @Autowired
    private ApartmentCreditHistoryRepository apartmentCreditHistoryRepository;

    private static final String watermark = "NIVAAS";

    @Override
    public byte[] generateDebitReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month) {
    	 log.info("Generating debit report for apartment {} for {} - {}", nivaasApartmentModel.getId(), Month.of(month), year);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add title
        document.add(new Paragraph(nivaasApartmentModel.getName()).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(16));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        //Add debit history
        document.add(new Paragraph("Debit Report for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
        document.add(new Paragraph(" "));

        document.add(createDebitReportTable(nivaasApartmentModel.getId(), year, month));

        // Add watermark
        try {
            addWatermarkAndBorder(pdfDoc);
        } catch (IOException e) {
        	 log.warn("Failed to add watermark to debit report: {}", e.getMessage());
            //TODO: log failed to add watermark
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] generateCreditReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month) {
    	log.info("Generating credit report for apartment {} for {} - {}", nivaasApartmentModel.getId(), Month.of(month), year);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add title
        document.add(new Paragraph(nivaasApartmentModel.getName()).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(16));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        //Add credit history
        document.add(new Paragraph("Credit Report for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
        document.add(new Paragraph(" "));

        document.add(createCreditReportTable(nivaasApartmentModel.getId(), year, month));

        // Add watermark
        try {
            addWatermarkAndBorder(pdfDoc);
        } catch (IOException e) {
        	 log.warn("Failed to add watermark to credit report: {}", e.getMessage());
            //TODO: log failed to add watermark
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] generateMonthlyReport(final NivaasApartmentModel nivaasApartmentModel, final int year, final int month) {
    	log.info("Generating monthly report for apartment {} for {} - {}", nivaasApartmentModel.getId(), Month.of(month), year);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add title
        document.add(new Paragraph(nivaasApartmentModel.getName()).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(16));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Maintenance report for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
        document.add(new Paragraph(" "));

        //Add credit history
        document.add(new Paragraph("Credit Report for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
        document.add(new Paragraph(" "));

        document.add(createCreditReportTable(nivaasApartmentModel.getId(), year, month));

        //Add debit history
        document.add(new Paragraph("Debit report for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
        document.add(new Paragraph(" "));

        document.add(createDebitReportTable(nivaasApartmentModel.getId(), year, month));

        // Add watermark
        try {
            addWatermarkAndBorder(pdfDoc);
        } catch (IOException e) {
        	log.warn("Failed to add watermark to monthly report: {}", e.getMessage());
            //TODO: log failed to add watermark
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private Table  createDebitReportTable(final Long apartmentId, final int year, final int month) {
    	log.debug("Creating debit report table for apartment {} for {} - {}", apartmentId, Month.of(month), year);
        // Add table with border
        float[] columnWidths = {1, 2, 6, 2, 2, 2};
        Table table = new Table(columnWidths);
        table.useAllAvailableWidth();

        // Add table header
        table.addHeaderCell(createCell("S.No", true, false));
        table.addHeaderCell(createCell("Transaction Date", true, false));
        table.addHeaderCell(createCell("Type", true, false));
        table.addHeaderCell(createCell("Description", true, false));
        table.addHeaderCell(createCell("Amount", true, false));
        table.addHeaderCell(createCell("Transaction By", true, false));

        List<ApartmentDebitHistoryModel> debits = apartmentDebitHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
        // Add table rows
        int counter = 1;
        double totalAmount = 0;
        for (ApartmentDebitHistoryModel debit : debits) {
            UserDTO user = accessMgmtClient.getUserById(debit.getUpdatedBy());
            table.addCell(createCell(String.valueOf(counter), false, false));
            table.addCell(createCell(debit.getTransactionDate().toString(), false, false));
            table.addCell(createCell(null != debit.getType() ? debit.getType().toString() : "", false, false));
            table.addCell(createCell(debit.getDescription(), false, false));
            table.addCell(createCell(debit.getAmount().toString(), false, false));
            table.addCell(createCell(null != user ? user.getFullName() : "", false, false));
            counter++;
            totalAmount += debit.getAmount();
        }
        if (counter > 1) {
            table.addFooterCell(createCell("", false, false));
            table.addFooterCell(createCell("Total", false, true));
            table.addFooterCell(createCell("", false, false));
            table.addFooterCell(createCell("", false, false));
            table.addFooterCell(createCell(String.valueOf(totalAmount), false, true));
        }
        return table;
    }

    private Table createCreditReportTable(final Long apartmentId, final int year, final int month) {
    	log.debug("Creating credit report table for apartment {} for {} - {}", apartmentId, Month.of(month), year);
        // Add table with border
        float[] columnWidths = {1, 2, 2, 2, 2};
        Table table = new Table(columnWidths);
        table.useAllAvailableWidth();

        // Add table header
        table.addHeaderCell(createCell("S.No", true, false));
        table.addHeaderCell(createCell("Flat Number", true, false));
        table.addHeaderCell(createCell("Due Date", true, false));
        table.addHeaderCell(createCell("Amount", true, false));
        table.addHeaderCell(createCell("Status", true, false));

        List<SocietyDue> credits = societyDueRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
        Map<Long, String> flatsMapByApartmentMap = flatService.getFlatsMapByApartment(apartmentId);
        // Add table rows
        int counter = 1;
        double totalAmount = 0;
        for (SocietyDue credit : credits) {
            final String flatNumber = flatsMapByApartmentMap.get(credit.getFlatId());
            table.addCell(createCell(String.valueOf(counter), false, false));
            table.addCell(createCell(null != flatNumber ? flatNumber : "", false, false));
            table.addCell(createCell(credit.getDueDate().toString(), false, false));
            table.addCell(createCell(null != credit.getCost()? String.valueOf(credit.getCost()) : "0", false, false));
            table.addCell(createCell(credit.getStatus().name(), false, false));
            counter++;
            totalAmount += credit.getCost();
        }
        if (counter > 1) {
            table.addFooterCell(createCell("", false, false));
            table.addFooterCell(createCell("Total", false, true));
            table.addFooterCell(createCell("", false, false));
            table.addFooterCell(createCell(String.valueOf(totalAmount), false, true));
        }
        return table;
    }

    private Cell createCell(String content, boolean isHeader, boolean isFooter) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cell.setBold();
        } else if (isFooter) {
            cell.setBold();
        }
        cell.setBorder(Border.NO_BORDER);
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }

    private void addWatermarkAndBorder(PdfDocument pdfDoc) throws IOException {
    	 log.debug("Adding watermark and border to PDF document");
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        int numberOfPages = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDoc.getPage(i);
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            // Draw border
            float x = page.getPageSize().getLeft();
            float y = page.getPageSize().getBottom();
            float width = page.getPageSize().getWidth();
            float height = page.getPageSize().getHeight();

            pdfCanvas.saveState();
            pdfCanvas.setLineWidth(2);
            pdfCanvas.setStrokeColor(ColorConstants.BLACK);
            pdfCanvas.rectangle(x + 10, y + 10, width - 20, height - 20);
            pdfCanvas.stroke();
            pdfCanvas.restoreState();

            //Add watermark
            pdfCanvas.saveState();
            pdfCanvas.setFillColor(ColorConstants.LIGHT_GRAY);
            Canvas canvas = new Canvas(pdfCanvas, new Rectangle(44, 44));
            canvas.setFont(font);
            canvas.setFontSize(60);
            canvas.showTextAligned(watermark, 298, 421, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
            canvas.close();
            pdfCanvas.restoreState();
        }
    }

	@Override
	public byte[] generateBalanceSheet(NivaasApartmentModel nivaasApartmentModel, int year, int month) {
		 log.info("Generating debit report for apartment {} for {} - {}", nivaasApartmentModel.getId(), Month.of(month), year);
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
	        PdfDocument pdfDoc = new PdfDocument(writer);
	        Document document = new Document(pdfDoc);

	        // Add title
	        document.add(new Paragraph(nivaasApartmentModel.getName()).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(16));
	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));

	        //Add debit history
	        document.add(new Paragraph("Balance Sheet for " + Month.of(month) + " - " + year).setBold().setFontSize(12));
	        document.add(new Paragraph(" "));

	        document.add(createBalanceSheet(nivaasApartmentModel.getId(), year, month, document));

	        // Add watermark
	        try {
	            addWatermarkAndBorder(pdfDoc);
	        } catch (IOException e) {
	        	 log.warn("Failed to add watermark to debit report: {}", e.getMessage());
	            //TODO: log failed to add watermark
	        }

	        document.close();
	        return byteArrayOutputStream.toByteArray();
	}
	
	private Table createBalanceSheet(final Long apartmentId, final int year, final int month, Document document) {
	    log.debug("Creating balance report table for apartment {} for {} - {}", apartmentId, Month.of(month), year);

	    // Fetch balance from the previous month
	    double previousMonthBalance = getPreviousMonthBalance(apartmentId, year, month);

	    // Create a Table for the balance sheet
	    float[] columnWidths = {1, 2, 4, 2, 2, 2}; // Columns: S.No, Transaction Date, Transaction By, Debit, Credit, Extra Column
	    Table table = new Table(columnWidths);
	    table.useAllAvailableWidth();

	    // Add table header
	    table.addHeaderCell(createCell("S.No", true, false));
	    table.addHeaderCell(createCell("Transaction Date", true, false));
	    table.addHeaderCell(createCell("Transaction By", true, false));
	    table.addHeaderCell(createCell("Credit", true, false));
	    table.addHeaderCell(createCell("Debit", true, false));
	    table.addHeaderCell(createCell("", false, false)); // Extra Column

	    // Fetch debit, credit history, and dues
	    List<ApartmentDebitHistoryModel> debits = apartmentDebitHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
	    List<ApartmentCreditHistoryModel> credits = apartmentCreditHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
	    List<SocietyDue> dues = societyDueRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);

	    // Initialize variables to track totals and updates
	    double cumulativeDebit = 0;
	    double cumulativeCredit = 0;

	    // Combine and sort the transactions by date and time to ensure chronological order
	    List<TransactionDto> transactions = new ArrayList<>();
	    for (ApartmentDebitHistoryModel debit : debits) {
	        transactions.add(new TransactionDto(debit.getTransactionDate(), debit.getAmount(), 0, debit.getUpdatedBy(), null));
	    }
	    for (ApartmentCreditHistoryModel credit : credits) {
	        transactions.add(new TransactionDto(credit.getTransactionDate(), 0, credit.getAmount(), credit.getUpdatedBy(), null));
	    }
	    for (SocietyDue due : dues) {
	        if (due.getStatus().equals(FlatPaymentStatus.PAID)) {
	            transactions.add(new TransactionDto(due.getDueDate(), 0, due.getCost(), flatService.findById(due.getFlatId()).getOwnerId(), flatService.findById(due.getFlatId()).getFlatNo()));
	        }
	    }

	    // Sort transactions by date and time to ensure the latest updates are reflected correctly
	    transactions.sort(Comparator.comparing(TransactionDto::getTransactionDate));

	    // Add table rows in the correct order
	    int counter = 1;

	    for (TransactionDto transaction : transactions) {
	        double debitAmount = transaction.getDebit();
	        double creditAmount = transaction.getCredit();
	        
	        // Update cumulative totals
	        cumulativeDebit += debitAmount;
	        cumulativeCredit += creditAmount;

	        String flatNo = transaction.getFlatNo();
	        String userFullName = accessMgmtClient.getUserById(transaction.getUpdatedBy()).getFullName();
	        String cellContent = (flatNo != null && !flatNo.isEmpty()) ? flatNo + " " + userFullName : userFullName;
	        
	        // Add row to the table
	        table.addCell(createCell(String.valueOf(counter), false, false));
	        table.addCell(createCell(transaction.getTransactionDate().toString(), false, false));
	        table.addCell(createCell(cellContent, false, false));
	        table.addCell(createCell(creditAmount > 0 ? String.valueOf(creditAmount) : "-", false, false)); // Display credit
	        table.addCell(createCell(debitAmount > 0 ? String.valueOf(debitAmount) : "-", false, false)); // Display debit
	        table.addCell(createCell("", false, false)); // Extra Column

	        counter++;
	    }

	    // Final totals
	    double netTotal = cumulativeCredit - cumulativeDebit + previousMonthBalance;

	    // Add space headers and footers conditionally to manage page breaks
	    addSpaceHeaderAndFooter(table); // Adjust spacing as needed
	    addSpaceHeaderAndFooter(table);
	    addSpaceHeaderAndFooter(table);
	    addSpaceHeaderAndFooter(table);
	    addSpaceHeaderAndFooter(table);
	    addSpaceHeaderAndFooter(table);
	    addSpaceHeaderAndFooter(table);
	    
	    String label = "Previous Balance: ";
	    String cellContent = label + String.format("%.2f", previousMonthBalance);

	    // Add a final row for net total with a label on the last page
	    table.addFooterCell(createCell("", false, false));
	    table.addFooterCell(createCell("Total", false, false));
	    table.addFooterCell(createCell(cellContent, false, true));
	    table.addFooterCell(createCell(String.format("+%.2f", cumulativeCredit), false, false)); // Total credit including dues
	    table.addFooterCell(createCell(String.format("-%.2f", cumulativeDebit), false, false)); // Total debit
	    table.addFooterCell(createCell(String.format("%.2f", netTotal), false, false)); // Net Total

	    return table; 
	}
	
	private double getPreviousMonthBalance(Long apartmentId, int year, int currentMonth) {
		double previousMonthBalance = 0;
		
		 for (int month = 1; month < currentMonth; month++) {
	    List<ApartmentDebitHistoryModel> previousDebits = apartmentDebitHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
	    List<ApartmentCreditHistoryModel> previousCredits = apartmentCreditHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
	    
	    

	    for (ApartmentDebitHistoryModel debit : previousDebits) {
	        previousMonthBalance -= debit.getAmount();
	    }
	    for (ApartmentCreditHistoryModel credit : previousCredits) {
	        previousMonthBalance += credit.getAmount();
	    }
		 }

	    return previousMonthBalance;
	}

	private void addSpaceHeaderAndFooter(final Table table) {
	    // Add space before footer
	    table.addCell(createCell("", false, false)); // Blank row
	    table.addCell(createCell("", false, false)); // Blank row
	    table.addCell(createCell("", false, false)); // Blank row
	    table.addCell(createCell("", false, false)); // Blank row
	    table.addCell(createCell("", false, false)); // Blank row
	    table.addCell(createCell("", false, false)); // Blank row
	}

}
