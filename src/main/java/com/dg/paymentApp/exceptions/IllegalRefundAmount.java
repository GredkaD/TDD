package com.dg.paymentApp.exceptions;

public class IllegalRefundAmount extends RuntimeException{
    public IllegalRefundAmount(String message) {
        super(message);
    }
}
