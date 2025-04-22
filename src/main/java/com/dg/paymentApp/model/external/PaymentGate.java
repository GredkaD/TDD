package com.dg.paymentApp.model.external;

import com.dg.paymentApp.model.TransactionResult;
import com.dg.paymentApp.exceptions.IllegalRefoundState;
import com.dg.paymentApp.exceptions.IllegalRefundAmount;
import com.dg.paymentApp.logger.Logger;
import com.dg.paymentApp.model.TransactionStatus;

import java.util.HashMap;
import java.util.Map;

public class PaymentGate {

//    Map<transactionID, payment>
    private final Map<String, Payment> payments = new HashMap<>();

    private String getNextId(){
        return payments.keySet().stream().map(Integer::parseInt).sorted().toList().getLast().toString();
    }

     public TransactionResult processPayment(String userId, Double amount){
         String transactionID = getNextId();
         Payment payment = new Payment(transactionID, userId, amount);
         payment.setPaid(true);
         payments.put(transactionID, payment);

         return new TransactionResult(true, transactionID, "Payment success", payment.status);
    }

//    public boolean refundPayment(String transactionId, Double refundAmount){
//        Payment payment = payments.get(transactionId);
//        try {
//            payment.setToRefund(true);
//            payment.setRefundAmount(refundAmount);
//            return true;
//
//        } catch (IllegalRefundAmount | IllegalRefoundState e){
//            Logger.logg(e.getMessage());
//            return false;
//        }
//    }

    public TransactionResult refundPayment(String transactionId){
        Payment payment = payments.get(transactionId);
        try {
            payment.setToRefund(true);
            payment.setRefundAmount(payment.amount);
            return getTransactionResult(transactionId);

        } catch (IllegalRefundAmount | IllegalRefoundState e){
            Logger.loggError(e.getMessage());
            payment.setStatus(TransactionStatus.FAILED);
            return getTransactionResult(transactionId);
        }

    }

    public TransactionStatus getPaymentStatus(String transactionId) throws NullPointerException {
        return payments.get(transactionId).status;
    }

    public TransactionResult getTransactionResult(String transactionId){
        TransactionStatus status = payments.get(transactionId).status;
        if (status == TransactionStatus.COMPLETED) {
            return new TransactionResult(true, transactionId, "Payment success", payments.get(transactionId).status);
        } else if (status == TransactionStatus.FAILED) {
            return new TransactionResult(false, transactionId, "Payment failed", payments.get(transactionId).status);
        } else {
            return new TransactionResult(false, transactionId, "Payment pending", payments.get(transactionId).status);
        }
    }

}
