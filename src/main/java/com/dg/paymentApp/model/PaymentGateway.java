package com.dg.paymentApp.model;

import com.dg.paymentApp.exceptions.NetworkException;
import com.dg.paymentApp.exceptions.PaymentException;
import com.dg.paymentApp.model.external.PaymentGate;
import com.dg.paymentApp.exceptions.RefundException;

public class PaymentGateway {

    private final PaymentGate paymentGate;

    public PaymentGateway(PaymentGate paymentGate) {
        this.paymentGate = paymentGate;
    }

    public TransactionResult charge(String userId, Double amount) throws NetworkException, PaymentException {
        if (amount == null){
            throw new NetworkException("Amount cannot be null");
        } else if (userId == null || userId.isEmpty()){
            throw new NetworkException("Username cannot be empty");
        } else if (amount > 1000000000.0 || amount < 0.0){
            throw new PaymentException("Amount cannot be negative or greater than 1000000000.0");
        } else {
            return paymentGate.processPayment(userId, amount);
        }
    }

    public TransactionResult refund(String transactionId) throws NetworkException, RefundException{

        if (transactionId == null || transactionId.isEmpty()){
            throw new NetworkException("TransactionId cannot be empty");
        }

        TransactionResult result = paymentGate.refundPayment(transactionId);
        if (result.transactionStatus == TransactionStatus.FAILED) {
            throw new RefundException("Refund failed");
        } else {
            return result;
        }
    }

    public TransactionStatus getStatus(String transactionId) throws NetworkException, NullPointerException{
        if (transactionId == null || transactionId.isEmpty()){
            throw new NetworkException("TransactionId cannot be empty or null");
        } else {
            return paymentGate.getPaymentStatus(transactionId);
        }
    }
}
