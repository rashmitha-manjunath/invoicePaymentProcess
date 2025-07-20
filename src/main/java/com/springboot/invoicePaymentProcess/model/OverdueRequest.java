package com.springboot.invoicePaymentProcess.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverdueRequest {

    private Double lateFee;
    private Integer overdueDays;
}
