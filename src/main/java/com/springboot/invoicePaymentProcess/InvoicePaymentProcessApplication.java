package com.springboot.invoicePaymentProcess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.springboot.invoicePaymentProcess"})
public class InvoicePaymentProcessApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicePaymentProcessApplication.class, args);
	}

}
