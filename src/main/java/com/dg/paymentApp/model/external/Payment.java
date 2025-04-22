package com.dg.paymentApp.model.external;

import com.dg.paymentApp.exceptions.IllegalRefoundState;
import com.dg.paymentApp.exceptions.IllegalRefundAmount;
import com.dg.paymentApp.model.TransactionStatus;

import java.util.Objects;

public class Payment {
    String id;
    String userId;
    Double amount;
    Boolean isPaid;
    Boolean toRefund;
    Double refundAmount;
    TransactionStatus status;

    public Payment(String id, String userId, Double amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.isPaid = false;
        this.toRefund = false;
        this.refundAmount = 0.0;
        this.status = TransactionStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
        setStatus(TransactionStatus.COMPLETED);
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Boolean getToRefund(){
        return toRefund;
    }

    public void setToRefund(Boolean toRefund) throws IllegalRefoundState {
        if (!toRefund && refundAmount != 0.0) {
            throw new IllegalRefoundState("You can't change 'to Refund' to false if 'refundAmount' is greater than 0");
        } else {
            this.toRefund = toRefund;
        }
    }
     public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) throws IllegalRefundAmount {
        if (toRefund) {
            if (refundAmount >= 0 && refundAmount < this.amount) {
                this.refundAmount = refundAmount;
            } else {
                throw new IllegalRefundAmount("Refund amount must be >= 0 and < amount when toRefund is true");
            }
        } else if (refundAmount != 0.0) {
            throw new IllegalRefundAmount("Refund amount must be 0 when toRefund is false");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Payment " + id + ", user: " + userId + ", amount:" + amount;
    }
}
