package com.automation.api.model;

import java.math.BigDecimal;

public class TransferRequest {

    private final String type = "transfer";
    private BigDecimal amount;
    private String description;
    private String toAccountNumber;

    public TransferRequest(BigDecimal amount, String description, String toAccountNumber) {
        this.amount = amount;
        this.description = description;
        this.toAccountNumber = toAccountNumber;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }
}
