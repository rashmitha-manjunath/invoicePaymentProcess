package com.springboot.invoicePaymentProcess.service;

import com.springboot.invoicePaymentProcess.exception.ResourceNotFoundException;
import com.springboot.invoicePaymentProcess.model.Invoice;
import com.springboot.invoicePaymentProcess.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceGeneratorService invoiceGeneratorService;

    private static final double LATE_FEE = 10.50;

    public Invoice createInvoice(Invoice invoice) {
        invoice.setStatus("PENDING");
        invoice.setPaidAmount(0.0);
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice payInvoice(Long id, Double amount) {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice != null) {
            invoice.setPaidAmount(invoice.getPaidAmount() + amount);
            if (invoice.getPaidAmount() >= invoice.getAmount()) {
                invoice.setStatus("PAID");
            } else {
                invoice.setStatus("PENDING");
            }
            invoiceRepository.save(invoice);
            return invoice;
        }
        return null;
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        // Update fields
        invoice.setAmount(invoiceDetails.getAmount());
        invoice.setPaidAmount(invoiceDetails.getPaidAmount());
        invoice.setDueDate(invoiceDetails.getDueDate());

        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        invoiceRepository.delete(invoice);
    }

    // Overdue processing logic
    public void processOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices();

        for (Invoice invoice : overdueInvoices) {
            if (isPartiallyPaid(invoice)) {
                handlePartiallyPaidInvoice(invoice);
            } else {
                handleUnpaidInvoice(invoice);
            }
        }
    }

    private boolean isPartiallyPaid(Invoice invoice) {
        return invoice.getPaidAmount() > 0 && invoice.getPaidAmount() < invoice.getAmount();
    }

    private void handlePartiallyPaidInvoice(Invoice invoice) {
        invoice.setStatus("PAID"); // Marking as paid
        invoiceRepository.save(invoice); // Updating existing invoice

        // Calculating the new invoice amount
        double remainingAmount = invoice.getAmount() - invoice.getPaidAmount() + LATE_FEE;

        // Creating new invoice for the remaining amount
        Invoice newInvoice = invoiceGeneratorService.createNewInvoice(remainingAmount);
        invoiceRepository.save(newInvoice); // Saving new invoice
    }

    private void handleUnpaidInvoice(Invoice invoice) {
        invoice.setStatus("VOID"); // Marking as void
        invoiceRepository.save(invoice); // Updating existing invoice

        // Creating new invoice for the full amount plus late fee
        Invoice newInvoice = invoiceGeneratorService.createNewInvoice(invoice.getAmount() + LATE_FEE);
        invoiceRepository.save(newInvoice); // Saving new invoice
    }
}