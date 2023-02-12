package com.payment.cardinity.model;

import java.util.UUID;

public class Recurring implements PaymentInstrument {

    private UUID paymentId;

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public Payment.Method getType() {
        return Payment.Method.RECURRING;
    }
}
