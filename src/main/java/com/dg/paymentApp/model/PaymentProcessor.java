package com.dg.paymentApp.model;

import com.dg.paymentApp.exceptions.NetworkException;
import com.dg.paymentApp.exceptions.PaymentException;
import com.dg.paymentApp.logger.Logger;
import com.dg.paymentApp.exceptions.RefundException;

public class PaymentProcessor {
    private final PaymentGateway paymentGateway;
    private final Logger logger;

    public PaymentProcessor(PaymentGateway paymentGateway, Logger logger) {
        this.paymentGateway = paymentGateway;
        this.logger = logger;
    }
    public PaymentProcessor(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
        this.logger = new Logger();
    }

    public TransactionResult processPayment(String userId, Double amount){
        try {
            return paymentGateway.charge(userId, amount);
        } catch (NetworkException | PaymentException e){
            Logger.loggError(e.getMessage());
            return new TransactionResult(false, "Payment failed", "Payment failed", TransactionStatus.FAILED);
        }
    }

    public TransactionResult refundPayment(String transactionId){
        try {
            return paymentGateway.refund(transactionId);
        } catch (NetworkException | RefundException e){
            Logger.loggError(e.getMessage());
            return new TransactionResult(false, "Refund failed", "Refund failed", TransactionStatus.FAILED);
        }

    }

    public TransactionStatus getPaymentStatus(String transactionId){
        try {
            return paymentGateway.getStatus(transactionId);
        } catch (NetworkException | NullPointerException e){
            logger.logg(e.getMessage());
            return TransactionStatus.FAILED;
        }
    }


}
