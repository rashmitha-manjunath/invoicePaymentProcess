package com.springboot.invoicePaymentProcess.repositories;

import com.springboot.invoicePaymentProcess.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < CURRENT_DATE AND i.status NOT IN ('PAID', 'VOID')")
    List<Invoice> findOverdueInvoices();
}
