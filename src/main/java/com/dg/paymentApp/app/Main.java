package com.dg.paymentApp.app;

import com.dg.paymentApp.model.PaymentProcessor;
import com.dg.paymentApp.model.TransactionStatus;
import com.dg.paymentApp.model.external.PaymentGate;
import com.dg.paymentApp.model.PaymentGateway;

public class Main {
    public static void main(String[] args) {
        PaymentGate paymentGate = new PaymentGate();
        PaymentGateway paymentGateway = new PaymentGateway(paymentGate);
        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentGateway);

    }
}