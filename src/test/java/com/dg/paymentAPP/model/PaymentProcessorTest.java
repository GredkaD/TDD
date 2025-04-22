package com.dg.paymentAPP.model;

import static com.dg.paymentApp.model.TransactionStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.dg.paymentApp.logger.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.dg.paymentApp.exceptions.NetworkException;
import com.dg.paymentApp.exceptions.PaymentException;
import com.dg.paymentApp.exceptions.RefundException;
import com.dg.paymentApp.model.PaymentGateway;
import com.dg.paymentApp.model.PaymentProcessor;
import com.dg.paymentApp.model.TransactionResult;
import com.dg.paymentApp.model.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock private PaymentGateway paymentGateway;
    @Mock private Logger logger;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    //Tests for processPayment
    @Test
    void shouldReturnSuccessTRForProcessPayment(){
        //given
        String userID = "123";
        Double amount = 100.0;
        String transactionID = "/trid";
        Mockito.when(paymentGateway.charge(userID, amount)).thenReturn(new TransactionResult(true, userID + transactionID, "Payment success", COMPLETED ));

        //when
        TransactionResult tr = paymentProcessor.processPayment(userID, amount);

        //then
        assertEquals(new TransactionResult(true, "123/trid", "Payment success", COMPLETED ), tr);
    }

    @Test
    void shouldReturnFailedTRForNullUserId(){
        //given
        Mockito.when(paymentGateway.charge(isNull(), anyDouble())).thenThrow(new NetworkException("Amount cannot be null"));

        //when
        TransactionResult result = paymentProcessor.processPayment(null, 100.0);

        //then
        TransactionResult expected = new TransactionResult(false, "Payment failed", "Payment failed", FAILED);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnFailedTRForNullAmount(){
        //given
        Mockito.when(paymentGateway.charge(anyString(), isNull())).thenThrow(new NetworkException("Username cannot be empty"));

        //when
        TransactionResult result = paymentProcessor.processPayment("123", null);

        //then
        TransactionResult expected = new TransactionResult(false, "Payment failed", "Payment failed", FAILED);
        assertEquals(expected, result);
    }

    @CsvSource({"123, -5", "123, 100000000000000000"})
    @ParameterizedTest
    void shouldReturnFailedTRForAmountOutOfRange(String userId, double amount){
        //given
        Mockito.when(paymentGateway.charge(anyString(), anyDouble())).
                thenThrow(new PaymentException("Amount cannot be negative or greater than 1000000000.0"));

        //when
        TransactionResult result = paymentProcessor.processPayment(userId, amount);

        //then
        TransactionResult expected = new TransactionResult(false, "Payment failed", "Payment failed", FAILED);
        assertEquals(expected, result);
    }

    //Tests for refundPayment

    @Test
    void shouldReturnFailedTRForNullTransactionID(){
        //given
        Mockito.when(paymentGateway.refund(isNull())).thenThrow(new NetworkException("TransactionId cannot be empty"));

        //when
        TransactionResult result = paymentProcessor.refundPayment(null);

        //then
        TransactionResult expected = new TransactionResult(false, "Refund failed", "Refund failed", FAILED);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnFailedTRForEmptyTransactionID(){
        //given
        Mockito.when(paymentGateway.refund("")).thenThrow(new NetworkException("TransactionId cannot be empty"));

        //when
        TransactionResult result = paymentProcessor.refundPayment("");

        //then
        TransactionResult expected = new TransactionResult(false, "Refund failed", "Refund failed", FAILED);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnFailedWithRefundException(){
        //given
        Mockito.when(paymentGateway.refund(anyString())).thenThrow(new RefundException("Refund failed"));

        //when
        TransactionResult result = paymentProcessor.refundPayment("123");

        //then
        TransactionResult expected = new TransactionResult(false, "Refund failed", "Refund failed", FAILED);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnSuccessTRForRefund(){
        //given
        String transactionId = "123";
        Mockito.when(paymentGateway.refund(anyString())).thenReturn(new TransactionResult(true, transactionId, "Payment success", COMPLETED));

        //when
        TransactionResult result = paymentProcessor.refundPayment(transactionId);

        //then
        TransactionResult expected = new TransactionResult(true, "123", "Payment success", COMPLETED);
        assertEquals(expected, result);
    }

    //Tests for getPaymentStatus

    @Test
    void shouldReturnFailedForNullId(){
        //given
        Mockito.when(paymentGateway.getStatus(isNull())).thenThrow(NetworkException.class);

        //when
        TransactionStatus result = paymentProcessor.getPaymentStatus(null);

        //then
        assertEquals(FAILED, result);

    }

    @Test
    void shouldReturnFailedForEmptyId(){
        //given
        Mockito.when(paymentGateway.getStatus("")).thenThrow(NetworkException.class);


        //when
        TransactionStatus result = paymentProcessor.getPaymentStatus("");

        //then
        assertEquals(FAILED, result);

    }


    @Test
    void shouldReturnTransactionDoNotExist(){
        //given
        Mockito.when(paymentGateway.getStatus(anyString())).thenThrow(NullPointerException.class);

        //when
        TransactionStatus result = paymentProcessor.getPaymentStatus("123");

        //then
        assertEquals(FAILED, result);

    }

    @Test
    void shouldReturnTS(){
        //given
        Mockito.when(paymentGateway.getStatus(anyString())).thenReturn(COMPLETED);

        //when
        TransactionStatus result = paymentProcessor.getPaymentStatus("123");

        //then
        assertEquals(COMPLETED, result);

    }

    //Logger

    @Test
    void loggerTest(){
        // given
        PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);
        Logger logger = new Logger();
        Logger spyLogger = Mockito.spy(logger);
        PaymentProcessor paymentProcessor = new PaymentProcessor(paymentGateway, spyLogger);

        Mockito.when(paymentGateway.getStatus(anyString()))
               .thenThrow(new NullPointerException("TransactionId cannot be empty or null"));

        // when
        paymentProcessor.getPaymentStatus("123");

        // then
        verify(spyLogger).logg("TransactionId cannot be empty or null");
    }

}
